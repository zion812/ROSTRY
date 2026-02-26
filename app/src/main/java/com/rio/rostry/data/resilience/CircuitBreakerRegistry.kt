package com.rio.rostry.data.resilience

import com.rio.rostry.domain.config.ConfigurationManager
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registry managing per-service circuit breakers.
 * Uses lazy initialization - circuit breakers are created on first use.
 * Integrates with Configuration Manager for threshold configuration.
 */
@Singleton
class CircuitBreakerRegistry @Inject constructor(
    private val configurationManager: ConfigurationManager
) {

    private val breakers = ConcurrentHashMap<String, CircuitBreaker>()

    /**
     * Get or create a circuit breaker for the given service.
     * Uses configuration from Configuration Manager if no custom config provided.
     */
    fun getBreaker(
        serviceName: String,
        config: CircuitBreakerConfig? = null
    ): CircuitBreaker {
        return breakers.getOrPut(serviceName) {
            val effectiveConfig = config ?: getConfigFromManager()
            CircuitBreakerImpl(name = serviceName, config = effectiveConfig)
        }
    }

    /**
     * Get circuit breaker configuration from Configuration Manager.
     */
    private fun getConfigFromManager(): CircuitBreakerConfig {
        val appConfig = configurationManager.get()
        return CircuitBreakerConfig(
            failureRateThreshold = appConfig.thresholds.circuitBreakerFailureRate,
            minimumCallsBeforeTrip = 10, // Fixed as per requirements
            openDurationMs = appConfig.timeouts.circuitBreakerOpenSeconds * 1000L,
            halfOpenMaxCalls = 1, // Fixed as per requirements
            recordWindowSize = 100
        )
    }

    /**
     * Get metrics for all registered circuit breakers.
     */
    fun getAllMetrics(): Map<String, CircuitMetrics> {
        return breakers.mapValues { it.value.getMetrics() }
    }

    /**
     * Reset a specific circuit breaker.
     */
    fun resetBreaker(serviceName: String) {
        breakers[serviceName]?.reset()
    }

    /**
     * Reset all circuit breakers.
     */
    fun resetAll() {
        breakers.values.forEach { it.reset() }
    }

    /**
     * Get all registered service names.
     */
    fun getRegisteredServices(): Set<String> = breakers.keys.toSet()
}
