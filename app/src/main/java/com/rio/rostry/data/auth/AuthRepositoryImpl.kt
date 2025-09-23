package com.rio.rostry.data.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.rio.rostry.domain.auth.AuthEvent
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    private val _isAuthenticated = MutableStateFlow(isFirebaseUserValid())
    override val isAuthenticated: Flow<Boolean> = _isAuthenticated

    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 8)
    override val events: Flow<AuthEvent> = _events

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var lastPhoneE164: String? = null

    override suspend fun startPhoneVerification(activity: Activity, phoneE164: String): Resource<Unit> {
        return try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                _isAuthenticated.value = isFirebaseUserValid()
                                _events.tryEmit(AuthEvent.AutoVerified)
                            } else {
                                Timber.e(task.exception, "Auto sign-in failed")
                            }
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Timber.e(e, "Phone verification failed")
                    _events.tryEmit(AuthEvent.VerificationFailed(mapError(e)))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    storedVerificationId = verificationId
                    resendToken = token
                    Timber.d("OTP code sent; verificationId stored")
                    _events.tryEmit(AuthEvent.CodeSent(verificationId))
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneE164)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            lastPhoneE164 = phoneE164
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "startPhoneVerification exception")
            Resource.Error("Failed to start verification: ${e.message}")
        }
    }

    override suspend fun verifyOtp(verificationId: String, otpCode: String): Resource<Unit> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            val result = firebaseAuth.signInWithCredential(credential).await()
            if (result.user != null) {
                _isAuthenticated.value = isFirebaseUserValid()
                Resource.Success(Unit)
            } else {
                Resource.Error("Verification failed: user is null")
            }
        } catch (e: Exception) {
            Timber.e(e, "verifyOtp exception")
            Resource.Error("Failed to verify OTP: ${e.message}")
        }
    }

    override suspend fun resendVerificationCode(activity: Activity): Resource<Unit> {
        val phone = lastPhoneE164
        val token = resendToken
        if (phone == null || token == null) {
            return Resource.Error("No previous verification session found.")
        }
        return try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                _isAuthenticated.value = isFirebaseUserValid()
                                _events.tryEmit(AuthEvent.AutoVerified)
                            } else {
                                Timber.e(task.exception, "Auto sign-in failed (resend)")
                            }
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Timber.e(e, "Phone verification failed (resend)")
                    _events.tryEmit(AuthEvent.VerificationFailed(mapError(e)))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    storedVerificationId = verificationId
                    resendToken = token
                    Timber.d("OTP code re-sent; verificationId updated")
                    _events.tryEmit(AuthEvent.CodeSent(verificationId))
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(token)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "resendVerificationCode exception")
            Resource.Error("Failed to resend code: ${e.message}")
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            _isAuthenticated.value = isFirebaseUserValid()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "signOut exception")
            Resource.Error("Failed to sign out: ${e.message}")
        }
    }

    private fun isFirebaseUserValid(): Boolean = firebaseAuth.currentUser != null

    private fun mapError(e: Exception): String {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid phone number or code. Please check and try again."
            is FirebaseAuthException -> when (e.errorCode) {
                "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please wait a while before retrying."
                else -> e.message ?: "Authentication error"
            }
            else -> e.message ?: "Network or server error. Please try again."
        }
    }
}
