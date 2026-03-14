package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.model.VerificationSubmission
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for user profile management operations.
 *
 * Phase 2: Domain and Data Decoupling
 * Defines user profile interfaces without implementation details.
 */
interface UserRepository {
    /**
     * Get current authenticated user.
     * @return Flow emitting the current user or null if not authenticated
     */
    fun getCurrentUser(): Flow<User?>

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

    // ── Admin Features ──────────────────────────────────────────────

    suspend fun getSystemUsers(limit: Int = 50): Resource<List<UserEntity>>

    suspend fun suspendUser(userId: String, reason: String, durationMinutes: Long? = null): Resource<Unit>

    suspend fun unsuspendUser(userId: String): Resource<Unit>

    suspend fun updateUserType(userId: String, newType: UserType): Resource<Unit>

    suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Resource<Unit>

    suspend fun updateVerificationSubmissionStatus(
        submissionId: String, userId: String, status: VerificationStatus,
        reviewerId: String, rejectionReason: String? = null
    ): Resource<Unit>

    fun streamPendingVerifications(): Flow<Resource<List<VerificationSubmission>>>

    fun getVerificationsByRoleAndStatus(role: UserType?, status: VerificationStatus?): Flow<Resource<List<VerificationSubmission>>>

    suspend fun getUsersByVerificationStatus(status: VerificationStatus): Resource<List<UserEntity>>

    // ── Analytics ────────────────────────────────────────────────────

    suspend fun countAllUsers(): Int

    suspend fun getNewUsersCount(since: Long): Int

    suspend fun getActiveUsersCount(since: Long): Int

    suspend fun getPendingVerificationCount(): Int

    suspend fun getAudienceSize(role: UserType?): Int
}
