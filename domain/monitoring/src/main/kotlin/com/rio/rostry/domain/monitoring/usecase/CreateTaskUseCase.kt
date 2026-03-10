package com.rio.rostry.domain.monitoring.usecase

import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.monitoring.model.Task
import com.rio.rostry.domain.monitoring.model.TaskType
import com.rio.rostry.domain.monitoring.model.TaskPriority
import java.time.Instant

/**
 * Use case for creating farm management tasks.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface CreateTaskUseCase {
    /**
     * Create a new farm management task.
     * @param request The task creation request
     * @return Result containing the created task or error
     */
    suspend operator fun invoke(request: CreateTaskRequest): Result<Task>
}

/**
 * Request for creating a task.
 */
data class CreateTaskRequest(
    val farmerId: String,
    val title: String,
    val description: String? = null,
    val taskType: TaskType,
    val priority: TaskPriority,
    val dueDate: Instant? = null,
    val assignedTo: String? = null,
    val relatedAssetId: String? = null
)
