package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing role upgrade requests.
 * 
 * Handles user requests to upgrade their role (e.g., from Enthusiast to Farmer)
 * and admin approval/rejection of these requests.
 */
interface RoleUpgradeRequestRepository {
    /**
     * Submits a role upgrade request.
     * 
     * @param userId The user ID
     * @param currentRole The user's current role
     * @param requestedRole The requested role
     * @return Result containing the request ID
     */
    suspend fun submitRequest(
        userId: String,
        currentRole: String,
        requestedRole: String
    ): Result<String>
    
    /**
     * Gets the pending request for a user.
     * 
     * @param userId The user ID
     * @return Result containing the request or null
     */
    suspend fun getPendingRequest(userId: String): Result<RoleUpgradeRequestData?>
    
    /**
     * Observes all pending requests.
     * 
     * @return Flow emitting list of pending requests
     */
    fun observePendingRequests(): Flow<List<RoleUpgradeRequestData>>
    
    /**
     * Observes all processed requests.
     * 
     * @return Flow emitting list of processed requests
     */
    fun observeProcessedRequests(): Flow<List<RoleUpgradeRequestData>>
    
    /**
     * Approves a role upgrade request.
     * 
     * @param requestId The request ID
     * @param adminId The admin ID
     * @param notes Optional admin notes
     * @return Result indicating success or failure
     */
    suspend fun approveRequest(
        requestId: String,
        adminId: String,
        notes: String?
    ): Result<Unit>
    
    /**
     * Rejects a role upgrade request.
     * 
     * @param requestId The request ID
     * @param adminId The admin ID
     * @param notes Optional admin notes
     * @return Result indicating success or failure
     */
    suspend fun rejectRequest(
        requestId: String,
        adminId: String,
        notes: String?
    ): Result<Unit>
}

/**
 * Domain model for role upgrade request data.
 */
data class RoleUpgradeRequestData(
    val requestId: String,
    val userId: String,
    val currentRole: String,
    val requestedRole: String,
    val status: String,
    val reviewedBy: String?,
    val adminNotes: String?,
    val reviewedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long
)

