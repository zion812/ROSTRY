package com.rio.rostry.domain.manager

import kotlinx.coroutines.flow.Flow

/**
 * Manages application configuration from remote sources with local caching.
 */
interface ConfigurationManager {

    /** Load configuration from remote, cache, or defaults (in priority order). */
    suspend fun load(): Result<AppConfiguration>

    /** Force refresh from remote source. */
    suspend fun refresh(): Result<Unit>

    /** Get current configuration (returns cached or defaults, never blocks). */
    fun get(): AppConfiguration

    /** Validate a configuration against the schema. */
    fun validate(config: AppConfiguration): ConfigValidationResult

    /** Observe configuration changes. */
    fun observe(): Flow<AppConfiguration>
}

sealed class ConfigValidationResult {
    object Valid : ConfigValidationResult()
    data class Invalid(val errors: List<String>) : ConfigValidationResult()
}
