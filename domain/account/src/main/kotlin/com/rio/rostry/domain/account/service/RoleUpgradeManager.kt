package com.rio.rostry.domain.account.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for role upgrade management.
 *
 * Centralized manager for role changes ensuring consistency across
 * all entry points: prerequisite validation, transition rules,
 * user type updates, role preference persistence, audit logging,
 * and analytics tracking.
 */
interface RoleUpgradeManager {

    /** Start the FARMER → ENTHUSIAST migration process. */
    suspend fun startMigration(userId: String): Result<String>

    /** Submit a request for role upgrade (requires admin approval). */
    suspend fun requestUpgrade(userId: String, targetRole: String, skipValidation: Boolean = false): Result<String>

    /** Check if the user has a pending upgrade request. */
    suspend fun getPendingRequest(userId: String): Result<Map<String, Any>?>

    /** Admin action: Approve a pending request. */
    suspend fun approveRequest(requestId: String, adminId: String, notes: String?): Result<Unit>

    /** Admin action: Reject a pending request. */
    suspend fun rejectRequest(requestId: String, adminId: String, notes: String?): Result<Unit>

    /** Force upgrade role (admin override). */
    suspend fun forceUpgradeRole(userId: String, targetRole: String): Result<Unit>
}
