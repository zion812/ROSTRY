package com.rio.rostry.data.fetcher

import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * DSL for declaratively defining fetchers with minimal boilerplate.
 * Provides auto-generated error handling, caching, and logging.
 */

/**
 * DSL entry point for building a fetcher definition.
 */
fun <T> fetcher(
    id: String,
    block: FetcherBuilder<T>.() -> Unit
): FetcherDefinition<T> {
    val builder = FetcherBuilder<T>(id)
    builder.block()
    return builder.build()
}

/**
 * Builder class for the fetcher DSL.
 */
class FetcherBuilder<T>(private val id: String) {
    var name: String = id
    var refreshInterval: Long = 15 * 60 * 1000L // 15 min default
    var cacheTtl: Long = 5 * 60 * 1000L // 5 min default
    var priority: FetcherPriority = FetcherPriority.NORMAL
    private var dependencies: MutableList<String> = mutableListOf()
    
    /**
     * Set display name for the fetcher.
     */
    fun name(value: String) {
        name = value
    }
    
    /**
     * Set refresh interval in milliseconds.
     */
    fun refreshEvery(ms: Long) {
        refreshInterval = ms
    }
    
    /**
     * Set cache TTL in milliseconds.
     */
    fun cacheFor(ms: Long) {
        cacheTtl = ms
    }
    
    /**
     * Set priority level.
     */
    fun priority(value: FetcherPriority) {
        priority = value
    }
    
    /**
     * Add a dependency on another fetcher.
     */
    fun dependsOn(fetcherId: String) {
        dependencies.add(fetcherId)
    }
    
    /**
     * Add multiple dependencies.
     */
    fun dependsOn(vararg fetcherIds: String) {
        dependencies.addAll(fetcherIds)
    }
    
    internal fun build(): FetcherDefinition<T> {
        return FetcherDefinition(
            id = id,
            name = name,
            refreshIntervalMs = refreshInterval,
            cacheTtlMs = cacheTtl,
            priority = priority,
            dependencies = dependencies.toList()
        )
    }
}

/**
 * Convenience extensions for common time durations.
 */
object FetcherDurations {
    const val SECONDS_30 = 30 * 1000L
    const val MINUTES_1 = 60 * 1000L
    const val MINUTES_2 = 2 * 60 * 1000L
    const val MINUTES_5 = 5 * 60 * 1000L
    const val MINUTES_15 = 15 * 60 * 1000L
    const val MINUTES_30 = 30 * 60 * 1000L
    const val HOURS_1 = 60 * 60 * 1000L
}

/**
 * Example usage of the DSL:
 * 
 * val dailyLogFetcher = fetcher<DailyLog>("fetcher_daily_log") {
 *     name("Daily Log")
 *     refreshEvery(FetcherDurations.MINUTES_5)
 *     cacheFor(FetcherDurations.MINUTES_2)
 *     priority(FetcherPriority.HIGH)
 *     dependsOn("fetcher_user_profile")
 * }
 */

/**
 * Registry extension to register fetchers using DSL.
 */
fun <T> FetcherRegistry.register(
    id: String,
    block: FetcherBuilder<T>.() -> Unit
) {
    val definition = fetcher(id, block)
    register(definition)
}

/**
 * Compose multiple fetchers into a single combined fetcher.
 */
fun <T> composeFetchers(
    id: String,
    name: String,
    vararg fetchers: FetcherDefinition<*>
): FetcherDefinition<T> {
    val allDependencies = fetchers.flatMap { it.dependencies } + fetchers.map { it.id }
    return FetcherDefinition(
        id = id,
        name = name,
        refreshIntervalMs = fetchers.minOfOrNull { it.refreshIntervalMs } ?: FetcherDurations.MINUTES_15,
        cacheTtlMs = fetchers.minOfOrNull { it.cacheTtlMs } ?: FetcherDurations.MINUTES_5,
        priority = fetchers.maxOfOrNull { it.priority } ?: FetcherPriority.NORMAL,
        dependencies = allDependencies.distinct()
    )
}
