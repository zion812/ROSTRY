package com.rio.rostry.data.resilience

/**
 * Enhanced circuit breaker interface with formal state machine.
 */
interface CircuitBreaker {
    /** Execute a block with circuit breaker protection. */
    suspend fun <T> execute(block: suspend () -> T): Result<T>

    /** Get current circuit state. */
    fun getState(): CircuitState

    /** Get metrics for monitoring. */
    fun getMetrics(): CircuitMetrics

    /** Reset to CLOSED state. */
    fun reset()
}

/** Formal circuit breaker states. */
enum class CircuitState {
    CLOSED,    // Normal = all calls pass through
    OPEN,      // Tripped = all calls rejected
    HALF_OPEN  // Testing = one call allowed to test
}

/** Configuration for circuit breaker behavior. */
data class CircuitBreakerConfig(
    val failureRateThreshold: Double = 0.5,  // 50% failure rate threshold
    val minimumCallsBeforeTrip: Int = 10,     // Minimum 10 requests before opening
    val openDurationMs: Long = 30_000L,       // 30 seconds open duration
    val halfOpenMaxCalls: Int = 1,            // Allow 1 test request in half-open
    val recordWindowSize: Int = 100
)

/** Metrics tracked by the circuit breaker for monitoring. */
data class CircuitMetrics(
    val state: CircuitState,
    val totalCalls: Int,
    val failedCalls: Int,
    val successCalls: Int,
    val failureRate: Double,
    val lastStateChange: Long
)
