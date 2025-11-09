package com.rio.rostry.domain.auth.model

/**
 * Sealed class representing the result of an authentication operation.
 * This provides type-safe handling of success, error, and loading states.
 */
sealed class AuthResult<out T> {
    /**
     * Successful authentication result with data
     */
    data class Success<T>(val data: T) : AuthResult<T>()
    
    /**
     * Authentication error with user-friendly error information
     */
    data class Error(val error: UserFriendlyError) : AuthResult<Nothing>()
    
    /**
     * Loading state during authentication operation
     */
    object Loading : AuthResult<Nothing>()
    
    /**
     * Helper method to check if result is successful
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * Helper method to check if result is error
     */
    fun isError(): Boolean = this is Error
    
    /**
     * Helper method to get data if successful, null otherwise
     */
    fun getOrNull(): T? = if (this is Success) data else null
    
    /**
     * Helper method to get error if failed, null otherwise
     */
    fun errorOrNull(): UserFriendlyError? = if (this is Error) error else null
}
