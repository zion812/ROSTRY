package com.rio.rostry.domain.farm.usecase

import com.rio.rostry.core.common.Result

/**
 * Domain interface for ownership transfer operations.
 *
 * Manages the secure 6-digit code transfer flow:
 * INITIATE → PENDING → CLAIMED → COMPLETED.
 */
interface OwnershipTransferUseCase {

    /** Initiate a new ownership transfer for a bird. */
    suspend fun initiateTransfer(birdId: String, ownerId: String): Result<Map<String, Any>>

    /** Claim a transfer using a 6-digit code. */
    suspend fun claimTransfer(code: String, claimerId: String): Result<Map<String, Any>>

    /** Execute a claimed transfer — atomically transfers ownership. */
    suspend fun executeTransfer(transferId: String): Result<Unit>

    /** Cancel a pending transfer. */
    suspend fun cancelTransfer(transferId: String, userId: String): Result<Unit>

    /** Get pending transfers for a user (as sender or recipient). */
    suspend fun getPendingTransfers(userId: String): Result<List<Map<String, Any>>>
}
