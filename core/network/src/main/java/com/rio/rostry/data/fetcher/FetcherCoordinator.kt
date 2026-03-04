package com.rio.rostry.data.fetcher

import com.rio.rostry.data.cache.CacheHealthMonitor
import com.rio.rostry.data.cache.CacheManager
import com.rio.rostry.data.health.FetcherHealthCheck
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.delay

/**
 * Central service to coordinate all data fetching operations.
 * Implements deduplication, priority queueing, and batching.
 * Integrates with health monitoring and cache metrics.
 */
@Singleton
class FetcherCoordinator @Inject constructor(
    private val registry: FetcherRegistry,
    private val requestCoalescer: RequestCoalescer,
    private val fetcherHealthCheck: FetcherHealthCheck,
    private val cacheHealthMonitor: CacheHealthMonitor,
    private val cacheManager: CacheManager
) {

    private val coordinatorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Track active requests to enable cancellation and monitoring
    private val activeRequests = ConcurrentHashMap<String, RequestStatus>()

    // Event bus for fetcher events
    // Configured with buffer to prevent blocking execution when no subscribers are present
    private val _events = MutableSharedFlow<FetcherEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    /**
     * Main entry point for fetching data.
     * deduplicates requests and routes through the coalescer.
     */
    fun <T> fetch(request: ClientRequest): Flow<Resource<T>> {
        val definition = registry.get<T>(request.fetcherId)
            ?: return flow { emit(Resource.Error("Fetcher not registered: ${request.fetcherId}")) }

        val requestKey = generateRequestKey(request)

        // Delegate to coalescer to handle merging simultaneous requests
        return requestCoalescer.executeOrJoin(requestKey, request) {
            executeFetch(definition, request)
        }
    }

    /**
     * Executes the actual fetch logic with cache integration and health monitoring.
     */
    private fun <T> executeFetch(definition: FetcherDefinition<T>, request: ClientRequest): Flow<Resource<T>> = flow {
        trackRequestStart(request)
        val startTime = System.currentTimeMillis()
        
        try {
            @Suppress("UNCHECKED_CAST")
            emit(Resource.Loading<T>() as Resource<T>)
            
            // 1. Check cache first (unless force refresh)
            if (!request.forceRefresh) {
                val cached = cacheManager.get<T>(request.fetcherId, request.params.hashCode().toString())
                if (cached != null) {
                    val latencyMs = System.currentTimeMillis() - startTime
                    
                    // Check if cache is still valid
                    val cacheEntry = cacheManager.getCacheEntry<T>(request.fetcherId, request.params.hashCode().toString())
                    val isStale = cacheEntry?.isExpired() == true
                    
                    if (!isStale) {
                        // Cache HIT - record metrics and return
                        cacheHealthMonitor.recordHit(request.fetcherId)
                        fetcherHealthCheck.recordSuccess(request.fetcherId, latencyMs)
                        trackRequestSuccess(request, latencyMs)
                        
                        @Suppress("UNCHECKED_CAST")
                        emit(Resource.Success(cached) as Resource<T>)
                        return@flow
                    } else {
                        // Stale cache - serve stale-while-revalidate
                        cacheHealthMonitor.recordStaleServed(request.fetcherId)
                        @Suppress("UNCHECKED_CAST")
                        emit(Resource.Success(cached) as Resource<T>)
                        // Continue to fetch fresh data below
                    }
                } else {
                    // Cache MISS
                    cacheHealthMonitor.recordMiss(request.fetcherId)
                }
            } else {
                // Force refresh = cache miss
                cacheHealthMonitor.recordMiss(request.fetcherId)
            }
            
            // 2. Fetch from source (simulated delay for now)
            val delayMs = when(request.priority) {
                FetcherPriority.CRITICAL -> 100L
                FetcherPriority.HIGH -> 300L
                else -> 500L
            }
            delay(delayMs)
            
            val latencyMs = System.currentTimeMillis() - startTime
            
            // 3. Record success metrics
            fetcherHealthCheck.recordSuccess(request.fetcherId, latencyMs)
            trackRequestSuccess(request, latencyMs)
            
            // TODO: In real impl, this would be actual data from repository
            // For now, emit success with null to satisfy flow collectors
            @Suppress("UNCHECKED_CAST")
            emit(Resource.Success(null as T))

        } catch (e: Exception) {
            val latencyMs = System.currentTimeMillis() - startTime
            
            // Record failure metrics
            fetcherHealthCheck.recordFailure(request.fetcherId, e)
            trackRequestError(request, e)
            
            @Suppress("UNCHECKED_CAST")
            emit(Resource.Error<T>(e.message ?: "Unknown fetch error") as Resource<T>)
        }
    }.flowOn(Dispatchers.IO)

    private fun generateRequestKey(request: ClientRequest): String {
        // Key based on ID and params (priority doesn't change the data nature)
        return "${request.fetcherId}_${request.params.hashCode()}"
    }

    private suspend fun trackRequestStart(request: ClientRequest) {
        activeRequests[request.fetcherId] = RequestStatus.RUNNING
        _events.emit(FetcherEvent.Started(request))
    }

    private suspend fun trackRequestSuccess(request: ClientRequest, latencyMs: Long = 0) {
        activeRequests[request.fetcherId] = RequestStatus.IDLE
        _events.emit(FetcherEvent.Completed(request, latencyMs))
    }

    private suspend fun trackRequestError(request: ClientRequest, e: Throwable) {
        activeRequests[request.fetcherId] = RequestStatus.ERROR
        _events.emit(FetcherEvent.Failed(request, e))
    }
}

enum class RequestStatus {
    IDLE, RUNNING, ERROR
}

sealed class FetcherEvent {
    data class Started(val request: ClientRequest) : FetcherEvent()
    data class Completed(val request: ClientRequest, val latencyMs: Long = 0) : FetcherEvent()
    data class Failed(val request: ClientRequest, val error: Throwable) : FetcherEvent()
}
