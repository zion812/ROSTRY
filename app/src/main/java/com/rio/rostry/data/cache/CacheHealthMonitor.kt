package com.rio.rostry.data.cache

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors cache health metrics: hit/miss ratios, evictions, and size.
 * Provides real-time observability for the caching layer.
 */
@Singleton
class CacheHealthMonitor @Inject constructor(
    private val cacheManager: CacheManager
) {

    // Per-fetcher metrics
    private val fetcherMetrics = ConcurrentHashMap<String, FetcherCacheMetrics>()

    private val _overallHealth = MutableStateFlow(CacheHealth.HEALTHY)
    val overallHealth: StateFlow<CacheHealth> = _overallHealth.asStateFlow()

    /**
     * Record a cache hit for a specific fetcher.
     */
    fun recordHit(fetcherId: String) {
        val metrics = fetcherMetrics.getOrPut(fetcherId) { FetcherCacheMetrics() }
        metrics.hits++
        updateOverallHealth()
    }

    /**
     * Record a cache miss for a specific fetcher.
     */
    fun recordMiss(fetcherId: String) {
        val metrics = fetcherMetrics.getOrPut(fetcherId) { FetcherCacheMetrics() }
        metrics.misses++
        updateOverallHealth()
    }

    /**
     * Record stale data served (for stale-while-revalidate).
     */
    fun recordStaleServed(fetcherId: String) {
        val metrics = fetcherMetrics.getOrPut(fetcherId) { FetcherCacheMetrics() }
        metrics.staleServed++
    }

    /**
     * Get metrics for a specific fetcher.
     */
    fun getMetrics(fetcherId: String): FetcherCacheMetrics? = fetcherMetrics[fetcherId]

    /**
     * Get all fetcher metrics.
     */
    fun getAllMetrics(): Map<String, FetcherCacheMetrics> = fetcherMetrics.toMap()

    /**
     * Get aggregated cache statistics.
     */
    fun getAggregatedStats(): AggregatedCacheStats {
        val allMetrics = fetcherMetrics.values
        val totalHits = allMetrics.sumOf { it.hits }
        val totalMisses = allMetrics.sumOf { it.misses }
        val totalRequests = totalHits + totalMisses
        
        return AggregatedCacheStats(
            totalHits = totalHits,
            totalMisses = totalMisses,
            hitRate = if (totalRequests > 0) totalHits.toFloat() / totalRequests else 0f,
            staleServed = allMetrics.sumOf { it.staleServed },
            fetcherCount = fetcherMetrics.size,
            cacheStats = cacheManager.getStats()
        )
    }

    /**
     * Reset all metrics (useful for testing or after cache clear).
     */
    fun resetMetrics() {
        fetcherMetrics.clear()
        _overallHealth.value = CacheHealth.HEALTHY
    }

    private fun updateOverallHealth() {
        val stats = getAggregatedStats()
        _overallHealth.value = when {
            stats.hitRate < 0.3f -> CacheHealth.DEGRADED
            stats.hitRate < 0.5f -> CacheHealth.WARNING
            else -> CacheHealth.HEALTHY
        }
    }
}

data class FetcherCacheMetrics(
    var hits: Long = 0,
    var misses: Long = 0,
    var staleServed: Long = 0
) {
    val hitRate: Float
        get() {
            val total = hits + misses
            return if (total > 0) hits.toFloat() / total else 0f
        }
}

data class AggregatedCacheStats(
    val totalHits: Long,
    val totalMisses: Long,
    val hitRate: Float,
    val staleServed: Long,
    val fetcherCount: Int,
    val cacheStats: CacheStats
)

enum class CacheHealth {
    HEALTHY,    // Hit rate > 50%
    WARNING,    // Hit rate 30-50%
    DEGRADED    // Hit rate < 30%
}
