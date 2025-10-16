package com.rio.rostry.community

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.repository.social.MessagingRepository
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
        val trendingTopics: List<String>,
        val recentPosts: Int,
        val upcomingEvents: Int
    )
    
    data class ExpertMatch(
        val userId: String,
        val name: String,
        val topic: String,
        val matchScore: Double,
        val availability: Boolean
    )

    // Data aggregation methods
    fun getTrendingPosts(userId: String, limit: Int): Flow<List<PostEntity>> {
        return flow {
            // Fetch trending posts ordered by engagement (likes count) then recency
            val posts = postsDao.getTrending(limit)
            emit(posts)
        }.catch { 
            emit(emptyList()) 
        }
    }

    fun getSuggestedGroups(userId: String, limit: Int): Flow<List<GroupEntity>> {
        return groupsDao.streamAll().map { groups ->
        // Filter and rank groups based on user interests
            groups.take(limit)
        }
    }

    fun getUpcomingEvents(userId: String, limit: Int): Flow<List<EventEntity>> {
        val now = System.currentTimeMillis()
        return eventsDao.streamUpcoming(now).map { events ->
            events.take(limit)
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
                    name = "Expert", // Would fetch from UserRepository
                    specialties = profile.specialties.split(","),
                    availability = profile.availableForBooking,
                    rating = profile.rating
                )
            }
        }
    }

    fun getActiveThreads(userId: String): Flow<List<ThreadPreview>> {
        return threadMetadataDao.streamUserThreads(userId).map { metadataList ->
            metadataList.map { metadata ->
                val context = if (metadata.contextType != null) {
                    ThreadContext(
                        type = metadata.contextType,
                        relatedEntityId = metadata.relatedEntityId,
                        topic = metadata.topic
                    )
                } else null
                
                ThreadPreview(
                    threadId = metadata.threadId,
                    title = metadata.title ?: "Conversation",
                    snippet = "Last message...",
                    participantIds = metadata.participantIds.split(","),
                    lastMessageAt = metadata.lastMessageAt,
                    unreadCount = 0,
                    context = context
                )
            }
        }
    }

    fun getRegionalDiscussions(userId: String): Flow<List<PostEntity>> {
        return socialRepository.feedRanked(20).map { pagingData ->
            // Filter by region (would need user location data)
            emptyList<PostEntity>()
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
                name = "Expert",
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
        // Use MessagingRepository to ensure proper Firebase metadata and user index
        val messagingContext = MessagingRepository.ThreadContext(
            type = context.type,
            relatedEntityId = context.relatedEntityId,
            topic = context.topic
        )
        return messagingRepository.createThreadWithContext(fromUserId, toUserId, messagingContext)
    }

    suspend fun getThreadContext(threadId: String): ThreadContext? {
        val metadata = threadMetadataDao.getByThreadId(threadId) ?: return null
        return if (metadata.contextType != null) {
            ThreadContext(
                type = metadata.contextType,
                relatedEntityId = metadata.relatedEntityId,
                topic = metadata.topic
            )
        } else null
    }

    suspend fun suggestThreadTopics(userId: String): List<String> {
        return listOf("Breeding", "Health", "Nutrition", "Market Prices", "Farm Management")
    }

    // Community health metrics
    suspend fun getCommunityStats(userId: String): CommunityStats {
        val recentPosts = postsDao.paging() // Would count recent posts
        val upcomingEvents = eventsDao.streamUpcoming(System.currentTimeMillis()).first()
        
        return CommunityStats(
            activeUsers = 100, // Placeholder
            trendingTopics = listOf("Breeding", "Health", "Market"),
            recentPosts = 50,
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
