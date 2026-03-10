package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.ShowRecord
import com.rio.rostry.core.model.ShowRecordStats
import com.rio.rostry.data.database.entity.ShowRecordEntity

/**
 * Mapper between ShowRecordEntity and ShowRecord domain model.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.4 - Data modules create mappers for entity ↔ model conversion
 */
object ShowRecordMapper {
    
    fun toDomain(entity: ShowRecordEntity): ShowRecord {
        return ShowRecord(
            id = entity.recordId,
            productId = entity.productId,
            ownerId = entity.ownerId,
            recordType = entity.recordType,
            eventName = entity.eventName,
            eventLocation = entity.eventLocation,
            eventDate = entity.eventDate,
            result = entity.result,
            placement = entity.placement,
            totalParticipants = entity.totalParticipants,
            category = entity.category,
            score = entity.score,
            opponentName = entity.opponentName,
            opponentOwnerName = entity.opponentOwnerName,
            judgesNotes = entity.judgesNotes,
            awards = entity.awards,
            photoUrls = entity.photoUrls,
            isVerified = entity.isVerified,
            verifiedBy = entity.verifiedBy,
            certificateUrl = entity.certificateUrl,
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isDeleted = entity.isDeleted,
            deletedAt = entity.deletedAt,
            dirty = entity.dirty,
            syncedAt = entity.syncedAt
        )
    }
    
    fun toEntity(model: ShowRecord): ShowRecordEntity {
        return ShowRecordEntity(
            recordId = model.id,
            productId = model.productId,
            ownerId = model.ownerId,
            recordType = model.recordType,
            eventName = model.eventName,
            eventLocation = model.eventLocation,
            eventDate = model.eventDate,
            result = model.result,
            placement = model.placement,
            totalParticipants = model.totalParticipants,
            category = model.category,
            score = model.score,
            opponentName = model.opponentName,
            opponentOwnerName = model.opponentOwnerName,
            judgesNotes = model.judgesNotes,
            awards = model.awards,
            photoUrls = model.photoUrls,
            isVerified = model.isVerified,
            verifiedBy = model.verifiedBy,
            certificateUrl = model.certificateUrl,
            notes = model.notes,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt,
            isDeleted = model.isDeleted,
            deletedAt = model.deletedAt,
            dirty = model.dirty,
            syncedAt = model.syncedAt
        )
    }
    
    fun statsToDomain(stats: com.rio.rostry.data.database.dao.ShowRecordStats): ShowRecordStats {
        return ShowRecordStats(
            recordType = stats.recordType,
            total = stats.total,
            wins = stats.wins,
            podiums = stats.podiums
        )
    }
}
