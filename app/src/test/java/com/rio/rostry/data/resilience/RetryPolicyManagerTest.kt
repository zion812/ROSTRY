package com.rio.rostry.data.resilience

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Unit tests for RetryPolicyManager.
 * 
 * Tests:
 * - Retry with transient errors
 * - No retry with client errors
 * - Exponential backoff timing (1s, 2s, 4s)
 * - Max retry limit
 * - Successful retry after failures
 * - Circuit breaker integration
 * - Retry budget exhaustion
 * 
 * **Validates: Requirements 16.1, 16.2, 16.3, 16.4, 16.5, 16.6, 16.7, 16.8**
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RetryPolicyManagerTest {

    private lateinit var retryPolicyManager: RetryPolicyManager

    @Before
    fun setup() {
        retryPolicyManager = RetryPolicyManager()
    }

    @Test
    fun `executeWithRetry succeeds on first attempt`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals("success", result.getOrNull())
        assertEquals(1, callCount)
    }

    @Test
    fun `executeWithRetry retries on transient network error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            if (callCount < 3) {
                throw SocketTimeoutException("Network timeout")
            }
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals("success", result.getOrNull())
        assertEquals(3, callCount) // First attempt + 2 retries
    }

    @Test
    fun `executeWithRetry retries on HTTP 500 error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            if (callCount < 2) {
                throw HttpException(Response.error<Any>(500, okhttp3.ResponseBody.create(null, "")))
            }
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, callCount)
    }

    @Test
    fun `executeWithRetry retries on HTTP 502 error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            if (callCount < 2) {
                throw HttpException(Response.error<Any>(502, okhttp3.ResponseBody.create(null, "")))
            }
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, callCount)
    }

    @Test
    fun `executeWithRetry retries on HTTP 503 error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            if (callCount < 2) {
                throw HttpException(Response.error<Any>(503, okhttp3.ResponseBody.create(null, "")))
            }
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, callCount)
    }

    @Test
    fun `executeWithRetry retries on HTTP 504 error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            if (callCount < 2) {
                throw HttpException(Response.error<Any>(504, okhttp3.ResponseBody.create(null, "")))
            }
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, callCount)
    }

    @Test
    fun `executeWithRetry retries on HTTP 408 error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            if (callCount < 2) {
                throw HttpException(Response.error<Any>(408, okhttp3.ResponseBody.create(null, "")))
            }
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, callCount)
    }

    @Test
    fun `executeWithRetry retries on HTTP 429 error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            if (callCount < 2) {
                throw HttpException(Response.error<Any>(429, okhttp3.ResponseBody.create(null, "")))
            }
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, callCount)
    }

    @Test
    fun `executeWithRetry does not retry on HTTP 400 error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            throw HttpException(Response.error<Any>(400, okhttp3.ResponseBody.create(null, "")))
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals(1, callCount) // No retries for client errors
    }

    @Test
    fun `executeWithRetry does not retry on HTTP 404 error`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            throw HttpException(Response.error<Any>(404, okhttp3.ResponseBody.create(null, "")))
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy.DEFAULT,
            block = operation
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals(1, callCount)
    }

    @Test
    fun `executeWithRetry respects max retry limit`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            throw IOException("Network error")
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy(maxRetries = 3),
            block = operation
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals(4, callCount) // Initial attempt + 3 retries
    }

    @Test
    fun `executeWithRetry uses exponential backoff`() = runTest {
        // Given
        var callCount = 0
        val delays = mutableListOf<Long>()
        var lastTime = System.currentTimeMillis()
        
        val operation = suspend {
            callCount++
            if (callCount > 1) {
                val currentTime = System.currentTimeMillis()
                delays.add(currentTime - lastTime)
                lastTime = currentTime
            }
            if (callCount <= 3) {
                throw IOException("Network error")
            }
            "success"
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy(maxRetries = 3, baseDelayMs = 1000L, maxDelayMs = 4000L, useJitter = false),
            block = operation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(4, callCount)
        
        // Verify exponential backoff: 1s, 2s, 4s (with some tolerance for test execution time)
        assertTrue("First delay should be ~1000ms, was ${delays[0]}", delays[0] in 900..1100)
        assertTrue("Second delay should be ~2000ms, was ${delays[1]}", delays[1] in 1900..2100)
        assertTrue("Third delay should be ~4000ms, was ${delays[2]}", delays[2] in 3900..4100)
    }

    @Test
    fun `executeWithRetry fails after exhausting all retries`() = runTest {
        // Given
        var callCount = 0
        val operation = suspend {
            callCount++
            throw IOException("Persistent network error")
        }

        // When
        val result = retryPolicyManager.executeWithRetry(
            key = "test-operation",
            policy = RetryPolicy(maxRetries = 2),
            block = operation
        )

        // Then
        assertTrue(result.isFailure)
        assertEquals(3, callCount) // Initial + 2 retries
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun `executeWithRetry rejects requests when circuit breaker is open`() = runTest {
        // Given
        val operation = suspend {
            throw IOException("Network error")
        }

        // Trigger circuit breaker by failing multiple times
        repeat(10) {
            retryPolicyManager.executeWithRetry(
                key = "test-circuit",
                policy = RetryPolicy(maxRetries = 0),
                block = operation
            )
        }

        // When - try again with circuit breaker open
        val result = retryPolicyManager.executeWithRetry(
            key = "test-circuit",
            policy = RetryPolicy(maxRetries = 3),
            block = operation
        )

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CircuitOpenException)
    }

    @Test
    fun `resetCircuitBreaker allows requests after circuit was open`() = runTest {
        // Given
        val operation = suspend {
            throw IOException("Network error")
        }

        // Trigger circuit breaker
        repeat(10) {
            retryPolicyManager.executeWithRetry(
                key = "test-reset",
                policy = RetryPolicy(maxRetries = 0),
                block = operation
            )
        }

        // When - reset and try again
        retryPolicyManager.resetCircuitBreaker("test-reset")
        
        var callCount = 0
        val successOperation = suspend {
            callCount++
            "success"
        }
        
        val result = retryPolicyManager.executeWithRetry(
            key = "test-reset",
            policy = RetryPolicy.DEFAULT,
            block = successOperation
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, callCount)
    }

    @Test
    fun `getCircuitBreakerStatus returns correct status`() = runTest {
        // Given
        val key = "test-status"
        
        // Initially closed
        assertEquals(CircuitBreakerStatus.CLOSED, retryPolicyManager.getCircuitBreakerStatus(key))

        // Trigger failures to open circuit
        val operation = suspend {
            throw IOException("Network error")
        }
        
        repeat(10) {
            retryPolicyManager.executeWithRetry(
                key = key,
                policy = RetryPolicy(maxRetries = 0),
                block = operation
            )
        }

        // Should be open now
        assertEquals(CircuitBreakerStatus.OPEN, retryPolicyManager.getCircuitBreakerStatus(key))
    }
}
