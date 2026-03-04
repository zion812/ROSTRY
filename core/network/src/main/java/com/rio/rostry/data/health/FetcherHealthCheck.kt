package com.rio.rostry.data.health

import com.rio.rostry.data.fetcher.FetcherRegistry
import com.rio.rostry.data.resilience.CircuitBreakerStatus
import com.rio.rostry.data.resilience.RetryPolicyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Periodic health checks for all critical fetchers.
 * Tracks success rates, latency percentiles, and auto-disables unhealthy fetchers.
 */
@Singleton
class FetcherHealthCheck @Inject constructor(
    private val registry: FetcherRegistry,
    private val retryPolicyManager: RetryPolicyManager
) {

    private val healthScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Health status per fetcher
    private val fetcherHealthStatus = ConcurrentHashMap<String, FetcherHealth>()
    
    // Observable overall system health
    private val _systemHealth = MutableStateFlow(SystemHealth.HEALTHY)
    val systemHealth: StateFlow<SystemHealth> = _systemHealth.asStateFlow()

    // Latency tracking (for percentile calculation)
    private val latencyRecords = ConcurrentHashMap<String, MutableList<Long>>()

    /**
     * Start periodic health checks.
     * @param intervalMs How often to run health checks (default 5 minutes)
     */
    fun startHealthChecks(intervalMs: Long = 5 * 60 * 1000L) {
        healthScope.launch {
            while (isActive) {
                performHealthChecks()
                delay(intervalMs)
            }
        }
    }

    /**
     * Record a successful fetch with latency.
     */
    fun recordSuccess(fetcherId: String, latencyMs: Long) {
        val health = fetcherHealthStatus.getOrPut(fetcherId) { FetcherHealth(fetcherId) }
        health.successCount++
        health.lastSuccessTime = System.currentTimeMillis()
        
        // Track latency (keep last 100 samples)
        val latencies = latencyRecords.getOrPut(fetcherId) { mutableListOf() }
        synchronized(latencies) {
            latencies.add(latencyMs)
            if (latencies.size > 100) latencies.removeAt(0)
        }
        
        updateHealth(fetcherId)
    }

    /**
     * Record a failed fetch.
     */
    fun recordFailure(fetcherId: String, error: Throwable) {
        val health = fetcherHealthStatus.getOrPut(fetcherId) { FetcherHealth(fetcherId) }
        health.failureCount++
        health.lastError = error.message
        health.lastErrorTime = System.currentTimeMillis()
        
        updateHealth(fetcherId)
    }

    /**
     * Get health status for a specific fetcher.
     */
    fun getHealth(fetcherId: String): FetcherHealth? = fetcherHealthStatus[fetcherId]

    /**
     * Get all fetcher health statuses.
     */
    fun getAllHealth(): Map<String, FetcherHealth> = fetcherHealthStatus.toMap()

    /**
     * Get latency percentiles for a fetcher.
     */
    fun getLatencyPercentiles(fetcherId: String): LatencyPercentiles? {
        val latencies = latencyRecords[fetcherId]?.sorted() ?: return null
        if (latencies.isEmpty()) return null
        
        return LatencyPercentiles(
            p50 = percentile(latencies, 0.5),
            p95 = percentile(latencies, 0.95),
            p99 = percentile(latencies, 0.99)
        )
    }

    /**
     * Check if a fetcher is healthy and should be used.
     */
    fun isHealthy(fetcherId: String): Boolean {
        val health = fetcherHealthStatus[fetcherId] ?: return true // Unknown = assume healthy
        return health.status != HealthStatus.UNHEALTHY
    }

    private fun performHealthChecks() {
        // Check circuit breaker status for all registered fetchers
        registry.getAll().forEach { definition ->
            val circuitStatus = retryPolicyManager.getCircuitBreakerStatus(definition.id)
            val health = fetcherHealthStatus.getOrPut(definition.id) { FetcherHealth(definition.id) }
            
            health.circuitBreakerStatus = circuitStatus
            
            // Mark as unhealthy if circuit is open
            if (circuitStatus == CircuitBreakerStatus.OPEN) {
                health.status = HealthStatus.UNHEALTHY
            }
        }
        
        updateSystemHealth()
    }

    private fun updateHealth(fetcherId: String) {
        val health = fetcherHealthStatus[fetcherId] ?: return
        val totalRequests = health.successCount + health.failureCount
        
        if (totalRequests > 10) {
            val successRate = health.successCount.toFloat() / totalRequests
            health.status = when {
                successRate >= 0.95 -> HealthStatus.HEALTHY
                successRate >= 0.8 -> HealthStatus.DEGRADED
                else -> HealthStatus.UNHEALTHY
            }
        }
        
        updateSystemHealth()
    }

    private fun updateSystemHealth() {
        val allHealth = fetcherHealthStatus.values
        val unhealthyCount = allHealth.count { it.status == HealthStatus.UNHEALTHY }
        val degradedCount = allHealth.count { it.status == HealthStatus.DEGRADED }
        
        _systemHealth.value = when {
            unhealthyCount > 0 -> SystemHealth.CRITICAL
            degradedCount > allHealth.size / 2 -> SystemHealth.DEGRADED
            degradedCount > 0 -> SystemHealth.WARNING
            else -> SystemHealth.HEALTHY
        }
    }

    private fun percentile(sortedList: List<Long>, percentile: Double): Long {
        val index = (sortedList.size * percentile).toInt().coerceIn(0, sortedList.size - 1)
        return sortedList[index]
    }
}

data class FetcherHealth(
    val fetcherId: String,
    var successCount: Long = 0,
    var failureCount: Long = 0,
    var lastSuccessTime: Long = 0,
    var lastErrorTime: Long = 0,
    var lastError: String? = null,
    var status: HealthStatus = HealthStatus.UNKNOWN,
    var circuitBreakerStatus: CircuitBreakerStatus = CircuitBreakerStatus.CLOSED
) {
    val successRate: Float
        get() {
            val total = successCount + failureCount
            return if (total > 0) successCount.toFloat() / total else 1f
        }
}

data class LatencyPercentiles(
    val p50: Long,
    val p95: Long,
    val p99: Long
)

enum class HealthStatus {
    UNKNOWN,
    HEALTHY,
    DEGRADED,
    UNHEALTHY
}

enum class SystemHealth {
    HEALTHY,
    WARNING,
    DEGRADED,
    CRITICAL
}
