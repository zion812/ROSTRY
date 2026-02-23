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
}
