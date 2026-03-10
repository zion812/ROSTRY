package com.rio.rostry.core.model

/**
 * Domain model for vaccination records.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class VaccinationRecord(
    val id: String,
    val productId: String,
    val vaccineName: String,
    val date: Long,
    val nextDueDate: Long? = null,
    val dosage: String? = null,
    val notes: String? = null,
    val farmerId: String,
    val createdAt: Long,
    val updatedAt: Long
)
