package com.rio.rostry.repository

import android.util.Log
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

class DebugAuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreUserRepository = FirestoreUserRepository()
    private val TAG = "DebugAuthRepository"

    // Sign up a new user with email and password
    suspend fun signUp(email: String, password: String, name: String): Result<User> {
        Log.d(TAG, "Attempting to sign up user with email: $email")
        return try {
            // Validate inputs
            if (email.isBlank() || password.isBlank() || name.isBlank()) {
                Log.w(TAG, "Sign up failed: Required fields are blank")
                return Result.failure(IllegalArgumentException("Email, password, and name are required"))
            }
            
            if (password.length < 6) {
                Log.w(TAG, "Sign up failed: Password too short")
                return Result.failure(IllegalArgumentException("Password must be at least 6 characters long"))
            }
            
            Log.d(TAG, "Creating user with Firebase Auth")
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                Log.d(TAG, "Firebase user created successfully with UID: ${firebaseUser.uid}")
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
                
                Log.d(TAG, "Saving user to Firestore")
                // Save user to Firestore
                firestoreUserRepository.createUser(user).collect { result ->
                    if (result.isFailure) {
                        Log.e(TAG, "Failed to save user to Firestore: ${result.exceptionOrNull()?.message}")
                    } else {
                        Log.d(TAG, "User saved to Firestore successfully")
                    }
                }
                
                Log.d(TAG, "Sign up completed successfully")
                Result.success(user)
            } else {
                Log.e(TAG, "Failed to create user - Firebase returned null user")
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e(TAG, "Sign up failed: Email already exists", e)
            Result.failure(AuthenticationException(AuthErrorType.EMAIL_ALREADY_EXISTS, "An account already exists with this email address"))
        } catch (e: Exception) {
            Log.e(TAG, "Sign up failed: Unknown error", e)
            Result.failure(AuthenticationException(AuthErrorType.UNKNOWN_ERROR, "Sign up failed: ${e.message}"))
        }
    }

    // Log in an existing user with email and password
    suspend fun login(email: String, password: String): Result<User> {
        Log.d(TAG, "Attempting to log in user with email: $email")
        return try {
            // Validate inputs
            if (email.isBlank() || password.isBlank()) {
                Log.w(TAG, "Login failed: Email or password is blank")
                return Result.failure(IllegalArgumentException("Email and password are required"))
            }
            
            Log.d(TAG, "Signing in with Firebase Auth")
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                Log.d(TAG, "Firebase login successful for user UID: ${firebaseUser.uid}")
                // Try to get user from Firestore
                var user: User? = null
                Log.d(TAG, "Fetching user from Firestore")
                firestoreUserRepository.getUser(firebaseUser.uid).collect { result ->
                    if (result.isSuccess) {
                        user = result.getOrNull()
                        Log.d(TAG, "Firestore fetch result: ${if (user != null) "User found" else "User not found"}")
                    } else {
                        Log.e(TAG, "Failed to fetch user from Firestore: ${result.exceptionOrNull()?.message}")
                    }
                }
                
                // If user doesn't exist in Firestore, create a new one
                if (user == null) {
                    Log.d(TAG, "User not found in Firestore, creating new user record")
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
                    
                    Log.d(TAG, "Saving new user to Firestore")
                    // Save user to Firestore
                    firestoreUserRepository.createUser(newUser).collect { result ->
                        if (result.isFailure) {
                            Log.e(TAG, "Failed to save user to Firestore: ${result.exceptionOrNull()?.message}")
                        } else {
                            Log.d(TAG, "New user saved to Firestore successfully")
                        }
                    }
                    
                    Log.d(TAG, "Login completed with new user creation")
                    Result.success(newUser)
                } else {
                    Log.d(TAG, "Login completed with existing user")
                    Result.success(user!!)
                }
            } else {
                Log.e(TAG, "Login failed: Firebase returned null user")
                Result.failure(AuthenticationException(AuthErrorType.INVALID_CREDENTIALS, "Failed to login"))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(TAG, "Login failed: Invalid credentials", e)
            Result.failure(AuthenticationException(AuthErrorType.INVALID_CREDENTIALS, "Invalid email or password"))
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e(TAG, "Login failed: User not found", e)
            Result.failure(AuthenticationException(AuthErrorType.USER_NOT_FOUND, "No user found with this email"))
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: Unknown error", e)
            Result.failure(AuthenticationException(AuthErrorType.UNKNOWN_ERROR, "Login failed: ${e.message}"))
        }
    }

    // Log out the current user
    fun logout(): Result<Unit> {
        Log.d(TAG, "Logging out current user")
        return try {
            firebaseAuth.signOut()
            Log.d(TAG, "Logout successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Logout failed", e)
            Result.failure(AuthenticationException(AuthErrorType.UNKNOWN_ERROR, "Logout failed: ${e.message}"))
        }
    }

    // Check if a user is currently logged in
    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        Log.d(TAG, "Getting current user: ${if (firebaseUser != null) "User found with UID: ${firebaseUser.uid}" else "No user logged in"}")
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
            Log.d(TAG, "Auth state changed: ${if (user != null) "User authenticated with UID: ${user.uid}" else "User unauthenticated"}")
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
        Log.d(TAG, "Refreshing token")
        return try {
            firebaseAuth.currentUser?.reload()?.await()
            Log.d(TAG, "Token refresh successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Token refresh failed", e)
            Result.failure(AuthenticationException(AuthErrorType.UNKNOWN_ERROR, "Token refresh failed: ${e.message}"))
        }
    }
}