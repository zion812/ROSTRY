package com.rio.rostry.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.rio.rostry.data.models.User
import com.rio.rostry.data.models.UserType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreUserRepository = FirestoreUserRepository()

    // Sign up a new user with email and password
    suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            // Validate inputs
            if (email.isBlank() || password.isBlank() || name.isBlank()) {
                return Result.failure(IllegalArgumentException("Email, password, and name are required"))
            }
            
            if (password.length < 6) {
                return Result.failure(IllegalArgumentException("Password must be at least 6 characters long"))
            }
            
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                // Create a User object from FirebaseUser
                val user = User(
                    userId = firebaseUser.uid,
                    name = name,
                    email = firebaseUser.email ?: "",
                    phone = firebaseUser.phoneNumber ?: "",
                    type = UserType.GENERAL, // Default type, can be updated later
                    profilePictureUrl = firebaseUser.photoUrl?.toString(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                // Save user to Firestore
                firestoreUserRepository.createUser(user).collect { result ->
                    if (result.isFailure) {
                        // Handle error if needed
                        println("Failed to save user to Firestore: ${result.exceptionOrNull()?.message}")
                    }
                }
                
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(AuthenticationException(AuthErrorType.EMAIL_ALREADY_EXISTS, "An account already exists with this email address"))
        } catch (e: Exception) {
            Result.failure(AuthenticationException(AuthErrorType.UNKNOWN_ERROR, "Sign up failed: ${e.message}"))
        }
    }

    // Log in an existing user with email and password
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Validate inputs
            if (email.isBlank() || password.isBlank()) {
                return Result.failure(IllegalArgumentException("Email and password are required"))
            }
            
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                // Try to get user from Firestore
                var user: User? = null
                firestoreUserRepository.getUser(firebaseUser.uid).collect { result ->
                    if (result.isSuccess) {
                        user = result.getOrNull()
                    }
                }
                
                // If user doesn't exist in Firestore, create a new one
                if (user == null) {
                    val newUser = User(
                        userId = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "User",
                        email = firebaseUser.email ?: "",
                        phone = firebaseUser.phoneNumber ?: "",
                        type = UserType.GENERAL, // Default type, can be updated later
                        profilePictureUrl = firebaseUser.photoUrl?.toString(),
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    // Save user to Firestore
                    firestoreUserRepository.createUser(newUser).collect { result ->
                        if (result.isFailure) {
                            // Handle error if needed
                            println("Failed to save user to Firestore: ${result.exceptionOrNull()?.message}")
                        }
                    }
                    
                    Result.success(newUser)
                } else {
                    Result.success(user!!)
                }
            } else {
                Result.failure(AuthenticationException(AuthErrorType.INVALID_CREDENTIALS, "Failed to login"))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(AuthenticationException(AuthErrorType.INVALID_CREDENTIALS, "Invalid email or password"))
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(AuthenticationException(AuthErrorType.USER_NOT_FOUND, "No user found with this email"))
        } catch (e: Exception) {
            Result.failure(AuthenticationException(AuthErrorType.UNKNOWN_ERROR, "Login failed: ${e.message}"))
        }
    }

    // Log out the current user
    fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AuthenticationException(AuthErrorType.UNKNOWN_ERROR, "Logout failed: ${e.message}"))
        }
    }

    // Check if a user is currently logged in
    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            User(
                userId = it.uid,
                name = it.displayName ?: "User",
                email = it.email ?: "",
                phone = it.phoneNumber ?: "",
                type = UserType.GENERAL, // Default type, can be updated later
                profilePictureUrl = it.photoUrl?.toString(),
                createdAt = it.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                updatedAt = it.metadata?.lastSignInTimestamp ?: System.currentTimeMillis()
            )
        }
    }

    // Observe authentication state changes
    fun observeAuthState(): Flow<AuthState> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(
                if (user != null) {
                    AuthState.Authenticated(user.uid)
                } else {
                    AuthState.Unauthenticated
                }
            )
        }
        
        firebaseAuth.addAuthStateListener(authStateListener)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    // Refresh token
    suspend fun refreshToken(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.reload()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AuthenticationException(AuthErrorType.UNKNOWN_ERROR, "Token refresh failed: ${e.message}"))
        }
    }
}

// Authentication state sealed class
sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val userId: String) : AuthState()
}

// Authentication error types
enum class AuthErrorType {
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    EMAIL_ALREADY_EXISTS,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}

// Custom authentication exception
class AuthenticationException(
    val errorType: AuthErrorType,
    message: String
) : Exception(message)