package com.rio.rostry.data.sync

import android.content.Context
import androidx.room.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * State of a queued operation
 */
enum class QueuedOperationState {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * Entity for storing offline operations in a queue
 */
@Entity(
    tableName = "offline_queue",
    indices = [
        Index(value = ["state"]),
        Index(value = ["priority"]),
        Index(value = ["entityType", "entityId"])
    ]
)
data class OfflineQueueEntity(
    @PrimaryKey val operationId: String,
    val entityType: String,
    val entityId: String,
    val operationType: String, // CREATE, UPDATE, DELETE
    val payload: String, // JSON serialized data
    val priority: Int = SyncPriority.NORMAL.value,
    val state: String = QueuedOperationState.PENDING.name,
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAttemptAt: Long? = null,
    val completedAt: Long? = null,
    val errorMessage: String? = null
)

/**
 * DAO for offline queue operations
 */
@Dao
interface OfflineQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(operation: OfflineQueueEntity)

    @Query("SELECT * FROM offline_queue WHERE state = 'PENDING' ORDER BY priority ASC, createdAt ASC")
    suspend fun getPendingOperations(): List<OfflineQueueEntity>

    @Query("SELECT * FROM offline_queue WHERE state = 'PENDING' ORDER BY priority ASC, createdAt ASC LIMIT :limit")
    suspend fun getPendingOperationsLimited(limit: Int): List<OfflineQueueEntity>

    @Query("SELECT * FROM offline_queue WHERE entityType = :entityType AND entityId = :entityId")
    suspend fun getOperationsForEntity(entityType: String, entityId: String): List<OfflineQueueEntity>

    @Query("UPDATE offline_queue SET state = :state, lastAttemptAt = :timestamp WHERE operationId = :operationId")
    suspend fun updateState(operationId: String, state: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE offline_queue SET state = 'COMPLETED', completedAt = :timestamp WHERE operationId = :operationId")
    suspend fun markCompleted(operationId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE offline_queue SET state = 'FAILED', retryCount = retryCount + 1, errorMessage = :error, lastAttemptAt = :timestamp WHERE operationId = :operationId")
    suspend fun markFailed(operationId: String, error: String?, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM offline_queue WHERE state = 'PENDING'")
    fun observePendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM offline_queue WHERE state = 'PENDING'")
    suspend fun getPendingCount(): Int

    @Query("SELECT COUNT(*) FROM offline_queue WHERE state = 'FAILED'")
    suspend fun getFailedCount(): Int

    @Query("DELETE FROM offline_queue WHERE state = 'COMPLETED' AND completedAt < :beforeTimestamp")
    suspend fun cleanupCompleted(beforeTimestamp: Long)

    @Query("DELETE FROM offline_queue WHERE operationId = :operationId")
    suspend fun delete(operationId: String)

    @Query("SELECT * FROM offline_queue WHERE state IN ('PENDING', 'FAILED') AND priority <= :maxPriority ORDER BY priority ASC, createdAt ASC")
    suspend fun getOperationsByPriority(maxPriority: Int): List<OfflineQueueEntity>
}

/**
 * Manager for offline operation queue.
 * Handles enqueueing, processing, and cleanup of offline operations.
 */
@Singleton
class OfflineQueueManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "OfflineQueueManager"
        private const val CLEANUP_THRESHOLD_DAYS = 7L
    }

    /**
     * Enqueue an operation for later sync
     */
    suspend fun enqueue(
        dao: OfflineQueueDao,
        entityType: String,
        entityId: String,
        operationType: String,
        payload: String,
        priority: SyncPriority = SyncPriority.NORMAL
    ): String {
        val operationId = "${entityType}_${entityId}_${System.currentTimeMillis()}"
        val operation = OfflineQueueEntity(
            operationId = operationId,
            entityType = entityType,
            entityId = entityId,
            operationType = operationType,
            payload = payload,
            priority = priority.value
        )
        dao.enqueue(operation)
        Timber.d("Enqueued operation $operationId with priority $priority")
        return operationId
    }

    /**
     * Process pending operations in priority order
     */
    suspend fun processPending(
        dao: OfflineQueueDao,
        batchSize: Int = 10,
        processor: suspend (OfflineQueueEntity) -> Boolean
    ): Int {
        val pending = dao.getPendingOperationsLimited(batchSize)
        var successCount = 0

        pending.forEach { operation ->
            dao.updateState(operation.operationId, QueuedOperationState.IN_PROGRESS.name)
            
            try {
                val success = processor(operation)
                if (success) {
                    dao.markCompleted(operation.operationId)
                    successCount++
                } else {
                    dao.markFailed(operation.operationId, "Processing returned false")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to process operation ${operation.operationId}")
                dao.markFailed(operation.operationId, e.message)
            }
        }

        Timber.d("Processed $successCount/${pending.size} operations")
        return successCount
    }

    /**
     * Cleanup old completed operations
     */
    suspend fun cleanup(dao: OfflineQueueDao) {
        val thresholdMs = CLEANUP_THRESHOLD_DAYS * 24 * 60 * 60 * 1000L
        val beforeTimestamp = System.currentTimeMillis() - thresholdMs
        dao.cleanupCompleted(beforeTimestamp)
        Timber.d("Cleaned up completed operations older than $CLEANUP_THRESHOLD_DAYS days")
    }

    /**
     * Get queue statistics
     */
    suspend fun getStats(dao: OfflineQueueDao): QueueStats {
        return QueueStats(
            pendingCount = dao.getPendingCount(),
            failedCount = dao.getFailedCount()
        )
    }
}

data class QueueStats(
    val pendingCount: Int,
    val failedCount: Int
)
