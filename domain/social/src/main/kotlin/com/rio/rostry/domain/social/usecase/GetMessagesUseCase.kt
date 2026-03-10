package com.rio.rostry.domain.social.usecase

import com.rio.rostry.core.model.Message
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing messages in a thread.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines message observation use case interface.
 */
interface GetMessagesUseCase {
    /**
     * Observe messages in a thread.
     * @param threadId The thread ID
     * @return Flow emitting list of messages
     */
    operator fun invoke(threadId: String): Flow<List<Message>>
}
