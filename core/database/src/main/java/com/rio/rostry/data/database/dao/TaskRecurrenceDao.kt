package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.TaskRecurrenceEntity

@Dao
interface TaskRecurrenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurrence: TaskRecurrenceEntity)

    @Query("SELECT * FROM task_recurrences WHERE taskId = :taskId")
    suspend fun getRecurrenceForTask(taskId: String): TaskRecurrenceEntity?

    @Query("SELECT * FROM task_recurrences WHERE isActive = 1 AND nextDue <= :currentTime")
    suspend fun getDueRecurrences(currentTime: Long): List<TaskRecurrenceEntity>
}
