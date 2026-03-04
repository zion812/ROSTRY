package com.rio.rostry.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a structured verification submission.
 * 
 * Each submission has a unique `submissionId` (UUID) and a human-readable `referenceNumber`
 * (e.g., "KYC-20231223-ABC123") that admins can use to identify and communicate with applicants.
 * 
 * Collection path: `verifications/{submissionId}`
 */
data class VerificationSubmission(
    val submissionId: String = "",
    val userId: String = "",
    
    // Human-readable reference for admin communication (e.g., "KYC-20231223-ABC123")
    val referenceNumber: String = "",
    
    // Applicant info for easy admin identification (denormalized from user)
    val applicantName: String? = null,
    val applicantPhone: String? = null,
    
    val upgradeType: UpgradeType = UpgradeType.GENERAL_TO_FARMER,
    val currentRole: UserType = UserType.GENERAL,
    val targetRole: UserType? = null,
    val currentStatus: VerificationStatus = VerificationStatus.PENDING,
    val documentUrls: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val documentTypes: Map<String, String> = emptyMap(), // URL to Type mapping
    val imageTypes: Map<String, String> = emptyMap(), // URL to Type mapping for images
    val farmLocation: Map<String, Double>? = null, // lat, lng
    val farmAddress: String? = null, // Human-readable farm address
    val additionalData: Map<String, Any> = emptyMap(),
    
    @ServerTimestamp
    val submittedAt: Date? = null,
    
    @ServerTimestamp
    val reviewedAt: Date? = null,
    
    val reviewedBy: String? = null,
    val rejectionReason: String? = null
)

