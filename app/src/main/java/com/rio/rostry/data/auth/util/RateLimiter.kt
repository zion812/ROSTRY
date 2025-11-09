package com.rio.rostry.data.auth.util

import com.rio.rostry.data.database.dao.RateLimitDao
import com.rio.rostry.data.database.entity.RateLimitEntity
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.UserFriendlyError
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for rate limiting authentication operations.
 * Prevents abuse by limiting how often users can request OTPs.
 */
@Singleton
class RateLimiter @Inject constructor(
    private val rateLimitDao: RateLimitDao
) {
    
    /**
     * Check if action is rate limited
     * 
     * @param userId User identifier (phone number or user ID)
     * @param action Action type (e.g., "AUTH_SEND_OTP")
     * @param windowMs Time window in milliseconds
     * @return AuthResult.Error if rate limited, null if allowed
     */
    suspend fun checkRateLimit(
        userId: String,
        action: String,
        windowMs: Long
    ): AuthResult.Error? {
        val now = System.currentTimeMillis()
        val limit = rateLimitDao.get(userId, action)
        
        if (limit != null && (now - limit.lastAt) < windowMs) {
            val elapsedSeconds = (now - limit.lastAt) / 1000
            val remainingSeconds = (windowMs / 1000) - elapsedSeconds
            
            return AuthResult.Error(
                UserFriendlyError.RateLimited(remainingSeconds.toInt())
            )
        }
        
        return null
    }
    
    /**
     * Record action attempt for rate limiting
     * 
     * @param userId User identifier
     * @param action Action type
     */
    suspend fun recordAttempt(userId: String, action: String) {
        val now = System.currentTimeMillis()
        rateLimitDao.upsert(
            RateLimitEntity(
                id = "$userId:$action",
                userId = userId,
                action = action,
                lastAt = now
            )
        )
    }
    
    /**
     * Get recommended window duration based on action and environment
     * 
     * @param action Action type
     * @param isDebug Whether in debug mode
     * @return Window duration in milliseconds
     */
    fun getWindowDuration(action: String, isDebug: Boolean): Long {
        return when (action) {
            "AUTH_SEND_OTP" -> if (isDebug) 5000 else 60000 // 5s debug, 60s prod
            "AUTH_VERIFY_OTP" -> 60000 // Always 60s
            "AUTH_RESEND_OTP" -> if (isDebug) 5000 else 10000 // 5s debug, 10s prod
            else -> 60000 // Default 60s
        }
    }
}
