package com.rio.rostry.domain.repository

import com.rio.rostry.data.database.entity.TaskRecurrenceEntity

interface TaskSchedulingRepository {
    suspend fun createRecurrence(recurrence: TaskRecurrenceEntity)
    suspend fun getRecurrenceForTask(taskId: String): TaskRecurrenceEntity?
    suspend fun getDueRecurrences(currentTime: Long): List<TaskRecurrenceEntity>
}
