package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY createdAt DESC")
    fun getAllChatMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE (senderId = :userId OR receiverId = :userId) ORDER BY createdAt DESC")
    fun getChatMessagesByUserId(userId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE orderId = :orderId ORDER BY createdAt ASC")
    fun getChatMessagesByOrderId(orderId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getChatMessageById(id: String): ChatMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(chatMessage: ChatMessage)

    @Update
    suspend fun updateChatMessage(chatMessage: ChatMessage)

    @Query("UPDATE chat_messages SET isRead = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markChatMessageAsRead(id: String, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteChatMessage(chatMessage: ChatMessage)

    // Temporarily removing purge method
    // @Query("DELETE FROM chat_messages WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    // suspend fun purgeDeletedChatMessages(beforeTimestamp: Long)
}