package com.rio.rostry.core.model

/**
 * Domain model for farm verification.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class FarmVerification(
    val id: String,
    val farmerId: String,
    val farmLocationLat: Double?,
    val farmLocationLng: Double?,
    val farmAddressLine1: String?,
    val farmAddressLine2: String?,
    val farmCity: String?,
    val farmState: String?,
    val farmPostalCode: String?,
    val farmCountry: String?,
    val verificationDocumentUrls: String, // JSON string
    val gpsAccuracy: Float?,
    val gpsTimestamp: Long?,
    val status: String, // UNVERIFIED, PENDING, VERIFIED, REJECTED
    val submittedAt: Long?,
    val reviewedAt: Long?,
    val reviewedBy: String?,
    val rejectionReason: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)
