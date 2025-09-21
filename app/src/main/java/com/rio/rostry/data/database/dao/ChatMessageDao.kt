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

    @Query("DELETE FROM chat_messages WHERE isDeleted = 1")
    suspend fun purgeDeleted()
}
