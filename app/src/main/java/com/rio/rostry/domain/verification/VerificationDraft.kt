package com.rio.rostry.domain.verification

/**
 * Represents a verification draft for a product.
 * Multiple drafts can exist for a single product and are merged into a final verification.
 */
data class VerificationDraft(
    val draftId: String,
    val productId: String,
    val verifierId: String,
    val fields: Map<String, Any>,
    val status: DraftStatus,
    val createdAt: Long,
    val mergedAt: Long? = null,
    val mergedInto: String? = null
)

/**
 * Status of a verification draft
 */
enum class DraftStatus {
    PENDING,    // Draft is being worked on
    MERGED,     // Draft has been merged into final verification
    DISCARDED   // Draft was discarded without merging
}

/**
 * Request to merge multiple drafts into a final verification
 */
data class DraftMergeRequest(
    val productId: String,
    val draftIds: List<String>,
    val conflictResolutions: Map<String, Any> = emptyMap()
)

/**
 * Result of a draft merge operation
 */
sealed class VerificationResult {
    data class Success(val verificationId: String) : VerificationResult()
    data class ConflictsDetected(val conflicts: List<FieldConflict>) : VerificationResult()
    data class Failure(val error: String) : VerificationResult()
}

/**
 * Represents a conflict between draft fields
 */
data class FieldConflict(
    val fieldName: String,
    val values: Map<String, Any> // draftId to value mapping
)
