package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.repository.StorageUsageRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.notif.FarmNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Worker that monitors cloud storage usage and triggers alerts/cleanup.
 * 
 * Features:
 * - Periodic storage recalculation
 * - Warning notifications at 80% usage
 * - Critical alerts at 95% usage with auto-cleanup trigger
 * 
 * Runs daily to check storage quota utilization.
 */
@HiltWorker
class StorageQuotaMonitorWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: StorageUsageRepository,
    private val firebaseAuth: FirebaseAuth,
    private val workerMetrics: WorkerMetrics
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WORK_NAME = "storage_quota_monitor_work"
        private const val WORKER_NAME = "StorageQuotaMonitorWorker"
        
        // Threshold constants
        private const val WARNING_THRESHOLD = 0.80  // 80% used
        private const val CRITICAL_THRESHOLD = 0.95 // 95% used
        
        // Notification cooldown key
        private const val PREFS_NAME = "storage_quota_prefs"
        private const val KEY_LAST_WARNING = "last_warning_notification"
        private const val KEY_LAST_CRITICAL = "last_critical_notification"
        private const val NOTIFICATION_COOLDOWN_MS = 24 * 60 * 60 * 1000L // 24 hours
        
        fun enqueuePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<StorageQuotaMonitorWorker>(
                1, TimeUnit.DAYS // Recalculate once a day
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag("storage_worker")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun enqueueOneTime(context: Context) {
            val request = OneTimeWorkRequestBuilder<StorageQuotaMonitorWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .addTag("storage_worker")
                .build()
            
            WorkManager.getInstance(context).enqueue(request)
        }
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun doWork(): Result = workerMetrics.trackExecution(WORKER_NAME) {
        val userId = firebaseAuth.currentUser?.uid ?: return@trackExecution Result.success()
        
        Timber.d("StorageQuotaMonitorWorker: Refreshing usage for $userId")
        
        when (val resource = repository.refreshUsage(userId)) {
            is Resource.Success -> {
                val quota = resource.data
                    ?: return@trackExecution Result.success() // No quota data yet
                
                // Calculate usage percentage
                val usagePercent = if (quota.quotaBytes > 0) {
                    (quota.usedBytes.toDouble() / quota.quotaBytes.toDouble())
                } else 0.0
                
                val usagePercentDisplay = (usagePercent * 100).toInt()
                
                Timber.d("StorageQuotaMonitorWorker: Usage at $usagePercentDisplay% " +
                    "(${formatBytes(quota.usedBytes)} / ${formatBytes(quota.quotaBytes)})")
                
                // Check thresholds and send appropriate notifications
                when {
                    usagePercent >= CRITICAL_THRESHOLD -> {
                        handleCriticalUsage(userId, usagePercentDisplay)
                    }
                    usagePercent >= WARNING_THRESHOLD -> {
                        handleWarningUsage(userId, usagePercentDisplay)
                    }
                }
                
                Result.success(
                    workDataOf(
                        "usage_percent" to usagePercentDisplay,
                        "used_bytes" to quota.usedBytes,
                        "quota_bytes" to quota.quotaBytes
                    )
                )
            }
            is Resource.Error -> {
                Timber.e("StorageQuotaMonitorWorker: Failed to refresh usage for $userId: ${resource.message}")
                if (runAttemptCount < 3) Result.retry() else Result.failure()
            }
            is Resource.Loading -> Result.retry()
        }
    }
    
    private fun handleCriticalUsage(userId: String, usagePercent: Int) {
        val now = System.currentTimeMillis()
        val lastNotified = prefs.getLong(KEY_LAST_CRITICAL, 0L)
        
        if (now - lastNotified > NOTIFICATION_COOLDOWN_MS) {
            Timber.w("StorageQuotaMonitorWorker: CRITICAL storage usage at $usagePercent%")
            
            FarmNotifier.showStorageWarning(
                context,
                title = "Storage Almost Full!",
                message = "You've used $usagePercent% of your storage. Delete old files or upgrade your plan.",
                isCritical = true
            )
            
            // Trigger automatic cleanup of old cache
            triggerAutoCleanup()
            
            prefs.edit().putLong(KEY_LAST_CRITICAL, now).apply()
        }
    }
    
    private fun handleWarningUsage(userId: String, usagePercent: Int) {
        val now = System.currentTimeMillis()
        val lastNotified = prefs.getLong(KEY_LAST_WARNING, 0L)
        
        if (now - lastNotified > NOTIFICATION_COOLDOWN_MS) {
            Timber.w("StorageQuotaMonitorWorker: Warning - storage usage at $usagePercent%")
            
            FarmNotifier.showStorageWarning(
                context,
                title = "Storage Running Low",
                message = "You've used $usagePercent% of your storage quota.",
                isCritical = false
            )
            
            prefs.edit().putLong(KEY_LAST_WARNING, now).apply()
        }
    }
    
    private fun triggerAutoCleanup() {
        Timber.d("StorageQuotaMonitorWorker: Triggering automatic cleanup")
        
        // Enqueue DataCleanupWorker for immediate cleanup
        val cleanupRequest = OneTimeWorkRequestBuilder<DataCleanupWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag("storage_cleanup")
            .build()
        
        WorkManager.getInstance(context).enqueue(cleanupRequest)
    }
    
    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824 -> "%.1f GB".format(bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
            bytes >= 1_024 -> "%.1f KB".format(bytes / 1_024.0)
            else -> "$bytes B"
        }
    }
}
