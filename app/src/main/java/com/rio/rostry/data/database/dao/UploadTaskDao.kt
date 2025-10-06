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

    @Query("UPDATE upload_tasks SET status = 'SUCCESS', progress = 100, updatedAt = :now, error = NULL WHERE taskId = :taskId")
    suspend fun markSuccess(taskId: String, now: Long)

    @Query("UPDATE upload_tasks SET status = 'FAILED', updatedAt = :now, error = :error, retries = retries + 1 WHERE taskId = :taskId")
    suspend fun markFailed(taskId: String, error: String?, now: Long)
}
