package com.rio.rostry.data.farm.mapper

import com.rio.rostry.core.model.BreedingRecord
import com.rio.rostry.core.model.FamilyTree
import com.rio.rostry.core.model.LifecycleEvent
import com.rio.rostry.data.database.entity.BreedingRecordEntity
import com.rio.rostry.data.database.entity.FamilyTreeEntity
import com.rio.rostry.data.database.entity.LifecycleEventEntity

/**
 * Maps BreedingRecordEntity to BreedingRecord domain model.
 */
fun BreedingRecordEntity.toBreedingRecord(): BreedingRecord =
    BreedingRecord(
        recordId = this.recordId,
        childId = this.childId,
        parentId = this.parentId,
        partnerId = this.partnerId,
        breedingDate = null,
        hatchDate = null,
        success = this.success,
        notes = this.notes,
        createdAt = this.timestamp,
        updatedAt = this.timestamp
    )

/**
 * Maps BreedingRecord domain model to BreedingRecordEntity.
 */
fun BreedingRecord.toEntity(): BreedingRecordEntity =
    BreedingRecordEntity(
        recordId = this.recordId,
        parentId = this.parentId,
        partnerId = this.partnerId,
        childId = this.childId,
        success = this.success,
        notes = this.notes,
        timestamp = this.createdAt
    )

/**
 * Maps FamilyTreeEntity to FamilyTree domain model.
 */
fun FamilyTreeEntity.toFamilyTree(): FamilyTree =
    FamilyTree(
        familyTreeId = this.treeId,
        maleId = this.productId,
        femaleId = this.parentProductId,
        pairId = this.childProductId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

/**
 * Maps LifecycleEventEntity to LifecycleEvent domain model.
 */
fun LifecycleEventEntity.toLifecycleEvent(): LifecycleEvent =
    LifecycleEvent(
        eventId = this.eventId,
        productId = this.productId,
        eventType = this.type,
        eventDate = this.timestamp,
        description = this.notes,
        metadata = null,
        createdAt = this.timestamp
    )

/**
 * Maps LifecycleEvent domain model to LifecycleEventEntity.
 */
fun LifecycleEvent.toEntity(): LifecycleEventEntity =
    LifecycleEventEntity(
        eventId = this.eventId,
        productId = this.productId,
        week = 0,
        stage = "",
        type = this.eventType,
        notes = this.description,
        timestamp = this.eventDate
    )
