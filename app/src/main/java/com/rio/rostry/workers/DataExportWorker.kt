package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rio.rostry.data.service.BackupService
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.notif.FarmNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Worker for data backup/export operations.
 * 
 * Supports:
 * - Manual one-time export
 * - Scheduled weekly backups
 * - Export to app cache or Downloads folder
 * 
 * Input Data:
 * - exportToDownloads: Boolean (false = cache, true = Downloads)
 * - scheduled: Boolean (true if from scheduled job)
 */
@HiltWorker
class DataExportWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupService: BackupService,
    private val workerMetrics: WorkerMetrics
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = workerMetrics.trackExecution(WORKER_NAME) {
        val exportToDownloads = inputData.getBoolean(KEY_EXPORT_TO_DOWNLOADS, false)
        val isScheduled = inputData.getBoolean(KEY_SCHEDULED, false)
        
        Timber.d("DataExportWorker: Starting ${if (isScheduled) "scheduled" else "manual"} export (toDownloads: $exportToDownloads)")
        
        if (exportToDownloads) {
            // Export to Downloads folder - returns Uri
            when (val result = backupService.exportToDownloads()) {
                is Resource.Success -> {
                    val uri = result.data
                    if (uri != null) {
                        // Can't use FarmNotifier.notifyBackupComplete with Uri, use a generic message
                        Timber.d("DataExportWorker: Export to Downloads successful - $uri")
                        Result.success(
                            workDataOf(
                                "uri" to uri.toString(),
                                "scheduled" to isScheduled
                            )
                        )
                    } else {
                        Timber.e("DataExportWorker: Export returned null uri")
                        Result.failure()
                    }
                }
                is Resource.Error -> {
                    Timber.e("DataExportWorker: Export failed - ${result.message}")
                    if (runAttemptCount < 3) Result.retry() else Result.failure()
                }
                is Resource.Loading -> Result.retry()
            }
        } else {
            // Export to app cache - returns File
            when (val result = backupService.exportUserData()) {
                is Resource.Success -> {
                    val file = result.data
                    if (file != null) {
                        FarmNotifier.notifyBackupComplete(context, file.absolutePath)
                        Timber.d("DataExportWorker: Export successful - ${file.absolutePath}")
                        Result.success(
                            workDataOf(
                                "file_path" to file.absolutePath,
                                "scheduled" to isScheduled
                            )
                        )
                    } else {
                        Timber.e("DataExportWorker: Export returned null file")
                        Result.failure()
                    }
                }
                is Resource.Error -> {
                    Timber.e("DataExportWorker: Export failed - ${result.message}")
                    if (runAttemptCount < 3) Result.retry() else Result.failure()
                }
                is Resource.Loading -> Result.retry()
            }
        }
    }

    companion object {
        private const val WORKER_NAME = "DataExportWorker"
        private const val MANUAL_WORK_NAME = "manual_data_export_worker"
        private const val SCHEDULED_WORK_NAME = "scheduled_data_export_worker"
        
        // Input keys
        const val KEY_EXPORT_TO_DOWNLOADS = "exportToDownloads"
        const val KEY_SCHEDULED = "scheduled"
        
        /**
         * Trigger a one-time manual export to app cache.
         */
        fun enqueue(context: Context) {
            enqueue(context, exportToDownloads = false)
        }
        
        /**
         * Trigger a one-time manual export.
         * 
         * @param exportToDownloads If true, exports to Downloads folder; otherwise to app cache
         */
        fun enqueue(context: Context, exportToDownloads: Boolean) {
            val inputData = workDataOf(
                KEY_EXPORT_TO_DOWNLOADS to exportToDownloads,
                KEY_SCHEDULED to false
            )
            
            val request = OneTimeWorkRequestBuilder<DataExportWorker>()
                .setInputData(inputData)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("data_export")
                .build()
                
            WorkManager.getInstance(context).enqueueUniqueWork(
                MANUAL_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
        
        /**
         * Schedule weekly automatic backups.
         * Runs every 7 days when device is charging and on WiFi.
         * Exports to Downloads for user accessibility.
         */
        fun scheduleWeekly(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
                .build()
            
            val inputData = workDataOf(
                KEY_EXPORT_TO_DOWNLOADS to true, // Save to Downloads for accessibility
                KEY_SCHEDULED to true
            )
            
            val request = PeriodicWorkRequestBuilder<DataExportWorker>(
                7, TimeUnit.DAYS,
                1, TimeUnit.DAYS // Flex interval - can run within 1 day of target
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.MINUTES)
                .addTag("scheduled_backup")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SCHEDULED_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
            
            Timber.d("DataExportWorker: Weekly backup scheduled")
        }
        
        /**
         * Cancel scheduled weekly backups.
         */
        fun cancelScheduled(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(SCHEDULED_WORK_NAME)
            Timber.d("DataExportWorker: Weekly backup cancelled")
        }
    }
}
