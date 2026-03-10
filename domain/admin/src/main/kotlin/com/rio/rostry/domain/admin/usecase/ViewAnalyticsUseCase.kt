package com.rio.rostry.domain.admin.usecase

import kotlinx.coroutines.flow.Flow

/**
 * Use case for viewing system analytics and logs.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface ViewAnalyticsUseCase {
    /**
     * Observe all users in the system.
     * @return Flow of users
     */
    fun getAllUsers(): Flow<List<com.rio.rostry.core.model.User>>

    /**
     * Observe system logs.
     * @return Flow of log entries
     */
    fun getSystemLogs(): Flow<List<String>>
}
