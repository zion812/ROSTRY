package com.rio.rostry.domain.account.usecase

import com.rio.rostry.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing the current authenticated user.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines current user observation use case interface.
 */
interface GetCurrentUserUseCase {
    /**
     * Observe the current authenticated user.
     * @return Flow emitting the current user or null if not authenticated
     */
    operator fun invoke(): Flow<User?>
}
