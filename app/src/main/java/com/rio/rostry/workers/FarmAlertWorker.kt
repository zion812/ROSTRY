package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.notifications.FarmAlertNotificationService
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class FarmAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val quarantineRecordDao: QuarantineRecordDao,
    private val productDao: ProductDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val mortalityRecordDao: MortalityRecordDao,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val farmAlertNotificationService: FarmAlertNotificationService,
    private val flowAnalyticsTracker: FlowAnalyticsTracker
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@withContext Result.success()
            val now = System.currentTimeMillis()

            // Track worker execution start
            flowAnalyticsTracker.trackEvent("farm_alert_worker_started", mapOf(
                "farmer_id" to farmerId,
                "timestamp" to now
            ))

            // Check for overdue vaccinations
            val overdueVaccinations = vaccinationRecordDao.getOverdueForFarmer(farmerId, now)
            overdueVaccinations.forEach { record ->
                farmAlertNotificationService.sendVaccinationReminder(
                    record.productId,
                    record.vaccineType ?: "Unknown",
                    record.scheduledAt,
                    "overdue"
                )
            }

            // Check for quarantine updates due (last update > 12 hours ago)
            val twelveHoursAgo = now - TimeUnit.HOURS.toMillis(12)
            val quarantineUpdatesDue = quarantineRecordDao.getUpdatesOverdueForFarmer(farmerId, twelveHoursAgo)
            quarantineUpdatesDue.forEach { record ->
                farmAlertNotificationService.sendQuarantineReminder(
                    record.productId,
                    record.lastUpdatedAt
                )
            }

            // Check for batches ready to split (12+ weeks old)
            val twelveWeeksMillis = TimeUnit.DAYS.toMillis(12 * 7L)
            val minBirthDate = now - twelveWeeksMillis
            val batchesReadyToSplit = productDao.getBatchesReadyToSplit(farmerId, minBirthDate)
            batchesReadyToSplit.forEach { product ->
                val birthDate = product.birthDate
                val ageWeeks = if (birthDate != null) ((now - birthDate) / TimeUnit.DAYS.toMillis(7)).toInt() else 0
                farmAlertNotificationService.sendBatchSplitReminder(
                    product.productId,
                    ageWeeks
                )
            }

            // Check for hatching due soon (< 1 day)
            val oneDayFromNow = now + TimeUnit.DAYS.toMillis(1)
            val hatchingDueSoon = hatchingBatchDao.getHatchingDueSoon(farmerId, now, oneDayFromNow)
            hatchingDueSoon.forEach { batch ->
                farmAlertNotificationService.sendHatchingReminder(
                    batch.batchId,
                    batch.expectedHatchAt ?: oneDayFromNow,
                    "day_before"
                )
            }

            // Check for mortality spikes (calculate recent rate, threshold 5%)
            val totalDeaths = mortalityRecordDao.countForFarmerBetween(
                farmerId,
                now - TimeUnit.DAYS.toMillis(7),
                now
            )
            val estimatedPopulation = productDao.countActiveByOwnerId(farmerId)
            val mortalityRate = if (estimatedPopulation > 0) (totalDeaths.toDouble() / estimatedPopulation) * 100 else 0.0
            if (mortalityRate > 5.0) {
                farmAlertNotificationService.sendMortalitySpikeAlert(farmerId, mortalityRate)
            }

            // Track worker execution success
            flowAnalyticsTracker.trackEvent("farm_alert_worker_completed", mapOf(
                "farmer_id" to farmerId,
                "overdue_vaccinations" to overdueVaccinations.size,
                "quarantine_updates_due" to quarantineUpdatesDue.size,
                "batches_ready_to_split" to batchesReadyToSplit.size,
                "hatching_due_soon" to hatchingDueSoon.size,
                "mortality_rate" to mortalityRate,
                "timestamp" to System.currentTimeMillis()
            ))

            Result.success()
        } catch (e: Exception) {
            // Track worker execution failure
            flowAnalyticsTracker.trackEvent("farm_alert_worker_failed", mapOf(
                "error" to e.message,
                "timestamp" to System.currentTimeMillis()
            ))
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "FarmAlertWorker"
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<FarmAlertWorker>(6, TimeUnit.HOURS)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()
            androidx.work.WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}