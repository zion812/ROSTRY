package com.rio.rostry.domain.auth.repository

import com.rio.rostry.domain.auth.model.AuthResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 * 
 * This provides a clean abstraction over Firebase Authentication,
 * using domain models for type safety and clear contracts.
 * 
 * Optimized for Free Tier (Google Sign-In only).
 */
interface AuthRepository {
    /**
     * Flow emitting authentication state changes
     */
    val isAuthenticated: Flow<Boolean>
    
    /**
     * Sign in using Google ID token
     * 
     * @param idToken Google ID token
     * @return AuthResult indicating success or failure
     */
    suspend fun signInWithGoogle(idToken: String): AuthResult<Unit>
    
    /**
     * Sign out the current user
     * 
     * @return AuthResult indicating success or failure
     */
    suspend fun signOut(): AuthResult<Unit>
}
