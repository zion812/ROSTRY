package com.rio.rostry.data.resilience

import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Production-grade circuit breaker with formal state machine.
 *
 * State transitions:
 *   CLOSED → OPEN : failure rate exceeds threshold over [minimumCallsBeforeTrip] calls
 *   OPEN → HALF_OPEN : after [openDurationMs] elapses
 *   HALF_OPEN → CLOSED : a probe call succeeds
 *   HALF_OPEN → OPEN : a probe call fails
 */
class CircuitBreakerImpl(
    private val name: String,
    private val config: CircuitBreakerConfig = CircuitBreakerConfig()
) : CircuitBreaker {

    @Volatile
    private var currentState: CircuitState = CircuitState.CLOSED

    private val totalCalls = AtomicInteger(0)
    private val failedCalls = AtomicInteger(0)
    private val halfOpenCalls = AtomicInteger(0)
    private val lastStateChange = AtomicLong(System.currentTimeMillis())
    private val openedAt = AtomicLong(0L)

    override suspend fun <T> execute(block: suspend () -> T): Result<T> {
        return when (currentState) {
            CircuitState.CLOSED -> executeClosed(block)
            CircuitState.OPEN -> executeOpen(block)
            CircuitState.HALF_OPEN -> executeHalfOpen(block)
        }
    }

    override fun getState(): CircuitState = currentState

    override fun getMetrics(): CircuitMetrics {
        val total = totalCalls.get()
        val failed = failedCalls.get()
        return CircuitMetrics(
            state = currentState,
            totalCalls = total,
            failedCalls = failed,
            successCalls = total - failed,
            failureRate = if (total > 0) failed.toDouble() / total else 0.0,
            lastStateChange = lastStateChange.get()
        )
    }

    override fun reset() {
        transitionTo(CircuitState.CLOSED, "manual reset")
        totalCalls.set(0)
        failedCalls.set(0)
        halfOpenCalls.set(0)
    }

    private suspend fun <T> executeClosed(block: suspend () -> T): Result<T> {
        return try {
            val result = block()
            onSuccess()
            Result.success(result)
        } catch (e: Exception) {
            onFailure()
            Result.failure(e)
        }
    }

    private suspend fun <T> executeOpen(block: suspend () -> T): Result<T> {
        val elapsed = System.currentTimeMillis() - openedAt.get()
        if (elapsed >= config.openDurationMs) {
            // Transition to half-open and try one probe call
            transitionTo(CircuitState.HALF_OPEN, "open duration ($elapsed ms) elapsed")
            halfOpenCalls.set(0)
            return executeHalfOpen(block)
        }
        // Still open — reject immediately
        return Result.failure(CircuitOpenException("Circuit breaker '$name' is OPEN. Try again later."))
    }

    private suspend fun <T> executeHalfOpen(block: suspend () -> T): Result<T> {
        val probeCount = halfOpenCalls.incrementAndGet()
        if (probeCount > config.halfOpenMaxCalls) {
            return Result.failure(CircuitOpenException("Circuit breaker '$name' is HALF_OPEN, max probes reached."))
        }

        return try {
            val result = block()
            // Success — close the circuit
            transitionTo(CircuitState.CLOSED, "probe call succeeded")
            totalCalls.set(0)
            failedCalls.set(0)
            halfOpenCalls.set(0)
            Result.success(result)
        } catch (e: Exception) {
            // Probe failed — reopen circuit
            transitionTo(CircuitState.OPEN, "probe call failed")
            openedAt.set(System.currentTimeMillis())
            Result.failure(e)
        }
    }

    private fun onSuccess() {
        totalCalls.incrementAndGet()
    }

    private fun onFailure() {
        totalCalls.incrementAndGet()
        failedCalls.incrementAndGet()
        checkThreshold()
    }

    private fun checkThreshold() {
        val total = totalCalls.get()
        val failed = failedCalls.get()
        if (total >= config.minimumCallsBeforeTrip) {
            val failureRate = failed.toDouble() / total
            if (failureRate >= config.failureRateThreshold) {
                transitionTo(CircuitState.OPEN, "failure rate ${"%.1f".format(failureRate * 100)}% >= ${"%.1f".format(config.failureRateThreshold * 100)}%")
                openedAt.set(System.currentTimeMillis())
                // Reset counters for next evaluation window
                totalCalls.set(0)
                failedCalls.set(0)
            }
        }
    }

    @Synchronized
    private fun transitionTo(newState: CircuitState, reason: String) {
        val oldState = currentState
        if (oldState != newState) {
            currentState = newState
            lastStateChange.set(System.currentTimeMillis())
            Timber.i("CircuitBreaker[$name]: $oldState → $newState ($reason)")
        }
    }
}
