package com.rio.rostry.domain.admin.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing system configuration.
 * 
 * Handles reading and writing system-wide configuration settings.
 */
interface SystemConfigRepository {
    /**
     * Gets a boolean configuration value.
     * 
     * @param key The configuration key
     * @param default The default value if not set
     * @return Flow emitting the configuration value
     */
    fun getBoolean(key: String, default: Boolean): Flow<Boolean>
    
    /**
     * Sets a boolean configuration value.
     * 
     * @param key The configuration key
     * @param value The value to set
     */
    suspend fun setBoolean(key: String, value: Boolean)
    
    /**
     * Gets a string configuration value.
     * 
     * @param key The configuration key
     * @param default The default value if not set
     * @return Flow emitting the configuration value
     */
    fun getString(key: String, default: String): Flow<String>
    
    /**
     * Sets a string configuration value.
     * 
     * @param key The configuration key
     * @param value The value to set
     */
    suspend fun setString(key: String, value: String)
}
