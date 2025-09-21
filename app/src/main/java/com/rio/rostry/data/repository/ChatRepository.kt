package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ChatMessageDao
import com.rio.rostry.data.database.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface ChatRepository {
    fun conversation(userA: String, userB: String): Flow<List<ChatMessageEntity>>
    suspend fun sendMessage(msg: ChatMessageEntity)
    suspend fun softDelete(messageId: String)
}

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val dao: ChatMessageDao
) : ChatRepository {
    override fun conversation(userA: String, userB: String): Flow<List<ChatMessageEntity>> = dao.conversation(userA, userB)

    override suspend fun sendMessage(msg: ChatMessageEntity) {
        val now = System.currentTimeMillis()
        dao.upsert(msg.copy(updatedAt = now, lastModifiedAt = now, dirty = true))
    }

    override suspend fun softDelete(messageId: String) {
        // Basic soft delete by upserting a tombstone record
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
