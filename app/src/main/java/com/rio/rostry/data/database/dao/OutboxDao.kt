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

    @Query("SELECT * FROM outbox WHERE status = 'PENDING' ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPending(limit: Int): List<OutboxEntity>

    @Query("UPDATE outbox SET status = :status, lastAttemptAt = :timestamp WHERE outboxId = :id")
    suspend fun updateStatus(id: String, status: String, timestamp: Long)

    @Query("UPDATE outbox SET retryCount = retryCount + 1, lastAttemptAt = :timestamp WHERE outboxId = :id")
    suspend fun incrementRetry(id: String, timestamp: Long)

    @Query("DELETE FROM outbox WHERE status = 'COMPLETED' AND createdAt < :threshold")
    suspend fun purgeCompleted(threshold: Long)

    @Query("SELECT * FROM outbox WHERE userId = :userId AND status != 'COMPLETED'")
    fun observePendingByUser(userId: String): Flow<List<OutboxEntity>>
}
