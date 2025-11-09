package com.rio.rostry.domain.auth.usecase

import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for signing out the current user.
 * 
 * This handles:
 * - Firebase sign out
 * - Clearing local session
 * - Clearing auth state
 * 
 * @property authRepository Repository for authentication operations
 */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Sign out the current user
     * 
     * @return AuthResult indicating success or failure
     */
    suspend operator fun invoke(): AuthResult<Unit> {
        return authRepository.signOut()
    }
}
