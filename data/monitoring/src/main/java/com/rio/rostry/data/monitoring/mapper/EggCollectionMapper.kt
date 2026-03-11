package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.EggCollection
import com.rio.rostry.data.database.entity.EggCollectionEntity

/**
 * Maps EggCollectionEntity to EggCollection domain model.
 */
fun EggCollectionEntity.toEggCollection(): EggCollection {
    return EggCollection(
        collectionId = this.collectionId,
        pairId = this.pairId,
        farmerId = this.farmerId,
        eggsCollected = this.eggsCollected,
        collectedAt = this.collectedAt,
        qualityGrade = this.qualityGrade,
        weight = this.weight,
        notes = this.notes,
        goodCount = this.goodCount,
        damagedCount = this.damagedCount,
        brokenCount = this.brokenCount,
        trayLayoutJson = this.trayLayoutJson,
        setForHatching = this.setForHatching,
        linkedBatchId = this.linkedBatchId,
        setForHatchingAt = this.setForHatchingAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        dirty = this.dirty,
        syncedAt = this.syncedAt
    )
}

/**
 * Maps EggCollection domain model to EggCollectionEntity.
 */
fun EggCollection.toEntity(): EggCollectionEntity {
    return EggCollectionEntity(
        collectionId = this.collectionId,
        pairId = this.pairId,
        farmerId = this.farmerId,
        eggsCollected = this.eggsCollected,
        collectedAt = this.collectedAt,
        qualityGrade = this.qualityGrade,
        weight = this.weight,
        notes = this.notes,
        goodCount = this.goodCount,
        damagedCount = this.damagedCount,
        brokenCount = this.brokenCount,
        trayLayoutJson = this.trayLayoutJson,
        setForHatching = this.setForHatching,
        linkedBatchId = this.linkedBatchId,
        setForHatchingAt = this.setForHatchingAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        dirty = this.dirty,
        syncedAt = this.syncedAt
    )
}
