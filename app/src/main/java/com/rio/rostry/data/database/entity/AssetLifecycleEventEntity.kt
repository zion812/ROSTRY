package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asset_lifecycle_events")
data class AssetLifecycleEventEntity(
    @PrimaryKey val eventId: String,
    val assetId: String,
    val farmerId: String,
    val eventType: String, // ACQUISITION, STAGE_CHANGE, TREATMENT, TRANSFER, DISPOSITION
    val fromStage: String?,
    val toStage: String?,
    val eventData: String, // JSON metadata
    val triggeredBy: String, // USER, SYSTEM, SCHEDULE
    val occurredAt: Long,
    val recordedAt: Long = System.currentTimeMillis(),
    val recordedBy: String,
    val notes: String?,
    val mediaItemsJson: String?,
    val dirty: Boolean = true,
    val syncedAt: Long?
)
