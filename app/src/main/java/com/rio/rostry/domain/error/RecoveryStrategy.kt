package com.rio.rostry.domain.error

import com.rio.rostry.data.resilience.FallbackManager
import com.rio.rostry.data.resilience.FallbackResult
import com.rio.rostry.data.resilience.RetryPolicy
import com.rio.rostry.data.resilience.RetryPolicyManager
import timber.log.Timber

/**
 * Strategy for recovering from errors automatically.
 */
interface RecoveryStrategy {
    suspend fun recover(error: Throwable, context: ErrorContext): Result<Unit>
}

/**
 * Recovery via retry with exponential backoff.
 * Delegates to the existing [RetryPolicyManager].
 * Uses exponential backoff with delays of 1s, 2s, 4s.
 */
class RetryRecoveryStrategy(
    private val retryPolicyManager: RetryPolicyManager,
    private val maxAttempts: Int = 3,
    private val retryAction: suspend () -> Unit
) : RecoveryStrategy {

    override suspend fun recover(error: Throwable, context: ErrorContext): Result<Unit> {
        Timber.d("RetryRecoveryStrategy: Attempting recovery for ${context.operationName}")
        return retryPolicyManager.executeWithRetry(
            key = context.operationName,
            policy = RetryPolicy(
                maxRetries = maxAttempts,
                baseDelayMs = 1000L,  // 1 second
                maxDelayMs = 4000L    // 4 seconds (for 1s, 2s, 4s progression)
            )
        ) {
            retryAction()
        }
    }
}

/**
 * Recovery via cached/stale data fallback.
 * Delegates to the existing [FallbackManager].
 */
class CacheFallbackStrategy(
    private val fallbackManager: FallbackManager,
    private val cacheKey: String,
    private val fetchAction: suspend () -> Any
) : RecoveryStrategy {

    override suspend fun recover(error: Throwable, context: ErrorContext): Result<Unit> {
        Timber.d("CacheFallbackStrategy: Falling back to cache for ${context.operationName}")
        return try {
            val result = fallbackManager.withFallback(
                cacheKey = cacheKey,
                primary = fetchAction
            )
            when (result) {
                is FallbackResult.Fresh -> Result.success(Unit)
                is FallbackResult.Stale -> Result.success(Unit) // Stale is acceptable
                is FallbackResult.Failed -> Result.failure(result.error)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Recovery by returning safe default values.
 * Used when no cached data is available and the operation is non-critical.
 */
class DefaultValueStrategy(
    private val applyDefaults: suspend () -> Unit
) : RecoveryStrategy {

    override suspend fun recover(error: Throwable, context: ErrorContext): Result<Unit> {
        Timber.d("DefaultValueStrategy: Applying defaults for ${context.operationName}")
        return try {
            applyDefaults()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Recovery for fatal errors — logs and degrades gracefully.
 */
class GracefulDegradationStrategy(
    private val degradeAction: suspend () -> Unit = {}
) : RecoveryStrategy {

    override suspend fun recover(error: Throwable, context: ErrorContext): Result<Unit> {
        Timber.w("GracefulDegradationStrategy: Degrading for ${context.operationName}")
        return try {
            degradeAction()
            Result.success(Unit)
        } catch (e: Exception) {
            // Even degradation failed — just log and move on
            Timber.e(e, "GracefulDegradationStrategy: Degradation also failed")
            Result.failure(e)
        }
    }
}
