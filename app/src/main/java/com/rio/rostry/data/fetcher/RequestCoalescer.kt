package com.rio.rostry.data.fetcher

import com.rio.rostry.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles "Smart Request Coalescing".
 * Merges multiple simultaneous requests for the same data into a single execution.
 */
@Singleton
class RequestCoalescer @Inject constructor() {

    private val coalescingScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Map of RequestKey -> SharedFlow of results
    private val inFlightRequests = ConcurrentHashMap<String, SharedFlow<Resource<*>>>()
    private val mutex = Mutex()

    /**
     * Executes the [block] or joins an existing execution if one is already in flight for [key].
     * 
     * @param key Unique key for the data request
     * @param windowMs Coalescing window - requests arriving within this time join the batch
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> executeOrJoin(
        key: String, 
        request: ClientRequest, 
        windowMs: Long = 100L,
        block: () -> Flow<Resource<T>>
    ): Flow<Resource<T>> {
        
        // We use a flow builder to manage the joining logic
        return flow {
             val sharedFlow = mutex.withLock {
                 @Suppress("UNCHECKED_CAST")
                 var existing = inFlightRequests[key] as? SharedFlow<Resource<T>>
                 
                 if (existing == null) {
                     // Start new request
                     val upstream = block()
                     
                     // Convert to SharedFlow that caches the last emission (replay=1)
                     // and stays active while subscribed
                     val newShared = upstream
                         .onStart { 
                             // Optional: add a tiny delay to allow other priority requests to coalesce
                             // if needed
                         }
                         .onCompletion {
                             // Clean up the map when the flow completes/is cancelled
                             mutex.withLock {
                                 inFlightRequests.remove(key)
                             }
                         }
                         .shareIn(
                             scope = coalescingScope,
                             started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 0, replayExpirationMillis = 0),
                             replay = 1
                         )
                     
                     inFlightRequests[key] = newShared
                     existing = newShared
                 }
                 existing!!
             }
             
             // Emit values from the shared flow
             sharedFlow.collect { value ->
                 emit(value)
             }
        }
    }
}
