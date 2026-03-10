package com.rio.rostry.core.model

/**
 * Domain model for product tracking.
 * Represents the tracking history of a product through its lifecycle.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Farm Domain model creation
 */
data class ProductTracking(
    val trackingId: String,
    val productId: String,
    val ownerId: String,
    val status: String,
    val metadataJson: String? = null,
    val timestamp: Long,
    val createdAt: Long,
    val updatedAt: Long
)
