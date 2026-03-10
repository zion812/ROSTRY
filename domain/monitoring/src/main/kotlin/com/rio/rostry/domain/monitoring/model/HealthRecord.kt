package com.rio.rostry.domain.monitoring.model

import java.time.Instant

/**
 * Domain model for health record.
 * 
 * Phase 2: Domain and Data Decoupling
 * Represents a health tracking record for a farm asset.
 */
data class HealthRecord(
    val id: String,
    val farmAssetId: String,
    val farmerId: String,
    val recordType: HealthRecordType,
    val status: String,
    val symptoms: List<String>?,
    val diagnosis: String?,
    val treatment: String?,
    val medications: List<String>?,
    val notes: String?,
    val recordedBy: String,
    val recordedAt: Instant,
    val updatedAt: Instant
)

/**
 * Type of health record.
 */
enum class HealthRecordType {
    CHECKUP,
    VACCINATION,
    ILLNESS,
    INJURY,
    TREATMENT,
    OBSERVATION
}
