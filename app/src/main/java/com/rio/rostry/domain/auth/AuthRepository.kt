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

    /**
     * Sign in using Google ID token.
     * This is the primary auth method for Firebase Free Tier (Spark Plan).
     */
    suspend fun signInWithGoogle(idToken: String): Resource<Unit>

    suspend fun signOut(): Resource<Unit>
}

sealed class AuthEvent {
    // New event for Google Sign-In
    object GoogleSignInSuccess : AuthEvent()
    data class GoogleSignInFailed(val message: String) : AuthEvent()
}
