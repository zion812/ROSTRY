package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatMessageRepository {
    fun getAllChatMessages(): Flow<List<ChatMessage>>
    fun getChatMessagesByUserId(userId: String): Flow<List<ChatMessage>>
    fun getChatMessagesByOrderId(orderId: String): Flow<List<ChatMessage>>
    suspend fun getChatMessageById(id: String): ChatMessage?
    suspend fun insertChatMessage(chatMessage: ChatMessage)
    suspend fun updateChatMessage(chatMessage: ChatMessage)
    suspend fun markChatMessageAsRead(id: String)
    suspend fun deleteChatMessage(id: String)
}