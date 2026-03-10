package com.rio.rostry.domain.social.repository

import com.rio.rostry.core.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing chat messages.
 * 
 * Handles one-on-one chat conversations between users.
 */
interface ChatRepository {
    /**
     * Gets the conversation between two users.
     * 
     * @param userA First user ID
     * @param userB Second user ID
     * @return Flow emitting list of chat messages
     */
    fun conversation(userA: String, userB: String): Flow<List<ChatMessage>>
    
    /**
     * Sends a chat message.
     * 
     * @param message The message to send
     */
    suspend fun sendMessage(message: ChatMessage)
    
    /**
     * Soft deletes a message.
     * 
     * @param messageId The message ID
     */
    suspend fun softDelete(messageId: String)
}
