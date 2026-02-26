package com.rio.rostry.data.resilience

import com.rio.rostry.data.cache.CacheManager
import com.rio.rostry.data.cache.CacheResult
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages graceful degradation when network or services fail.
 * Serves stale cache data with visual indicators, provides partial results,
 * and shows farmer-friendly error messages.
 * 
 * Implements four fallback strategies:
 * 1. Cached data fallback - return stale cached data
 * 2. Default value fallback - return sensible defaults
 * 3. Empty result fallback - return empty collections/null
 * 4. Descriptive error fallback - return user-friendly error messages
 */
@Singleton
class FallbackManager @Inject constructor(
    private val cacheManager: CacheManager
) {

    /**
     * Attempt to get data from primary source, falling back to cache on failure.
     * Returns stale data with indicator if primary fails.
     * Implements: Cached data fallback strategy (Requirement 11.3, 21.1)
     */
    suspend fun <T> withFallback(
        cacheKey: String,
        ttlMs: Long = CacheManager.DEFAULT_TTL_MS,
        primary: suspend () -> T
    ): FallbackResult<T> {
        return try {
            val result = primary()
            // Update cache with fresh data
            cacheManager.put(cacheKey, result, ttlMs)
            FallbackResult.Fresh(result)
        } catch (e: Exception) {
            // Try to serve stale cache
            val cached = cacheManager.getStale<T>(cacheKey)
            if (cached != null) {
                FallbackResult.Stale(
                    data = cached.data,
                    ageMs = cached.ageMs,
                    error = e
                )
            } else {
                FallbackResult.Failed(
                    error = e,
                    userMessage = getFriendlyErrorMessage(e)
                )
            }
        }
    }

    /**
     * Flow-based fallback that emits stale data first, then attempts refresh.
     */
    fun <T> flowWithFallback(
        cacheKey: String,
        ttlMs: Long = CacheManager.DEFAULT_TTL_MS,
        fetch: suspend () -> T
    ): Flow<Resource<T>> = flow {
        // First, try to emit stale data if available
        val cached = cacheManager.getStale<T>(cacheKey)
        if (cached != null) {
            emit(Resource.Loading(cached.data))
        } else {
            emit(Resource.Loading())
        }

        // Then attempt fresh fetch
        try {
            val fresh = fetch()
            cacheManager.put(cacheKey, fresh, ttlMs)
            emit(Resource.Success(fresh))
        } catch (e: Exception) {
            if (cached != null) {
                // Emit stale as success with warning
                emit(Resource.Success(cached.data)) // UI should show stale indicator
            } else {
                emit(Resource.Error(getFriendlyErrorMessage(e)))
            }
        }
    }

    /**
     * Convert technical errors to farmer-friendly messages.
     * Implements standard error messages per Requirements 14.4, 14.5, 14.6.
     * Implements: Descriptive error fallback strategy (Requirement 21.4)
     */
    fun getFriendlyErrorMessage(error: Throwable): String {
        return when {
            // Network errors (Requirement 14.4)
            error.message?.contains("timeout", ignoreCase = true) == true ||
            error.message?.contains("network", ignoreCase = true) == true ||
            error.message?.contains("connect", ignoreCase = true) == true ||
            error is java.net.SocketTimeoutException ||
            error is java.net.UnknownHostException ||
            error is java.io.IOException ->
                "Unable to connect. Please check your internet connection."
            
            // Server errors (Requirement 14.6)
            error is CircuitOpenException ||
            error is RetryBudgetExhaustedException ||
            error.message?.contains("503", ignoreCase = true) == true ||
            error.message?.contains("502", ignoreCase = true) == true ||
            error.message?.contains("500", ignoreCase = true) == true ->
                "Service temporarily unavailable. Please try again later."
            
            // Permission errors (Requirement 14.6)
            error is SecurityException ||
            error.message?.contains("permission", ignoreCase = true) == true ||
            error.message?.contains("unauthorized", ignoreCase = true) == true ||
            error.message?.contains("403", ignoreCase = true) == true ->
                "You don't have permission to perform this action."
            
            // Validation errors (Requirement 14.5) - these should be handled specifically by callers
            error is IllegalArgumentException ->
                error.message?.let { "Invalid input: $it" } 
                    ?: "Invalid input. Please check your data and try again."
            
            error is IllegalStateException ->
                error.message?.let { "Operation not allowed: $it" }
                    ?: "This operation cannot be performed right now."
            
            // Not found errors
            error.message?.contains("not found", ignoreCase = true) == true ||
            error.message?.contains("404", ignoreCase = true) == true ->
                "The requested data could not be found."
            
            // Default fallback
            else ->
                "Something went wrong. Please try again or contact support if the problem persists."
        }
    }

    /**
     * Get suggested actions for an error.
     */
    fun getSuggestedActions(error: Throwable): List<SuggestedAction> {
        return when {
            error.message?.contains("network", ignoreCase = true) == true ->
                listOf(
                    SuggestedAction.RETRY,
                    SuggestedAction.CHECK_CONNECTION,
                    SuggestedAction.WORK_OFFLINE
                )
            
            error is CircuitOpenException ->
                listOf(SuggestedAction.WAIT_AND_RETRY)
            
            else ->
                listOf(SuggestedAction.RETRY, SuggestedAction.CONTACT_SUPPORT)
        }
    }

    /**
     * Default value fallback strategy.
     * Returns a sensible default value when primary source fails.
     * Implements: Default value fallback strategy (Requirement 21.2)
     */
    suspend fun <T> withDefaultFallback(
        defaultValue: T,
        primary: suspend () -> T
    ): T {
        return try {
            primary()
        } catch (e: Exception) {
            defaultValue
        }
    }

    /**
     * Empty result fallback strategy.
     * Returns an empty collection or null when primary source fails.
     * Implements: Empty result fallback strategy (Requirement 21.3)
     */
    suspend fun <T> withEmptyFallback(
        primary: suspend () -> List<T>
    ): List<T> {
        return try {
            primary()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Nullable empty result fallback strategy.
     * Returns null when primary source fails.
     * Implements: Empty result fallback strategy (Requirement 21.3)
     */
    suspend fun <T> withNullFallback(
        primary: suspend () -> T
    ): T? {
        return try {
            primary()
        } catch (e: Exception) {
            null
        }
    }
}

sealed class FallbackResult<T> {
    data class Fresh<T>(val data: T) : FallbackResult<T>()
    data class Stale<T>(
        val data: T,
        val ageMs: Long,
        val error: Throwable
    ) : FallbackResult<T>()
    data class Failed<T>(
        val error: Throwable,
        val userMessage: String
    ) : FallbackResult<T>()
}

enum class SuggestedAction {
    RETRY,
    CHECK_CONNECTION,
    WORK_OFFLINE,
    WAIT_AND_RETRY,
    CONTACT_SUPPORT,
    REPORT_ISSUE
}
