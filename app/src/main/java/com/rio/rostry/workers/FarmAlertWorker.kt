package com.rio.rostry.workers

import android.content.Context
import android.content.SharedPreferences
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
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Background worker that checks for farm alerts and sends notifications.
 * 
 * Features:
 * - Alert deduplication: Same alert not sent within cooldown period
 * - Severity escalation: Overdue items get escalated severity
 * - Smart batching: Multiple alerts can be grouped (future)
 * 
 * Runs every 6 hours to check for:
 * - Overdue vaccinations
 * - Quarantine updates due
 * - Batches ready to split
 * - Hatching due soon
 * - Mortality spikes
 */
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
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
    private val alertRepository: com.rio.rostry.data.repository.AlertRepository
) : CoroutineWorker(context, params) {
    
    private val alertPrefs: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@withContext Result.success()
            val now = System.currentTimeMillis()

            Timber.d("FarmAlertWorker: Starting alert check for farmer $farmerId")

            // Track worker execution start
            flowAnalyticsTracker.trackEvent("farm_alert_worker_started", mapOf(
                "farmer_id" to farmerId,
                "timestamp" to now
            ))

            var alertsSent = 0

            // Check for overdue vaccinations
            val overdueVaccinations = vaccinationRecordDao.getOverdueForFarmer(farmerId, now)
            overdueVaccinations.forEach { record ->
                val alertKey = "vaccination:${record.productId}:${record.vaccineType}"
                val overdueHours = (now - record.scheduledAt) / TimeUnit.HOURS.toMillis(1)
                
                if (shouldSendAlert(alertKey, VACCINATION_COOLDOWN_HOURS)) {
                    val severity = calculateSeverity(overdueHours)
                    farmAlertNotificationService.sendVaccinationReminder(
                        record.productId,
                        record.vaccineType ?: "Unknown",
                        record.scheduledAt,
                        if (severity == "CRITICAL") "overdue" else "due_today"
                    )
                    
                    alertRepository.createAlert(
                        title = "Vaccination Due",
                        message = "Vaccine ${record.vaccineType ?: "Unknown"} is due for batch/bird ${record.productId}",
                        severity = severity,
                        type = "BIOSECURITY",
                        relatedId = record.productId
                    )

                    markAlertSent(alertKey)
                    alertsSent++
                }
            }

            // Check for quarantine updates due (last update > 12 hours ago)
            val twelveHoursAgo = now - TimeUnit.HOURS.toMillis(12)
            val quarantineUpdatesDue = quarantineRecordDao.getUpdatesOverdueForFarmer(farmerId, twelveHoursAgo)
            quarantineUpdatesDue.forEach { record ->
                val alertKey = "quarantine:${record.productId}"
                
                if (shouldSendAlert(alertKey, QUARANTINE_COOLDOWN_HOURS)) {
                    farmAlertNotificationService.sendQuarantineReminder(
                        record.productId,
                        record.lastUpdatedAt
                    )

                    alertRepository.createAlert(
                        title = "Quarantine Update Required",
                        message = "Update required for quarantine record ${record.productId}",
                        severity = "MEDIUM",
                        type = "BIOSECURITY",
                        relatedId = record.productId
                    )

                    markAlertSent(alertKey)
                    alertsSent++
                }
            }

            // Check for batches ready to split (12+ weeks old)
            val twelveWeeksMillis = TimeUnit.DAYS.toMillis(12 * 7L)
            val minBirthDate = now - twelveWeeksMillis
            val batchesReadyToSplit = productDao.getBatchesReadyToSplit(farmerId, minBirthDate)
            batchesReadyToSplit.forEach { product ->
                val alertKey = "batch_split:${product.productId}"
                
                if (shouldSendAlert(alertKey, BATCH_SPLIT_COOLDOWN_HOURS)) {
                    val birthDate = product.birthDate
                    val ageWeeks = if (birthDate != null) ((now - birthDate) / TimeUnit.DAYS.toMillis(7)).toInt() else 0
                    farmAlertNotificationService.sendBatchSplitReminder(
                        product.productId,
                        ageWeeks
                    )

                    alertRepository.createAlert(
                        title = "Batch Ready to Split",
                        message = "Batch ${product.name} is ready to split.",
                        severity = "INFO",
                        type = "SYSTEM",
                        relatedId = product.productId
                    )

                    markAlertSent(alertKey)
                    alertsSent++
                }
            }

            // Check for hatching due soon (< 1 day)
            val oneDayFromNow = now + TimeUnit.DAYS.toMillis(1)
            val hatchingDueSoon = hatchingBatchDao.getHatchingDueSoon(farmerId, now, oneDayFromNow)
            hatchingDueSoon.forEach { batch ->
                val alertKey = "hatching:${batch.batchId}"
                
                if (shouldSendAlert(alertKey, HATCHING_COOLDOWN_HOURS)) {
                    farmAlertNotificationService.sendHatchingReminder(
                        batch.batchId,
                        batch.expectedHatchAt ?: oneDayFromNow,
                        "day_before"
                    )

                    alertRepository.createAlert(
                        title = "Hatching Due Soon",
                        message = "Batch ${batch.name} is due to hatch soon.",
                        severity = "HIGH",
                        type = "MORTALITY", // Using MORTALITY as a proxy for production events
                        relatedId = batch.batchId
                    )

                    markAlertSent(alertKey)
                    alertsSent++
                }
            }

            // Check for mortality spikes (calculate recent rate, threshold 5%)
            val totalDeaths = mortalityRecordDao.countForFarmerBetween(
                farmerId,
                now - TimeUnit.DAYS.toMillis(7),
                now
            )
            val estimatedPopulation = productDao.countActiveByOwnerId(farmerId)
            val mortalityRate = if (estimatedPopulation > 0) (totalDeaths.toDouble() / estimatedPopulation) * 100 else 0.0
            
            if (mortalityRate > MORTALITY_THRESHOLD) {
                val alertKey = "mortality_spike:$farmerId"
                val severity = when {
                    mortalityRate > 10.0 -> "CRITICAL"
                    mortalityRate > 7.0 -> "HIGH"
                    else -> "MEDIUM"
                }
                
                // Shorter cooldown for critical alerts
                val cooldown = if (severity == "CRITICAL") 6L else MORTALITY_COOLDOWN_HOURS
                
                if (shouldSendAlert(alertKey, cooldown)) {
                    farmAlertNotificationService.sendMortalitySpikeAlert(farmerId, mortalityRate)
                    
                    alertRepository.createAlert(
                        title = "Mortality Spike Detected",
                        message = "Mortality rate is ${String.format("%.1f", mortalityRate)}% in the last 7 days.",
                        severity = severity,
                        type = "MORTALITY",
                        relatedId = null
                    )

                    markAlertSent(alertKey)
                    alertsSent++
                }
            }

            // Track worker execution success
            flowAnalyticsTracker.trackEvent("farm_alert_worker_completed", mapOf(
                "farmer_id" to farmerId,
                "overdue_vaccinations" to overdueVaccinations.size,
                "quarantine_updates_due" to quarantineUpdatesDue.size,
                "batches_ready_to_split" to batchesReadyToSplit.size,
                "hatching_due_soon" to hatchingDueSoon.size,
                "mortality_rate" to mortalityRate,
                "alerts_sent" to alertsSent,
                "timestamp" to System.currentTimeMillis()
            ))
            
            Timber.d("FarmAlertWorker: Completed. Sent $alertsSent alerts")

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "FarmAlertWorker failed")
            // Track worker execution failure
            flowAnalyticsTracker.trackEvent("farm_alert_worker_failed", mapOf(
                "error" to e.message,
                "timestamp" to System.currentTimeMillis()
            ))
            Result.retry()
        }
    }
    
    /**
     * Check if enough time has passed since last alert of this type.
     * Returns true if alert should be sent, false if still in cooldown.
     */
    private fun shouldSendAlert(alertKey: String, cooldownHours: Long): Boolean {
        val lastSent = alertPrefs.getLong(alertKey, 0L)
        val cooldownMs = TimeUnit.HOURS.toMillis(cooldownHours)
        val now = System.currentTimeMillis()
        
        return (now - lastSent) > cooldownMs
    }
    
    /**
     * Record that an alert was sent for deduplication.
     */
    private fun markAlertSent(alertKey: String) {
        alertPrefs.edit()
            .putLong(alertKey, System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Calculate alert severity based on overdue hours.
     */
    private fun calculateSeverity(overdueHours: Long): String = when {
        overdueHours > 72 -> "CRITICAL"
        overdueHours > 24 -> "HIGH"
        overdueHours > 6 -> "MEDIUM"
        else -> "LOW"
    }

    companion object {
        private const val UNIQUE_NAME = "FarmAlertWorker"
        private const val PREFS_NAME = "farm_alert_cooldowns"
        
        // Cooldown periods (hours) - prevents alert fatigue
        private const val VACCINATION_COOLDOWN_HOURS = 24L
        private const val QUARANTINE_COOLDOWN_HOURS = 12L
        private const val BATCH_SPLIT_COOLDOWN_HOURS = 72L  // 3 days
        private const val HATCHING_COOLDOWN_HOURS = 12L
        private const val MORTALITY_COOLDOWN_HOURS = 24L
        
        // Thresholds
        private const val MORTALITY_THRESHOLD = 5.0
        
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<FarmAlertWorker>(6, TimeUnit.HOURS)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .addTag("farm_alert_worker")
                .build()
            androidx.work.WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}