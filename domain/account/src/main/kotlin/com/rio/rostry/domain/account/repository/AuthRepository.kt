package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for authentication operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines authentication interfaces without implementation details.
 */
interface AuthRepository {
    /**
     * Observe the current authenticated user.
     * @return Flow emitting the current user or null if not authenticated
     */
    fun observeCurrentUser(): Flow<User?>
    
    /**
     * Sign in with phone number and OTP.
     * @param phoneNumber The phone number to authenticate
     * @param otp The one-time password
     * @return Result containing the authenticated user or error
     */
    suspend fun signInWithPhone(phoneNumber: String, otp: String): Result<User>
    
    /**
     * Sign in with Google credentials.
     * @param idToken The Google ID token
     * @return Result containing the authenticated user or error
     */
    suspend fun signInWithGoogle(idToken: String): Result<User>
    
    /**
     * Sign out the current user.
     * @return Result indicating success or error
     */
    suspend fun signOut(): Result<Unit>
    
    /**
     * Request OTP for phone number.
     * @param phoneNumber The phone number to send OTP to
     * @return Result containing verification ID or error
     */
    suspend fun requestOtp(phoneNumber: String): Result<String>
}
