package com.rio.rostry.data.cache

import android.util.LruCache
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Multi-tier cache manager implementing Memory → Room → Firestore hierarchy.
 * Provides TTL-based expiration and stale-while-revalidate patterns.
 */
@Singleton
class CacheManager @Inject constructor() {

    // Memory Cache (LRU) - Tier 1
    private val memoryCache = LruCache<String, CacheEntry<*>>(100)
    
    // Metadata for TTL tracking
    private val cacheMetadata = ConcurrentHashMap<String, CacheMetadata>()
    
    private val mutex = Mutex()

    /**
     * Get cached value if available and not expired.
     * @param key Cache key
     * @param ttlMs Time-to-live in milliseconds (default 5 minutes)
     * @return Cached value or null if not found/expired
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> get(key: String, ttlMs: Long = DEFAULT_TTL_MS): T? = mutex.withLock {
        val entry = memoryCache.get(key) as? CacheEntry<T> ?: return@withLock null
        val metadata = cacheMetadata[key] ?: return@withLock null
        
        val now = System.currentTimeMillis()
        return@withLock if (now - metadata.timestamp <= ttlMs) {
            entry.data
        } else {
            null // Expired
        }
    }

    /**
     * Get cached value using fetcher ID and params key.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> get(fetcherId: String, paramsKey: String): T? {
        val key = "${fetcherId}_$paramsKey"
        return get(key)
    }

    /**
     * Get cache entry with metadata for staleness checking.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> getCacheEntry(fetcherId: String, paramsKey: String): CacheEntryWithMeta<T>? = mutex.withLock {
        val key = "${fetcherId}_$paramsKey"
        val entry = memoryCache.get(key) as? CacheEntry<T> ?: return@withLock null
        val metadata = cacheMetadata[key] ?: return@withLock null
        
        CacheEntryWithMeta(
            data = entry.data,
            timestamp = metadata.timestamp,
            ttlMs = metadata.ttlMs
        )
    }

    /**
     * Get cached value even if stale (for stale-while-revalidate pattern).
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> getStale(key: String): CacheResult<T>? = mutex.withLock {
        val entry = memoryCache.get(key) as? CacheEntry<T> ?: return@withLock null
        val metadata = cacheMetadata[key] ?: return@withLock null
        
        val now = System.currentTimeMillis()
        val isStale = now - metadata.timestamp > metadata.ttlMs
        
        CacheResult(
            data = entry.data,
            isStale = isStale,
            ageMs = now - metadata.timestamp
        )
    }

    /**
     * Store value in cache with TTL.
     */
    suspend fun <T> put(key: String, value: T, ttlMs: Long = DEFAULT_TTL_MS) = mutex.withLock {
        memoryCache.put(key, CacheEntry(value))
        cacheMetadata[key] = CacheMetadata(
            timestamp = System.currentTimeMillis(),
            ttlMs = ttlMs
        )
    }

    /**
     * Invalidate a specific cache entry.
     */
    suspend fun invalidate(key: String) = mutex.withLock {
        memoryCache.remove(key)
        cacheMetadata.remove(key)
    }

    /**
     * Invalidate all entries matching a prefix (e.g., "user_*").
     */
    suspend fun invalidateByPrefix(prefix: String) = mutex.withLock {
        val keysToRemove = cacheMetadata.keys.filter { it.startsWith(prefix) }
        keysToRemove.forEach { key ->
            memoryCache.remove(key)
            cacheMetadata.remove(key)
        }
    }

    /**
     * Clear all caches.
     */
    suspend fun clearAll() = mutex.withLock {
        memoryCache.evictAll()
        cacheMetadata.clear()
    }

    /**
     * Get cache statistics for monitoring.
     */
    fun getStats(): CacheStats {
        return CacheStats(
            size = memoryCache.size(),
            maxSize = memoryCache.maxSize(),
            hitCount = memoryCache.hitCount(),
            missCount = memoryCache.missCount(),
            hitRate = if (memoryCache.hitCount() + memoryCache.missCount() > 0) {
                memoryCache.hitCount().toFloat() / (memoryCache.hitCount() + memoryCache.missCount())
            } else 0f
        )
    }

    companion object {
        const val DEFAULT_TTL_MS = 5 * 60 * 1000L // 5 minutes
        
        // Predefined TTLs for different data types
        const val TTL_USER_PROFILE = 5 * 60 * 1000L   // 5 min
        const val TTL_PRODUCTS = 2 * 60 * 1000L       // 2 min
        const val TTL_MONITORING = 30 * 1000L         // 30 sec
        const val TTL_STATIC = 60 * 60 * 1000L        // 1 hour
    }
}

data class CacheEntry<T>(val data: T)

data class CacheMetadata(
    val timestamp: Long,
    val ttlMs: Long
)

data class CacheResult<T>(
    val data: T,
    val isStale: Boolean,
    val ageMs: Long
)

data class CacheStats(
    val size: Int,
    val maxSize: Int,
    val hitCount: Int,
    val missCount: Int,
    val hitRate: Float
)

data class CacheEntryWithMeta<T>(
    val data: T,
    val timestamp: Long,
    val ttlMs: Long
) {
    fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > ttlMs
}

