package com.rio.rostry.core.model

/**
 * Domain model for a growth record.
 * 
 * Represents a measurement of growth for a farm asset.
 */
data class GrowthRecord(
    val recordId: String,
    val productId: String,
    val weight: Double?,
    val height: Double?,
    val notes: String?,
    val recordedAt: Long,
    val createdAt: Long
)
