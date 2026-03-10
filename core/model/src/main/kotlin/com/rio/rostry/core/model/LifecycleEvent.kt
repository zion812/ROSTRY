package com.rio.rostry.core.model

/**
 * Domain model representing a lifecycle event for a product.
 * 
 * Tracks significant events in a product's lifecycle.
 */
data class LifecycleEvent(
    val eventId: String,
    val productId: String,
    val eventType: String,
    val eventDate: Long,
    val description: String?,
    val metadata: String?,
    val createdAt: Long
)
