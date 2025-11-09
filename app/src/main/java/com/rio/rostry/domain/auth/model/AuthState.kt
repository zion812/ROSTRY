package com.rio.rostry.domain.auth.model

/**
 * Sealed class representing different authentication states in the app.
 * This provides a clear state machine for authentication flow.
 */
sealed class AuthState {
    /**
     * User is not authenticated
     */
    object Unauthenticated : AuthState()
    
    /**
     * User is authenticated but profile is incomplete
     */
    data class Authenticated(
        val userId: String,
        val needsProfileCompletion: Boolean = false
    ) : AuthState()
    
    /**
     * Verification in progress
     */
    data class VerificationPending(
        val verificationId: String,
        val phoneNumber: String
    ) : AuthState()
    
    /**
     * Phone linking required for existing user (Google/Email sign-in)
     */
    data class PhoneLinkingRequired(
        val userId: String,
        val provider: String
    ) : AuthState()
}

/**
 * Sealed class representing phone authentication flow states
 */
sealed class PhoneAuthState {
    /**
     * Initial state
     */
    object Idle : PhoneAuthState()
    
    /**
     * Sending OTP to phone number
     */
    object SendingCode : PhoneAuthState()
    
    /**
     * OTP sent successfully
     */
    data class CodeSent(
        val verificationId: String,
        val phoneNumber: String
    ) : PhoneAuthState()
    
    /**
     * Error occurred during phone verification
     */
    data class Error(val error: UserFriendlyError) : PhoneAuthState()
}

/**
 * Sealed class representing OTP verification states
 */
sealed class OtpVerificationState {
    /**
     * Waiting for user to enter OTP
     */
    object Idle : OtpVerificationState()
    
    /**
     * Verifying the entered OTP
     */
    object Verifying : OtpVerificationState()
    
    /**
     * OTP verified successfully
     */
    object Success : OtpVerificationState()
    
    /**
     * Verification failed
     */
    data class Error(
        val error: UserFriendlyError,
        val attemptsRemaining: Int
    ) : OtpVerificationState()
    
    /**
     * Resending OTP code
     */
    object Resending : OtpVerificationState()
}
