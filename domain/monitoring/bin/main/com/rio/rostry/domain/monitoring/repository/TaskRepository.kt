package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.Task
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for task operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface TaskRepository {
    /**
     * Get all tasks for a farmer.
     * @param farmerId The farmer ID
     * @return Flow of tasks
     */
    fun getTasksByFarmer(farmerId: String): Flow<List<Task>>

    /**
     * Get task by ID.
     * @param taskId The task ID
     * @return Result containing the task or error
     */
    suspend fun getTaskById(taskId: String): Result<Task>

    /**
     * Create a new task.
     * @param task The task to create
     * @return Result containing the created task or error
     */
    suspend fun createTask(task: Task): Result<Task>

    /**
     * Update a task.
     * @param task The updated task
     * @return Result indicating success or error
     */
    suspend fun updateTask(task: Task): Result<Unit>

    /**
     * Delete a task.
     * @param taskId The task ID to delete
     * @return Result indicating success or error
     */
    suspend fun deleteTask(taskId: String): Result<Unit>

    /**
     * Mark task as complete.
     * @param taskId The task ID
     * @return Result indicating success or error
     */
    suspend fun completeTask(taskId: String): Result<Unit>

    /**
     * Get pending tasks.
     * @param farmerId The farmer ID
     * @return Flow of pending tasks
     */
    fun getPendingTasks(farmerId: String): Flow<List<Task>>
}
