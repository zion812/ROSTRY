package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for user profile management operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Defines user profile interfaces without implementation details.
 */
interface UserRepository {
    /**
     * Get user by ID.
     * @param userId The user ID to fetch
     * @return Result containing the user or error
     */
    suspend fun getUserById(userId: String): Result<User>
    
    /**
     * Observe user by ID.
     * @param userId The user ID to observe
     * @return Flow emitting the user or null if not found
     */
    fun observeUserById(userId: String): Flow<User?>
    
    /**
     * Update user profile.
     * @param user The user with updated information
     * @return Result indicating success or error
     */
    suspend fun updateUserProfile(user: User): Result<Unit>
    
    /**
     * Update user display name.
     * @param userId The user ID
     * @param displayName The new display name
     * @return Result indicating success or error
     */
    suspend fun updateDisplayName(userId: String, displayName: String): Result<Unit>
    
    /**
     * Update user photo URL.
     * @param userId The user ID
     * @param photoUrl The new photo URL
     * @return Result indicating success or error
     */
    suspend fun updatePhotoUrl(userId: String, photoUrl: String): Result<Unit>
    
    /**
     * Delete user account.
     * @param userId The user ID to delete
     * @return Result indicating success or error
     */
    suspend fun deleteUser(userId: String): Result<Unit>
}
