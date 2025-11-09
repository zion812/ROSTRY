package com.rio.rostry.workers

import android.content.Context
import android.os.Build
import android.os.StatFs
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.monitoring.PerformanceMonitor
import com.rio.rostry.utils.performance.DatabaseOptimizer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class DatabaseMaintenanceWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDatabase: AppDatabase,
    private val databaseOptimizer: DatabaseOptimizer
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val startTime = System.currentTimeMillis()
        Timber.d("Starting database maintenance work")

        return try {
            val db = appDatabase.openHelper.writableDatabase

            fun queryInt(pragma: String): Int {
                var cursor: android.database.Cursor? = null
                return try {
                    cursor = db.query(pragma)
                    if (cursor.moveToFirst()) cursor.getInt(0) else 0
                } catch (_: Exception) { 0 } finally { cursor?.close() }
            }

            // Compute size via PRAGMAs
            val pageCount = queryInt("PRAGMA page_count;")
            val pageSize = queryInt("PRAGMA page_size;")
            val sizeMbBefore = (((pageCount.toLong() * pageSize.toLong())) / (1024 * 1024)).toInt()

            // Determine filesystem free space using DB parent directory
            val dbFile = applicationContext.getDatabasePath(com.rio.rostry.data.database.AppDatabase.DATABASE_NAME)
            val dbDirPath = dbFile.parentFile!!.absolutePath
            val statFs = StatFs(dbDirPath)
            val freeSpaceMb = ((statFs.availableBytes) / (1024 * 1024)).toInt()

            if (databaseOptimizer.shouldCompactDb(sizeMbBefore, freeSpaceMb)) {
                Timber.d("VACUUM needed, executing VACUUM and ANALYZE")
                db.execSQL("VACUUM")
                db.execSQL("ANALYZE")
                Timber.d("VACUUM and ANALYZE completed")
            } else {
                Timber.d("VACUUM not needed, skipping maintenance")
            }

            val pageCountAfter = queryInt("PRAGMA page_count;")
            val pageSizeAfter = queryInt("PRAGMA page_size;")
            val sizeMbAfter = (((pageCountAfter.toLong() * pageSizeAfter.toLong())) / (1024 * 1024)).toInt()
            val duration = System.currentTimeMillis() - startTime

            PerformanceMonitor.recordMetric("db_maintenance_duration_ms", duration)
            PerformanceMonitor.recordMetric("db_size_mb_before", sizeMbBefore.toLong())
            PerformanceMonitor.recordMetric("db_size_mb_after", sizeMbAfter.toLong())

            Timber.d("Database maintenance completed successfully in ${duration}ms. Size: ${sizeMbBefore}MB -> ${sizeMbAfter}MB")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Database maintenance failed")
            PerformanceMonitor.recordMetric("db_maintenance_failure", 1)
            Result.failure()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val constraintsBuilder = Constraints.Builder()
                .setRequiresCharging(true)
                .setRequiresBatteryNotLow(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                constraintsBuilder.setRequiresDeviceIdle(true)
            }
            val constraints = constraintsBuilder.build()

            val request = PeriodicWorkRequest.Builder(DatabaseMaintenanceWorker::class.java, 7, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "database_maintenance",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )

            Timber.d("Database maintenance work scheduled weekly with charging/idle constraints")
        }
    }
}