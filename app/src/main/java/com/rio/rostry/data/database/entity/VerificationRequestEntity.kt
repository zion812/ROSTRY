package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing verification requests locally.
 * 
 * This entity tracks the lifecycle of a user's KYC verification request,
 * from draft state through submission and admin review.
 * 
 * Status flow: DRAFT → PENDING → APPROVED/REJECTED
 * - DRAFT: Request created but not yet submitted (upload in progress or failed)
 * - PENDING: Successfully submitted to Firestore, awaiting admin review
 * - APPROVED: Admin approved the verification
 * - REJECTED: Admin rejected with a reason
 */
@Entity(
    tableName = "verification_requests",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"])
    ]
)
data class VerificationRequestEntity(
    @PrimaryKey
    val requestId: String,
    
    /** User ID this request belongs to */
    val userId: String,
    
    /** Firebase Storage URL for government ID document */
    val govtIdUrl: String? = null,
    
    /** Firebase Storage URL for farm photo (selfie with chickens) */
    val farmPhotoUrl: String? = null,
    
    /** Request status: DRAFT, PENDING, APPROVED, REJECTED */
    val status: String = "DRAFT",
    
    /** Reason for rejection (if status is REJECTED) */
    val rejectionReason: String? = null,
    
    /** Timestamp when request was submitted to Firestore */
    val submittedAt: Long? = null,
    
    /** Timestamp when admin reviewed the request */
    val reviewedAt: Long? = null,
    
    /** Creation timestamp */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** Last update timestamp */
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_DRAFT = "DRAFT"
        const val STATUS_PENDING = "PENDING"
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_REJECTED = "REJECTED"
    }
    
    /** Check if request can be edited (only in DRAFT or REJECTED state) */
    fun canEdit(): Boolean = status == STATUS_DRAFT || status == STATUS_REJECTED
    
    /** Check if request is awaiting admin review */
    fun isPending(): Boolean = status == STATUS_PENDING
    
    /** Check if request has been approved */
    fun isApproved(): Boolean = status == STATUS_APPROVED
    
    /** Check if both required documents have been uploaded */
    fun hasAllDocuments(): Boolean = !govtIdUrl.isNullOrBlank() && !farmPhotoUrl.isNullOrBlank()
}
