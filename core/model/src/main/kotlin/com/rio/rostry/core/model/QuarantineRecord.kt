package com.rio.rostry.core.model

/**
 * Domain model for quarantine records.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class QuarantineRecord(
    val id: String,
    val productId: String,
    val reason: String,
    val status: String,
    val startDate: Long,
    val endDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
