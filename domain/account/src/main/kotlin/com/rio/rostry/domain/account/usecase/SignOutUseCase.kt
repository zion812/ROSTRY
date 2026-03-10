package com.rio.rostry.domain.account.usecase

import com.rio.rostry.core.model.Result

/**
 * Use case for signing out the current user.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines sign out use case interface.
 */
interface SignOutUseCase {
    /**
     * Sign out the current user.
     * @return Result indicating success or error
     */
    suspend operator fun invoke(): Result<Unit>
}
