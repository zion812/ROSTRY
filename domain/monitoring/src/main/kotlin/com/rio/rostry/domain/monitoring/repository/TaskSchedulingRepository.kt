package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.TaskRecurrence
import com.rio.rostry.core.common.Result

/**
 * Repository contract for task scheduling and recurrence management.
 * 
 * Handles recurring tasks, scheduled operations, and automated task generation
 * for farm management activities.
 */
interface TaskSchedulingRepository {
    
    /**
     * Create a new task recurrence schedule.
     */
    suspend fun createRecurrence(recurrence: TaskRecurrence): Result<Unit>
    
    /**
     * Get the recurrence schedule for a specific task.
     */
    suspend fun getRecurrenceForTask(taskId: String): Result<TaskRecurrence?>
    
    /**
     * Get all recurrences that are due at the current time.
     */
    suspend fun getDueRecurrences(currentTime: Long): Result<List<TaskRecurrence>>
}
