package com.rio.rostry.data.farm.mapper

import com.rio.rostry.core.model.AssetLifecycleEvent
import com.rio.rostry.data.database.entity.AssetLifecycleEventEntity

fun AssetLifecycleEventEntity.toAssetLifecycleEvent(): AssetLifecycleEvent {
    return AssetLifecycleEvent(
        eventId = this.eventId,
        assetId = this.assetId,
        farmerId = this.farmerId,
        eventType = this.eventType,
        fromStage = this.fromStage,
        toStage = this.toStage,
        eventData = this.eventData,
        triggeredBy = this.triggeredBy,
        occurredAt = this.occurredAt,
        recordedAt = this.recordedAt,
        recordedBy = this.recordedBy,
        notes = this.notes,
        mediaItemsJson = this.mediaItemsJson,
        dirty = this.dirty,
        syncedAt = this.syncedAt
    )
}

fun AssetLifecycleEvent.toEntity(): AssetLifecycleEventEntity {
    return AssetLifecycleEventEntity(
        eventId = this.eventId,
        assetId = this.assetId,
        farmerId = this.farmerId,
        eventType = this.eventType,
        fromStage = this.fromStage,
        toStage = this.toStage,
        eventData = this.eventData,
        triggeredBy = this.triggeredBy,
        occurredAt = this.occurredAt,
        recordedAt = this.recordedAt,
        recordedBy = this.recordedBy,
        notes = this.notes,
        mediaItemsJson = this.mediaItemsJson,
        dirty = this.dirty,
        syncedAt = this.syncedAt
    )
}
