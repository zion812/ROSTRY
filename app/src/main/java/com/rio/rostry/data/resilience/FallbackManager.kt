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
 */
@Singleton
class FallbackManager @Inject constructor(
    private val cacheManager: CacheManager
) {

    /**
     * Attempt to get data from primary source, falling back to cache on failure.
     * Returns stale data with indicator if primary fails.
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
     */
    fun getFriendlyErrorMessage(error: Throwable): String {
        return when {
            error.message?.contains("timeout", ignoreCase = true) == true ->
                "Connection is slow. Please check your internet and try again."
            
            error.message?.contains("network", ignoreCase = true) == true ||
            error.message?.contains("connect", ignoreCase = true) == true ->
                "No internet connection. Your changes will sync when you're back online."
            
            error is CircuitOpenException ->
                "Service temporarily unavailable. Please try again in a few minutes."
            
            error is RetryBudgetExhaustedException ->
                "Too many requests. Please wait a moment before trying again."
            
            error.message?.contains("permission", ignoreCase = true) == true ->
                "You don't have permission to perform this action."
            
            error.message?.contains("not found", ignoreCase = true) == true ->
                "The requested data could not be found."
            
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
