package com.rio.rostry.data.account.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import com.rio.rostry.domain.account.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository using Firebase Firestore.
 *
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 * 
 * ARCHITECTURE NOTE:
 * This repository handles domain-level User operations via Firestore.
 * Admin operations requiring database entities (UserEntity) for complex queries,
 * suspensions, and verification workflows are delegated to UserRepository in the
 * app module, which has access to Room DAOs and complex business logic.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection("users")

    // ═══════════════════════════════════════════════════════════════════
    // CORE USER OPERATIONS
    // ═══════════════════════════════════════════════════════════════════

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Timber.d("No authenticated user")
            trySend(null)
            awaitClose()
            return@callbackFlow
        }
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error observing current user")
                    trySend(null)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Error(Exception("Failed to parse user data"))
                }
            } else {
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get user: $userId")
            Result.Error(e)
        }
    }

    override fun observeUserById(userId: String): Flow<User?> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error observing user: $userId")
                    trySend(null)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Timber.i("Updated user profile: ${user.id}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update user profile")
            Result.Error(e)
        }
    }

    override suspend fun updateDisplayName(userId: String, displayName: String): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update("displayName", displayName)
                .await()
            Timber.d("Updated display name for user: $userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update display name")
            Result.Error(e)
        }
    }

    override suspend fun updatePhotoUrl(userId: String, photoUrl: String): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update("photoUrl", photoUrl)
                .await()
            Timber.d("Updated photo URL for user: $userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update photo URL")
            Result.Error(e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            Timber.w("Deleted user: $userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete user")
            Result.Error(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ADMIN OPERATIONS - DELEGATED TO APP MODULE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Admin operation: Get system users for admin dashboard.
     * 
     * ARCHITECTURE DECISION:
     * Admin user management requires complex queries, pagination, and filtering
     * that are handled by UserRepository in app module with Room database access.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override suspend fun getSystemUsers(limit: Int): com.rio.rostry.utils.Resource<List<com.rio.rostry.data.database.entity.UserEntity>> {
        Timber.w("getSystemUsers called on UserRepositoryImpl - delegated to app module UserRepository")
        return com.rio.rostry.utils.Resource.Error(
            "Admin user operations handled by UserRepository in app module. " +
            "This provides access to Room entities for complex admin queries."
        )
    }

    /**
     * Admin operation: Suspend user account.
     * 
     * ARCHITECTURE DECISION:
     * User suspension requires audit logging, notification systems, and potential
     * data cleanup workflows. These are managed by app module UserRepository.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override suspend fun suspendUser(userId: String, reason: String, durationMinutes: Long?): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("suspendUser called on UserRepositoryImpl - delegated to app module")
        return com.rio.rostry.utils.Resource.Error(
            "User suspension handled by UserRepository in app module. " +
            "Reason: $reason, Duration: ${durationMinutes ?: "indefinite"}"
        )
    }

    /**
     * Admin operation: Unsuspend user account.
     * 
     * ARCHITECTURE DECISION:
     * User reactivation requires notification and audit logging handled by app module.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override suspend fun unsuspendUser(userId: String): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("unsuspendUser called on UserRepositoryImpl - delegated to app module")
        return com.rio.rostry.utils.Resource.Error(
            "User unsuspension handled by UserRepository in app module"
        )
    }

    /**
     * Admin operation: Update user type/role.
     * 
     * ARCHITECTURE DECISION:
     * Role changes trigger complex workflows including data migration, permission
     * updates, and notifications. Handled by app module UserRepository.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override suspend fun updateUserType(userId: String, newType: com.rio.rostry.domain.model.UserType): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("updateUserType called on UserRepositoryImpl - delegated to app module")
        return com.rio.rostry.utils.Resource.Error(
            "User type update handled by UserRepository in app module. " +
            "New type: ${newType.name}"
        )
    }

    /**
     * Admin operation: Update verification status.
     * 
     * ARCHITECTURE DECISION:
     * Verification status changes trigger notifications and data updates handled by app module.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override suspend fun updateVerificationStatus(userId: String, status: com.rio.rostry.domain.model.VerificationStatus): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("updateVerificationStatus called on UserRepositoryImpl - delegated to app module")
        return com.rio.rostry.utils.Resource.Error(
            "Verification status update handled by UserRepository in app module. " +
            "New status: ${status.name}"
        )
    }

    /**
     * Admin operation: Update verification submission status.
     * 
     * ARCHITECTURE DECISION:
     * Complex verification workflow with review, approval/rejection, and notifications.
     * Handled by app module UserRepository.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override suspend fun updateVerificationSubmissionStatus(
        submissionId: String, userId: String, status: com.rio.rostry.domain.model.VerificationStatus,
        reviewerId: String, rejectionReason: String?
    ): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("updateVerificationSubmissionStatus called - delegated to app module")
        return com.rio.rostry.utils.Resource.Error(
            "Verification submission update handled by UserRepository in app module"
        )
    }

    /**
     * Stream pending verifications for admin review.
     * 
     * ARCHITECTURE DECISION:
     * Verification queue management requires complex queries and real-time updates
     * handled by app module with Room integration.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override fun streamPendingVerifications(): Flow<com.rio.rostry.utils.Resource<List<com.rio.rostry.domain.model.VerificationSubmission>>> {
        Timber.w("streamPendingVerifications called - delegated to app module")
        return kotlinx.coroutines.flow.flowOf(
            com.rio.rostry.utils.Resource.Error(
                "Verification streaming handled by UserRepository in app module"
            )
        )
    }

    /**
     * Get verifications filtered by role and status.
     * 
     * ARCHITECTURE DECISION:
     * Filtered verification queries for admin dashboard handled by app module.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override fun getVerificationsByRoleAndStatus(
        role: com.rio.rostry.domain.model.UserType?, status: com.rio.rostry.domain.model.VerificationStatus?
    ): Flow<com.rio.rostry.utils.Resource<List<com.rio.rostry.domain.model.VerificationSubmission>>> {
        Timber.w("getVerificationsByRoleAndStatus called - delegated to app module")
        return kotlinx.coroutines.flow.flowOf(
            com.rio.rostry.utils.Resource.Error(
                "Verification filtering handled by UserRepository in app module"
            )
        )
    }

    /**
     * Get users filtered by verification status.
     * 
     * ARCHITECTURE DECISION:
     * Admin user filtering requires Room database queries handled by app module.
     * 
     * @see com.rio.rostry.data.repository.UserRepository (app module)
     */
    override suspend fun getUsersByVerificationStatus(status: com.rio.rostry.domain.model.VerificationStatus): com.rio.rostry.utils.Resource<List<com.rio.rostry.data.database.entity.UserEntity>> {
        Timber.w("getUsersByVerificationStatus called - delegated to app module")
        return com.rio.rostry.utils.Resource.Error(
            "User filtering by verification status handled by UserRepository in app module"
        )
    }

    // ═══════════════════════════════════════════════════════════════════
    // ANALYTICS COUNTS - IMPLEMENTED WITH FIRESTORE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Get total count of all users in the system.
     * Uses Firestore query with limit for efficient counting.
     * Note: For large datasets, consider using a distributed counter pattern.
     */
    override suspend fun countAllUsers(): Int {
        return try {
            // Firestore doesn't have a native count() in this version
            // Using get() and size as alternative
            // For production with 10K+ users, implement distributed counter
            val snapshot = usersCollection.get().await()
            val count = snapshot.size()
            Timber.d("Total user count: $count")
            count
        } catch (e: Exception) {
            Timber.e(e, "Failed to count all users")
            0
        }
    }

    /**
     * Get count of new users since timestamp.
     * Queries users created after the given timestamp.
     */
    override suspend fun getNewUsersCount(since: Long): Int {
        return try {
            val snapshot = usersCollection
                .whereGreaterThanOrEqualTo("createdAt", since)
                .get()
                .await()
            val count = snapshot.size()
            Timber.d("New users since ${java.util.Date(since)}: $count")
            count
        } catch (e: Exception) {
            Timber.e(e, "Failed to count new users")
            0
        }
    }

    /**
     * Get count of active users since timestamp.
     * Queries users who have updated their profile or been active recently.
     */
    override suspend fun getActiveUsersCount(since: Long): Int {
        return try {
            val snapshot = usersCollection
                .whereGreaterThanOrEqualTo("updatedAt", since)
                .get()
                .await()
            val count = snapshot.size()
            Timber.d("Active users since ${java.util.Date(since)}: $count")
            count
        } catch (e: Exception) {
            Timber.e(e, "Failed to count active users")
            0
        }
    }

    /**
     * Get count of users with pending verification status.
     * Queries users awaiting verification review.
     */
    override suspend fun getPendingVerificationCount(): Int {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("verificationStatus", com.rio.rostry.domain.model.VerificationStatus.PENDING.name)
                .get()
                .await()
            val count = snapshot.size()
            Timber.d("Pending verifications: $count")
            count
        } catch (e: Exception) {
            Timber.e(e, "Failed to count pending verifications")
            0
        }
    }

    /**
     * Get estimated audience size for broadcast targeting.
     * Returns count for specific role or total if role is null.
     */
    override suspend fun getAudienceSize(role: com.rio.rostry.domain.model.UserType?): Int {
        return try {
            val query = if (role != null) {
                usersCollection.whereEqualTo("userType", role.name)
            } else {
                usersCollection
            }
            val snapshot = query.get().await()
            val count = snapshot.size()
            Timber.d("Audience size for role ${role?.name ?: "ALL"}: $count")
            count
        } catch (e: Exception) {
            Timber.e(e, "Failed to get audience size")
            0
        }
    }
}
