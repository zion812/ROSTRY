package com.rio.rostry.domain.auth

import android.app.Activity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    // Emits true when a user is authenticated and session is valid
    val isAuthenticated: Flow<Boolean>

    // Emits auth events such as CodeSent(verificationId), VerificationFailed, AutoVerified
    val events: Flow<AuthEvent>

    val currentVerificationId: StateFlow<String?>

    val currentPhoneE164: StateFlow<String?>

    // ============================================================
    // Google Sign-In (Free Tier Primary Auth)
    // ============================================================
    
    /**
     * Sign in using Google ID token.
     * This is the primary auth method for Firebase Free Tier (Spark Plan).
     */
    suspend fun signInWithGoogle(idToken: String): Resource<Unit>

    // ============================================================
    // Phone Auth (DISABLED on Free Tier - Requires Blaze Plan)
    // ============================================================
    
    // Indian phone number verification (E.164 expected, e.g., +91XXXXXXXXXX)
    @Deprecated("Phone Auth disabled on Free Tier. Use signInWithGoogle instead.", level = DeprecationLevel.WARNING)
    suspend fun startPhoneVerification(activity: Activity, phoneE164: String): Resource<Unit>

    @Deprecated("Phone Auth disabled on Free Tier. Use signInWithGoogle instead.", level = DeprecationLevel.WARNING)
    suspend fun verifyOtp(verificationId: String, otpCode: String): Resource<Unit>

    @Deprecated("Phone Auth disabled on Free Tier. Use signInWithGoogle instead.", level = DeprecationLevel.WARNING)
    suspend fun resendVerificationCode(activity: Activity): Resource<Unit>

    suspend fun signOut(): Resource<Unit>

    // Link phone number to existing authenticated user (for Google/Email sign-ins)
    @Deprecated("Phone Auth disabled on Free Tier.", level = DeprecationLevel.WARNING)
    suspend fun linkPhoneToCurrentUser(activity: Activity, phoneE164: String): Resource<Unit>

    // Verify OTP and complete phone linking
    @Deprecated("Phone Auth disabled on Free Tier.", level = DeprecationLevel.WARNING)
    suspend fun verifyOtpAndLink(verificationId: String, otpCode: String): Resource<Unit>
}

sealed class AuthEvent {
    data class CodeSent(val verificationId: String) : AuthEvent()
    data class VerificationFailed(val message: String) : AuthEvent()
    object AutoVerified : AuthEvent()
    data class PhoneLinkSuccess(val phoneNumber: String) : AuthEvent()
    data class PhoneLinkFailed(val message: String) : AuthEvent()
    // New event for Google Sign-In
    object GoogleSignInSuccess : AuthEvent()
    data class GoogleSignInFailed(val message: String) : AuthEvent()
}