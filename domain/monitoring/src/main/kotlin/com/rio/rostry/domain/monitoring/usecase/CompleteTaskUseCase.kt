package com.rio.rostry.domain.monitoring.usecase

import com.rio.rostry.core.model.Result

/**
 * Use case for completing farm management tasks.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface CompleteTaskUseCase {
    /**
     * Mark a task as complete.
     * @param taskId The ID of the task to complete
     * @return Result indicating success or error
     */
    suspend operator fun invoke(taskId: String): Result<Unit>
}
