package com.rio.rostry.domain.social.usecase

import com.rio.rostry.core.model.Message
import com.rio.rostry.core.model.Result

/**
 * Use case for sending a message.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines message sending use case interface.
 */
interface SendMessageUseCase {
    /**
     * Send a message in a thread.
     * @param threadId The thread ID
     * @param content The message content
     * @param attachments List of attachment URLs
     * @return Result containing the sent message or error
     */
    suspend operator fun invoke(
        threadId: String,
        content: String,
        attachments: List<String> = emptyList()
    ): Result<Message>
}
