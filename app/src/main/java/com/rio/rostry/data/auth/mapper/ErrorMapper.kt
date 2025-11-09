package com.rio.rostry.data.auth.mapper

import android.os.Build
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.rio.rostry.BuildConfig
import com.rio.rostry.domain.auth.model.UserFriendlyError

/**
 * Maps Firebase exceptions to user-friendly error messages.
 * Centralizes all error handling logic in one place.
 */
object ErrorMapper {
    
    /**
     * Map Firebase exception to UserFriendlyError
     * 
     * @param exception Firebase exception
     * @param retryAfterSeconds Optional retry delay for rate limiting
     * @return UserFriendlyError with appropriate message
     */
    fun mapFirebaseError(
        exception: Exception,
        retryAfterSeconds: Int = 60
    ): UserFriendlyError {
        return when (exception) {
            is FirebaseTooManyRequestsException -> {
                UserFriendlyError.RateLimited(retryAfterSeconds)
            }
            
            is FirebaseNetworkException -> {
                UserFriendlyError.NetworkError
            }
            
            is FirebaseAuthInvalidCredentialsException -> {
                UserFriendlyError.InvalidCredentials
            }
            
            is FirebaseAuthUserCollisionException -> {
                UserFriendlyError.PhoneAlreadyInUse
            }
            
            is FirebaseAuthException -> {
                mapAuthException(exception)
            }
            
            is FirebaseException -> {
                mapGenericFirebaseError(exception)
            }
            
            else -> {
                UserFriendlyError.Unknown(
                    exception.message ?: "An unexpected error occurred"
                )
            }
        }
    }
    
    /**
     * Map FirebaseAuthException by error code
     */
    private fun mapAuthException(exception: FirebaseAuthException): UserFriendlyError {
        return when (exception.errorCode) {
            "ERROR_OPERATION_NOT_ALLOWED" -> {
                UserFriendlyError.PhoneAuthDisabled
            }
            
            "ERROR_TOO_MANY_REQUESTS" -> {
                UserFriendlyError.RateLimited(300) // 5 minutes for this error
            }
            
            "ERROR_NETWORK_REQUEST_FAILED" -> {
                UserFriendlyError.NetworkError
            }
            
            "ERROR_SESSION_EXPIRED" -> {
                UserFriendlyError.SessionExpired
            }
            
            "ERROR_QUOTA_EXCEEDED" -> {
                UserFriendlyError.RateLimited(3600) // 1 hour for quota
            }
            
            "ERROR_INVALID_PHONE_NUMBER" -> {
                UserFriendlyError.InvalidPhoneFormat
            }
            
            "ERROR_USER_NOT_FOUND" -> {
                UserFriendlyError.NotAuthenticated
            }
            
            else -> {
                UserFriendlyError.Unknown(
                    exception.message ?: "Authentication failed"
                )
            }
        }
    }
    
    /**
     * Map generic Firebase errors with helpful hints
     */
    private fun mapGenericFirebaseError(exception: FirebaseException): UserFriendlyError {
        val message = exception.message ?: ""
        
        // Check for billing not enabled
        if (message.contains("BILLING_NOT_ENABLED", ignoreCase = true)) {
            return UserFriendlyError.BillingNotEnabled
        }
        
        // Check for network issues with emulator hint
        if (isNetworkRelatedError(message)) {
            return UserFriendlyError.NetworkError
        }
        
        return UserFriendlyError.Unknown(message)
    }
    
    /**
     * Check if error message indicates network issue
     */
    private fun isNetworkRelatedError(message: String): Boolean {
        val networkKeywords = listOf(
            "ECONNREFUSED",
            "NAME_NOT_RESOLVED",
            "Unable to resolve host",
            "Failed to resolve name",
            "timeout",
            "timed out"
        )
        
        return networkKeywords.any { keyword ->
            message.contains(keyword, ignoreCase = true)
        }
    }
    
    /**
     * Check if running on emulator
     */
    fun isEmulator(): Boolean {
        return BuildConfig.DEBUG && (
            Build.FINGERPRINT.contains("generic", ignoreCase = true) ||
            Build.HARDWARE.contains("goldfish", ignoreCase = true) ||
            Build.HARDWARE.contains("ranchu", ignoreCase = true) ||
            Build.PRODUCT.contains("sdk", ignoreCase = true) ||
            Build.BRAND.contains("generic", ignoreCase = true)
        )
    }
}
