package com.rio.rostry.data.repository

import com.rio.rostry.data.local.ChatMessageDao
import com.rio.rostry.domain.model.ChatMessage as DomainChatMessage
import com.rio.rostry.data.model.ChatMessage as DataChatMessage
import com.rio.rostry.domain.repository.ChatMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ChatMessageRepositoryImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) : BaseRepository(), ChatMessageRepository {

    override fun getAllChatMessages(): Flow<List<DomainChatMessage>> {
        return chatMessageDao.getAllChatMessages().map { messages ->
            messages.map { it.toDomainModel() }
        }
    }

    override fun getChatMessagesByUserId(userId: String): Flow<List<DomainChatMessage>> {
        return chatMessageDao.getChatMessagesByUserId(userId).map { messages ->
            messages.map { it.toDomainModel() }
        }
    }

    override fun getChatMessagesByOrderId(orderId: String): Flow<List<DomainChatMessage>> {
        return chatMessageDao.getChatMessagesByOrderId(orderId).map { messages ->
            messages.map { it.toDomainModel() }
        }
    }

    override suspend fun getChatMessageById(id: String): DomainChatMessage? {
        return chatMessageDao.getChatMessageById(id)?.toDomainModel()
    }

    override suspend fun insertChatMessage(chatMessage: DomainChatMessage) {
        chatMessageDao.insertChatMessage(chatMessage.toDataModel())
    }

    override suspend fun updateChatMessage(chatMessage: DomainChatMessage) {
        val updatedMessage = chatMessage.copy(updatedAt = Date())
        chatMessageDao.updateChatMessage(updatedMessage.toDataModel())
    }

    override suspend fun markChatMessageAsRead(id: String) {
        chatMessageDao.markChatMessageAsRead(id)
    }

    override suspend fun deleteChatMessage(id: String) {
        // We need to first get the chat message by ID and then delete it
        val chatMessage = chatMessageDao.getChatMessageById(id)
        chatMessage?.let {
            chatMessageDao.deleteChatMessage(it)
        }
    }

    private fun DataChatMessage.toDomainModel(): DomainChatMessage {
        return DomainChatMessage(
            id = id,
            senderId = senderId,
            receiverId = receiverId,
            orderId = orderId,
            message = message,
            messageType = messageType,
            isRead = isRead,
            imageUrl = imageUrl,
            fileUrl = fileUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainChatMessage.toDataModel(): DataChatMessage {
        return DataChatMessage(
            id = id,
            senderId = senderId,
            receiverId = receiverId,
            orderId = orderId,
            message = message,
            messageType = messageType,
            isRead = isRead,
            imageUrl = imageUrl,
            fileUrl = fileUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}