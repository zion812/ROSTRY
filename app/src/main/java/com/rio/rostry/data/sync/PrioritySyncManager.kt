package com.rio.rostry.data.sync

import android.content.Context
import androidx.work.*
import com.rio.rostry.data.database.dao.*
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Priority levels for sync operations
 */
enum class SyncPriority(val value: Int) {
    CRITICAL(0),   // User-facing data: orders, transfers
    HIGH(1),       // Core operations: products, vaccinations
    NORMAL(2),     // Regular data: activity logs, growth
    LOW(3),        // Background data: analytics, metadata
    BACKGROUND(4)  // Optional/deferrable: media, large files
}

/**
 * Sync task with priority and metadata
 */
data class SyncTask(
    val entityType: String,
    val entityId: String,
    val priority: SyncPriority,
    val retryCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val failedAt: Long? = null,
    val errorMessage: String? = null
)

/**
 * Priority-based sync manager that ensures critical data syncs first.
 * Handles offline queue, retry logic, and conflict resolution.
 */
@Singleton
class PrioritySyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vaccinationDao: VaccinationRecordDao,
    private val growthDao: GrowthRecordDao,
    private val mortalityDao: MortalityRecordDao,
    private val activityLogDao: FarmActivityLogDao
) {
    private val workManager = WorkManager.getInstance(context)
    
    companion object {
        private const val TAG = "PrioritySyncManager"
        private const val SYNC_WORK_NAME = "priority_sync_work"
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_BACKOFF_MINUTES = 5L
    }

    /**
     * Schedule sync based on priority
     */
    fun scheduleSyncForEntity(entityType: String, entityId: String, priority: SyncPriority) {
        Timber.d("Scheduling sync for $entityType:$entityId with priority $priority")
        
        val constraints = when (priority) {
            SyncPriority.CRITICAL, SyncPriority.HIGH -> {
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            }
            SyncPriority.NORMAL -> {
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            }
            SyncPriority.LOW, SyncPriority.BACKGROUND -> {
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresCharging(false)
                    .build()
            }
        }

        val initialDelay = when (priority) {
            SyncPriority.CRITICAL -> 0L
            SyncPriority.HIGH -> 1L
            SyncPriority.NORMAL -> 5L
            SyncPriority.LOW -> 15L
            SyncPriority.BACKGROUND -> 30L
        }

        val workRequest = OneTimeWorkRequestBuilder<PrioritySyncWorker>()
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(
                    "entity_type" to entityType,
                    "entity_id" to entityId,
                    "priority" to priority.value
                )
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                RETRY_BACKOFF_MINUTES,
                TimeUnit.MINUTES
            )
            .addTag("sync_$entityType")
            .addTag("priority_${priority.name}")
            .build()

        workManager.enqueue(workRequest)
    }

    /**
     * Get sync priority for entity type
     */
    fun getPriorityForEntityType(entityType: String): SyncPriority = when (entityType) {
        "order", "transfer" -> SyncPriority.CRITICAL
        "product", "vaccination", "farm_asset" -> SyncPriority.HIGH
        "growth_record", "mortality_record", "activity_log" -> SyncPriority.NORMAL
        "daily_log", "hatching_batch" -> SyncPriority.LOW
        "media", "analytics" -> SyncPriority.BACKGROUND
        else -> SyncPriority.NORMAL
    }

    /**
     * Force immediate sync of all critical entities
     */
    fun forceCriticalSync() {
        Timber.i("Forcing critical sync")
        val request = OneTimeWorkRequestBuilder<PrioritySyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(workDataOf("force_critical" to true))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        
        workManager.enqueueUniqueWork(
            "force_critical_sync",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    /**
     * Cancel all pending syncs for an entity
     */
    fun cancelSyncForEntity(entityType: String, entityId: String) {
        workManager.cancelAllWorkByTag("sync_${entityType}_$entityId")
    }

    /**
     * Get pending sync count by priority
     */
    suspend fun getPendingSyncStats(): Map<SyncPriority, Int> {
        val stats = mutableMapOf<SyncPriority, Int>()
        SyncPriority.entries.forEach { priority ->
            val info = workManager.getWorkInfosByTag("priority_${priority.name}").get()
            stats[priority] = info.count { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
        }
        return stats
    }
}

/**
 * Worker that performs priority-based sync operations
 */
class PrioritySyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val entityType = inputData.getString("entity_type")
        val entityId = inputData.getString("entity_id")
        val priority = inputData.getInt("priority", SyncPriority.NORMAL.value)
        val forceCritical = inputData.getBoolean("force_critical", false)

        Timber.d("PrioritySyncWorker: type=$entityType, id=$entityId, priority=$priority")

        return try {
            // TODO: Implement actual sync logic per entity type
            // This is a placeholder that should call appropriate repository sync methods
            
            if (forceCritical) {
                // Sync all critical entities
                Timber.i("Performing forced critical sync")
            } else if (entityType != null && entityId != null) {
                // Sync specific entity
                Timber.d("Syncing $entityType:$entityId")
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Sync failed for $entityType:$entityId")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure(workDataOf("error" to e.message))
            }
        }
    }
}
