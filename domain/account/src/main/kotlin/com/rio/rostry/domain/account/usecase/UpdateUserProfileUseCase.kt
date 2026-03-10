package com.rio.rostry.domain.account.usecase

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User

/**
 * Use case for updating user profile information.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines user profile update use case interface.
 */
interface UpdateUserProfileUseCase {
    /**
     * Update user profile.
     * @param user The user with updated information
     * @return Result indicating success or error
     */
    suspend operator fun invoke(user: User): Result<Unit>
}
