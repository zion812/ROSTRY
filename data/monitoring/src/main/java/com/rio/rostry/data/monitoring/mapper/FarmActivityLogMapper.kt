package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.FarmActivityLog
import com.rio.rostry.data.database.entity.FarmActivityLogEntity

/**
 * Mapper between FarmActivityLogEntity and FarmActivityLog domain model.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.4 - Data modules create mappers for entity ↔ model conversion
 */
object FarmActivityLogMapper {
    
    fun toDomain(entity: FarmActivityLogEntity): FarmActivityLog {
        return FarmActivityLog(
            id = entity.activityId,
            farmerId = entity.farmerId,
            productId = entity.productId,
            activityType = entity.activityType,
            amountInr = entity.amountInr,
            quantity = entity.quantity,
            category = entity.category,
            description = entity.description,
            notes = entity.notes,
            photoUrls = entity.photoUrls,
            mediaItemsJson = entity.mediaItemsJson,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            dirty = entity.dirty,
            syncedAt = entity.syncedAt
        )
    }
    
    fun toEntity(model: FarmActivityLog): FarmActivityLogEntity {
        return FarmActivityLogEntity(
            activityId = model.id,
            farmerId = model.farmerId,
            productId = model.productId,
            activityType = model.activityType,
            amountInr = model.amountInr,
            quantity = model.quantity,
            category = model.category,
            description = model.description,
            notes = model.notes,
            photoUrls = model.photoUrls,
            mediaItemsJson = model.mediaItemsJson,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt,
            dirty = model.dirty,
            syncedAt = model.syncedAt
        )
    }
}
