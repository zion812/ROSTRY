package com.rio.rostry.domain.social.repository

import com.rio.rostry.core.model.Post
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for social feed operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface SocialFeedRepository {
    /**
     * Get social feed posts.
     * @return Flow of posts
     */
    fun getFeedPosts(): Flow<List<Post>>

    /**
     * Get post by ID.
     * @param postId The post ID
     * @return Result containing the post or error
     */
    suspend fun getPostById(postId: String): Result<Post>

    /**
     * Create a new post.
     * @param post The post to create
     * @return Result containing the created post or error
     */
    suspend fun createPost(post: Post): Result<Post>

    /**
     * Update a post.
     * @param post The updated post
     * @return Result indicating success or error
     */
    suspend fun updatePost(post: Post): Result<Unit>

    /**
     * Delete a post.
     * @param postId The post ID to delete
     * @return Result indicating success or error
     */
    suspend fun deletePost(postId: String): Result<Unit>

    /**
     * Like a post.
     * @param postId The post ID
     * @return Result indicating success or error
     */
    suspend fun likePost(postId: String): Result<Unit>

    /**
     * Get posts by user ID.
     * @param userId The user ID
     * @return Flow of user's posts
     */
    fun getPostsByUser(userId: String): Flow<List<Post>>
}
