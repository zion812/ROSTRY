---
Version: 1.0
Last Updated: 2026-02-05
Audience: Developers
Status: Active
Related_Docs: [data-layer-architecture.md, data-contracts.md, architecture.md, fetcher-system.md]
Tags: [cache, performance, optimization, offline, storage]
---

# Cache Management Documentation

## Overview

The ROSTRY cache management system provides intelligent caching with health monitoring and performance optimization. It implements a multi-layered caching strategy that balances performance, memory usage, and data freshness.

## Architecture

### Core Components

#### 1. CacheManager
The central cache management API and usage.

- **Purpose**: Primary interface for all cache operations
- **Key Features**:
  - Put/get operations for cached data
  - Cache size management
  - TTL and expiration handling
  - Cache statistics and metrics

#### 2. CacheHealthMonitor
Cache health monitoring and metrics.

- **Purpose**: Monitors cache performance and health
- **Key Features**:
  - Cache hit/miss ratio tracking
  - Performance metrics collection
  - Health status reporting
  - Alerting for cache issues

#### 3. CachePolicy
Cache policies and TTL strategies.

- **Purpose**: Defines caching behavior and expiration rules
- **Key Features**:
  - TTL-based expiration
  - Cache strategy selection
  - Size limitations
  - Eviction policies

#### 4. CacheInvalidator
Cache invalidation strategies.

- **Purpose**: Manages cache invalidation and cleanup
- **Key Features**:
  - Key-based invalidation
  - Pattern-based invalidation
  - Tag-based invalidation
  - Bulk invalidation operations

## Cache Strategies

### 1. TTL (Time-To-Live) Strategy
Automatically expires cached items after a specified duration:

```kotlin
data class CachePolicy(
    val strategy: CacheStrategy,
    val ttl: Duration,
    val maxSize: Int,
    val evictionPolicy: EvictionPolicy
)

// Example usage
val userCachePolicy = CachePolicy(
    strategy = CacheStrategy.TTL,
    ttl = 30.minutes,
    maxSize = 1000,
    evictionPolicy = EvictionPolicy.LRU
)
```

### 2. Stale-While-Revalidate Strategy
Serve stale data while fetching fresh data in the background:

```kotlin
val productCachePolicy = CachePolicy(
    strategy = CacheStrategy.STALE_WHILE_REVALIDATE,
    ttl = 10.minutes,  // Serve stale for 10 minutes while revalidating
    maxSize = 500,
    evictionPolicy = EvictionPolicy.LRU
)
```

### 3. Cache-Aside Strategy
Lazy loading pattern where cache is populated on first access:

```kotlin
suspend fun getDataWithCache(key: String): Data {
    // Check cache first
    val cached = cacheManager.get<Data>(key)
    if (cached != null) {
        return cached
    }
    
    // Fetch from source if not in cache
    val data = fetchDataFromSource(key)
    
    // Store in cache
    cacheManager.put(key, data, cachePolicy)
    
    return data
}
```

## Cache Operations

### Basic Operations

#### Put Operation
Store data in cache with optional TTL:

```kotlin
suspend fun <T> put(key: String, data: T, policy: CachePolicy? = null) {
    val cacheEntry = CacheEntry(
        data = data,
        timestamp = Clock.System.now(),
        ttl = policy?.ttl ?: defaultTtl
    )
    
    cacheStore[key] = cacheEntry
}
```

#### Get Operation
Retrieve data from cache if available and not expired:

```kotlin
suspend fun <T> get(key: String): T? {
    val entry = cacheStore[key] ?: return null
    
    if (isExpired(entry)) {
        // Remove expired entry
        cacheStore.remove(key)
        return null
    }
    
    return entry.data as T
}
```

#### Remove Operation
Remove specific entry from cache:

```kotlin
suspend fun remove(key: String) {
    cacheStore.remove(key)
}
```

#### Clear Operation
Clear all entries from cache:

```kotlin
suspend fun clear() {
    cacheStore.clear()
}
```

## Eviction Policies

### 1. LRU (Least Recently Used)
Removes the least recently accessed items when cache reaches maximum size:

```kotlin
class LruEvictionPolicy<T> : EvictionPolicy<T> {
    override fun evict(cache: MutableMap<String, CacheEntry<T>>, maxSize: Int) {
        if (cache.size <= maxSize) return
        
        // Sort by access time and remove oldest entries
        val sortedEntries = cache.entries.sortedBy { it.value.lastAccessTime }
        val toRemove = sortedEntries.take(cache.size - maxSize)
        
        toRemove.forEach { cache.remove(it.key) }
    }
}
```

### 2. LFU (Least Frequently Used)
Removes the least frequently accessed items:

```kotlin
class LfuEvictionPolicy<T> : EvictionPolicy<T> {
    override fun evict(cache: MutableMap<String, CacheEntry<T>>, maxSize: Int) {
        if (cache.size <= maxSize) return
        
        // Sort by access count and remove least used
        val sortedEntries = cache.entries.sortedBy { it.value.accessCount }
        val toRemove = sortedEntries.take(cache.size - maxSize)
        
        toRemove.forEach { cache.remove(it.key) }
    }
}
```

### 3. FIFO (First In, First Out)
Removes the oldest entries based on insertion time:

```kotlin
class FifoEvictionPolicy<T> : EvictionPolicy<T> {
    override fun evict(cache: MutableMap<String, CacheEntry<T>>, maxSize: Int) {
        if (cache.size <= maxSize) return
        
        // Remove entries in insertion order
        val insertionOrder = cache.entries.toList()
        val toRemove = insertionOrder.take(cache.size - maxSize)
        
        toRemove.forEach { cache.remove(it.key) }
    }
}
```

## Cache Invalidation Strategies

### 1. Direct Invalidation
Invalidate specific cache keys:

```kotlin
suspend fun invalidate(key: String) {
    cacheManager.remove(key)
}
```

### 2. Pattern-Based Invalidation
Invalidate keys matching a pattern:

```kotlin
suspend fun invalidateByPattern(pattern: String) {
    val regex = Regex(pattern.replace("*", ".*"))
    val keysToRemove = cacheManager.keys.filter { regex.matches(it) }
    
    keysToRemove.forEach { cacheManager.remove(it) }
}
```

### 3. Tag-Based Invalidation
Invalidate all items associated with specific tags:

```kotlin
suspend fun invalidateByTag(tag: String) {
    val keysWithTag = tagIndex[tag] ?: emptySet()
    
    keysWithTag.forEach { cacheManager.remove(it) }
    
    // Remove tag association
    tagIndex.remove(tag)
}
```

### 4. Time-Based Invalidation
Invalidate all entries older than a specific time:

```kotlin
suspend fun invalidateOlderThan(timestamp: Instant) {
    val keysToInvalidate = cacheManager.entries
        .filter { it.value.timestamp < timestamp }
        .map { it.key }
    
    keysToInvalidate.forEach { cacheManager.remove(it) }
}
```

## Performance Metrics

### Cache Hit Ratio
Percentage of requests served from cache:

```kotlin
val cacheHitRatio: Double
    get() = if (totalRequests == 0) 0.0 else hits.toDouble() / totalRequests.toDouble()
```

### Average Response Time
Average time to serve cached vs uncached requests:

```kotlin
val averageCachedResponseTime: Duration
    get() = if (hits == 0) Duration.ZERO else totalCachedTime / hits
```

### Memory Usage
Current cache memory consumption:

```kotlin
val memoryUsage: Long
    get() = cacheStore.values.sumOf { estimateSize(it.data) }
```

## Integration with Fetcher System

### Cache Integration Pattern
The cache manager integrates with the fetcher system:

```kotlin
class FetcherCoordinator @Inject constructor(
    private val cacheManager: CacheManager,
    private val fetchers: List<Fetcher<*>>
) {
    
    suspend fun <T> fetch(request: ClientRequest<T>): Resource<T> {
        // Check cache first if policy allows
        if (request.cachePolicy.strategy.allowsCacheRead()) {
            val cached = cacheManager.get<T>(request.key)
            if (cached != null) {
                return Resource.Success(cached)
            }
        }
        
        // Fetch from source
        val result = executeFetcher(request)
        
        // Cache result if successful and policy allows
        if (result is Resource.Success && request.cachePolicy.strategy.allowsCacheWrite()) {
            cacheManager.put(request.key, result.data, request.cachePolicy)
        }
        
        return result
    }
}
```

### Cache-First Strategy
Prioritize cached data when available:

```kotlin
suspend fun <T> fetchWithCacheFirst(request: ClientRequest<T>): Resource<T> {
    // Try cache first
    val cached = cacheManager.get<T>(request.key)
    if (cached != null) {
        return Resource.Success(cached)
    }
    
    // Fall back to network
    val result = fetchFromNetwork(request)
    
    // Cache successful results
    if (result is Resource.Success) {
        cacheManager.put(request.key, result.data, request.cachePolicy)
    }
    
    return result
}
```

## Configuration Options

### Global Cache Configuration
Configure global cache settings:

```kotlin
data class GlobalCacheConfig(
    val defaultTtl: Duration = 5.minutes,
    val maxSize: Int = 1000,
    val evictionPolicy: EvictionPolicy = EvictionPolicy.LRU,
    val enableMetrics: Boolean = true,
    val enableHealthChecks: Boolean = true
)
```

### Per-Request Configuration
Override global settings for specific requests:

```kotlin
val requestSpecificPolicy = CachePolicy(
    strategy = CacheStrategy.NETWORK_FIRST,
    ttl = 1.hour,
    maxSize = 50,
    evictionPolicy = EvictionPolicy.LFU
)
```

## Security Considerations

### Data Encryption
Encrypt sensitive data in cache:

```kotlin
class EncryptedCacheManager : CacheManager {
    override suspend fun <T> put(key: String, data: T, policy: CachePolicy?) {
        val encryptedData = encrypt(data)
        internalCache.put(key, encryptedData, policy)
    }
    
    override suspend fun <T> get(key: String): T? {
        val encryptedData = internalCache.get<ByteArray>(key) ?: return null
        return decrypt<T>(encryptedData)
    }
}
```

### Sensitive Data Filtering
Sanitize data before caching:

```kotlin
suspend fun <T> putSafe(key: String, data: T, policy: CachePolicy?) {
    val sanitizedData = sanitizeSensitiveFields(data)
    cacheManager.put(key, sanitizedData, policy)
}
```

## Monitoring and Health Checks

### Health Status
Monitor cache health:

```kotlin
data class CacheHealthStatus(
    val isHealthy: Boolean,
    val hitRatio: Double,
    val responseTime: Duration,
    val memoryUsage: Long,
    val maxSize: Long,
    val errorRate: Double
)
```

### Health Check Implementation
Regular health monitoring:

```kotlin
class CacheHealthMonitor @Inject constructor(
    private val cacheManager: CacheManager
) {
    
    suspend fun checkHealth(): CacheHealthStatus {
        val metrics = cacheManager.metrics
        val hitRatio = metrics.hitCount.toDouble() / (metrics.hitCount + metrics.missCount).toDouble()
        val memoryUsage = cacheManager.memoryUsage
        
        return CacheHealthStatus(
            isHealthy = hitRatio > HEALTH_THRESHOLD && 
                       memoryUsage < MAX_MEMORY_USAGE &&
                       metrics.errorRate < ERROR_RATE_THRESHOLD,
            hitRatio = hitRatio,
            responseTime = metrics.averageResponseTime,
            memoryUsage = memoryUsage,
            maxSize = cacheManager.maxSize,
            errorRate = metrics.errorRate
        )
    }
    
    companion object {
        private const val HEALTH_THRESHOLD = 0.8
        private const val MAX_MEMORY_USAGE = 0.8 // 80% of max
        private const val ERROR_RATE_THRESHOLD = 0.05 // 5%
    }
}
```

## Performance Optimization

### Cache Warming
Pre-populate cache with commonly accessed data:

```kotlin
class CacheWarmer @Inject constructor(
    private val cacheManager: CacheManager,
    private val dataSource: DataSource
) {
    
    suspend fun warmCache() {
        // Pre-load frequently accessed data
        val commonKeys = getCommonKeys()
        commonKeys.forEach { key ->
            try {
                val data = dataSource.fetch(key)
                cacheManager.put(key, data, getWarmupPolicy())
            } catch (e: Exception) {
                // Log error but continue warming
                Timber.w(e, "Failed to warm cache for key: $key")
            }
        }
    }
}
```

### Adaptive TTL
Adjust TTL based on access patterns:

```kotlin
class AdaptiveCachePolicy : CachePolicy {
    private val accessPatterns = mutableMapOf<String, MutableList<Instant>>()
    
    fun updateTtl(key: String, newTtl: Duration) {
        // Adjust TTL based on access frequency
        val accesses = accessPatterns[key] ?: emptyList()
        val frequency = calculateAccessFrequency(accesses)
        
        // Increase TTL for frequently accessed items
        val adjustedTtl = if (frequency > FREQUENCY_THRESHOLD) {
            newTtl * 2
        } else {
            newTtl
        }
        
        // Store adjusted TTL
    }
}
```

## Testing Cache Management

### Unit Testing Pattern
```kotlin
@Test
fun `cache put and get operations work correctly`() = runTest {
    // Given
    val cacheManager = InMemoryCacheManager()
    val testData = "test data"
    val testKey = "test-key"
    
    // When
    cacheManager.put(testKey, testData)
    val result = cacheManager.get<String>(testKey)
    
    // Then
    assertEquals(testData, result)
}

@Test
fun `expired cache entries are removed`() = runTest {
    // Given
    val cacheManager = InMemoryCacheManager()
    val testData = "test data"
    val testKey = "test-key"
    val shortTtlPolicy = CachePolicy(
        strategy = CacheStrategy.TTL,
        ttl = 1.milliseconds,
        maxSize = 100,
        evictionPolicy = EvictionPolicy.LRU
    )
    
    // When
    cacheManager.put(testKey, testData, shortTtlPolicy)
    delay(5) // Wait for expiration
    
    // Then
    assertNull(cacheManager.get<String>(testKey))
}
```

### Integration Testing
```kotlin
@HiltTest
class CacheIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var cacheManager: CacheManager
    
    @Test
    fun `cache metrics are updated correctly`() = runTest {
        // Given
        val testKey = "metric-test"
        val testData = "test data"
        
        // When
        cacheManager.put(testKey, testData)
        val result1 = cacheManager.get<String>(testKey) // Cache hit
        val result2 = cacheManager.get<String>("non-existent") // Cache miss
        
        // Then
        assertNotNull(result1)
        assertNull(result2)
        
        // Verify metrics
        val metrics = cacheManager.metrics
        assertTrue(metrics.hitCount >= 1)
        assertTrue(metrics.missCount >= 1)
    }
}
```

## Troubleshooting

### Common Issues

#### Cache Miss Issues
- **Symptom**: High cache miss rate
- **Solution**: Review TTL settings and access patterns

#### Memory Issues
- **Symptom**: OutOfMemoryError or high memory usage
- **Solution**: Adjust cache size limits and eviction policies

#### Stale Data Issues
- **Symptom**: Serving outdated data
- **Solution**: Review TTL settings and implement proper invalidation

#### Thread Safety Issues
- **Symptom**: ConcurrentModificationException
- **Solution**: Ensure thread-safe cache implementations

### Debugging Tips
- Enable detailed logging for cache operations
- Monitor cache hit ratios to identify performance issues
- Use cache statistics to optimize configuration
- Implement cache warming for critical data