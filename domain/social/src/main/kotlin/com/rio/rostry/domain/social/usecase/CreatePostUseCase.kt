package com.rio.rostry.domain.social.usecase

import com.rio.rostry.core.model.Post
import com.rio.rostry.core.model.Result

/**
 * Use case for creating a new post.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines post creation use case interface.
 */
interface CreatePostUseCase {
    /**
     * Create a new post.
     * @param content The post content
     * @param images List of image URLs
     * @param videos List of video URLs
     * @return Result containing the created post or error
     */
    suspend operator fun invoke(
        content: String,
        images: List<String> = emptyList(),
        videos: List<String> = emptyList()
    ): Result<Post>
}
