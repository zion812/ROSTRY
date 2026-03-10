package com.rio.rostry.data.social.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.data.database.dao.LikesDao
import com.rio.rostry.domain.social.repository.LikesRepository
import com.rio.rostry.domain.social.repository.SocialFeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of LikesRepository for managing post likes.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Social Domain repository migration
 */
@Singleton
class LikesRepositoryImpl @Inject constructor(
    private val socialFeedRepository: SocialFeedRepository,
    private val likesDao: LikesDao
) : LikesRepository {

    override suspend fun likePost(postId: String): Result<Unit> {
        return try {
            // Get current user from UserRepository
            // Note: UserRepository needs to expose getCurrentUser method
            // For now, using a simplified approach
            socialFeedRepository.likePost(postId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun unlikePost(postId: String): Result<Unit> {
        return try {
            // TODO: Implement unlike in SocialFeedRepository
            // For now, returning success
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getLikeCount(postId: String): Flow<Int> {
        // TODO: Implement like count in SocialFeedRepository
        // For now, using DAO directly
        return likesDao.countLikes(postId)
    }

    override fun isPostLiked(postId: String, userId: String): Flow<Boolean> {
        return likesDao.isLiked(postId, userId)
    }
}


