package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Background worker for data cleanup tasks.
 * Purges old cache entries, archives completed transfers, and compresses logs.
 */
@HiltWorker
class DataCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("DataCleanupWorker starting cleanup tasks")
        
        return try {
            // 1. Purge old cache entries (older than 7 days)
            purgeOldCacheEntries()
            
            // 2. Archive completed transfers (older than 30 days)
            archiveCompletedTransfers()
            
            // 3. Compress audit logs (older than 30 days)
            compressAuditLogs()
            
            // 4. Clear temporary files
            clearTempFiles()
            
            // 5. Optimize database if needed
            optimizeDatabase()
            
            Timber.d("DataCleanupWorker completed successfully")
            Result.success()
            
        } catch (e: Exception) {
            Timber.e(e, "DataCleanupWorker failed")
            Result.retry()
        }
    }

    private suspend fun purgeOldCacheEntries() {
        Timber.d("Purging cache entries older than 7 days")
        // TODO: Integrate with CacheManager to clear old entries
        // cacheManager.clearOlderThan(7, TimeUnit.DAYS)
    }

    private suspend fun archiveCompletedTransfers() {
        Timber.d("Archiving completed transfers older than 30 days")
        // TODO: Move completed transfers to archive table/collection
        // transferRepository.archiveCompletedOlderThan(30, TimeUnit.DAYS)
    }

    private suspend fun compressAuditLogs() {
        Timber.d("Compressing audit logs older than 30 days")
        // TODO: Compress or aggregate old audit logs
        // auditRepository.compressLogsOlderThan(30, TimeUnit.DAYS)
    }

    private suspend fun clearTempFiles() {
        Timber.d("Clearing temporary files")
        val tempDir = applicationContext.cacheDir
        tempDir.listFiles()?.forEach { file ->
            if (file.isFile && isOlderThan(file.lastModified(), 7, TimeUnit.DAYS)) {
                file.delete()
            }
        }
    }

    private suspend fun optimizeDatabase() {
        Timber.d("Optimizing database")
        // TODO: Run VACUUM or other optimization if needed
        // appDatabase.runInTransaction { appDatabase.query("VACUUM", emptyArray()) }
    }

    private fun isOlderThan(timestamp: Long, amount: Long, unit: TimeUnit): Boolean {
        val cutoff = System.currentTimeMillis() - unit.toMillis(amount)
        return timestamp < cutoff
    }

    companion object {
        const val WORK_NAME = "data_cleanup_worker"
        
        // Schedule cleanup to run once per day during off-peak hours
        const val REPEAT_INTERVAL_HOURS = 24L
    }
}
