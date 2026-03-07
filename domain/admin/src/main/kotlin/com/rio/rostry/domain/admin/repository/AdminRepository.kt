package com.rio.rostry.domain.admin.repository

import com.rio.rostry.core.model.AdminMetrics
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for admin operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface AdminRepository {
    /**
     * Get admin metrics.
     * @return Result containing admin metrics or error
     */
    suspend fun getAdminMetrics(): Result<AdminMetrics>

    /**
     * Get all users.
     * @return Flow of users
     */
    fun getAllUsers(): Flow<List<com.rio.rostry.core.model.User>>

    /**
     * Ban a user.
     * @param userId The user ID to ban
     * @param reason The ban reason
     * @return Result indicating success or error
     */
    suspend fun banUser(userId: String, reason: String): Result<Unit>

    /**
     * Unban a user.
     * @param userId The user ID to unban
     * @return Result indicating success or error
     */
    suspend fun unbanUser(userId: String): Result<Unit>

    /**
     * Get system logs.
     * @return Flow of log entries
     */
    fun getSystemLogs(): Flow<List<String>>

    /**
     * Update feature toggle.
     * @param key The toggle key
     * @param enabled Whether to enable or disable
     * @return Result indicating success or error
     */
    suspend fun updateFeatureToggle(key: String, enabled: Boolean): Result<Unit>
}
