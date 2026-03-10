package com.rio.rostry.domain.social.usecase

import com.rio.rostry.core.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing feed posts.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines feed observation use case interface.
 */
interface GetFeedPostsUseCase {
    /**
     * Observe feed posts for the current user.
     * @return Flow emitting list of posts
     */
    operator fun invoke(): Flow<List<Post>>
}
