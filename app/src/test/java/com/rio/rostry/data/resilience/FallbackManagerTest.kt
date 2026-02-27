package com.rio.rostry.data.resilience

import com.rio.rostry.data.cache.CacheManager
import com.rio.rostry.data.cache.CacheResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException

class FallbackManagerTest {

    private lateinit var cacheManager: CacheManager
    private lateinit var fallbackManager: FallbackManager

    @Before
    fun setup() {
        cacheManager = mockk(relaxed = true)
        fallbackManager = FallbackManager(cacheManager)
    }

    @Test
    fun withFallback_primarySucceeds_returnsFresh() = runTest {
        val result = fallbackManager.withFallback(
            cacheKey = "test_key",
            primary = { "Fresh data" }
        )

        assertTrue(result is FallbackResult.Fresh)
        assertEquals("Fresh data", (result as FallbackResult.Fresh).data)
        coVerify { cacheManager.put("test_key", "Fresh data", any()) }
    }

    @Test
    fun withFallback_primaryFails_returnsStaleFromCache() = runTest {
        val cachedData = CacheResult("Stale data", true, 60000L)
        coEvery { cacheManager.getStale<String>("test_key") } returns cachedData

        val result = fallbackManager.withFallback(
            cacheKey = "test_key",
            primary = { throw IOException("Network error") }
        )

        assertTrue(result is FallbackResult.Stale)
        val staleResult = result as FallbackResult.Stale
        assertEquals("Stale data", staleResult.data)
        assertEquals(60000L, staleResult.ageMs)
    }

    @Test
    fun withFallback_primaryFailsNoCacheAvailable_returnsFailed() = runTest {
        coEvery { cacheManager.getStale<String>("test_key") } returns null

        val result = fallbackManager.withFallback(
            cacheKey = "test_key",
            primary = { throw IOException("Network error") }
        )

        assertTrue(result is FallbackResult.Failed)
        val failedResult = result as FallbackResult.Failed
        assertTrue(failedResult.userMessage.contains("Unable to connect"))
    }

    @Test
    fun withDefaultFallback_primarySucceeds_returnsPrimaryValue() = runTest {
        val result = fallbackManager.withDefaultFallback(
            defaultValue = "Default",
            primary = { "Primary" }
        )

        assertEquals("Primary", result)
    }

    @Test
    fun withDefaultFallback_primaryFails_returnsDefaultValue() = runTest {
        val result = fallbackManager.withDefaultFallback(
            defaultValue = "Default",
            primary = { throw IOException("Error") }
        )

        assertEquals("Default", result)
    }

    @Test
    fun withEmptyFallback_primarySucceeds_returnsPrimaryList() = runTest {
        val result = fallbackManager.withEmptyFallback(
            primary = { listOf("Item1", "Item2") }
        )

        assertEquals(2, result.size)
        assertEquals("Item1", result[0])
        assertEquals("Item2", result[1])
    }

    @Test
    fun withEmptyFallback_primaryFails_returnsEmptyList() = runTest {
        val result = fallbackManager.withEmptyFallback<String>(
            primary = { throw IOException("Error") }
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun withNullFallback_primarySucceeds_returnsPrimaryValue() = runTest {
        val result = fallbackManager.withNullFallback(
            primary = { "Value" }
        )

        assertEquals("Value", result)
    }

    @Test
    fun withNullFallback_primaryFails_returnsNull() = runTest {
        val result = fallbackManager.withNullFallback<String>(
            primary = { throw IOException("Error") }
        )

        assertNull(result)
    }

    @Test
    fun getFriendlyErrorMessage_networkError_returnsConnectionMessage() {
        val error = SocketTimeoutException("Connection timeout")
        val message = fallbackManager.getFriendlyErrorMessage(error)
        assertEquals("Unable to connect. Please check your internet connection.", message)
    }

    @Test
    fun getFriendlyErrorMessage_circuitOpen_returnsServiceUnavailableMessage() {
        val error = CircuitOpenException("Circuit is open")
        val message = fallbackManager.getFriendlyErrorMessage(error)
        assertEquals("Service temporarily unavailable. Please try again later.", message)
    }

    @Test
    fun getFriendlyErrorMessage_permissionError_returnsPermissionMessage() {
        val error = SecurityException("Access denied")
        val message = fallbackManager.getFriendlyErrorMessage(error)
        assertEquals("You don't have permission to perform this action.", message)
    }

    @Test
    fun getFriendlyErrorMessage_validationError_returnsValidationMessage() {
        val error = IllegalArgumentException("Invalid email format")
        val message = fallbackManager.getFriendlyErrorMessage(error)
        assertTrue(message.contains("Invalid input"))
        assertTrue(message.contains("Invalid email format"))
    }

    @Test
    fun getSuggestedActions_networkError_returnsNetworkActions() {
        val error = IOException("Network error")
        val actions = fallbackManager.getSuggestedActions(error)
        assertTrue(actions.contains(SuggestedAction.RETRY))
        assertTrue(actions.contains(SuggestedAction.CHECK_CONNECTION))
        assertTrue(actions.contains(SuggestedAction.WORK_OFFLINE))
    }

    @Test
    fun getSuggestedActions_circuitOpen_returnsWaitAction() {
        val error = CircuitOpenException("Circuit is open")
        val actions = fallbackManager.getSuggestedActions(error)
        assertTrue(actions.contains(SuggestedAction.WAIT_AND_RETRY))
    }
}
