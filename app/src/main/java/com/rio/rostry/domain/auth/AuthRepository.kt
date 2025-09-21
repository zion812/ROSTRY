package com.rio.rostry.domain.auth

import android.app.Activity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Emits true when a user is authenticated and session is valid
    val isAuthenticated: Flow<Boolean>

    // Emits auth events such as CodeSent(verificationId), VerificationFailed, AutoVerified
    val events: Flow<AuthEvent>

    // Indian phone number verification (E.164 expected, e.g., +91XXXXXXXXXX)
    suspend fun startPhoneVerification(activity: Activity, phoneE164: String): Resource<Unit>

    suspend fun verifyOtp(verificationId: String, otpCode: String): Resource<Unit>

    suspend fun resendVerificationCode(activity: Activity): Resource<Unit>

    suspend fun signOut(): Resource<Unit>
}

sealed class AuthEvent {
    data class CodeSent(val verificationId: String) : AuthEvent()
    data class VerificationFailed(val message: String) : AuthEvent()
    object AutoVerified : AuthEvent()
}
