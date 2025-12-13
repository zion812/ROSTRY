package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.notifications.FarmEventType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@HiltWorker
class QuarantineReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val quarantineRecordDao: QuarantineRecordDao,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val intelligentNotificationService: IntelligentNotificationService,
    private val taskRepository: TaskRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            val farmerId = firebaseAuth.currentUser?.uid ?: return@withContext Result.success()
            val active = quarantineRecordDao.getAllActiveForFarmer(farmerId)
            val twelveHours = TimeUnit.HOURS.toMillis(12)
            active.filter { it.lastUpdatedAt + twelveHours < now }
                .forEach {
                    val overdueHours = (now - it.lastUpdatedAt) / (1000 * 60 * 60)
                    
                    // Degrade health score for overdue items
                    // Decrease by 2 points every time the worker runs (approx every 3 hours) while overdue
                    val newScore = (it.healthScore - 2).coerceAtLeast(0)
                    if (newScore != it.healthScore) {
                        val updated = it.copy(healthScore = newScore, dirty = true)
                        quarantineRecordDao.update(updated)
                    }

                    val metadata = mapOf(
                        "quarantineId" to it.quarantineId,
                        "lastUpdatedAt" to it.lastUpdatedAt,
                        "overdueHours" to overdueHours,
                        "healthScore" to newScore
                    )
                    intelligentNotificationService.notifyFarmEvent(
                        FarmEventType.QUARANTINE_OVERDUE,
                        it.productId,
                        "Quarantine Update Required",
                        "Update overdue by $overdueHours hours. Health score dropped to $newScore%.",
                        metadata
                    )
                    val existingTasks = taskRepository.findPendingByTypeProduct(farmerId, it.productId, "QUARANTINE_CHECK")
                    if (existingTasks.isEmpty()) {
                        taskRepository.generateQuarantineCheckTask(it.productId, farmerId, now)
                    }
                }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "QuarantineReminderWorkerPeriodic"
        fun schedule(context: Context) {
            val initialDelayMinutes = Random.nextLong(0, 180)
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()
            val request = PeriodicWorkRequestBuilder<QuarantineReminderWorker>(1, TimeUnit.DAYS, 3, TimeUnit.HOURS)
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }
}
