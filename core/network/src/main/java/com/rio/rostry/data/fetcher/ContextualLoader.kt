package com.rio.rostry.data.fetcher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implements contextual/lazy loading for FarmerHomeScreen fetcher cards.
 * Only loads visible cards, prefetches next 2-3, and unloads off-screen data.
 */
@Singleton
class ContextualLoader @Inject constructor(
    private val fetcherCoordinator: FetcherCoordinator,
    private val registry: FetcherRegistry
) {

    private val loaderScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Track which fetchers are currently visible
    private val visibleFetchers = ConcurrentHashMap.newKeySet<String>()
    
    // Track active loading jobs for cancellation
    private val activeJobs =ConcurrentHashMap<String, Job>()
    
    // Observable loading states
    private val _loadingStates = MutableStateFlow<Map<String, LoadingState>>(emptyMap())
    val loadingStates: StateFlow<Map<String, LoadingState>> = _loadingStates.asStateFlow()

    /**
     * Notify that a fetcher card became visible on screen.
     * Triggers immediate loading if not already loaded.
     */
    fun onFetcherVisible(fetcherId: String) {
        visibleFetchers.add(fetcherId)
        loadFetcher(fetcherId, priority = FetcherPriority.HIGH)
    }

    /**
     * Notify that a fetcher card scrolled off screen.
     * May trigger unloading to reduce memory pressure.
     */
    fun onFetcherHidden(fetcherId: String) {
        visibleFetchers.remove(fetcherId)
        // Optionally unload after delay if memory pressure is high
        // For now, we keep data cached
    }

    /**
     * Prefetch fetchers that are likely to be visible soon.
     * Call this during scroll to prepare next cards.
     */
    fun prefetchNext(fetcherIds: List<String>) {
        fetcherIds.forEach { fetcherId ->
            if (!visibleFetchers.contains(fetcherId) && !isLoaded(fetcherId)) {
                loadFetcher(fetcherId, priority = FetcherPriority.NORMAL)
            }
        }
    }

    /**
     * Reload all visible fetchers (e.g., on pull-to-refresh).
     */
    fun refreshVisible() {
        visibleFetchers.forEach { fetcherId ->
            loadFetcher(fetcherId, priority = FetcherPriority.CRITICAL, forceRefresh = true)
        }
    }

    /**
     * Cancel loading for a specific fetcher.
     */
    fun cancelLoading(fetcherId: String) {
        activeJobs[fetcherId]?.cancel()
        activeJobs.remove(fetcherId)
        updateState(fetcherId, LoadingState.IDLE)
    }

    /**
     * Cancel all active loading operations.
     */
    fun cancelAll() {
        activeJobs.values.forEach { it.cancel() }
        activeJobs.clear()
        _loadingStates.value = emptyMap()
    }

    /**
     * Get currently visible fetcher IDs.
     */
    fun getVisibleFetchers(): Set<String> = visibleFetchers.toSet()

    private fun loadFetcher(
        fetcherId: String,
        priority: FetcherPriority,
        forceRefresh: Boolean = false
    ) {
        // Cancel existing job if any
        activeJobs[fetcherId]?.cancel()
        
        updateState(fetcherId, LoadingState.LOADING)
        
        val job = loaderScope.launch {
            try {
                val request = ClientRequest(
                    fetcherId = fetcherId,
                    priority = priority,
                    forceRefresh = forceRefresh
                )
                
                fetcherCoordinator.fetch<Any>(request).collect { result ->
                    // Update state based on result
                    when (result) {
                        is com.rio.rostry.utils.Resource.Loading -> updateState(fetcherId, LoadingState.LOADING)
                        is com.rio.rostry.utils.Resource.Success -> updateState(fetcherId, LoadingState.LOADED)
                        is com.rio.rostry.utils.Resource.Error -> updateState(fetcherId, LoadingState.ERROR)
                    }
                }
                
            } catch (e: Exception) {
                updateState(fetcherId, LoadingState.ERROR)
            }
        }
        
        activeJobs[fetcherId] = job
    }

    private fun isLoaded(fetcherId: String): Boolean {
        return _loadingStates.value[fetcherId] == LoadingState.LOADED
    }

    private fun updateState(fetcherId: String, state: LoadingState) {
        _loadingStates.value = _loadingStates.value.toMutableMap().apply {
            put(fetcherId, state)
        }
    }
}

enum class LoadingState {
    IDLE,
    LOADING,
    LOADED,
    ERROR
}
