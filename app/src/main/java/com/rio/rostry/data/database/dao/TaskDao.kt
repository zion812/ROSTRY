package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= :now) AND dueAt <= :now ORDER BY CASE priority WHEN 'URGENT' THEN 3 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 1 ELSE 0 END DESC, dueAt ASC")
    fun observeDueForFarmer(farmerId: String, now: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND completedAt IS NULL AND dueAt < :now ORDER BY dueAt ASC")
    fun observeOverdueForFarmer(farmerId: String, now: Long): Flow<List<TaskEntity>>

    // Due window for today: between now and end-of-day
    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= :now) AND dueAt BETWEEN :now AND :endOfDay ORDER BY dueAt ASC")
    fun observeDueWindowForFarmer(farmerId: String, now: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= :now) ORDER BY dueAt ASC LIMIT 1")
    fun observeNextPendingTask(farmerId: String, now: Long): Flow<TaskEntity?>

    @Query("SELECT COUNT(*) FROM tasks WHERE farmerId = :farmerId AND completedAt IS NULL AND dueAt < :now")
    fun observeOverdueCountForFarmer(farmerId: String, now: Long): Flow<Int>

    @Query("UPDATE tasks SET completedAt = :completedAt, completedBy = :completedBy, updatedAt = :updatedAt, dirty = 1 WHERE taskId = :taskId")
    suspend fun markComplete(taskId: String, completedAt: Long, completedBy: String, updatedAt: Long)

    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND completedAt IS NOT NULL ORDER BY completedAt DESC LIMIT :limit")
    fun observeRecentCompletedForFarmer(farmerId: String, limit: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun observeByFarmer(farmerId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dirty = 1")
    suspend fun getDirty(): List<TaskEntity>

    @Query("UPDATE tasks SET dirty = 0, syncedAt = :syncedAt WHERE taskId IN (:taskIds)")
    suspend fun clearDirty(taskIds: List<String>, syncedAt: Long)

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun delete(taskId: String)

    // One-shot due query for a given type (used by workers)
    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND taskType = :taskType AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= :now) AND dueAt <= :now ORDER BY dueAt ASC")
    suspend fun getDueByType(farmerId: String, taskType: String, now: Long): List<TaskEntity>

    // Batch-based helpers for incubation tasks
    @Query("SELECT * FROM tasks WHERE batchId = :batchId ORDER BY dueAt ASC")
    fun observeByBatchId(batchId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND batchId = :batchId AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= :now) AND dueAt <= :now ORDER BY dueAt ASC")
    suspend fun getDueByBatch(farmerId: String, batchId: String, now: Long): List<TaskEntity>

    // Update metadata JSON
    @Query("UPDATE tasks SET metadata = :metadata, updatedAt = :updatedAt, dirty = 1 WHERE taskId = :taskId")
    suspend fun updateMetadata(taskId: String, metadata: String?, updatedAt: Long)

    // Pending (not completed) tasks by type/product for dedupe/idempotency checks
    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND productId = :productId AND taskType = :taskType AND completedAt IS NULL")
    suspend fun findPendingByTypeProduct(farmerId: String, productId: String, taskType: String): List<TaskEntity>

    // Pending tasks by type only (no product, e.g., feed logging, health checks)
    @Query("SELECT * FROM tasks WHERE farmerId = :farmerId AND taskType = :taskType AND completedAt IS NULL")
    suspend fun findPendingByType(farmerId: String, taskType: String): List<TaskEntity>

    // Update dueAt for an existing task
    @Query("UPDATE tasks SET dueAt = :dueAt, updatedAt = :updatedAt, dirty = 1 WHERE taskId = :taskId")
    suspend fun updateDueAt(taskId: String, dueAt: Long, updatedAt: Long)

    // Snooze until timestamp
    @Query("UPDATE tasks SET snoozeUntil = :snoozeUntil, updatedAt = :updatedAt, dirty = 1 WHERE taskId = :taskId")
    suspend fun updateSnoozeUntil(taskId: String, snoozeUntil: Long?, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM tasks WHERE farmerId = :farmerId AND completedAt BETWEEN :start AND :end")
    fun observeCompletedCountForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<Int>

    // Cursor-based incremental sync
    @Query("SELECT * FROM tasks WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int): List<TaskEntity>
}
