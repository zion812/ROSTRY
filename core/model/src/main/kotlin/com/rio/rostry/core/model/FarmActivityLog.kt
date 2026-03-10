package com.rio.rostry.core.model

/**
 * Domain model for farm activity logs.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class FarmActivityLog(
    val id: String,
    val farmerId: String,
    val productId: String? = null,
    val activityType: String, // EXPENSE, SANITATION, MAINTENANCE, MEDICATION, MORTALITY, FEED, WEIGHT, OTHER
    val amountInr: Double? = null,
    val quantity: Double? = null,
    val category: String? = null,
    val description: String? = null,
    val notes: String? = null,
    val photoUrls: String? = null,
    val mediaItemsJson: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val dirty: Boolean = true,
    val syncedAt: Long? = null
)
