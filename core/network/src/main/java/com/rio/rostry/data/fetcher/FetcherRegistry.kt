package com.rio.rostry.data.fetcher

import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

/**
 * Registry to define and hold all available fetchers in the system.
 * Acts as a central catalog for the coordination layer.
 */
@Singleton
class FetcherRegistry @Inject constructor() {

    private val fetchers = ConcurrentHashMap<String, FetcherDefinition<*>>()

    fun <T> register(definition: FetcherDefinition<T>) {
        fetchers[definition.id] = definition
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(id: String): FetcherDefinition<T>? {
        return fetchers[id] as? FetcherDefinition<T>
    }

    fun getAll(): List<FetcherDefinition<*>> = fetchers.values.toList()

    companion object {
        // Common Farmer Fetcher IDs
        const val ID_DAILY_LOG = "fetcher_daily_log"
        const val ID_TASKS = "fetcher_tasks"
        const val ID_VACCINATION = "fetcher_vaccination"
        const val ID_GROWTH = "fetcher_growth"
        const val ID_MORTALITY = "fetcher_mortality"
        const val ID_FEED = "fetcher_feed"
        const val ID_WEATHER = "fetcher_weather"
        const val ID_USER_PROFILE = "fetcher_user_profile"
        const val ID_PRODUCTS = "fetcher_products"
    }
}

/**
 * Metadata defining a data fetcher.
 * 
 * @param id Unique identifier for the fetcher
 * @param name Human-readable name
 * @param refreshIntervalMs How often to auto-refresh (default 15 min)
 * @param cacheTtlMs How long cache is considered valid
 * @param priority Default priority for this fetcher
 * @param dependencies List of other fetcher IDs this one depends on
 */
data class FetcherDefinition<T>(
    val id: String,
    val name: String,
    val refreshIntervalMs: Long = 15 * 60 * 1000L,
    val cacheTtlMs: Long = 5 * 60 * 1000L,
    val priority: FetcherPriority = FetcherPriority.NORMAL,
    val dependencies: List<String> = emptyList()
)

enum class FetcherPriority {
    CRITICAL,   // User is waiting (e.g., Pull-to-refresh, navigation)
    HIGH,       // Visible on screen
    NORMAL,     // Background sync
    LOW         // Prefetching, Analyics
}

data class ClientRequest(
    val fetcherId: String,
    val forceRefresh: Boolean = false,
    val priority: FetcherPriority = FetcherPriority.NORMAL,
    val params: Map<String, Any> = emptyMap()
)
