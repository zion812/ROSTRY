package com.rio.rostry.data.account.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
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
 * 
 * Supports:
 * - Google Sign-In
 * - Email/Password authentication
 * - Email verification
 * - Password reset
 * - Legacy phone auth (deprecated)
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    // Store verification IDs for phone auth (in production, use secure storage)
    private val verificationIds = mutableMapOf<String, String>()

    // ==================== OBSERVER ====================

    override fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            val user = firebaseUser?.let {
                User(
                    id = it.uid,
                    email = it.email,
                    displayName = it.displayName,
                    photoUrl = it.photoUrl?.toString(),
                    phoneNumber = it.phoneNumber,
                    isEmailVerified = it.isEmailVerified
                )
            }
            trySend(user)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    // ==================== GOOGLE SIGN-IN ====================

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
                    phoneNumber = firebaseUser.phoneNumber,
                    isEmailVerified = firebaseUser.isEmailVerified
                )
                Result.Success(user)
            } else {
                Result.Error(Exception("Failed to get user after sign in"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ==================== EMAIL/PASSWORD AUTH ====================

    override suspend fun signUpWithEmail(email: String, password: String, displayName: String?): Result<User> {
        return try {
            // Validate inputs
            if (email.isBlank()) {
                return Result.Error(IllegalArgumentException("Email cannot be empty"))
            }
            if (password.length < 6) {
                return Result.Error(IllegalArgumentException("Password must be at least 6 characters"))
            }

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Set display name if provided
                if (!displayName.isNullOrBlank()) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    firebaseUser.updateProfile(profileUpdates).await()
                }

                // Send email verification
                firebaseUser.sendEmailVerification().await()

                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = displayName ?: firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    phoneNumber = firebaseUser.phoneNumber,
                    isEmailVerified = firebaseUser.isEmailVerified
                )
                Result.Success(user)
            } else {
                Result.Error(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            // Handle specific Firebase auth errors
            val errorMessage = when (e) {
                is FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 6 characters."
                is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                is FirebaseAuthUserCollisionException -> "An account already exists with this email"
                else -> e.message ?: "Sign up failed"
            }
            Result.Error(Exception(errorMessage))
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            if (email.isBlank()) {
                return Result.Error(IllegalArgumentException("Email cannot be empty"))
            }
            if (password.isBlank()) {
                return Result.Error(IllegalArgumentException("Password cannot be empty"))
            }

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    phoneNumber = firebaseUser.phoneNumber,
                    isEmailVerified = firebaseUser.isEmailVerified
                )
                Result.Success(user)
            } else {
                Result.Error(Exception("Failed to sign in"))
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseAuthInvalidUserException -> "No account found with this email"
                is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                else -> e.message ?: "Sign in failed"
            }
            Result.Error(Exception(errorMessage))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            if (email.isBlank()) {
                return Result.Error(IllegalArgumentException("Email cannot be empty"))
            }

            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseAuthInvalidUserException -> "No account found with this email"
                else -> e.message ?: "Failed to send reset email"
            }
            Result.Error(Exception(errorMessage))
        }
    }

    // ==================== EMAIL VERIFICATION ====================

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
                ?: return Result.Error(Exception("No user signed in"))

            firebaseUser.sendEmailVerification().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun isEmailVerified(): Result<Boolean> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
                ?: return Result.Error(Exception("No user signed in"))
            
            // Reload user to get latest verification status
            firebaseUser.reload().await()
            Result.Success(firebaseUser.isEmailVerified)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun reauthenticate(providerId: String, idToken: String): Result<Unit> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
                ?: return Result.Error(Exception("No user signed in"))

            val credential = when (providerId) {
                "google.com" -> GoogleAuthProvider.getCredential(idToken, null)
                "password" -> {
                    // For password re-auth, we need the password
                    // This is typically handled by showing a password dialog first
                    return Result.Error(Exception("Password re-authentication requires password input"))
                }
                else -> return Result.Error(Exception("Unsupported provider: $providerId"))
            }

            firebaseUser.reauthenticate(credential).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ==================== SIGN OUT ====================

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ==================== PHONE AUTH (Deprecated) ====================

    @Deprecated("Phone auth is deprecated. Use Google or Email sign-in instead.")
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
                phoneNumber = firebaseUser.phoneNumber,
                isEmailVerified = firebaseUser.isEmailVerified
            )
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    @Deprecated("Phone auth is deprecated. Use Google or Email sign-in instead.")
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