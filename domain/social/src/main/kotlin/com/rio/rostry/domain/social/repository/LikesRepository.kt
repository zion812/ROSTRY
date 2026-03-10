package com.rio.rostry.domain.social.repository

import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing post likes.
 * 
 * Handles liking and unliking posts, and tracking like counts.
 */
interface LikesRepository {
    /**
     * Likes a post.
     * 
     * @param postId The post ID
     * @return Result indicating success or failure
     */
    suspend fun likePost(postId: String): Result<Unit>
    
    /**
     * Unlikes a post.
     * 
     * @param postId The post ID
     * @return Result indicating success or failure
     */
    suspend fun unlikePost(postId: String): Result<Unit>
    
    /**
     * Gets the like count for a post.
     * 
     * @param postId The post ID
     * @return Flow emitting the like count
     */
    fun getLikeCount(postId: String): Flow<Int>
    
    /**
     * Checks if a post is liked by a user.
     * 
     * @param postId The post ID
     * @param userId The user ID
     * @return Flow emitting true if liked, false otherwise
     */
    fun isPostLiked(postId: String, userId: String): Flow<Boolean>
}

