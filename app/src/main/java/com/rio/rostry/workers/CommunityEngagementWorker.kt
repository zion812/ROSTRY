package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.community.CommunityEngagementService
import com.rio.rostry.data.database.dao.CommunityRecommendationDao
import com.rio.rostry.data.database.dao.UserInterestDao
import com.rio.rostry.data.database.entity.CommunityRecommendationEntity
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker
class CommunityEngagementWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val communityEngagementService: CommunityEngagementService,
    private val communityRecommendationDao: CommunityRecommendationDao,
    private val userInterestDao: UserInterestDao,
    private val sessionManager: SessionManager,
    private val currentUserProvider: CurrentUserProvider
) : CoroutineWorker(appContext, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Get current user
            val userId = currentUserProvider.userIdOrNull()
                ?: sessionManager.currentDemoUserId().first()
                ?: return Result.success() // No user to fetch for

            // Get user type
            val userType = sessionManager.sessionRole().first()
                ?: return Result.success()

            val now = System.currentTimeMillis()
            val expiresAt = now + (24 * 60 * 60 * 1000) // 24 hours

            val recommendations = mutableListOf<CommunityRecommendationEntity>()

            // Generate mentor recommendations
            val mentors = communityEngagementService.suggestMentors(userId)
            mentors.forEach { match ->
                recommendations.add(
                    CommunityRecommendationEntity(
                        recommendationId = UUID.randomUUID().toString(),
                        userId = userId,
                        type = "MENTOR",
                        targetId = match.userId,
                        score = match.score,
                        reason = "High reputation user",
                        createdAt = now,
                        expiresAt = expiresAt,
                        dismissed = false
                    )
                )
            }

            // Generate connection recommendations
            val connections = communityEngagementService.suggestConnections(userId, userType)
            connections.take(10).forEach { suggestion ->
                recommendations.add(
                    CommunityRecommendationEntity(
                        recommendationId = UUID.randomUUID().toString(),
                        userId = userId,
                        type = "CONNECTION",
                        targetId = suggestion.userId,
                        score = suggestion.matchScore,
                        reason = suggestion.reason,
                        createdAt = now,
                        expiresAt = expiresAt,
                        dismissed = false
                    )
                )
            }

            // Generate group recommendations
            val groups = communityEngagementService.getSuggestedGroups(userId, 10).first()
            groups.forEach { group ->
                recommendations.add(
                    CommunityRecommendationEntity(
                        recommendationId = UUID.randomUUID().toString(),
                        userId = userId,
                        type = "GROUP",
                        targetId = group.groupId,
                        score = 0.8,
                        reason = "Based on your interests",
                        createdAt = now,
                        expiresAt = expiresAt,
                        dismissed = false
                    )
                )
            }

            // Generate event recommendations
            val events = communityEngagementService.getUpcomingEvents(userId, 10).first()
            events.forEach { event ->
                recommendations.add(
                    CommunityRecommendationEntity(
                        recommendationId = UUID.randomUUID().toString(),
                        userId = userId,
                        type = "EVENT",
                        targetId = event.eventId,
                        score = 0.7,
                        reason = "Upcoming in your area",
                        createdAt = now,
                        expiresAt = expiresAt,
                        dismissed = false
                    )
                )
            }

            // Generate expert recommendations
            val experts = communityEngagementService.getAvailableExperts(null).first()
            experts.take(5).forEach { expert ->
                recommendations.add(
                    CommunityRecommendationEntity(
                        recommendationId = UUID.randomUUID().toString(),
                        userId = userId,
                        type = "EXPERT",
                        targetId = expert.userId,
                        score = expert.rating / 5.0,
                        reason = "Expert in ${expert.specialties.joinToString()}",
                        createdAt = now,
                        expiresAt = expiresAt,
                        dismissed = false
                    )
                )
            }

            // Store all recommendations
            if (recommendations.isNotEmpty()) {
                communityRecommendationDao.upsertAll(recommendations)
            }

            // Clean up expired recommendations
            communityRecommendationDao.deleteExpired(now)

            Timber.d("CommunityEngagementWorker: Generated ${recommendations.size} recommendations for user $userId")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "CommunityEngagementWorker: Failed to generate recommendations")
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "community_engagement"
        
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<CommunityEngagementWorker>(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
