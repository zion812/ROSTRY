package com.rio.rostry.domain.config

import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing application configuration with remote updates and validation.
 */
interface ConfigurationManager {
    /**
     * Load configuration from remote source.
     * Falls back to cache or defaults if remote is unavailable.
     */
    suspend fun load(): Result<AppConfiguration>

    /**
     * Refresh configuration from remote source.
     * Should be called periodically (every 5 minutes).
     */
    suspend fun refresh(): Result<Unit>

    /**
     * Get current configuration synchronously.
     * Returns cached configuration or defaults if not loaded.
     */
    fun get(): AppConfiguration

    /**
     * Validate configuration against defined schemas.
     */
    fun validate(config: AppConfiguration): ValidationResult

    /**
     * Observe configuration changes as a Flow.
     */
    fun observe(): Flow<AppConfiguration>
}
