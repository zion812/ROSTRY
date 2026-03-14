package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for authentication operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines authentication interfaces without implementation details.
 * 
 * Supports:
 * - Google Sign-In
 * - Email/Password authentication
 * - Email verification with OTP
 * - Password reset
 * - Guest session management
 */
interface AuthRepository {
    /**
     * Observe the current authenticated user.
     * @return Flow emitting the current user or null if not authenticated
     */
    fun observeCurrentUser(): Flow<User?>
    
    // ==================== FIREBASE AUTH ====================
    
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
    
    // ==================== EMAIL/PASSWORD AUTH ====================
    
    /**
     * Sign up with email and password.
     * Creates a new Firebase user and returns the user object.
     * 
     * @param email User's email address
     * @param password User's password (min 6 characters for Firebase)
     * @param displayName Optional display name for the user
     * @return Result containing the created user or error
     */
    suspend fun signUpWithEmail(email: String, password: String, displayName: String? = null): Result<User>
    
    /**
     * Sign in with email and password.
     * 
     * @param email User's email address
     * @param password User's password
     * @return Result containing the authenticated user or error
     */
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    
    /**
     * Send a password reset email to the user.
     * 
     * @param email The email address to send reset instructions
     * @return Result indicating success or error
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    // ==================== EMAIL VERIFICATION ====================
    
    /**
     * Send email verification to the current user.
     * Uses Firebase's built-in email verification.
     * 
     * @return Result containing verification email sent status or error
     */
    suspend fun sendEmailVerification(): Result<Unit>
    
    /**
     * Check if the current user's email is verified.
     * 
     * @return Result containing verification status or error
     */
    suspend fun isEmailVerified(): Result<Boolean>
    
    /**
     * Re-authenticate user before sensitive operations.
     * Required for email/password change or account deletion.
     * 
     * @param providerId The provider ID (e.g., "google.com", "password")
     * @param idToken The ID token for re-authentication
     * @return Result indicating success or error
     */
    suspend fun reauthenticate(providerId: String, idToken: String): Result<Unit>
    
    // ==================== PHONE AUTH (Deprecated, kept for migration) ====================
    
    /**
     * Sign in with phone number and OTP.
     * @param phoneNumber The phone number to authenticate
     * @param otp The one-time password
     * @return Result containing the authenticated user or error
     * @deprecated Use Google or Email sign-in instead
     */
    @Deprecated("Phone auth is deprecated. Use Google or Email sign-in instead.")
    suspend fun signInWithPhone(phoneNumber: String, otp: String): Result<User>
    
    /**
     * Request OTP for phone number.
     * @param phoneNumber The phone number to send OTP to
     * @return Result containing verification ID or error
     * @deprecated Phone auth is deprecated
     */
    @Deprecated("Phone auth is deprecated. Use Google or Email sign-in instead.")
    suspend fun requestOtp(phoneNumber: String): Result<String>
}
