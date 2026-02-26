package com.rio.rostry.data.resilience

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

class CircuitBreakerImplTest {

    private lateinit var circuitBreaker: CircuitBreaker

    @Before
    fun setup() {
        val config = CircuitBreakerConfig(
            failureRateThreshold = 0.5,
            minimumCallsBeforeTrip = 10,
            halfOpenMaxCalls = 3,
            openDurationMs = 60_000L // Large so it stays OPEN during the test
        )
        circuitBreaker = CircuitBreakerImpl("test_service", config)
    }

    @Test
    fun getState_initiallyClosed() = runTest {
        assertEquals(CircuitState.CLOSED, circuitBreaker.getState())
    }

    @Test
    fun execute_successfulCallsKeepClosed() = runTest {
        repeat(5) {
            circuitBreaker.execute { "Success" }
        }
        assertEquals(CircuitState.CLOSED, circuitBreaker.getState())
        assertEquals(5, circuitBreaker.getMetrics().successCalls)
        assertEquals(0, circuitBreaker.getMetrics().failedCalls)
    }

    @Test
    fun execute_failureThresholdExceeded_transitionsToOpen() = runTest {
        // minimumCallsBeforeTrip=10, threshold=0.5 → need >= 5 of 10 failures
        repeat(4) {
             circuitBreaker.execute { "Success" }
        }
        repeat(6) {
             circuitBreaker.execute { throw IOException("Fail") }
        }
        // After 10 calls with 6 failures (60% >= 50%), should be OPEN
        assertEquals(CircuitState.OPEN, circuitBreaker.getState())
    }

    @Test
    fun execute_whenOpen_returnsCircuitOpenFailure() = runTest {
        // Trip the breaker: 10+ failures
        repeat(11) {
             circuitBreaker.execute { throw IOException("Fail") }
        }

        assertEquals(CircuitState.OPEN, circuitBreaker.getState())

        // Next call should return Result.failure with CircuitOpenException
        val result = circuitBreaker.execute { "Should not run" }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CircuitOpenException)
    }

    @Test
    fun reset_returnsToClosedState() = runTest {
        // Trip the breaker
        repeat(11) {
            circuitBreaker.execute { throw IOException("Fail") }
        }
        assertEquals(CircuitState.OPEN, circuitBreaker.getState())

        // Reset
        circuitBreaker.reset()
        assertEquals(CircuitState.CLOSED, circuitBreaker.getState())
        assertEquals(0, circuitBreaker.getMetrics().totalCalls)
    }

    @Test
    fun execute_openToHalfOpen_afterTimeout() = runTest {
        // Create breaker with short timeout for testing
        val shortTimeoutBreaker = CircuitBreakerImpl(
            "test_service",
            CircuitBreakerConfig(
                failureRateThreshold = 0.5,
                minimumCallsBeforeTrip = 10,
                halfOpenMaxCalls = 1,
                openDurationMs = 100L // 100ms timeout
            )
        )

        // Trip the breaker
        repeat(11) {
            shortTimeoutBreaker.execute { throw IOException("Fail") }
        }
        assertEquals(CircuitState.OPEN, shortTimeoutBreaker.getState())

        // Wait for timeout
        Thread.sleep(150)

        // Next call should transition to HALF_OPEN and execute
        val result = shortTimeoutBreaker.execute { "Success" }
        assertTrue(result.isSuccess)
        assertEquals(CircuitState.CLOSED, shortTimeoutBreaker.getState())
    }

    @Test
    fun execute_halfOpenToOpen_onFailure() = runTest {
        // Create breaker with short timeout for testing
        val shortTimeoutBreaker = CircuitBreakerImpl(
            "test_service",
            CircuitBreakerConfig(
                failureRateThreshold = 0.5,
                minimumCallsBeforeTrip = 10,
                halfOpenMaxCalls = 1,
                openDurationMs = 100L // 100ms timeout
            )
        )

        // Trip the breaker
        repeat(11) {
            shortTimeoutBreaker.execute { throw IOException("Fail") }
        }
        assertEquals(CircuitState.OPEN, shortTimeoutBreaker.getState())

        // Wait for timeout
        Thread.sleep(150)

        // Next call should transition to HALF_OPEN, fail, and reopen
        val result = shortTimeoutBreaker.execute { throw IOException("Still failing") }
        assertTrue(result.isFailure)
        assertEquals(CircuitState.OPEN, shortTimeoutBreaker.getState())
    }

    @Test
    fun getMetrics_tracksFailureRate() = runTest {
        repeat(7) {
            circuitBreaker.execute { "Success" }
        }
        repeat(3) {
            circuitBreaker.execute { throw IOException("Fail") }
        }

        val metrics = circuitBreaker.getMetrics()
        assertEquals(10, metrics.totalCalls)
        assertEquals(7, metrics.successCalls)
        assertEquals(3, metrics.failedCalls)
        assertEquals(0.3, metrics.failureRate, 0.01)
    }
}
