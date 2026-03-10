package com.rio.rostry.core.model

/**
 * Domain model for mortality records.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class MortalityRecord(
    val id: String,
    val productId: String?,
    val quantity: Double,
    val cause: String,
    val date: Long,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
