package com.rio.rostry.domain.auth.model

/**
 * Sealed class representing user-friendly error messages for authentication failures.
 * Each error type has a predefined user-friendly message.
 */
sealed class UserFriendlyError {
    /**
     * Network connectivity issues
     */
    object NetworkError : UserFriendlyError()
    
    /**
     * Too many requests - rate limited
     */
    data class RateLimited(val retryAfterSeconds: Int) : UserFriendlyError()
    
    /**
     * Invalid credentials (wrong OTP, invalid phone, etc.)
     */
    object InvalidCredentials : UserFriendlyError()
    
    /**
     * Phone number already exists/linked to another account
     */
    object PhoneAlreadyInUse : UserFriendlyError()
    
    /**
     * Verification session has expired
     */
    object SessionExpired : UserFriendlyError()
    
    /**
     * Invalid phone number format
     */
    object InvalidPhoneFormat : UserFriendlyError()
    
    /**
     * Firebase billing not enabled (for phone auth)
     */
    object BillingNotEnabled : UserFriendlyError()
    
    /**
     * Phone sign-in not enabled in Firebase Console
     */
    object PhoneAuthDisabled : UserFriendlyError()
    
    /**
     * User not authenticated (for operations requiring auth)
     */
    object NotAuthenticated : UserFriendlyError()
    
    /**
     * Unknown error with custom message
     */
    data class Unknown(val errorMessage: String) : UserFriendlyError()
    
    /**
     * Get user-friendly error message
     */
    fun getMessage(): String = when (this) {
        NetworkError -> "No internet connection. Please check your network and try again."
        is RateLimited -> "Too many attempts. Please wait $retryAfterSeconds seconds before trying again."
        InvalidCredentials -> "Invalid code. Please check and try again."
        PhoneAlreadyInUse -> "This phone number is already registered with another account."
        SessionExpired -> "Verification session expired. Please request a new code."
        InvalidPhoneFormat -> "Invalid phone number format. Please enter a valid Indian number."
        BillingNotEnabled -> "Phone verification is temporarily unavailable. Please try another method."
        PhoneAuthDisabled -> "Phone sign-in is not enabled. Please contact support."
        NotAuthenticated -> "You must be signed in to perform this action."
        is Unknown -> errorMessage
    }
    
    /**
     * Get short error title for UI
     */
    fun getTitle(): String = when (this) {
        NetworkError -> "Connection Error"
        is RateLimited -> "Too Many Attempts"
        InvalidCredentials -> "Invalid Code"
        PhoneAlreadyInUse -> "Phone In Use"
        SessionExpired -> "Session Expired"
        InvalidPhoneFormat -> "Invalid Format"
        BillingNotEnabled -> "Service Unavailable"
        PhoneAuthDisabled -> "Not Enabled"
        NotAuthenticated -> "Sign In Required"
        is Unknown -> "Error"
    }
}
