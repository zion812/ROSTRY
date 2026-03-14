package com.rio.rostry.data.account.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import com.rio.rostry.domain.account.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implementation of AuthRepository using Firebase Authentication.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    // Store verification IDs for phone auth (in production, use secure storage)
    private val verificationIds = mutableMapOf<String, String>()

    override fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            val user = firebaseUser?.let {
                User(
                    id = it.uid,
                    email = it.email,
                    displayName = it.displayName,
                    photoUrl = it.photoUrl?.toString(),
                    phoneNumber = it.phoneNumber
                )
            }
            trySend(user)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithPhone(phoneNumber: String, otp: String): Result<User> {
        return try {
            val verificationId = verificationIds[phoneNumber]
                ?: return Result.Error(Exception("No verification pending for this number. Please request OTP first."))

            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(Exception("Failed to sign in with phone"))

            // Clean up verification ID after successful sign in
            verificationIds.remove(phoneNumber)

            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString(),
                phoneNumber = firebaseUser.phoneNumber
            )
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    phoneNumber = firebaseUser.phoneNumber
                )
                Result.Success(user)
            } else {
                Result.Error(Exception("Failed to get user after sign in"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun requestOtp(phoneNumber: String): Result<String> {
        return try {
            val verificationId = suspendCancellableCoroutine { continuation ->
                val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        val storedVerificationId = credential.smsCode ?: ""
                        verificationIds[phoneNumber] = storedVerificationId
                        if (continuation.isActive) {
                            continuation.resume(storedVerificationId)
                        }
                    }

                    override fun onVerificationFailed(exception: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(exception)
                        }
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        verificationIds[phoneNumber] = verificationId
                        if (continuation.isActive) {
                            continuation.resume(verificationId)
                        }
                    }
                }

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60L,
                    TimeUnit.SECONDS,
                    null,
                    callbacks
                )
            }

            Result.Success(verificationId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}