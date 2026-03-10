package com.rio.rostry.domain.admin.usecase

import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving moderation queue.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface GetModerationQueueUseCase {
    /**
     * Observe pending moderation items.
     * @return Flow of items pending moderation
     */
    operator fun invoke(): Flow<List<String>>

    /**
     * Get the count of items in the moderation queue.
     * @return Result containing the queue count or error
     */
    suspend fun getQueueCount(): Result<Int>
}
