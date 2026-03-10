package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.FarmVerification
import com.rio.rostry.data.database.entity.FarmVerificationEntity

/**
 * Mapper between FarmVerificationEntity and FarmVerification domain model.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.4 - Data modules create mappers for entity ↔ model conversion
 */
object FarmVerificationMapper {
    
    fun toDomain(entity: FarmVerificationEntity): FarmVerification {
        return FarmVerification(
            id = entity.verificationId,
            farmerId = entity.farmerId,
            farmLocationLat = entity.farmLocationLat,
            farmLocationLng = entity.farmLocationLng,
            farmAddressLine1 = entity.farmAddressLine1,
            farmAddressLine2 = entity.farmAddressLine2,
            farmCity = entity.farmCity,
            farmState = entity.farmState,
            farmPostalCode = entity.farmPostalCode,
            farmCountry = entity.farmCountry,
            verificationDocumentUrls = entity.verificationDocumentUrls,
            gpsAccuracy = entity.gpsAccuracy,
            gpsTimestamp = entity.gpsTimestamp,
            status = entity.status.name,
            submittedAt = entity.submittedAt,
            reviewedAt = entity.reviewedAt,
            reviewedBy = entity.reviewedBy,
            rejectionReason = entity.rejectionReason,
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toEntity(model: FarmVerification, statusEnum: com.rio.rostry.domain.model.VerificationStatus): FarmVerificationEntity {
        return FarmVerificationEntity(
            verificationId = model.id,
            farmerId = model.farmerId,
            farmLocationLat = model.farmLocationLat,
            farmLocationLng = model.farmLocationLng,
            farmAddressLine1 = model.farmAddressLine1,
            farmAddressLine2 = model.farmAddressLine2,
            farmCity = model.farmCity,
            farmState = model.farmState,
            farmPostalCode = model.farmPostalCode,
            farmCountry = model.farmCountry,
            verificationDocumentUrls = model.verificationDocumentUrls,
            gpsAccuracy = model.gpsAccuracy,
            gpsTimestamp = model.gpsTimestamp,
            status = statusEnum,
            submittedAt = model.submittedAt,
            reviewedAt = model.reviewedAt,
            reviewedBy = model.reviewedBy,
            rejectionReason = model.rejectionReason,
            notes = model.notes,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt
        )
    }
}
