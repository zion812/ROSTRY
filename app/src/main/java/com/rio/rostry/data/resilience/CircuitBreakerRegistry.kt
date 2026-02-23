package com.rio.rostry.data.resilience

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registry managing per-service circuit breakers.
 * Uses lazy initialization - circuit breakers are created on first use.
 */
@Singleton
class CircuitBreakerRegistry @Inject constructor() {

    private val breakers = ConcurrentHashMap<String, CircuitBreaker>()

    /**
     * Get or create a circuit breaker for the given service.
     */
    fun getBreaker(
        serviceName: String,
        config: CircuitBreakerConfig = CircuitBreakerConfig()
    ): CircuitBreaker {
        return breakers.getOrPut(serviceName) {
            CircuitBreakerImpl(name = serviceName, config = config)
        }
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
