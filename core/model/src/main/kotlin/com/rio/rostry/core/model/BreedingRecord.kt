package com.rio.rostry.core.model

/**
 * Domain model representing a breeding record.
 * 
 * Tracks parentage relationships for lineage and traceability.
 */
data class BreedingRecord(
    val recordId: String,
    val childId: String,
    val parentId: String,
    val partnerId: String,
    val breedingDate: Long?,
    val hatchDate: Long?,
    val success: Boolean,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)
