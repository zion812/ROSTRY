package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.VaccinationRecord
import com.rio.rostry.data.database.entity.VaccinationRecordEntity

object VaccinationRecordMapper {
    fun toDomain(entity: VaccinationRecordEntity): VaccinationRecord =
        VaccinationRecord(
            id = entity.vaccinationId,
            productId = entity.productId,
            vaccineName = entity.vaccineType,
            date = entity.administeredAt ?: entity.scheduledAt,
            nextDueDate = null, // Not directly available in entity
            dosage = entity.doseMl?.toString(),
            notes = entity.efficacyNotes,
            farmerId = entity.farmerId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )

    fun toEntity(model: VaccinationRecord): VaccinationRecordEntity =
        VaccinationRecordEntity(
            vaccinationId = model.id,
            productId = model.productId,
            farmerId = model.farmerId,
            vaccineType = model.vaccineName,
            doseMl = model.dosage?.toDoubleOrNull(),
            administeredAt = model.date,
            scheduledAt = model.date,
            efficacyNotes = model.notes,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt
        )
}
