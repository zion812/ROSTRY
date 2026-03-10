package com.rio.rostry.core.model

/**
 * Domain model representing a lifecycle event for a farm asset.
 */
data class AssetLifecycleEvent(
    val eventId: String,
    val assetId: String,
    val farmerId: String,
    val eventType: String,
    val fromStage: String? = null,
    val toStage: String? = null,
    val eventData: String,
    val triggeredBy: String,
    val occurredAt: Long,
    val recordedAt: Long = System.currentTimeMillis(),
    val recordedBy: String,
    val notes: String? = null,
    val mediaItemsJson: String? = null,
    val dirty: Boolean = true,
    val syncedAt: Long? = null
)
