package com.rio.rostry.domain.auth.repository

import android.app.Activity
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.OtpCode
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 * 
 * This provides a clean abstraction over Firebase Authentication,
 * using domain models for type safety and clear contracts.
 */
interface AuthRepository {
    /**
     * Flow emitting authentication state changes
     */
    val isAuthenticated: Flow<Boolean>
    
    /**
     * Start phone number verification process
     * 
     * @param activity Android activity for SafetyNet verification
     * @param phoneNumber Phone number in E.164 format
     * @return AuthResult with VerificationId if successful
     */
    suspend fun startPhoneVerification(
        activity: Activity,
        phoneNumber: PhoneNumber
    ): AuthResult<VerificationId>
    
    /**
     * Verify OTP code and sign in user
     * 
     * @param verificationId Verification ID from phone auth
     * @param otpCode 6-digit OTP code
     * @return AuthResult indicating success or failure
     */
    suspend fun verifyOtp(
        verificationId: VerificationId,
        otpCode: OtpCode
    ): AuthResult<Unit>
    
    /**
     * Resend OTP code to the same phone number
     * 
     * @param activity Android activity for SafetyNet verification
     * @return AuthResult with new VerificationId if successful
     */
    suspend fun resendVerificationCode(
        activity: Activity
    ): AuthResult<VerificationId>
    
    /**
     * Start phone linking process for existing user
     * 
     * @param activity Android activity for SafetyNet verification
     * @param phoneNumber Phone number to link
     * @return AuthResult with VerificationId if successful
     */
    suspend fun linkPhoneToCurrentUser(
        activity: Activity,
        phoneNumber: PhoneNumber
    ): AuthResult<VerificationId>
    
    /**
     * Complete phone linking with OTP verification
     * 
     * @param verificationId Verification ID from phone linking
     * @param otpCode 6-digit OTP code
     * @return AuthResult indicating success or failure
     */
    suspend fun verifyOtpAndLink(
        verificationId: VerificationId,
        otpCode: OtpCode
    ): AuthResult<Unit>
    
    /**
     * Sign out the current user
     * 
     * @return AuthResult indicating success or failure
     */
    suspend fun signOut(): AuthResult<Unit>
    
    /**
     * Get current phone number being verified (if any)
     * Used to restore state after process death
     */
    suspend fun getCurrentVerificationPhone(): PhoneNumber?
}
