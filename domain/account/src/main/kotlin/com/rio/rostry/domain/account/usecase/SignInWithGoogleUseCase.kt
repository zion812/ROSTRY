package com.rio.rostry.domain.account.usecase

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User

/**
 * Use case for signing in with Google credentials.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines Google authentication use case interface.
 */
interface SignInWithGoogleUseCase {
    /**
     * Sign in with Google ID token.
     * @param idToken The Google ID token
     * @return Result containing the authenticated user or error
     */
    suspend operator fun invoke(idToken: String): Result<User>
}
