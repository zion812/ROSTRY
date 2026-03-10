package com.rio.rostry.domain.social.repository

import com.rio.rostry.core.model.Message
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for messaging operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines messaging interfaces without implementation details.
 */
interface MessagingRepository {
    /**
     * Get messages in a thread.
     * @param threadId The thread ID
     * @return Flow emitting list of messages in the thread
     */
    fun getMessages(threadId: String): Flow<List<Message>>
    
    /**
     * Send a message.
     * @param message The message to send
     * @return Result containing the sent message or error
     */
    suspend fun sendMessage(message: Message): Result<Message>
    
    /**
     * Mark a message as read.
     * @param messageId The message ID to mark as read
     * @return Result indicating success or error
     */
    suspend fun markAsRead(messageId: String): Result<Unit>
    
    /**
     * Get unread message count for a user.
     * @param userId The user ID
     * @return Result containing the unread count or error
     */
    suspend fun getUnreadCount(userId: String): Result<Int>
    
    /**
     * Get message threads for a user.
     * @param userId The user ID
     * @return Flow emitting list of thread IDs
     */
    fun getThreads(userId: String): Flow<List<String>>
}
