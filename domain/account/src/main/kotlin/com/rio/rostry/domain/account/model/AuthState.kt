package com.rio.rostry.domain.account.model

import com.rio.rostry.core.model.User

/**
 * Domain model for authentication state.
 * 
 * Phase 2: Domain and Data Decoupling
 * Represents the current authentication state of the application.
 */
sealed class AuthState {
    /**
     * User is not authenticated.
     */
    data object Unauthenticated : AuthState()
    
    /**
     * Authentication is in progress.
     */
    data object Loading : AuthState()
    
    /**
     * User is authenticated.
     */
    data class Authenticated(val user: User) : AuthState()
    
    /**
     * Authentication failed.
     */
    data class Error(val exception: Throwable) : AuthState()
}
