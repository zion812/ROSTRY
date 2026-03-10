package com.rio.rostry.data.social.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rio.rostry.core.model.Post
import com.rio.rostry.core.model.Message
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.social.repository.SocialFeedRepository
import com.rio.rostry.domain.social.repository.MessagingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SocialFeedRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 * Requirement 4.5 - Data modules use Hilt to bind implementations
 */
@Singleton
class SocialFeedRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : SocialFeedRepository {

    private val postsCollection = firestore.collection("posts")
    private val likesCollection = firestore.collection("post_likes")

    override fun getFeedPosts(): Flow<List<Post>> = callbackFlow {
        val listener = postsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Post::class.java)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getPostById(postId: String): Result<Post> {
        return try {
            val document = postsCollection.document(postId).get().await()
            if (document.exists()) {
                val post = document.toObject(Post::class.java)
                if (post != null) {
                    Result.Success(post)
                } else {
                    Result.Error(Exception("Failed to parse post data"))
                }
            } else {
                Result.Error(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createPost(post: Post): Result<Post> {
        return try {
            postsCollection.document(post.id).set(post).await()
            Result.Success(post)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updatePost(post: Post): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "content" to post.content,
                "images" to post.images,
                "videos" to post.videos,
                "visibility" to post.visibility.name,
                "updatedAt" to System.currentTimeMillis()
            )
            postsCollection.document(post.id).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            postsCollection.document(postId).delete().await()
            // Also delete associated likes
            val likesQuery = likesCollection.whereEqualTo("postId", postId).get().await()
            likesQuery.documents.forEach { it.reference.delete() }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun likePost(postId: String): Result<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid 
                ?: return Result.Error(Exception("User not authenticated"))
            
            val likeId = "${userId}_${postId}"
            val likeData = hashMapOf(
                "id" to likeId,
                "userId" to userId,
                "postId" to postId,
                "createdAt" to System.currentTimeMillis()
            )
            
            // Add like
            likesCollection.document(likeId).set(likeData).await()
            
            // Increment likes count on post
            postsCollection.document(postId)
                .update("likesCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getPostsByUser(userId: String): Flow<List<Post>> = callbackFlow {
        val listener = postsCollection
            .whereEqualTo("authorId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Post::class.java)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }
}

/**
 * Implementation of MessagingRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 * Requirement 4.5 - Data modules use Hilt to bind implementations
 */
@Singleton
class MessagingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : MessagingRepository {

    private val messagesCollection = firestore.collection("messages")
    private val threadsCollection = firestore.collection("message_threads")

    override fun getMessages(threadId: String): Flow<List<Message>> = callbackFlow {
        val listener = messagesCollection
            .whereEqualTo("threadId", threadId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(message: Message): Result<Message> {
        return try {
            // Save message
            messagesCollection.document(message.id).set(message).await()
            
            // Update thread last message timestamp
            threadsCollection.document(message.threadId)
                .update("lastMessageAt", message.createdAt)
                .await()
            
            Result.Success(message)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun markAsRead(messageId: String): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "isRead" to true,
                "readAt" to System.currentTimeMillis()
            )
            messagesCollection.document(messageId).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUnreadCount(userId: String): Result<Int> {
        return try {
            // Get all threads where user is a participant
            val threadsQuery = threadsCollection
                .whereArrayContains("participantIds", userId)
                .get()
                .await()
            
            var unreadCount = 0
            
            // For each thread, count unread messages
            for (threadDoc in threadsQuery.documents) {
                val threadId = threadDoc.id
                val messagesQuery = messagesCollection
                    .whereEqualTo("threadId", threadId)
                    .whereEqualTo("isRead", false)
                    .whereNotEqualTo("senderId", userId)
                    .get()
                    .await()
                
                unreadCount += messagesQuery.size()
            }
            
            Result.Success(unreadCount)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getThreads(userId: String): Flow<List<String>> = callbackFlow {
        val listener = threadsCollection
            .whereArrayContains("participantIds", userId)
            .orderBy("lastMessageAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val threadIds = snapshot?.documents?.map { it.id } ?: emptyList()
                trySend(threadIds)
            }
        awaitClose { listener.remove() }
    }
}
