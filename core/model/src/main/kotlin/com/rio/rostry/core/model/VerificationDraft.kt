package com.rio.rostry.core.model

/**
 * Domain model representing a verification draft.
 * 
 * Stores form state for farmer/enthusiast verification processes
 * to allow users to resume incomplete verifications.
 */
data class VerificationDraft(
    val draftId: String,
    val userId: String,
    val upgradeType: String?,
    val farmLocation: FarmLocation?,
    val uploadedImages: List<String>,
    val uploadedDocuments: List<String>,
    val uploadedImageTypes: Map<String, String>,
    val uploadedDocTypes: Map<String, String>,
    val uploadProgress: Map<String, Int>,
    val lastSavedAt: Long,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Farm location data for verification.
 */
data class FarmLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val region: String?,
    val district: String?
)
