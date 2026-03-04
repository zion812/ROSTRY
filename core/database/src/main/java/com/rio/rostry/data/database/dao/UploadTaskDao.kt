package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.UploadTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UploadTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: UploadTaskEntity)

    @Update
    suspend fun update(task: UploadTaskEntity)

    @Query("SELECT * FROM upload_tasks WHERE status IN ('QUEUED','UPLOADING') ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPending(limit: Int = 50): List<UploadTaskEntity>

    @Query("SELECT * FROM upload_tasks WHERE contextJson LIKE '%' || :transferId || '%' ORDER BY createdAt ASC")
    fun observeByContext(transferId: String): Flow<List<UploadTaskEntity>>

    @Query("UPDATE upload_tasks SET progress = :progress, status = 'UPLOADING', updatedAt = :now WHERE taskId = :taskId")
    suspend fun updateProgress(taskId: String, progress: Int, now: Long)

    @Query("UPDATE upload_tasks SET status = 'SUCCESS', progress = 100, contextJson = :contextJson, updatedAt = :now, error = NULL WHERE taskId = :taskId")
    suspend fun markSuccess(taskId: String, contextJson: String?, now: Long)

    @Query("UPDATE upload_tasks SET status = 'FAILED', updatedAt = :now, error = :error, retries = retries + 1 WHERE taskId = :taskId")
    suspend fun markFailed(taskId: String, error: String?, now: Long)

    @Query("UPDATE upload_tasks SET retries = retries + 1, updatedAt = :timestamp WHERE taskId = :taskId")
    suspend fun incrementRetries(taskId: String, timestamp: Long)

    @Query("SELECT * FROM upload_tasks WHERE remotePath = :remotePath LIMIT 1")
    suspend fun getByRemotePath(remotePath: String): UploadTaskEntity?

    @Query("DELETE FROM upload_tasks WHERE status IN ('SUCCESS','FAILED') AND updatedAt < :cutoffTime")
    suspend fun deleteOldCompleted(cutoffTime: Long)

    @Query("SELECT * FROM upload_tasks WHERE status = 'FAILED' AND retries < 3 ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getFailedTasks(limit: Int): List<UploadTaskEntity>

    @Query("SELECT * FROM upload_tasks WHERE remotePath = :remotePath LIMIT 1")
    fun observeByRemotePath(remotePath: String): Flow<UploadTaskEntity?>

    @Query("SELECT * FROM upload_tasks WHERE remotePath LIKE '%/' || :userId || '/%' AND status = 'SUCCESS'")
    suspend fun getSuccessfulByUser(userId: String): List<UploadTaskEntity>

    @Query("SELECT * FROM upload_tasks WHERE remotePath LIKE '%/' || :userId || '/%' AND status IN ('QUEUED','UPLOADING')")
    suspend fun getIncompleteByUser(userId: String): List<UploadTaskEntity>

    @Query("SELECT COUNT(*) FROM upload_tasks WHERE status IN ('QUEUED','UPLOADING')")
    fun observePendingCount(): Flow<Int>
}
