package com.rio.rostry.data.repository.social

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.database.*
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Social feed
interface SocialRepository {
    fun feed(pageSize: Int = 20): Flow<PagingData<PostEntity>>
    suspend fun createPost(authorId: String, type: String, text: String?, mediaUrl: String?, thumbnailUrl: String?, productId: String?): String
    fun streamComments(postId: String): Flow<List<CommentEntity>>
    suspend fun addComment(postId: String, authorId: String, text: String)
    fun countLikes(postId: String): Flow<Int>
    suspend fun like(postId: String, userId: String)
    suspend fun unlike(postId: String, userId: String)
}

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val postsDao: PostsDao,
    private val commentsDao: CommentsDao,
    private val likesDao: LikesDao,
) : SocialRepository {
    override fun feed(pageSize: Int): Flow<PagingData<PostEntity>> =
        Pager(PagingConfig(pageSize = pageSize)) { postsDao.paging() }.flow

    override suspend fun createPost(authorId: String, type: String, text: String?, mediaUrl: String?, thumbnailUrl: String?, productId: String?): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        postsDao.upsert(
            PostEntity(
                postId = id,
                authorId = authorId,
                type = type,
                text = text,
                mediaUrl = mediaUrl,
                thumbnailUrl = thumbnailUrl,
                productId = productId,
                createdAt = now,
                updatedAt = now
            )
        )
        return id
    }

    override fun streamComments(postId: String): Flow<List<CommentEntity>> = commentsDao.streamByPost(postId)

    override suspend fun addComment(postId: String, authorId: String, text: String) {
        commentsDao.upsert(
            CommentEntity(
                commentId = UUID.randomUUID().toString(),
                postId = postId,
                authorId = authorId,
                text = text,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override fun countLikes(postId: String): Flow<Int> = likesDao.count(postId)

    override suspend fun like(postId: String, userId: String) {
        likesDao.upsert(
            LikeEntity(
                likeId = UUID.randomUUID().toString(),
                postId = postId,
                userId = userId,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun unlike(postId: String, userId: String) {
        likesDao.unlike(postId, userId)
    }
}

// Messaging (Firebase Realtime Database)
interface MessagingRepository {
    suspend fun sendDirectMessage(threadId: String, fromUserId: String, toUserId: String, text: String)
    fun streamThread(threadId: String): Flow<List<MessageDTO>>
    data class MessageDTO(val messageId: String, val fromUserId: String, val toUserId: String?, val text: String, val timestamp: Long)
}

@Singleton
class MessagingRepositoryImpl @Inject constructor(
    private val firebaseDb: FirebaseDatabase,
) : MessagingRepository {
    override suspend fun sendDirectMessage(threadId: String, fromUserId: String, toUserId: String, text: String) {
        val msgId = UUID.randomUUID().toString()
        val data = mapOf(
            "messageId" to msgId,
            "fromUserId" to fromUserId,
            "toUserId" to toUserId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        firebaseDb.getReference("dm/$threadId/$msgId").setValue(data)
    }

    override fun streamThread(threadId: String): Flow<List<MessagingRepository.MessageDTO>> = callbackFlow {
        val ref = firebaseDb.getReference("dm/$threadId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { c ->
                    val id = c.child("messageId").getValue(String::class.java) ?: return@mapNotNull null
                    val from = c.child("fromUserId").getValue(String::class.java) ?: return@mapNotNull null
                    val to = c.child("toUserId").getValue(String::class.java)
                    val text = c.child("text").getValue(String::class.java) ?: return@mapNotNull null
                    val ts = c.child("timestamp").getValue(Long::class.java) ?: 0L
                    MessagingRepository.MessageDTO(id, from, to, text, ts)
                }.sortedBy { it.timestamp }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
