package com.rio.rostry.community

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.core.common.session.CurrentUserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityEngagementService @Inject constructor(
    private val socialRepository: SocialRepository,
    private val messagingRepository: MessagingRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val reputationDao: ReputationDao,
    private val followsDao: FollowsDao,
    private val groupsDao: GroupsDao,
    private val eventsDao: EventsDao,
    private val expertBookingsDao: ExpertBookingsDao,
    private val postsDao: PostsDao,
    private val chatMessageDao: ChatMessageDao,
    private val threadMetadataDao: ThreadMetadataDao,
    private val expertProfileDao: ExpertProfileDao
) {
    companion object {
        private const val TAG = "CommunityEngagement"
    }

    // Data classes
    data class Match(val userId: String, val score: Double)

    data class ExpertInfo(
        val userId: String,
        val name: String,
        val specialties: List<String>,
        val availability: Boolean,
        val rating: Double
    )

    data class ThreadPreview(
        val threadId: String,
        val title: String,
        val snippet: String,
        val participantIds: List<String>,
        val lastMessageAt: Long,
        val unreadCount: Int,
        val context: ThreadContext?
    )

    data class ConnectionSuggestion(
        val userId: String,
        val name: String,
        val userType: UserType,
        val matchScore: Double,
        val reason: String
    )

    data class ThreadContext(
        val type: String,
        val relatedEntityId: String?,
        val topic: String?,
        val metadata: Map<String, String> = emptyMap()
    )

    data class CommunityStats(
        val activeUsers: Int,
        val trendingTopics: List<TrendingTopic>,
        val recentPosts: Int,
        val upcomingEvents: Int
    )

    data class TrendingTopic(
        val topic: String,
        val postCount: Int,
        val engagementScore: Double,
        val trendDirection: TrendDirection
    )

    enum class TrendDirection {
        UP, DOWN, STABLE
    }

    data class ExpertMatch(
        val userId: String,
        val name: String,
        val topic: String,
        val matchScore: Double,
        val availability: Boolean
    )

    data class CommunityUser(
        val userId: String,
        val name: String,
        val userType: UserType,
        val reputation: Int,
        val isFollowing: Boolean,
        val commonInterests: List<String>
    )

    // ==================== Trending Topics ====================

    /**
     * Get trending topics based on post engagement and recency.
     * Uses social graph queries to calculate trending scores.
     */
    fun getTrendingTopics(limit: Int = 10): Flow<List<TrendingTopic>> = flow {
        Timber.d("Fetching trending topics (limit: $limit)")

        // Get recent posts for trend analysis
        val recentPosts = postsDao.getRecentPosts(100).first()

        // Analyze topics from posts
        val topicCounts = mutableMapOf<String, Int>()
        val topicEngagement = mutableMapOf<String, Double>()

        recentPosts.forEach { post ->
            // Extract topics from post content/tags
            val topics = parseTopicsFromPost(post)
            topics.forEach { topic ->
                topicCounts[topic] = (topicCounts[topic] ?: 0) + 1
                topicEngagement[topic] = (topicEngagement[topic] ?: 0.0) + (post.likesCount.toDouble() + post.commentsCount * 2)
            }
        }

        // Calculate trending scores and sort
        val trending = topicCounts.map { (topic, count) ->
            val engagement = topicEngagement[topic] ?: 0.0
            val score = (count * 0.3) + (engagement * 0.7)

            TrendingTopic(
                topic = topic,
                postCount = count,
                engagementScore = score,
                trendDirection = calculateTrendDirection(topic, score)
            )
        }.sortedByDescending { it.engagementScore }
            .take(limit)

        Timber.d("Found ${trending.size} trending topics")
        emit(trending)
    }.catch { e ->
        Timber.e(e, "Error fetching trending topics")
        emit(emptyList())
    }

    private fun parseTopicsFromPost(post: PostEntity): List<String> {
        // Parse topics from content, hashtags, and categories
        val topics = mutableListOf<String>()

        // Add category if present
        post.category?.let { topics.add(it) }

        // Parse hashtags from content
        val hashtagRegex = "#(\\w+)".toRegex()
        hashtagRegex.findAll(post.content ?: "").forEach {
            topics.add(it.groupValues[1].lowercase())
        }

        return topics.distinct()
    }

    private fun calculateTrendDirection(topic: String, currentScore: Double): TrendDirection {
        // In a real implementation, compare with historical scores
        // For now, use a simple heuristic
        return when {
            currentScore > 100 -> TrendDirection.UP
            currentScore < 50 -> TrendDirection.DOWN
            else -> TrendDirection.STABLE
        }
    }

    // ==================== Community Suggestions ====================

    /**
     * Get community suggestions based on social graph and user interests.
     * Uses follows, groups, and reputation data for recommendations.
     */
    fun getCommunitySuggestions(userId: String, limit: Int = 20): Flow<List<CommunityUser>> = flow {
        Timber.d("Fetching community suggestions for user: $userId")

        // Get users the current user already follows
        val following = followsDao.getFollowing(userId).first().map { it.targetUserId }.toSet()

        // Get user's groups for interest-based suggestions
        val userGroups = groupsDao.getUserGroups(userId).first()
        val userGroupIds = userGroups.map { it.groupId }.toSet()

        // Get top users by reputation (excluding followed)
        val topUsers = reputationDao.top(50).first()
            .filter { it.userId != userId && it.userId !in following }
            .take(limit)

        // Get users in same groups
        val groupMembers = if (userGroupIds.isNotEmpty()) {
            userGroupIds.flatMap { groupId ->
                groupsDao.getMembers(groupId).first()
                    .map { it.userId }
            }.filter { it != userId && it !in following }
        } else emptyList()

        // Combine and deduplicate
        val suggestedIds = (topUsers.map { it.userId } + groupMembers).distinct().take(limit)

        // Build CommunityUser objects
        val suggestions = suggestedIds.mapNotNull { id ->
            val rep = topUsers.find { it.userId == id }
            val user = userRepository.getUserById(id)

            if (user != null) {
                CommunityUser(
                    userId = id,
                    name = user.fullName,
                    userType = user.userType,
                    reputation = rep?.score ?: 0,
                    isFollowing = false,
                    commonInterests = findCommonInterests(userId, id, userGroups)
                )
            } else null
        }.sortedByDescending { it.reputation }

        Timber.d("Found ${suggestions.size} community suggestions")
        emit(suggestions)
    }.catch { e ->
        Timber.e(e, "Error fetching community suggestions")
        emit(emptyList())
    }

    private fun findCommonInterests(userId1: String, userId2: String, userGroups: List<GroupEntity>): List<String> {
        val user1GroupIds = userGroups.map { it.groupId }.toSet()
        val user2GroupIds = groupsDao.getUserGroups(userId2).first().map { it.groupId }.toSet()

        return (user1GroupIds intersect user2GroupIds).mapNotNull { groupId ->
            groupsDao.getById(groupId)?.name
        }
    }

    // ==================== Social Graph Queries ====================

    /**
     * Get user's social graph metrics.
     */
    fun getSocialGraphMetrics(userId: String): Flow<SocialGraphMetrics> = flow {
        Timber.d("Calculating social graph metrics for user: $userId")

        val followerCount = followsDao.getFollowerCount(userId)
        val followingCount = followsDao.getFollowingCount(userId)
        val reputation = reputationDao.getByUserId(userId)

        val metrics = SocialGraphMetrics(
            userId = userId,
            followersCount = followerCount,
            followingCount = followingCount,
            reputationScore = reputation?.score ?: 0,
            influenceScore = calculateInfluenceScore(followerCount, reputation?.score ?: 0),
            connectionDegree = calculateConnectionDegree(userId)
        )

        emit(metrics)
    }.catch { e ->
        Timber.e(e, "Error calculating social graph metrics")
        emit(SocialGraphMetrics(userId, 0, 0, 0, 0, 0))
    }

    private fun calculateInfluenceScore(followers: Int, reputation: Int): Double {
        return (followers * 0.6) + (reputation * 0.4)
    }

    private suspend fun calculateConnectionDegree(userId: String): Int {
        // Calculate degrees of separation from active community members
        val following = followsDao.getFollowing(userId).first().map { it.targetUserId }
        val followingRep = following.mapNotNull { followsDao.getReputation(it) }
        val activeThreshold = followingRep.maxOfOrNull { it.score } ?: 0

        return if (followingRep.any { it.score > activeThreshold * 0.8 }) 1 else 2
    }

    // ==================== Data aggregation methods ====================

    fun getTrendingPosts(userId: String, limit: Int): Flow<List<PostEntity>> {
        return flow {
            Timber.d("Fetching trending posts for user: $userId")
            val posts = postsDao.getTrending(limit)
            emit(posts)
        }.catch {
            Timber.e(it, "Error fetching trending posts")
            emit(emptyList())
        }
    }

    fun getSuggestedGroups(userId: String, limit: Int): Flow<List<GroupEntity>> {
        return groupsDao.streamAll().map { groups ->
            // Filter and rank groups based on user interests and activity
            groups
                .filter { group -> !isUserInGroup(userId, group.groupId) }
                .sortedByDescending { it.memberCount }
                .take(limit)
        }
    }

    private suspend fun isUserInGroup(userId: String, groupId: String): Boolean {
        return groupsDao.getMember(userId, groupId) != null
    }

    fun getUpcomingEvents(userId: String, limit: Int): Flow<List<EventEntity>> {
        val now = System.currentTimeMillis()
        return eventsDao.streamUpcoming(now).map { events ->
            events
                .sortedBy { it.eventDate }
                .take(limit)
        }
    }

    fun getAvailableExperts(topic: String?): Flow<List<ExpertInfo>> {
        return if (topic != null) {
            expertProfileDao.streamBySpecialty(topic)
        } else {
            expertProfileDao.streamAvailableExperts()
        }.map { profiles ->
            profiles.map { profile ->
                ExpertInfo(
                    userId = profile.userId,
                    name = profile.displayName ?: "Expert",
                    specialties = profile.specialties.split(",").map { it.trim() },
                    availability = profile.availableForBooking,
                    rating = profile.rating
                )
            }
        }
    }

    fun getActiveThreads(userId: String): Flow<List<ThreadPreview>> {
        return threadMetadataDao.streamUserThreads(userId).map { metadataList ->
            metadataList.map { metadata ->
                val ctxType = metadata.contextType
                val context = if (ctxType != null) {
                    ThreadContext(
                        type = ctxType,
                        relatedEntityId = metadata.relatedEntityId,
                        topic = metadata.topic
                    )
                } else null

                ThreadPreview(
                    threadId = metadata.threadId,
                    title = metadata.title ?: "Conversation",
                    snippet = metadata.lastMessageSnippet ?: "No messages yet",
                    participantIds = metadata.participantIds.split(",").filter { it.isNotBlank() },
                    lastMessageAt = metadata.lastMessageAt,
                    unreadCount = metadata.unreadCount,
                    context = context
                )
            }
        }
    }

    fun getRegionalDiscussions(userId: String): Flow<List<PostEntity>> {
        return socialRepository.feedRanked(20).map { pagingData ->
            // Filter by region using user's location
            val user = userRepository.getUserById(userId)
            val userRegion = user?.location

            if (userRegion != null) {
                pagingData.filter { post ->
                    post.location?.region == userRegion
                }
            } else {
                pagingData.take(10)
            }
        }
    }

    // Mentor/connection suggestions
    suspend fun suggestMentors(forUserId: String): List<Match> {
        val topExperts = reputationDao.top(10).first()
        return topExperts.map { rep ->
            Match(
                userId = rep.userId,
                score = rep.score.toDouble() / 1000.0 // Normalize score
            )
        }
    }

    suspend fun suggestConnections(userId: String, userType: UserType): List<ConnectionSuggestion> {
        val topUsers = reputationDao.top(20).first()
        return topUsers.map { rep ->
            ConnectionSuggestion(
                userId = rep.userId,
                name = "User ${rep.userId.take(8)}",
                userType = userType,
                matchScore = rep.score.toDouble() / 1000.0,
                reason = "High reputation in community"
            )
        }
    }

    suspend fun findExpertsByTopic(topic: String): List<ExpertMatch> {
        val experts = expertProfileDao.streamBySpecialty(topic).first()
        return experts.map { expert ->
            ExpertMatch(
                userId = expert.userId,
                name = expert.displayName ?: "Expert",
                topic = topic,
                matchScore = expert.rating / 5.0,
                availability = expert.availableForBooking
            )
        }
    }

    // Context-aware messaging
    suspend fun createContextualThread(
        fromUserId: String,
        toUserId: String,
        context: ThreadContext
    ): String {
        val messagingContext = MessagingRepository.ThreadContext(
            type = context.type,
            relatedEntityId = context.relatedEntityId,
            topic = context.topic
        )
        return messagingRepository.createThreadWithContext(fromUserId, toUserId, messagingContext)
    }

    suspend fun getThreadContext(threadId: String): ThreadContext? {
        val metadata = threadMetadataDao.getByThreadId(threadId) ?: return null
        val ctxType = metadata.contextType
        return if (ctxType != null) {
            ThreadContext(
                type = ctxType,
                relatedEntityId = metadata.relatedEntityId,
                topic = metadata.topic
            )
        } else null
    }

    suspend fun suggestThreadTopics(userId: String): List<String> {
        // Get topics from user's groups and interests
        val userGroups = groupsDao.getUserGroups(userId).first()
        val topics = userGroups.mapNotNull { it.category }.distinct()

        // Add default topics if needed
        val defaultTopics = listOf("Breeding", "Health", "Nutrition", "Market Prices", "Farm Management")
        return (topics + defaultTopics).distinct().take(10)
    }

    // Community health metrics
    suspend fun getCommunityStats(userId: String): CommunityStats {
        val recentPosts = postsDao.getRecentPosts(50).first()
        val upcomingEvents = eventsDao.streamUpcoming(System.currentTimeMillis()).first()
        val activeUsers = reputationDao.countActiveUsers()

        val trendingTopics = getTrendingTopics(5).first()

        return CommunityStats(
            activeUsers = activeUsers,
            trendingTopics = trendingTopics,
            recentPosts = recentPosts.size,
            upcomingEvents = upcomingEvents.size
        )
    }

    suspend fun getUserEngagementScore(userId: String): Double {
        val rep = reputationDao.getByUserId(userId)
        return (rep?.score ?: 0).toDouble() / 100.0
    }

    // Helper methods
    private fun generateThreadTitle(context: ThreadContext): String {
        return when (context.type) {
            "PRODUCT_INQUIRY" -> "Product Inquiry"
            "EXPERT_CONSULT" -> "Expert Consultation: ${context.topic ?: "General"}"
            "BREEDING_DISCUSSION" -> "Breeding Discussion"
            else -> "Conversation"
        }
    }
}

/**
 * Social graph metrics for a user.
 */
data class SocialGraphMetrics(
    val userId: String,
    val followersCount: Int,
    val followingCount: Int,
    val reputationScore: Int,
    val influenceScore: Double,
    val connectionDegree: Int
)