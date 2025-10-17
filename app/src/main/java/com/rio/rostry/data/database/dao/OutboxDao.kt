package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.OutboxEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: OutboxEntity)

    @Query(
        "SELECT * FROM outbox WHERE status = 'PENDING' AND retryCount < maxRetries " +
            "ORDER BY CASE priority " +
            "WHEN 'CRITICAL' THEN 4 " +
            "WHEN 'HIGH' THEN 3 " +
            "WHEN 'NORMAL' THEN 2 " +
            "WHEN 'LOW' THEN 1 " +
            "ELSE 0 END DESC, createdAt ASC LIMIT :limit"
    )
    suspend fun getPendingPrioritized(limit: Int): List<OutboxEntity>

    @Query("UPDATE outbox SET status = :status, lastAttemptAt = :timestamp WHERE outboxId = :id")
    suspend fun updateStatus(id: String, status: String, timestamp: Long)

    @Query("UPDATE outbox SET retryCount = retryCount + 1, lastAttemptAt = :timestamp WHERE outboxId = :id")
    suspend fun incrementRetry(id: String, timestamp: Long)

    @Query("DELETE FROM outbox WHERE status = 'COMPLETED' AND createdAt < :threshold")
    suspend fun purgeCompleted(threshold: Long)

    @Query("SELECT * FROM outbox WHERE userId = :userId AND status != 'COMPLETED'")
    fun observePendingByUser(userId: String): Flow<List<OutboxEntity>>

    @Query("SELECT * FROM outbox WHERE entityType = :type AND status = 'PENDING' ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPendingByType(type: String, limit: Int): List<OutboxEntity>

    @Query("SELECT COUNT(*) FROM outbox WHERE userId = :userId AND entityType = :type AND status = 'PENDING'")
    fun observePendingCountByType(userId: String, type: String): Flow<Int>

    @Query("UPDATE outbox SET status = :status, lastAttemptAt = :timestamp WHERE outboxId IN (:ids)")
    suspend fun updateStatusBatch(ids: List<String>, status: String, timestamp: Long)

    @Query("SELECT * FROM outbox WHERE status = 'FAILED' AND userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getFailedByUser(userId: String, limit: Int): List<OutboxEntity>

    @Query("SELECT * FROM outbox WHERE entityType = :type AND status = :status ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getByTypeAndStatus(type: String, status: String, limit: Int): List<OutboxEntity>

    @Query("UPDATE outbox SET retryCount = 0, status = :status, lastAttemptAt = :timestamp WHERE outboxId = :id")
    suspend fun resetRetryAndStatus(id: String, status: String, timestamp: Long)

    // Compatibility for tests: simple pending fetch ordered by createdAt
    @Query("SELECT * FROM outbox WHERE status = 'PENDING' ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPending(limit: Int): List<OutboxEntity>
}

