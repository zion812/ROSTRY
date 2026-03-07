package com.rio.rostry.domain.social.repository

import com.rio.rostry.core.model.Message
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for messaging operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface MessagingRepository {
    /**
     * Get messages for a thread.
     * @param threadId The thread ID
     * @return Flow of messages
     */
    fun getMessages(threadId: String): Flow<List<Message>>

    /**
     * Send a message.
     * @param message The message to send
     * @return Result containing the sent message or error
     */
    suspend fun sendMessage(message: Message): Result<Message>

    /**
     * Mark message as read.
     * @param messageId The message ID
     * @return Result indicating success or error
     */
    suspend fun markAsRead(messageId: String): Result<Unit>

    /**
     * Get unread message count.
     * @param userId The user ID
     * @return Result containing the unread count or error
     */
    suspend fun getUnreadCount(userId: String): Result<Int>

    /**
     * Get all threads for a user.
     * @param userId The user ID
     * @return Flow of thread IDs
     */
    fun getThreads(userId: String): Flow<List<String>>
}
