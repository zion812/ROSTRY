package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rio.rostry.data.service.BackupService
import com.rio.rostry.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Worker for automatic periodic backups.
 * Runs weekly when the device is charging and idle.
 */
@HiltWorker
class AutoBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupService: BackupService
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        const val WORK_NAME = "auto_backup_worker"
        private const val MAX_BACKUPS_TO_KEEP = 5
        
        /**
         * Schedule periodic auto-backup (weekly).
         */
        fun schedulePeriodicBackup(context: Context, enabled: Boolean) {
            val workManager = WorkManager.getInstance(context)
            
            if (!enabled) {
                workManager.cancelUniqueWork(WORK_NAME)
                return
            }
            
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(true)
                .setRequiresDeviceIdle(true)
                .build()
            
            val request = PeriodicWorkRequestBuilder<AutoBackupWorker>(
                7, TimeUnit.DAYS // Weekly backup
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
        
        /**
         * Run backup immediately (one-time).
         */
        fun runBackupNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<AutoBackupWorker>()
                .build()
            
            WorkManager.getInstance(context).enqueue(request)
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            // Create backup
            when (val result = backupService.exportUserData()) {
                is Resource.Success -> {
                    // Clean up old backups, keep only recent ones
                    cleanupOldBackups()
                    Result.success()
                }
                is Resource.Error -> {
                    Result.retry()
                }
                is Resource.Loading -> {
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    /**
     * Clean up old backups, keeping only the most recent ones.
     */
    private fun cleanupOldBackups() {
        try {
            val exportDir = File(applicationContext.cacheDir, "exports")
            if (!exportDir.exists()) return
            
            val backupFiles = exportDir.listFiles()
                ?.filter { it.name.endsWith(".zip") }
                ?.sortedByDescending { it.lastModified() }
                ?: return
            
            // Delete old backups beyond the limit
            if (backupFiles.size > MAX_BACKUPS_TO_KEEP) {
                backupFiles.drop(MAX_BACKUPS_TO_KEEP).forEach { file ->
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // Silent cleanup failure - not critical
        }
    }
}
