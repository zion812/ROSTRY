package com.rio.rostry.data.social.repository

import com.rio.rostry.core.model.ChatMessage
import com.rio.rostry.data.database.dao.ChatMessageDao
import com.rio.rostry.data.database.entity.ChatMessageEntity
import com.rio.rostry.data.social.mapper.toChatMessage
import com.rio.rostry.data.social.mapper.toEntity
import com.rio.rostry.domain.social.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ChatRepository for managing chat messages.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Social Domain repository migration
 */
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val dao: ChatMessageDao
) : ChatRepository {

    override fun conversation(userA: String, userB: String): Flow<List<ChatMessage>> {
        return dao.conversation(userA, userB).map { entities ->
            entities.map { it.toChatMessage() }
        }
    }

    override suspend fun sendMessage(message: ChatMessage) {
        val now = System.currentTimeMillis()
        val entity = message.toEntity().copy(
            updatedAt = now,
            lastModifiedAt = now,
            dirty = true
        )
        dao.upsert(entity)
    }

    override suspend fun softDelete(messageId: String) {
        val now = System.currentTimeMillis()
        dao.upsert(
            ChatMessageEntity(
                messageId = messageId,
                senderId = "",
                receiverId = "",
                body = "",
                isDeleted = true,
                deletedAt = now,
                updatedAt = now,
                lastModifiedAt = now,
                dirty = true
            )
        )
    }
}
