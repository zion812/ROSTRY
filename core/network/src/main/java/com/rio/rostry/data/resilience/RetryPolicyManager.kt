package com.rio.rostry.data.resilience

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

private const val TAG = "RetryPolicyManager"

/**
 * Manages retry policies with exponential backoff, jitter, and circuit breaker pattern.
 * Prevents thundering herd and provides per-endpoint retry budgets.
 */
@Singleton
class RetryPolicyManager @Inject constructor() {

    // Circuit breaker state per endpoint
    private val circuitBreakers = ConcurrentHashMap<String, CircuitBreakerState>()
    
    // Retry budget tracking
    private val retryBudgets = ConcurrentHashMap<String, AtomicInteger>()

    /**
     * Execute a suspending block with retry policy.
     * Respects circuit breaker state and retry budgets.
     * Logs each retry attempt with attempt number and delay.
     * Logs successful retries.
     */
    suspend fun <T> executeWithRetry(
        key: String,
        policy: RetryPolicy = RetryPolicy.DEFAULT,
        block: suspend () -> T
    ): Result<T> {
        val circuitBreaker = circuitBreakers.getOrPut(key) { CircuitBreakerState() }
        
        // Check circuit breaker
        if (circuitBreaker.isOpen()) {
            Log.w(TAG, "Circuit breaker open for $key - rejecting request")
            return Result.failure(CircuitOpenException("Circuit breaker open for $key"))
        }

        // Check retry budget
        val budget = retryBudgets.getOrPut(key) { AtomicInteger(policy.retryBudget) }
        if (budget.get() <= 0) {
            Log.w(TAG, "Retry budget exhausted for $key")
            return Result.failure(RetryBudgetExhaustedException("Retry budget exhausted for $key"))
        }

        var lastException: Throwable? = null
        var attempt = 0

        while (attempt <= policy.maxRetries) {
            try {
                val result = block()
                circuitBreaker.onSuccess()
                budget.set(policy.retryBudget) // Reset budget on success
                
                // Log successful retry (if this was a retry, not the first attempt)
                if (attempt > 0) {
                    Log.i(TAG, "Retry successful for $key after $attempt attempt(s)")
                }
                
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e
                circuitBreaker.onFailure()
                budget.decrementAndGet()
                
                val isClientError = e is retrofit2.HttpException && e.code() in 400..499 && e.code() != 408 && e.code() != 429
                if (isClientError) {
                    Log.w(TAG, "Non-retryable client error for $key - error: ${e.message}")
                    break
                }
                
                if (attempt < policy.maxRetries) {
                    val delayMs = calculateBackoff(attempt, policy)
                    
                    // Log retry attempt with attempt number and delay
                    Log.d(TAG, "Retry attempt ${attempt + 1}/${policy.maxRetries} for $key - delaying ${delayMs}ms - error: ${e.message}")
                    
                    delay(delayMs)
                } else {
                    // Log final failure after all retries exhausted
                    Log.w(TAG, "All retry attempts exhausted for $key after ${attempt + 1} attempts - error: ${e.message}")
                }
                attempt++
            }
        }

        return Result.failure(lastException ?: RuntimeException("Unknown error"))
    }

    /**
     * Create a Flow transformer that applies retry policy.
     * Logs each retry attempt with attempt number and delay.
     */
    fun <T> retryFlow(key: String, policy: RetryPolicy = RetryPolicy.DEFAULT): (Flow<T>) -> Flow<T> = { upstream ->
        var attempt = 0
        upstream.retryWhen { cause, _ ->
            val circuitBreaker = circuitBreakers.getOrPut(key) { CircuitBreakerState() }
            
            if (circuitBreaker.isOpen()) {
                Log.w(TAG, "Circuit breaker open for $key - rejecting flow retry")
                emit(throw CircuitOpenException("Circuit breaker open"))
                false
            } else if (attempt >= policy.maxRetries) {
                Log.w(TAG, "All flow retry attempts exhausted for $key after ${attempt + 1} attempts")
                false
            } else {
                val isClientError = cause is retrofit2.HttpException && cause.code() in 400..499 && cause.code() != 408 && cause.code() != 429
                if (isClientError) {
                    Log.w(TAG, "Non-retryable client error for $key - rejecting flow retry")
                    false
                } else {
                    circuitBreaker.onFailure()
                    val delayMs = calculateBackoff(attempt, policy)
                    
                    // Log retry attempt
                    Log.d(TAG, "Flow retry attempt ${attempt + 1}/${policy.maxRetries} for $key - delaying ${delayMs}ms - error: ${cause.message}")
                    
                    delay(delayMs)
                    attempt++
                    true
                }
            }
        }
    }

    /**
     * Manually reset circuit breaker for an endpoint.
     */
    fun resetCircuitBreaker(key: String) {
        circuitBreakers[key]?.reset()
    }

    /**
     * Get circuit breaker status for monitoring.
     */
    fun getCircuitBreakerStatus(key: String): CircuitBreakerStatus {
        val state = circuitBreakers[key] ?: return CircuitBreakerStatus.CLOSED
        return when {
            state.isOpen() -> CircuitBreakerStatus.OPEN
            state.isHalfOpen() -> CircuitBreakerStatus.HALF_OPEN
            else -> CircuitBreakerStatus.CLOSED
        }
    }

    private fun calculateBackoff(attempt: Int, policy: RetryPolicy): Long {
        val exponentialDelay = policy.baseDelayMs * 2.0.pow(attempt.toDouble()).toLong()
        val cappedDelay = min(exponentialDelay, policy.maxDelayMs)
        
        // Add jitter (±25%) only if enabled in policy
        return if (policy.useJitter) {
            val jitter = (cappedDelay * 0.25 * (Random.nextFloat() * 2 - 1)).toLong()
            cappedDelay + jitter
        } else {
            cappedDelay
        }
    }
}

/**
 * Configuration for retry behavior.
 * Default policy uses exponential backoff with delays of 1s, 2s, 4s.
 */
data class RetryPolicy(
    val maxRetries: Int = 3,
    val baseDelayMs: Long = 1000L,  // 1 second base delay
    val maxDelayMs: Long = 4000L,   // 4 seconds max delay (for 1s, 2s, 4s progression)
    val retryBudget: Int = 10,      // Max retries per window
    val useJitter: Boolean = false  // Disable jitter by default for predictable delays
) {
    companion object {
        val DEFAULT = RetryPolicy()
        val CRITICAL = RetryPolicy(maxRetries = 5, baseDelayMs = 500L, useJitter = true)
        val ANALYTICS = RetryPolicy(maxRetries = 1, baseDelayMs = 2000L)
        val BACKGROUND = RetryPolicy(maxRetries = 3, baseDelayMs = 5000L, maxDelayMs = 60000L, useJitter = true)
    }
}

/**
 * Tracks circuit breaker state.
 */
class CircuitBreakerState(
    private val failureThreshold: Int = 5,
    private val recoveryTimeMs: Long = 30000L
) {
    private var failureCount = AtomicInteger(0)
    private var lastFailureTime: Long = 0
    private var state: CircuitBreakerStatus = CircuitBreakerStatus.CLOSED

    fun onSuccess() {
        failureCount.set(0)
        state = CircuitBreakerStatus.CLOSED
    }

    fun onFailure() {
        val count = failureCount.incrementAndGet()
        lastFailureTime = System.currentTimeMillis()
        if (count >= failureThreshold) {
            state = CircuitBreakerStatus.OPEN
        }
    }

    fun isOpen(): Boolean {
        if (state == CircuitBreakerStatus.OPEN) {
            val elapsed = System.currentTimeMillis() - lastFailureTime
            if (elapsed >= recoveryTimeMs) {
                state = CircuitBreakerStatus.HALF_OPEN
                return false
            }
            return true
        }
        return false
    }

    fun isHalfOpen(): Boolean = state == CircuitBreakerStatus.HALF_OPEN

    fun reset() {
        failureCount.set(0)
        state = CircuitBreakerStatus.CLOSED
    }
}

enum class CircuitBreakerStatus {
    CLOSED,     // Normal operation
    OPEN,       // Failing, rejecting requests
    HALF_OPEN   // Testing if service recovered
}

class CircuitOpenException(message: String) : Exception(message)
class RetryBudgetExhaustedException(message: String) : Exception(message)
