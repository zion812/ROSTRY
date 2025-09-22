package com.rio.rostry.data.repository.social

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.utils.upload.MediaUploader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.net.Uri
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Social feed
interface SocialRepository {
    fun feed(pageSize: Int = 20): Flow<PagingData<PostEntity>>
    fun feedRanked(pageSize: Int = 20): Flow<PagingData<PostEntity>>
    suspend fun createPost(authorId: String, type: String, text: String?, mediaUrl: String?, thumbnailUrl: String?, productId: String?): String
    suspend fun createPostWithMedia(authorId: String, type: String, text: String?, mediaUri: Uri, isVideo: Boolean): String
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
    private val reputationDao: ReputationDao,
    private val badgesDao: BadgesDao,
    private val storage: FirebaseStorage,
    private val rateLimitDao: RateLimitDao,
    @ApplicationContext private val appContext: Context,
) : SocialRepository {
    private suspend fun checkRateLimit(action: String, userId: String, windowMs: Long) {
        val now = System.currentTimeMillis()
        val existing = rateLimitDao.get(userId, action)
        if (existing != null && now - existing.lastAt < windowMs) {
            throw IllegalStateException("Rate limited: $action")
        }
        val record = RateLimitEntity(
            id = existing?.id ?: UUID.randomUUID().toString(),
            userId = userId,
            action = action,
            lastAt = now
        )
        rateLimitDao.upsert(record)
    }

    override fun feed(pageSize: Int): Flow<PagingData<PostEntity>> =
        Pager(PagingConfig(pageSize = pageSize)) { postsDao.paging() }.flow

    override fun feedRanked(pageSize: Int): Flow<PagingData<PostEntity>> =
        Pager(PagingConfig(pageSize = pageSize)) { postsDao.pagingRanked() }.flow

    override suspend fun createPost(authorId: String, type: String, text: String?, mediaUrl: String?, thumbnailUrl: String?, productId: String?): String {
        checkRateLimit("post", authorId, 5_000)
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
        // Reputation: +5 for post
        val rep = reputationDao.getByUserId(authorId)
        reputationDao.upsert(ReputationEntity(rep?.repId ?: UUID.randomUUID().toString(), authorId, (rep?.score ?: 0) + 5, now))
        // Badge sample: First Post
        if (rep == null) {
            badgesDao.upsert(BadgeEntity(UUID.randomUUID().toString(), authorId, "First Post", "Created your first post", now))
        }
        return id
    }

    override suspend fun createPostWithMedia(authorId: String, type: String, text: String?, mediaUri: Uri, isVideo: Boolean): String {
        val url = if (isVideo) MediaUploader.uploadVideo(appContext, storage, mediaUri) else MediaUploader.uploadImage(appContext, storage, mediaUri)
        return createPost(authorId, type, text, mediaUrl = url, thumbnailUrl = null, productId = null)
    }

    override fun streamComments(postId: String): Flow<List<CommentEntity>> = commentsDao.streamByPost(postId)

    override suspend fun addComment(postId: String, authorId: String, text: String) {
        checkRateLimit("comment", authorId, 2_000)
        commentsDao.upsert(
            CommentEntity(
                commentId = UUID.randomUUID().toString(),
                postId = postId,
                authorId = authorId,
                text = text,
                createdAt = System.currentTimeMillis()
            )
        )
        // Reputation: +1 for comment
        val rep = reputationDao.getByUserId(authorId)
        reputationDao.upsert(ReputationEntity(rep?.repId ?: UUID.randomUUID().toString(), authorId, (rep?.score ?: 0) + 1, System.currentTimeMillis()))
    }

    override fun countLikes(postId: String): Flow<Int> = likesDao.count(postId)

    override suspend fun like(postId: String, userId: String) {
        checkRateLimit("like", userId, 500)
        likesDao.upsert(
            LikeEntity(
                likeId = UUID.randomUUID().toString(),
                postId = postId,
                userId = userId,
                createdAt = System.currentTimeMillis()
            )
        )
        // Reputation: +1 for like given; and +2 for post author (if needed, fetch and update)
    }

    override suspend fun unlike(postId: String, userId: String) {
        likesDao.unlike(postId, userId)
    }
}

// Messaging (Firebase Realtime Database)
interface MessagingRepository {
    suspend fun sendDirectMessage(threadId: String, fromUserId: String, toUserId: String, text: String)
    fun streamThread(threadId: String): Flow<List<MessageDTO>>
    suspend fun sendGroupMessage(groupId: String, fromUserId: String, text: String)
    fun streamGroup(groupId: String): Flow<List<MessageDTO>>
    suspend fun sendDirectFile(threadId: String, fromUserId: String, toUserId: String, fileUri: Uri, fileName: String)
    suspend fun sendGroupFile(groupId: String, fromUserId: String, fileUri: Uri, fileName: String)
    suspend fun markThreadSeen(threadId: String, userId: String)
    data class MessageDTO(val messageId: String, val fromUserId: String, val toUserId: String?, val text: String, val timestamp: Long)
}

@Singleton
class MessagingRepositoryImpl @Inject constructor(
    private val firebaseDb: FirebaseDatabase,
    private val storage: FirebaseStorage,
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

    override suspend fun markThreadSeen(threadId: String, userId: String) {
        firebaseDb.getReference("dm_meta/$threadId/seen/$userId").setValue(true)
    }

    override suspend fun sendGroupMessage(groupId: String, fromUserId: String, text: String) {
        val msgId = UUID.randomUUID().toString()
        val data = mapOf(
            "messageId" to msgId,
            "fromUserId" to fromUserId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        firebaseDb.getReference("gc/$groupId/$msgId").setValue(data)
    }

    override fun streamGroup(groupId: String): Flow<List<MessagingRepository.MessageDTO>> = callbackFlow {
        val ref = firebaseDb.getReference("gc/$groupId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { c ->
                    val id = c.child("messageId").getValue(String::class.java) ?: return@mapNotNull null
                    val from = c.child("fromUserId").getValue(String::class.java) ?: return@mapNotNull null
                    val text = c.child("text").getValue(String::class.java) ?: ""
                    val ts = c.child("timestamp").getValue(Long::class.java) ?: 0L
                    MessagingRepository.MessageDTO(id, from, null, text, ts)
                }.sortedBy { it.timestamp }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun sendDirectFile(threadId: String, fromUserId: String, toUserId: String, fileUri: Uri, fileName: String) {
        val path = "chat_files/$threadId/${UUID.randomUUID()}_$fileName"
        val ref = storage.reference.child(path)
        ref.putFile(fileUri).await()
        val url = ref.downloadUrl.await().toString()
        val msgId = UUID.randomUUID().toString()
        val data = mapOf(
            "messageId" to msgId,
            "fromUserId" to fromUserId,
            "toUserId" to toUserId,
            "fileUrl" to url,
            "fileName" to fileName,
            "timestamp" to System.currentTimeMillis()
        )
        firebaseDb.getReference("dm/$threadId/$msgId").setValue(data)
    }

    override suspend fun sendGroupFile(groupId: String, fromUserId: String, fileUri: Uri, fileName: String) {
        val path = "group_files/$groupId/${UUID.randomUUID()}_$fileName"
        val ref = storage.reference.child(path)
        ref.putFile(fileUri).await()
        val url = ref.downloadUrl.await().toString()
        val msgId = UUID.randomUUID().toString()
        val data = mapOf(
            "messageId" to msgId,
            "fromUserId" to fromUserId,
            "fileUrl" to url,
            "fileName" to fileName,
            "timestamp" to System.currentTimeMillis()
        )
        firebaseDb.getReference("gc/$groupId/$msgId").setValue(data)
    }
}
