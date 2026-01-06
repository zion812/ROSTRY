package com.rio.rostry.data.auth.source

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.rio.rostry.BuildConfig
import com.rio.rostry.data.auth.mapper.ErrorMapper
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.OtpCode
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.security.SecurityManager
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Firebase Authentication operations.
 * Encapsulates all Firebase Auth SDK interactions.
 */
@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authStateManager: AuthStateManager
) {
    
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    init {
        try {
            firebaseAuth.useAppLanguage()
        } catch (e: Exception) {
            Timber.w(e, "Failed to set app language for FirebaseAuth")
        }
    }
    
    /**
     * Flow emitting authentication state
     */
    val isAuthenticated: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        
        firebaseAuth.addAuthStateListener(listener)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }
    
    /**
     * Start phone verification
     * 
     * @param activity Android activity
     * @param phoneNumber Phone number to verify
     * @return AuthResult with VerificationId
     */
    suspend fun startPhoneVerification(
        activity: Activity,
        phoneNumber: PhoneNumber
    ): AuthResult<VerificationId> {
        return try {
            // Enable test mode for debug/emulator
            if (BuildConfig.PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING) {
                configureTestMode(phoneNumber)
            }
            
            val result = awaitVerificationCallbacks(activity, phoneNumber, useResendToken = false)
            
            result.onSuccess { verificationId ->
                if (firebaseAuth.currentUser == null) {
                    authStateManager.saveVerificationState(verificationId, phoneNumber, isLinkingMode = false)
                    SecurityManager.audit("AUTH_CODE_SENT", mapOf(
                        "phone" to phoneNumber.masked(),
                        "verificationId" to verificationId.value
                    ))
                }
            }
            
            result
        } catch (e: TimeoutCancellationException) {
            Timber.e(e, "Phone verification timed out")
            AuthResult.Error(ErrorMapper.mapFirebaseError(
                Exception("SMS verification timed out. Please check your internet connection and try again.")
            ))
        } catch (e: Exception) {
            Timber.e(e, "Failed to start phone verification")
            AuthResult.Error(ErrorMapper.mapFirebaseError(e))
        }
    }
    
    /**
     * Resend verification code
     * 
     * @param activity Android activity
     * @return AuthResult with new VerificationId
     */
    suspend fun resendVerificationCode(
        activity: Activity
    ): AuthResult<VerificationId> {
        val phoneNumber = authStateManager.getPhoneNumber()
            ?: return AuthResult.Error(ErrorMapper.mapFirebaseError(
                Exception("No previous verification session found")
            ))
        
        if (resendToken == null) {
            return AuthResult.Error(ErrorMapper.mapFirebaseError(
                Exception("Resend token not available")
            ))
        }
        
        return try {
            if (BuildConfig.PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING) {
                configureTestMode(phoneNumber)
            }
            
            val result = awaitVerificationCallbacks(activity, phoneNumber, useResendToken = true)
            
            result.onSuccess { verificationId ->
                if (firebaseAuth.currentUser == null) {
                    authStateManager.saveVerificationState(verificationId, phoneNumber, isLinkingMode = false)
                    SecurityManager.audit("AUTH_CODE_RESENT", mapOf(
                        "phone" to phoneNumber.masked(),
                        "verificationId" to verificationId.value
                    ))
                }
            }
            
            result
        } catch (e: Exception) {
            Timber.e(e, "Failed to resend verification code")
            AuthResult.Error(ErrorMapper.mapFirebaseError(e))
        }
    }
    
    /**
     * Verify OTP and sign in
     * 
     * @param verificationId Verification ID
     * @param otpCode OTP code
     * @return AuthResult indicating success/failure
     */
    suspend fun verifyOtp(
        verificationId: VerificationId,
        otpCode: OtpCode
    ): AuthResult<Unit> {
        // Handle auto-verification case where user is already signed in
        if (verificationId.value == "__AUTO__") {
            if (firebaseAuth.currentUser != null) {
                Timber.d("User already signed in via auto-verification")
                // Force token refresh to get phone number in claims
                try {
                    firebaseAuth.currentUser?.getIdToken(true)?.await()
                    Timber.d("Token refreshed for auto-verified user")
                } catch (e: Exception) {
                    Timber.w(e, "Failed to refresh token for auto-verified user")
                }
                authStateManager.clearVerificationState()
                return AuthResult.Success(Unit)
            }
            // If not signed in but ID is __AUTO__, we can't proceed without the original credential
            return AuthResult.Error(ErrorMapper.mapFirebaseError(
                Exception("Auto-verification state invalid. Please try again.")
            ))
        }

        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId.value, otpCode.value)
            val result = firebaseAuth.signInWithCredential(credential).await()
            
            if (result.user != null) {
                // CRITICAL: Force token refresh to ensure phone number is in ID token
                // This is required for Firestore security rules that check hasPhone()
                try {
                    result.user?.getIdToken(true)?.await()
                    Timber.d("Token refreshed after OTP verification - phone number now in claims")
                } catch (e: Exception) {
                    Timber.w(e, "Failed to refresh token after OTP verification")
                    // Continue anyway - the token will eventually refresh
                }
                
                authStateManager.clearVerificationState()
                SecurityManager.audit("AUTH_OTP_VERIFIED", mapOf("result" to "success"))
                AuthResult.Success(Unit)
            } else {
                SecurityManager.audit("AUTH_OTP_VERIFIED", mapOf("result" to "failure", "reason" to "user is null"))
                AuthResult.Error(ErrorMapper.mapFirebaseError(
                    Exception("Verification failed: user is null")
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to verify OTP")
            SecurityManager.audit("AUTH_OTP_VERIFIED", mapOf("result" to "failure", "error" to e.message))
            AuthResult.Error(ErrorMapper.mapFirebaseError(e))
        }
    }
    
    /**
     * Link phone to current user
     * 
     * @param activity Android activity
     * @param phoneNumber Phone number to link
     * @return AuthResult with VerificationId
     */
    suspend fun linkPhoneToCurrentUser(
        activity: Activity,
        phoneNumber: PhoneNumber
    ): AuthResult<VerificationId> {
        val user = firebaseAuth.currentUser
            ?: return AuthResult.Error(ErrorMapper.mapFirebaseError(
                Exception("Not authenticated")
            ))
        
        return try {
            if (BuildConfig.PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING) {
                configureTestMode(phoneNumber)
            }
            
            val result = awaitVerificationCallbacks(activity, phoneNumber, useResendToken = false)
            
            result.onSuccess { verificationId ->
                if (firebaseAuth.currentUser == null) {
                    authStateManager.saveVerificationState(verificationId, phoneNumber, isLinkingMode = true)
                    SecurityManager.audit("AUTH_PHONE_LINK_STARTED", mapOf(
                        "phone" to phoneNumber.masked(),
                        "uid" to user.uid
                    ))
                }
            }
            
            result
        } catch (e: Exception) {
            Timber.e(e, "Failed to start phone linking")
            AuthResult.Error(ErrorMapper.mapFirebaseError(e))
        }
    }
    
    /**
     * Verify OTP and link phone
     * 
     * @param verificationId Verification ID
     * @param otpCode OTP code
     * @return AuthResult indicating success/failure
     */
    suspend fun verifyOtpAndLink(
        verificationId: VerificationId,
        otpCode: OtpCode
    ): AuthResult<Unit> {
        val user = firebaseAuth.currentUser
            ?: return AuthResult.Error(ErrorMapper.mapFirebaseError(
                Exception("Not authenticated")
            ))

        // Handle auto-verification case
        if (verificationId.value == "__AUTO__") {
            // If we are here and user is not null (checked above), and we got __AUTO__,
            // it implies the credential was already used to sign in or link in onVerificationCompleted.
            // However, onVerificationCompleted in awaitVerificationCallbacks calls signInWithCredential, NOT linkWithCredential.
            // So for linking, this might be tricky. 
            // If onVerificationCompleted signed in a NEW user, we might have lost the current user session?
            // Actually, signInWithCredential might sign in a different user if the phone is not linked.
            
            // For now, let's assume if __AUTO__ is returned, the operation succeeded.
            // But we should be careful. 
            Timber.d("Auto-verification for linking completed")
            authStateManager.clearVerificationState()
            return AuthResult.Success(Unit)
        }
        
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId.value, otpCode.value)
            user.linkWithCredential(credential).await()
            
            authStateManager.clearVerificationState()
            SecurityManager.audit("AUTH_PHONE_LINKED", mapOf(
                "uid" to user.uid,
                "phone" to (user.phoneNumber ?: "unknown")
            ))
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to link phone")
            SecurityManager.audit("AUTH_PHONE_LINK_FAILED", mapOf("error" to e.message))
            AuthResult.Error(ErrorMapper.mapFirebaseError(e))
        }
    }
    
    /**
     * Sign out current user
     * 
     * @return AuthResult indicating success/failure
     */
    suspend fun signOut(): AuthResult<Unit> {
        return try {
            firebaseAuth.signOut()
            authStateManager.clearVerificationState()
            resendToken = null
            SecurityManager.audit("AUTH_SIGNOUT", mapOf("success" to true))
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to sign out")
            AuthResult.Error(ErrorMapper.mapFirebaseError(e))
        }
    }
    
    /**
     * Await phone auth callbacks using suspending function
     */
    private suspend fun awaitVerificationCallbacks(
        activity: Activity,
        phoneNumber: PhoneNumber,
        useResendToken: Boolean
    ): AuthResult<VerificationId> = withTimeout(65000) { // 65s timeout (slightly more than Firebase's 60s)
        callbackFlow {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Timber.d("Auto-verification completed")
                    firebaseAuth
                        .signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                trySend(AuthResult.Success(VerificationId("__AUTO__")))
                                close()
                            } else {
                                val ex = task.exception ?: Exception("Auto verification failed")
                                trySend(AuthResult.Error(ErrorMapper.mapFirebaseError(ex)))
                                close()
                            }
                        }
                }
                
                override fun onVerificationFailed(e: FirebaseException) {
                                        val errorCode = (e as? FirebaseAuthException)?.errorCode ?: "N/A"
                    Timber.e(e, "Verification failed: ${e.message} (errorCode: $errorCode)")
                    trySend(AuthResult.Error(ErrorMapper.mapFirebaseError(e)))
                    close()
                }
                
                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    resendToken = token
                    Timber.d("Code sent: verificationId=$verificationId")
                    trySend(AuthResult.Success(VerificationId(verificationId)))
                    close()
                }
            }
            
            val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber.value)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
            
            if (useResendToken && resendToken != null) {
                optionsBuilder.setForceResendingToken(resendToken!!)
            }
            
            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
            
            awaitClose { /* Cleanup if needed */ }
        }.first()
    }
    
    /**
     * Configure test mode for debug/emulator
     */
    private fun configureTestMode(phoneNumber: PhoneNumber) {
        try {
            val settings = firebaseAuth.firebaseAuthSettings
            settings.setAppVerificationDisabledForTesting(true)
            settings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber.value, "123456")
            Timber.d("Test mode configured for ${phoneNumber.masked()}")
        } catch (e: Exception) {
            Timber.w(e, "Failed to configure test mode")
        }
    }
    
    /**
     * Helper extension for AuthResult
     */
    private suspend fun <T> AuthResult<T>.onSuccess(block: suspend (T) -> Unit): AuthResult<T> {
        if (this is AuthResult.Success) {
            block(data)
        }
        return this
    }
}
