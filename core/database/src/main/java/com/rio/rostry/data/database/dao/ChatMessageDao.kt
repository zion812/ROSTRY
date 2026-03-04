package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rio.rostry.data.database.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Upsert
    suspend fun upsertAll(items: List<ChatMessageEntity>)

    @Upsert
    suspend fun upsert(item: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE ((senderId = :userA AND receiverId = :userB) OR (senderId = :userB AND receiverId = :userA)) AND isDeleted = 0 ORDER BY sentAt ASC")
    fun conversation(userA: String, userB: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int = 500): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE messageId = :messageId LIMIT 1")
    suspend fun getById(messageId: String): ChatMessageEntity?

    @Query("DELETE FROM chat_messages WHERE isDeleted = 1")
    suspend fun purgeDeleted()

    @Query("SELECT * FROM chat_messages WHERE dirty = 1 ORDER BY sentAt ASC LIMIT :limit")
    suspend fun getDirty(limit: Int = 500): List<ChatMessageEntity>

    @Query("UPDATE chat_messages SET dirty = 0, syncedAt = :syncedAt WHERE messageId IN (:messageIds)")
    suspend fun clearDirty(messageIds: List<String>, syncedAt: Long)

    @Query("SELECT COUNT(*) FROM chat_messages WHERE (senderId = :userId OR receiverId = :userId) AND dirty = 1")
    fun observePendingCount(userId: String): Flow<Int>
}
