package com.rio.rostry.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a structured verification submission.
 */
data class VerificationSubmission(
    val submissionId: String = "",
    val userId: String = "",
    val upgradeType: UpgradeType = UpgradeType.GENERAL_TO_FARMER,
    val currentRole: UserType = UserType.GENERAL,
    val targetRole: UserType? = null,
    val currentStatus: VerificationStatus = VerificationStatus.PENDING,
    val documentUrls: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val documentTypes: Map<String, String> = emptyMap(), // URL to Type mapping
    val farmLocation: Map<String, Double>? = null, // lat, lng
    val additionalData: Map<String, Any> = emptyMap(),
    
    @ServerTimestamp
    val submittedAt: Date? = null,
    
    @ServerTimestamp
    val reviewedAt: Date? = null,
    
    val reviewedBy: String? = null,
    val rejectionReason: String? = null
)
