package com.rio.rostry.domain.social.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

// Messaging repository interface
interface LegacyMessagingRepository {

    suspend fun sendDirectMessage(threadId: String, fromUserId: String, toUserId: String, text: String)
    fun streamThread(threadId: String): Flow<List<MessageDTO>>
    suspend fun sendGroupMessage(groupId: String, fromUserId: String, text: String)
    fun streamGroup(groupId: String): Flow<List<MessageDTO>>
    suspend fun sendDirectFile(threadId: String, fromUserId: String, toUserId: String, fileUri: Uri, fileName: String)
    suspend fun sendGroupFile(groupId: String, fromUserId: String, fileUri: Uri, fileName: String)
    suspend fun markThreadSeen(threadId: String, userId: String)
    fun streamUserThreads(userId: String): Flow<List<String>>
    fun streamUnreadCount(userId: String): Flow<Int>
    
    suspend fun sendOffer(threadId: String, fromUserId: String, toUserId: String, price: Double, quantity: Double, unit: String)
    
    // Context-aware messaging enhancements
    suspend fun createThreadWithContext(fromUserId: String, toUserId: String, context: ThreadContext): String
    suspend fun updateThreadMetadata(threadId: String, title: String?, lastMessageAt: Long)
    fun streamThreadMetadata(threadId: String): Flow<ThreadMetadata?>
    fun streamUserThreadsWithMetadata(userId: String): Flow<List<ThreadWithMetadata>>
    
    data class MessageDTO(
        val messageId: String, 
        val fromUserId: String, 
        val toUserId: String?, 
        val text: String, 
        val timestamp: Long,
        val type: String = "TEXT",
        val metadata: String? = null
    )
    data class ThreadContext(val type: String, val relatedEntityId: String?, val topic: String?)
    data class ThreadMetadata(val threadId: String, val title: String?, val context: ThreadContext?, val participantIds: List<String>, val lastMessageAt: Long)
    data class ThreadWithMetadata(val threadId: String, val metadata: ThreadMetadata?, val lastMessage: MessageDTO?, val unreadCount: Int)
}
