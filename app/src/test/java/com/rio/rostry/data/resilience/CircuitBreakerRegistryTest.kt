package com.rio.rostry.data.resilience

import com.rio.rostry.domain.config.AppConfiguration
import com.rio.rostry.domain.config.ConfigurationManager
import com.rio.rostry.domain.config.FeatureConfig
import com.rio.rostry.domain.config.SecurityConfig
import com.rio.rostry.domain.config.ThresholdConfig
import com.rio.rostry.domain.config.TimeoutConfig
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CircuitBreakerRegistryTest {

    private lateinit var configurationManager: ConfigurationManager
    private lateinit var registry: CircuitBreakerRegistry

    @Before
    fun setup() {
        configurationManager = mockk()
        every { configurationManager.get() } returns AppConfiguration(
            security = SecurityConfig(
                adminIdentifiers = emptyList(),
                moderationBlocklist = emptyList(),
                allowedFileTypes = emptyList()
            ),
            thresholds = ThresholdConfig(
                storageQuotaMB = 500,
                maxBatchSize = 100,
                circuitBreakerFailureRate = 0.5,
                hubCapacity = 1000,
                deliveryRadiusKm = 50.0
            ),
            timeouts = TimeoutConfig(
                networkRequestSeconds = 30,
                circuitBreakerOpenSeconds = 30,
                retryDelaysSeconds = listOf(1, 2, 4)
            ),
            features = FeatureConfig(
                enableRecommendations = true,
                enableDisputes = true,
                enableBreedingCompatibility = true
            )
        )
        registry = CircuitBreakerRegistry(configurationManager)
    }

    @Test
    fun getBreaker_createsNewBreakerForService() = runTest {
        val breaker = registry.getBreaker("firebase_storage")
        assertNotNull(breaker)
        assertEquals(CircuitState.CLOSED, breaker.getState())
    }

    @Test
    fun getBreaker_returnsSameBreakerForSameService() = runTest {
        val breaker1 = registry.getBreaker("firebase_storage")
        val breaker2 = registry.getBreaker("firebase_storage")
        assertSame(breaker1, breaker2)
    }

    @Test
    fun getBreaker_usesConfigurationManagerThresholds() = runTest {
        val breaker = registry.getBreaker("test_service")
        // Trigger some failures to check the threshold
        repeat(11) {
            breaker.execute { throw Exception("Test") }
        }
        // Should be open because we exceeded 50% failure rate
        assertEquals(CircuitState.OPEN, breaker.getState())
    }

    @Test
    fun getAllMetrics_returnsMetricsForAllBreakers() = runTest {
        registry.getBreaker("service1")
        registry.getBreaker("service2")
        registry.getBreaker("service3")

        val metrics = registry.getAllMetrics()
        assertEquals(3, metrics.size)
        assertTrue(metrics.containsKey("service1"))
        assertTrue(metrics.containsKey("service2"))
        assertTrue(metrics.containsKey("service3"))
    }

    @Test
    fun resetBreaker_resetsSpecificBreaker() = runTest {
        val breaker = registry.getBreaker("test_service")
        // Trip the breaker
        repeat(11) {
            breaker.execute { throw Exception("Test") }
        }
        assertEquals(CircuitState.OPEN, breaker.getState())

        // Reset
        registry.resetBreaker("test_service")
        assertEquals(CircuitState.CLOSED, breaker.getState())
    }

    @Test
    fun resetAll_resetsAllBreakers() = runTest {
        val breaker1 = registry.getBreaker("service1")
        val breaker2 = registry.getBreaker("service2")

        // Trip both breakers
        repeat(11) {
            breaker1.execute { throw Exception("Test") }
            breaker2.execute { throw Exception("Test") }
        }
        assertEquals(CircuitState.OPEN, breaker1.getState())
        assertEquals(CircuitState.OPEN, breaker2.getState())

        // Reset all
        registry.resetAll()
        assertEquals(CircuitState.CLOSED, breaker1.getState())
        assertEquals(CircuitState.CLOSED, breaker2.getState())
    }

    @Test
    fun getRegisteredServices_returnsAllServiceNames() = runTest {
        registry.getBreaker("service1")
        registry.getBreaker("service2")
        registry.getBreaker("service3")

        val services = registry.getRegisteredServices()
        assertEquals(3, services.size)
        assertTrue(services.contains("service1"))
        assertTrue(services.contains("service2"))
        assertTrue(services.contains("service3"))
    }
}
