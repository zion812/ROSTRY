package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.LikesDao
import com.rio.rostry.data.repository.social.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface LikesRepository {
    suspend fun likePost(postId: String)
    suspend fun unlikePost(postId: String)
    fun getLikeCount(postId: String): Flow<Int>
    fun isPostLiked(postId: String, userId: String): Flow<Boolean>
}

@Singleton
class LikesRepositoryImpl @Inject constructor(
    private val socialRepository: SocialRepository,
    private val userRepository: UserRepository,
    private val likesDao: LikesDao
) : LikesRepository {

    override suspend fun likePost(postId: String) {
        val user = userRepository.getCurrentUser().first().data ?: throw IllegalStateException("No current user")
        socialRepository.like(postId, user.userId)
    }

    override suspend fun unlikePost(postId: String) {
        val user = userRepository.getCurrentUser().first().data ?: throw IllegalStateException("No current user")
        socialRepository.unlike(postId, user.userId)
    }

    override fun getLikeCount(postId: String): Flow<Int> {
        return socialRepository.countLikes(postId)
    }

    override fun isPostLiked(postId: String, userId: String): Flow<Boolean> {
        return likesDao.isLiked(postId, userId)
    }
}