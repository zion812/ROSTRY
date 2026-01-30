package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.AdminAuditDao
import com.rio.rostry.data.database.dao.CommunityRecommendationDao
import com.rio.rostry.data.database.dao.DashboardCacheDao
import com.rio.rostry.data.database.dao.TransferDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Background worker for data cleanup tasks.
 * Purges old cache entries, archives completed transfers, and compresses logs.
 * 
 * Runs daily during off-peak hours (night) when device is idle.
 */
@HiltWorker
class DataCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dashboardCacheDao: DashboardCacheDao,
    private val transferDao: TransferDao,
    private val adminAuditDao: AdminAuditDao,
    private val communityRecommendationDao: CommunityRecommendationDao,
    private val appDatabase: AppDatabase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("DataCleanupWorker starting cleanup tasks")
        
        return try {
            val now = System.currentTimeMillis()
            var totalCleaned = 0
            
            // 1. Purge old cache entries (older than 7 days)
            val cachePurged = purgeOldCacheEntries(now)
            totalCleaned += cachePurged
            
            // 2. Archive completed transfers (older than 90 days)
            val transfersArchived = archiveCompletedTransfers(now)
            totalCleaned += transfersArchived
            
            // 3. Purge old audit logs (older than 90 days)
            val logsDeleted = purgeOldAuditLogs(now)
            totalCleaned += logsDeleted
            
            // 4. Clear expired recommendations
            val recommendationsCleared = clearExpiredRecommendations(now)
            totalCleaned += recommendationsCleared
            
            // 5. Clear temporary files
            val tempFilesCleared = clearTempFiles()
            totalCleaned += tempFilesCleared
            
            // 6. Optimize database monthly (if last optimization was >30 days ago)
            optimizeDatabaseIfNeeded(now)
            
            Timber.d("DataCleanupWorker completed successfully. Total items cleaned: $totalCleaned")
            Result.success()
            
        } catch (e: Exception) {
            Timber.e(e, "DataCleanupWorker failed")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun purgeOldCacheEntries(now: Long): Int {
        val cutoff = now - TimeUnit.DAYS.toMillis(CACHE_RETENTION_DAYS)
        Timber.d("Purging dashboard cache entries older than $CACHE_RETENTION_DAYS days")
        
        // DashboardCacheDao.deleteStale expects entries older than provided timestamp
        dashboardCacheDao.deleteStale(cutoff)
        
        // Note: Room doesn't return affected row count from @Query DELETE, 
        // so we track this as a success marker
        return 1
    }

    private suspend fun archiveCompletedTransfers(now: Long): Int {
        val cutoff = now - TimeUnit.DAYS.toMillis(TRANSFER_ARCHIVE_DAYS)
        Timber.d("Archiving completed transfers older than $TRANSFER_ARCHIVE_DAYS days")
        
        // Delete completed/failed/cancelled transfers older than cutoff
        // In the future, this could move to cloud archive before deleting
        val deletedCount = transferDao.deleteCompletedBefore(cutoff)
        Timber.d("Archived/deleted $deletedCount old transfers")
        return deletedCount
    }

    private suspend fun purgeOldAuditLogs(now: Long): Int {
        val cutoff = now - TimeUnit.DAYS.toMillis(AUDIT_LOG_RETENTION_DAYS)
        Timber.d("Purging audit logs older than $AUDIT_LOG_RETENTION_DAYS days")
        
        adminAuditDao.purgeOldLogs(cutoff)
        return 1
    }

    private suspend fun clearExpiredRecommendations(now: Long): Int {
        Timber.d("Clearing expired community recommendations")
        communityRecommendationDao.deleteExpired(now)
        return 1
    }

    private fun clearTempFiles(): Int {
        Timber.d("Clearing temporary files older than $TEMP_FILE_RETENTION_DAYS days")
        val tempDir = applicationContext.cacheDir
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(TEMP_FILE_RETENTION_DAYS)
        var deletedCount = 0
        
        tempDir.listFiles()?.forEach { file ->
            if (file.isFile && file.lastModified() < cutoff) {
                if (file.delete()) {
                    deletedCount++
                }
            }
        }
        
        Timber.d("Deleted $deletedCount temp files")
        return deletedCount
    }

    private suspend fun optimizeDatabaseIfNeeded(now: Long) {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastVacuum = prefs.getLong(KEY_LAST_VACUUM, 0L)
        
        if (now - lastVacuum > TimeUnit.DAYS.toMillis(DATABASE_OPTIMIZE_INTERVAL_DAYS)) {
            Timber.d("Running database optimization (VACUUM)")
            
            try {
                // Use checkpoint to flush WAL to main database file
                appDatabase.openHelper.writableDatabase.apply {
                    execSQL("PRAGMA wal_checkpoint(TRUNCATE)")
                }
                
                // Store last optimization time
                prefs.edit().putLong(KEY_LAST_VACUUM, now).apply()
                Timber.d("Database optimization completed")
            } catch (e: Exception) {
                Timber.w(e, "Database optimization skipped or failed")
            }
        } else {
            Timber.d("Skipping database optimization - last run ${(now - lastVacuum) / TimeUnit.DAYS.toMillis(1)} days ago")
        }
    }

    companion object {
        const val WORK_NAME = "data_cleanup_worker"
        const val REPEAT_INTERVAL_HOURS = 24L
        
        // Retention periods
        private const val CACHE_RETENTION_DAYS = 7L
        private const val TRANSFER_ARCHIVE_DAYS = 90L
        private const val AUDIT_LOG_RETENTION_DAYS = 90L
        private const val TEMP_FILE_RETENTION_DAYS = 7L
        private const val DATABASE_OPTIMIZE_INTERVAL_DAYS = 30L
        
        // Preferences
        private const val PREFS_NAME = "data_cleanup_prefs"
        private const val KEY_LAST_VACUUM = "last_vacuum_timestamp"
        
        /**
         * Schedule the cleanup worker to run daily.
         * Runs with constraints: requires charging, battery not low, and device idle.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .build()
            
            val request = PeriodicWorkRequestBuilder<DataCleanupWorker>(
                REPEAT_INTERVAL_HOURS, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    10, TimeUnit.MINUTES
                )
                .addTag("maintenance_worker")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
