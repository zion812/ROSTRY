package com.rio.rostry.domain.social.repository

import android.net.Uri
import androidx.paging.PagingData
import com.rio.rostry.core.model.Comment
import com.rio.rostry.core.model.Post
import com.rio.rostry.core.model.Reputation
import com.rio.rostry.core.model.Story
import kotlinx.coroutines.flow.Flow

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
    fun feed(pageSize: Int = 20): Flow<PagingData<Post>>
    fun feedRanked(pageSize: Int = 20): Flow<PagingData<Post>>
    fun feedFollowing(userId: String, pageSize: Int = 20): Flow<PagingData<Post>>
    fun getUserPosts(userId: String, pageSize: Int = 20): Flow<PagingData<Post>>
    suspend fun createPost(
        authorId: String,
        type: String,
        text: String?,
        mediaUrl: String?,
        thumbnailUrl: String?,
        productId: String?,
        hashtags: List<String>? = null,
        mentions: List<String>? = null,
        parentPostId: String? = null
    ): String
    suspend fun createPostWithMedia(authorId: String, type: String, text: String?, mediaUri: Uri, isVideo: Boolean): String
    fun streamComments(postId: String): Flow<List<Comment>>
    fun getReplies(postId: String): Flow<List<Post>>
    suspend fun addComment(postId: String, authorId: String, text: String)
    fun countLikes(postId: String): Flow<Int>
    suspend fun like(postId: String, userId: String)
    suspend fun unlike(postId: String, userId: String)
    suspend fun deletePost(postId: String)
    
    // Engagement metrics
    suspend fun getEngagementMetrics(postId: String): EngagementMetrics
    suspend fun getEngagementMetricsBatch(postIds: List<String>): Map<String, EngagementMetrics>
    suspend fun getTrendingPosts(limit: Int, daysBack: Int): List<Post>
    suspend fun getTrendingHashtags(limit: Int, daysBack: Int): List<String>
    suspend fun trackPostView(postId: String, userId: String, durationSeconds: Int)
    
    // Stories
    suspend fun createStory(authorId: String, mediaUri: Uri, isVideo: Boolean): String
    fun streamActiveStories(): Flow<List<Story>>
    
    // Reputation
    fun getReputation(userId: String): Flow<Reputation?>
    
    // Profile stats - data-driven counts
    fun getUserPostsCount(userId: String): Flow<Int>
    fun getFollowersCount(userId: String): Flow<Int>
    fun getFollowingCount(userId: String): Flow<Int>
    
    // Follow system
    suspend fun follow(followerId: String, followedId: String)
    suspend fun unfollow(followerId: String, followedId: String)
    fun isFollowing(followerId: String, followedId: String): Flow<Boolean>
}

