package com.rio.rostry.data.resilience

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
     */
    suspend fun <T> executeWithRetry(
        key: String,
        policy: RetryPolicy = RetryPolicy.DEFAULT,
        block: suspend () -> T
    ): Result<T> {
        val circuitBreaker = circuitBreakers.getOrPut(key) { CircuitBreakerState() }
        
        // Check circuit breaker
        if (circuitBreaker.isOpen()) {
            return Result.failure(CircuitOpenException("Circuit breaker open for $key"))
        }

        // Check retry budget
        val budget = retryBudgets.getOrPut(key) { AtomicInteger(policy.retryBudget) }
        if (budget.get() <= 0) {
            return Result.failure(RetryBudgetExhaustedException("Retry budget exhausted for $key"))
        }

        var lastException: Throwable? = null
        var attempt = 0

        while (attempt <= policy.maxRetries) {
            try {
                val result = block()
                circuitBreaker.onSuccess()
                budget.set(policy.retryBudget) // Reset budget on success
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e
                circuitBreaker.onFailure()
                budget.decrementAndGet()
                
                if (attempt < policy.maxRetries) {
                    val delayMs = calculateBackoff(attempt, policy)
                    delay(delayMs)
                }
                attempt++
            }
        }

        return Result.failure(lastException ?: RuntimeException("Unknown error"))
    }

    /**
     * Create a Flow transformer that applies retry policy.
     */
    fun <T> retryFlow(key: String, policy: RetryPolicy = RetryPolicy.DEFAULT): (Flow<T>) -> Flow<T> = { upstream ->
        var attempt = 0
        upstream.retryWhen { cause, _ ->
            val circuitBreaker = circuitBreakers.getOrPut(key) { CircuitBreakerState() }
            
            if (circuitBreaker.isOpen()) {
                emit(throw CircuitOpenException("Circuit breaker open"))
                false
            } else if (attempt >= policy.maxRetries) {
                false
            } else {
                circuitBreaker.onFailure()
                val delayMs = calculateBackoff(attempt, policy)
                delay(delayMs)
                attempt++
                true
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
        
        // Add jitter (±25%)
        val jitter = (cappedDelay * 0.25 * (Random.nextFloat() * 2 - 1)).toLong()
        return cappedDelay + jitter
    }
}

/**
 * Configuration for retry behavior.
 */
data class RetryPolicy(
    val maxRetries: Int = 3,
    val baseDelayMs: Long = 1000L,
    val maxDelayMs: Long = 30000L,
    val retryBudget: Int = 10  // Max retries per window
) {
    companion object {
        val DEFAULT = RetryPolicy()
        val CRITICAL = RetryPolicy(maxRetries = 5, baseDelayMs = 500L)
        val ANALYTICS = RetryPolicy(maxRetries = 1, baseDelayMs = 2000L)
        val BACKGROUND = RetryPolicy(maxRetries = 3, baseDelayMs = 5000L, maxDelayMs = 60000L)
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
