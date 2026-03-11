package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.QuarantineRecord
import com.rio.rostry.data.database.entity.QuarantineRecordEntity

object QuarantineRecordMapper {
    fun toDomain(entity: QuarantineRecordEntity): QuarantineRecord =
        QuarantineRecord(
            id = entity.quarantineId,
            productId = entity.productId,
            reason = entity.reason,
            status = entity.status,
            startDate = entity.startedAt,
            endDate = entity.endedAt,
            notes = entity.vetNotes,
            createdAt = entity.startedAt,
            updatedAt = entity.updatedAt
        )

    fun toEntity(model: QuarantineRecord): QuarantineRecordEntity =
        QuarantineRecordEntity(
            quarantineId = model.id,
            productId = model.productId,
            reason = model.reason,
            status = model.status,
            startedAt = model.startDate,
            endedAt = model.endDate,
            vetNotes = model.notes,
            updatedAt = model.updatedAt
        )
}
