package com.rio.rostry.domain.service

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.social.repository.CommunityRepository
import com.rio.rostry.domain.social.repository.MessagingRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for managing community engagement features.
 * Provides real-time analytics and recommendations for community features.
 */
@Singleton
class CommunityEngagementService @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val messagingRepository: MessagingRepository,
    private val firestore: FirebaseFirestore
) {
    private val communitiesCollection = firestore.collection("communities")
    private val postsCollection = firestore.collection("posts")
    private val messagesCollection = firestore.collection("messages")

    /**
     * Get engagement metrics for a community.
     */
    suspend fun getEngagementMetrics(communityId: String): Result<Map<String, Int>> {
        return try {
            val now = System.currentTimeMillis()
            val startOfDay = now - (now % (24 * 60 * 60 * 1000))
            
            // Get active members count
            val communityDoc = communitiesCollection.document(communityId).get().await()
            val memberCount = communityDoc.get("memberCount") as? Long ?: 0
            
            // Get posts today
            val postsToday = postsCollection
                .whereEqualTo("communityId", communityId)
                .whereGreaterThan("createdAt", startOfDay)
                .get()
                .await()
                .size()
            
            // Get messages today
            val messagesToday = messagesCollection
                .whereEqualTo("communityId", communityId)
                .whereGreaterThan("timestamp", startOfDay)
                .get()
                .await()
                .size()
            
            val metrics = mapOf(
                "activeMembers" to memberCount.toInt(),
                "postsToday" to postsToday,
                "messagesToday" to messagesToday
            )
            
            Result.Success(metrics)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get trending topics in a community based on engagement.
     */
    suspend fun getTrendingTopics(communityId: String, limit: Int = 10): Result<List<String>> {
        return try {
            val now = System.currentTimeMillis()
            val sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000)
            
            // Get recent posts with their engagement metrics
            val recentPosts = postsCollection
                .whereEqualTo("communityId", communityId)
                .whereGreaterThan("createdAt", sevenDaysAgo)
                .get()
                .await()
                .documents
            
            // Calculate trending score for each unique topic/hashtag
            val topicScores = mutableMapOf<String, Double>()
            
            recentPosts.forEach { post ->
                val likes = (post.get("likesCount") as? Long) ?: 0
                val comments = (post.get("commentsCount") as? Long) ?: 0
                val shares = (post.get("sharesCount") as? Long) ?: 0
                val recency = (now - (post.getLong("createdAt") ?: now)) / (1000 * 60 * 60) // hours ago
                
                // Extract hashtags from content
                val content = post.getString("content") ?: ""
                val hashtags = Regex("#(\\w+)").findAll(content).map { it.groupValues[1] }.toList()
                
                hashtags.forEach { hashtag ->
                    val trendingScore = (likes + comments * 2 + shares * 3) / (recency + 1)
                    topicScores[hashtag] = (topicScores[hashtag] ?: 0.0) + trendingScore
                }
            }
            
            // Sort by score and return top topics
            val trendingTopics = topicScores.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { "#${it.key}" }
            
            Result.Success(trendingTopics)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get community suggestions for a user based on their interests and network.
     */
    suspend fun getCommunitySuggestions(userId: String, limit: Int = 5): Result<List<String>> {
        return try {
            // Get user's current communities
            val userCommunities = communityRepository.getUserCommunities(userId).getOrNull() ?: emptyList()
            val userCommunityIds = userCommunities.map { it.id }.toSet()
            
            // Get communities the user's friends are in
            val friendCommunities = communityRepository.getFriendsCommunities(userId).getOrNull() ?: emptyList()
            
            // Score communities based on friend membership
            val communityScores = mutableMapOf<String, Double>()
            
            friendCommunities.forEach { community ->
                if (community.id !in userCommunityIds) {
                    val memberCount = community.memberCount ?: 0
                    val friendCount = community.members.count { it in userCommunityIds }
                    val score = friendCount * 10.0 + (memberCount * 0.1)
                    communityScores[community.id] = (communityScores[community.id] ?: 0.0) + score
                }
            }
            
            // Get popular communities user hasn't joined
            val popularCommunities = communitiesCollection
                .orderBy("memberCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()
                .documents
            
            popularCommunities.forEach { doc ->
                val communityId = doc.id
                if (communityId !in userCommunityIds && communityId !in communityScores) {
                    val memberCount = doc.getLong("memberCount") ?: 0
                    communityScores[communityId] = memberCount * 0.05
                }
            }
            
            // Sort and return top suggestions
            val suggestions = communityScores.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key }
            
            Result.Success(suggestions)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}