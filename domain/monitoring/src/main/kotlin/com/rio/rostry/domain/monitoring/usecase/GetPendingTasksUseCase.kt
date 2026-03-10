package com.rio.rostry.domain.monitoring.usecase

import com.rio.rostry.domain.monitoring.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving pending tasks.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface GetPendingTasksUseCase {
    /**
     * Get all pending tasks for a farmer.
     * @param farmerId The farmer ID
     * @return Flow of pending tasks
     */
    operator fun invoke(farmerId: String): Flow<List<Task>>
}
