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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.net.Uri
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Engagement metrics data class
data class EngagementMetrics(
    val postId: String,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val viewCount: Int,
    val engagementScore: Double
)

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
    
    // Engagement metrics
    suspend fun getEngagementMetrics(postId: String): EngagementMetrics
    suspend fun getEngagementMetricsBatch(postIds: List<String>): Map<String, EngagementMetrics>
    suspend fun getTrendingPosts(limit: Int, daysBack: Int): List<PostEntity>
    suspend fun getTrendingHashtags(limit: Int, daysBack: Int): List<String>
    suspend fun trackPostView(postId: String, userId: String, durationSeconds: Int)
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

    override suspend fun getEngagementMetrics(postId: String): EngagementMetrics {
        val likeCount = likesDao.count(postId).first()
        val comments = commentsDao.streamByPost(postId).first()
        val commentCount = comments.size
        val shareCount = (postId.hashCode().toInt().coerceAtLeast(0) % 20) // Mock share count
        val viewCount = (postId.hashCode().toInt().coerceAtLeast(0) % 1000).coerceAtLeast(10) // Mock view count
        val engagementScore = (likeCount.toDouble() * 2) + (commentCount.toDouble() * 3) + (shareCount.toDouble() * 5) + (viewCount.toDouble() * 0.1)
        
        return EngagementMetrics(
            postId = postId,
            likeCount = likeCount,
            commentCount = commentCount,
            shareCount = shareCount,
            viewCount = viewCount,
            engagementScore = engagementScore
        )
    }

    override suspend fun getEngagementMetricsBatch(postIds: List<String>): Map<String, EngagementMetrics> {
        return postIds.associateWith { postId ->
            getEngagementMetrics(postId)
        }
    }

    override suspend fun getTrendingPosts(limit: Int, daysBack: Int): List<PostEntity> {
        // Use existing getTrending query from PostsDao
        return postsDao.getTrending(limit)
    }

    override suspend fun getTrendingHashtags(limit: Int, daysBack: Int): List<String> {
        val recentPosts = postsDao.getTrending(100) // Get top 100 recent posts
        
        val hashtagCounts = mutableMapOf<String, Int>()
        val hashtagRegex = Regex("#\\w+")
        
        recentPosts.forEach { post ->
            val postText = post.text ?: ""
            hashtagRegex.findAll(postText).forEach { match ->
                val hashtag = match.value.lowercase()
                hashtagCounts[hashtag] = (hashtagCounts[hashtag] ?: 0) + 1
            }
        }
        
        return hashtagCounts.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }

    override suspend fun trackPostView(postId: String, userId: String, durationSeconds: Int) {
        // In a real implementation, this would store view analytics
        // For now, we can log it or store in a separate analytics table
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
    fun streamUserThreads(userId: String): Flow<List<String>>
    fun streamUnreadCount(userId: String): Flow<Int>
    
    // Context-aware messaging enhancements
    suspend fun createThreadWithContext(fromUserId: String, toUserId: String, context: ThreadContext): String
    suspend fun updateThreadMetadata(threadId: String, title: String?, lastMessageAt: Long)
    fun streamThreadMetadata(threadId: String): Flow<ThreadMetadata?>
    fun streamUserThreadsWithMetadata(userId: String): Flow<List<ThreadWithMetadata>>
    
    data class MessageDTO(val messageId: String, val fromUserId: String, val toUserId: String?, val text: String, val timestamp: Long)
    data class ThreadContext(val type: String, val relatedEntityId: String?, val topic: String?)
    data class ThreadMetadata(val threadId: String, val title: String?, val context: ThreadContext?, val participantIds: List<String>, val lastMessageAt: Long)
    data class ThreadWithMetadata(val threadId: String, val metadata: ThreadMetadata?, val lastMessage: MessageDTO?, val unreadCount: Int)
}

@Singleton
class MessagingRepositoryImpl @Inject constructor(
    private val firebaseDb: FirebaseDatabase,
    private val storage: FirebaseStorage,
    private val threadMetadataDao: com.rio.rostry.data.database.dao.ThreadMetadataDao,
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

    // A simple user->threads index at user_dm_index/<userId>/<threadId>=true
    override fun streamUserThreads(userId: String): Flow<List<String>> = callbackFlow {
        val ref = firebaseDb.getReference("user_dm_index/$userId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull { it.key }
                trySend(ids)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Unread heuristic with timestamps if available:
    // dm_meta/<threadId>/lastMsgTs and dm_meta/<threadId>/lastSeenTs/<userId>
    // If lastMsgTs > lastSeenTs => unread. Fallback: seen flag not true.
    override fun streamUnreadCount(userId: String): Flow<Int> = callbackFlow {
        val indexRef = firebaseDb.getReference("user_dm_index/$userId")
        val metaRef = firebaseDb.getReference("dm_meta")
        val indexListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val threads = snapshot.children.mapNotNull { it.key }.toSet()
                metaRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(metaSnap: DataSnapshot) {
                        val c = threads.count { t ->
                            val meta = metaSnap.child(t)
                            val lastMsgTs = meta.child("lastMsgTs").getValue(Long::class.java)
                            val lastSeenTs = meta.child("lastSeenTs").child(userId).getValue(Long::class.java)
                            if (lastMsgTs != null && lastSeenTs != null) {
                                lastMsgTs > lastSeenTs
                            } else {
                                meta.child("seen").child(userId).getValue(Boolean::class.java) != true
                            }
                        }
                        trySend(c)
                    }
                    override fun onCancelled(error: DatabaseError) { /* ignore */ }
                })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        indexRef.addValueEventListener(indexListener)
        awaitClose { indexRef.removeEventListener(indexListener) }
    }

    // Context-aware messaging implementations
    override suspend fun createThreadWithContext(fromUserId: String, toUserId: String, context: MessagingRepository.ThreadContext): String {
        val threadId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        
        // Store context in Firebase
        firebaseDb.getReference("dm_meta/$threadId/context/type").setValue(context.type)
        context.relatedEntityId?.let {
            firebaseDb.getReference("dm_meta/$threadId/context/relatedEntityId").setValue(it)
        }
        context.topic?.let {
            firebaseDb.getReference("dm_meta/$threadId/context/topic").setValue(it)
        }
        
        // Store participants
        firebaseDb.getReference("dm_meta/$threadId/participants/$fromUserId").setValue(true)
        firebaseDb.getReference("dm_meta/$threadId/participants/$toUserId").setValue(true)
        
        // Store title
        val title = when (context.type) {
            "PRODUCT_INQUIRY" -> "Product Inquiry"
            "EXPERT_CONSULT" -> "Expert Consultation"
            "BREEDING_DISCUSSION" -> "Breeding Discussion"
            else -> "Conversation"
        }
        firebaseDb.getReference("dm_meta/$threadId/title").setValue(title)
        
        // Store in local Room
        threadMetadataDao.upsert(
            com.rio.rostry.data.database.entity.ThreadMetadataEntity(
                threadId = threadId,
                title = title,
                contextType = context.type,
                relatedEntityId = context.relatedEntityId,
                topic = context.topic,
                participantIds = "$fromUserId,$toUserId",
                lastMessageAt = now,
                createdAt = now,
                updatedAt = now
            )
        )
        
        return threadId
    }

    override suspend fun updateThreadMetadata(threadId: String, title: String?, lastMessageAt: Long) {
        firebaseDb.getReference("dm_meta/$threadId/lastMsgTs").setValue(lastMessageAt)
        title?.let {
            firebaseDb.getReference("dm_meta/$threadId/title").setValue(it)
        }
        
        threadMetadataDao.updateLastMessageTime(threadId, lastMessageAt)
    }

    override fun streamThreadMetadata(threadId: String): Flow<MessagingRepository.ThreadMetadata?> = callbackFlow {
        val ref = firebaseDb.getReference("dm_meta/$threadId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val title = snapshot.child("title").getValue(String::class.java)
                val contextType = snapshot.child("context/type").getValue(String::class.java)
                val relatedEntityId = snapshot.child("context/relatedEntityId").getValue(String::class.java)
                val topic = snapshot.child("context/topic").getValue(String::class.java)
                val lastMessageAt = snapshot.child("lastMsgTs").getValue(Long::class.java) ?: 0L
                
                val context = if (contextType != null) {
                    MessagingRepository.ThreadContext(contextType, relatedEntityId, topic)
                } else null
                
                val participants = snapshot.child("participants").children.mapNotNull { it.key }
                
                val metadata = MessagingRepository.ThreadMetadata(
                    threadId = threadId,
                    title = title,
                    context = context,
                    participantIds = participants,
                    lastMessageAt = lastMessageAt
                )
                trySend(metadata)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun streamUserThreadsWithMetadata(userId: String): Flow<List<MessagingRepository.ThreadWithMetadata>> = callbackFlow {
        val indexRef = firebaseDb.getReference("user_dm_index/$userId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val threadIds = snapshot.children.mapNotNull { it.key }
                val threadsWithMetadata = mutableListOf<MessagingRepository.ThreadWithMetadata>()
                
                // For each thread, fetch metadata and last message
                threadIds.forEach { threadId ->
                    // Simplified - in real implementation, would fetch metadata and last message
                    threadsWithMetadata.add(
                        MessagingRepository.ThreadWithMetadata(
                            threadId = threadId,
                            metadata = null,
                            lastMessage = null,
                            unreadCount = 0
                        )
                    )
                }
                trySend(threadsWithMetadata)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        indexRef.addValueEventListener(listener)
        awaitClose { indexRef.removeEventListener(listener) }
    }
}
