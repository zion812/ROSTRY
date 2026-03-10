package com.rio.rostry.domain.admin.usecase

import com.rio.rostry.core.model.Result

/**
 * Use case for managing user accounts (ban/unban).
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface ManageUserUseCase {
    /**
     * Ban a user from the platform.
     * @param userId The user ID to ban
     * @param reason The reason for banning
     * @return Result indicating success or error
     */
    suspend fun banUser(userId: String, reason: String): Result<Unit>

    /**
     * Unban a previously banned user.
     * @param userId The user ID to unban
     * @return Result indicating success or error
     */
    suspend fun unbanUser(userId: String): Result<Unit>
}
