package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for user management operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface UserRepository {
    /**
     * Get user by ID.
     * @param userId The user ID
     * @return Result containing the user or error
     */
    suspend fun getUserById(userId: String): Result<User>

    /**
     * Update user profile.
     * @param user The updated user data
     * @return Result indicating success or error
     */
    suspend fun updateUser(user: User): Result<Unit>

    /**
     * Create a new user.
     * @param user The user to create
     * @return Result containing the created user or error
     */
    suspend fun createUser(user: User): Result<User>

    /**
     * Observe user profile changes.
     * @param userId The user ID to observe
     * @return Flow of user updates
     */
    fun observeUser(userId: String): Flow<User?>

    /**
     * Set user type (role).
     * @param userId The user ID
     * @param userType The new user type
     * @return Result indicating success or error
     */
    suspend fun setUserType(userId: String, userType: String): Result<Unit>

    /**
     * Get current user ID.
     * @return The current user ID or null if not authenticated
     */
    fun getCurrentUserId(): String?
}
