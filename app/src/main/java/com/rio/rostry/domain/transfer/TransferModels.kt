package com.rio.rostry.domain.transfer

import com.rio.rostry.domain.model.VerificationStatus

/**
 * Transfer search request with filters.
 */
data class TransferSearchRequest(
    val userId: String,
    val query: String,
    val filters: TransferFilters
)

/**
 * Filters for product search in transfer workflow.
 */
data class TransferFilters(
    val category: String? = null,
    val verificationStatus: VerificationStatus? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null
)

/**
 * Recipient search request.
 */
data class RecipientSearchRequest(
    val query: String,
    val excludeUserId: String
)

/**
 * Transfer conflict detected during transfer process.
 */
data class TransferConflict(
    val field: String,
    val localValue: Any,
    val remoteValue: Any,
    val conflictType: ConflictType
)

/**
 * Types of conflicts that can occur during transfer.
 */
enum class ConflictType {
    OWNERSHIP_MISMATCH,
    STATUS_MISMATCH,
    DATA_INCONSISTENCY
}

/**
 * Result of a transfer operation.
 */
sealed class TransferOperationResult {
    data class Success(val transferId: String) : TransferOperationResult()
    data class Failure(val error: String) : TransferOperationResult()
    data class ConflictDetected(val conflicts: List<TransferConflict>) : TransferOperationResult()
}

/**
 * Product search result for transfer.
 */
data class TransferProductResult(
    val productId: String,
    val name: String,
    val breed: String?,
    val category: String?,
    val verificationStatus: VerificationStatus?,
    val price: Double?,
    val imageUrl: String?,
    val ownerId: String
)

/**
 * Recipient search result.
 */
data class RecipientResult(
    val userId: String,
    val name: String,
    val email: String?,
    val username: String?,
    val profileImageUrl: String?
)
