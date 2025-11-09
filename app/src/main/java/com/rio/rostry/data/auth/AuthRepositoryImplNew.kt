package com.rio.rostry.data.auth

import android.app.Activity
import com.rio.rostry.data.auth.mapper.ErrorMapper
import com.rio.rostry.data.auth.source.AuthStateManager
import com.rio.rostry.data.auth.source.FirebaseAuthDataSource
import com.rio.rostry.data.auth.util.RateLimiter
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.OtpCode
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.repository.AuthRepository
import com.rio.rostry.security.SecurityManager
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clean implementation of AuthRepository using extracted concerns.
 * 
 * This implementation follows clean architecture principles:
 * - Single responsibility per class
 * - Dependency injection
 * - Clear separation of concerns
 * - Type-safe domain models
 * 
 * Dependencies:
 * - FirebaseAuthDataSource: Firebase operations
 * - RateLimiter: Rate limiting logic
 * - AuthStateManager: State persistence
 * - ErrorMapper: Error handling (used by DataSource)
 */
@Singleton
class AuthRepositoryImplNew @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val rateLimiter: RateLimiter,
    private val authStateManager: AuthStateManager
) : AuthRepository {
    
    override val isAuthenticated: Flow<Boolean> =
        firebaseAuthDataSource.isAuthenticated
    
    override suspend fun startPhoneVerification(
        activity: Activity,
        phoneNumber: PhoneNumber
    ): AuthResult<VerificationId> {
        Timber.d("Starting phone verification for ${phoneNumber.masked()}")
        
        // Check rate limit
        val windowMs = rateLimiter.getWindowDuration(
            action = "AUTH_SEND_OTP",
            isDebug = ErrorMapper.isEmulator()
        )
        
        val rateLimitError = rateLimiter.checkRateLimit(
            userId = phoneNumber.value,
            action = "AUTH_SEND_OTP",
            windowMs = windowMs
        )
        
        if (rateLimitError != null) {
            Timber.w("Rate limit exceeded for ${phoneNumber.masked()}")
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf(
                "action" to "AUTH_SEND_OTP",
                "phone" to phoneNumber.masked()
            ))
            return rateLimitError
        }
        
        // Start verification
        val result = firebaseAuthDataSource.startPhoneVerification(activity, phoneNumber)
        
        // Record attempt for rate limiting
        if (result is AuthResult.Success) {
            rateLimiter.recordAttempt(phoneNumber.value, "AUTH_SEND_OTP")
        }
        
        return result
    }
    
    override suspend fun verifyOtp(
        verificationId: VerificationId,
        otpCode: OtpCode
    ): AuthResult<Unit> {
        Timber.d("Verifying OTP for verificationId=${verificationId.value}")
        
        // Check rate limit
        val phoneNumber = authStateManager.getPhoneNumber()
        val userId = phoneNumber?.value ?: verificationId.value
        
        val rateLimitError = rateLimiter.checkRateLimit(
            userId = userId,
            action = "AUTH_VERIFY_OTP",
            windowMs = 60000 // Always 60s for OTP verification
        )
        
        if (rateLimitError != null) {
            Timber.w("OTP verification rate limit exceeded")
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf(
                "action" to "AUTH_VERIFY_OTP"
            ))
            return rateLimitError
        }
        
        // Verify OTP
        val result = firebaseAuthDataSource.verifyOtp(verificationId, otpCode)
        
        // Record attempt
        rateLimiter.recordAttempt(userId, "AUTH_VERIFY_OTP")
        
        return result
    }
    
    override suspend fun resendVerificationCode(
        activity: Activity
    ): AuthResult<VerificationId> {
        Timber.d("Resending verification code")
        
        val phoneNumber = authStateManager.getPhoneNumber()
            ?: return AuthResult.Error(
                ErrorMapper.mapFirebaseError(
                    Exception("No previous verification session found")
                )
            )
        
        // Check rate limit
        val windowMs = rateLimiter.getWindowDuration(
            action = "AUTH_RESEND_OTP",
            isDebug = ErrorMapper.isEmulator()
        )
        
        val rateLimitError = rateLimiter.checkRateLimit(
            userId = phoneNumber.value,
            action = "AUTH_RESEND_OTP",
            windowMs = windowMs
        )
        
        if (rateLimitError != null) {
            Timber.w("Resend rate limit exceeded for ${phoneNumber.masked()}")
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf(
                "action" to "AUTH_RESEND_OTP",
                "phone" to phoneNumber.masked()
            ))
            return rateLimitError
        }
        
        // Resend code
        val result = firebaseAuthDataSource.resendVerificationCode(activity)
        
        // Record attempt
        if (result is AuthResult.Success) {
            rateLimiter.recordAttempt(phoneNumber.value, "AUTH_RESEND_OTP")
        }
        
        return result
    }
    
    override suspend fun linkPhoneToCurrentUser(
        activity: Activity,
        phoneNumber: PhoneNumber
    ): AuthResult<VerificationId> {
        Timber.d("Starting phone linking for ${phoneNumber.masked()}")
        
        // Check rate limit
        val windowMs = rateLimiter.getWindowDuration(
            action = "AUTH_SEND_OTP",
            isDebug = ErrorMapper.isEmulator()
        )
        
        val rateLimitError = rateLimiter.checkRateLimit(
            userId = phoneNumber.value,
            action = "AUTH_SEND_OTP",
            windowMs = windowMs
        )
        
        if (rateLimitError != null) {
            Timber.w("Phone linking rate limit exceeded for ${phoneNumber.masked()}")
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf(
                "action" to "AUTH_SEND_OTP",
                "phone" to phoneNumber.masked(),
                "context" to "phone_linking"
            ))
            return rateLimitError
        }
        
        // Start phone linking
        val result = firebaseAuthDataSource.linkPhoneToCurrentUser(activity, phoneNumber)
        
        // Record attempt
        if (result is AuthResult.Success) {
            rateLimiter.recordAttempt(phoneNumber.value, "AUTH_SEND_OTP")
        }
        
        return result
    }
    
    override suspend fun verifyOtpAndLink(
        verificationId: VerificationId,
        otpCode: OtpCode
    ): AuthResult<Unit> {
        Timber.d("Verifying OTP for phone linking")
        
        // Check rate limit
        val phoneNumber = authStateManager.getPhoneNumber()
        val userId = phoneNumber?.value ?: verificationId.value
        
        val rateLimitError = rateLimiter.checkRateLimit(
            userId = userId,
            action = "AUTH_VERIFY_OTP",
            windowMs = 60000
        )
        
        if (rateLimitError != null) {
            Timber.w("OTP verification rate limit exceeded for phone linking")
            SecurityManager.audit("AUTH_RATE_LIMIT", mapOf(
                "action" to "AUTH_VERIFY_OTP",
                "context" to "phone_linking"
            ))
            return rateLimitError
        }
        
        // Verify and link
        val result = firebaseAuthDataSource.verifyOtpAndLink(verificationId, otpCode)
        
        // Record attempt
        rateLimiter.recordAttempt(userId, "AUTH_VERIFY_OTP")
        
        return result
    }
    
    override suspend fun signOut(): AuthResult<Unit> {
        Timber.d("Signing out user")
        return firebaseAuthDataSource.signOut()
    }
    
    override suspend fun getCurrentVerificationPhone(): PhoneNumber? {
        return authStateManager.getPhoneNumber()
    }
}
