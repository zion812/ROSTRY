package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.MortalityRecord
import com.rio.rostry.data.database.entity.MortalityRecordEntity

object MortalityRecordMapper {
    fun toDomain(entity: MortalityRecordEntity): MortalityRecord =
        MortalityRecord(
            id = entity.deathId,
            productId = entity.productId,
            quantity = entity.quantity.toDouble(),
            cause = entity.causeCategory,
            date = entity.occurredAt,
            notes = entity.circumstances,
            createdAt = entity.occurredAt,
            updatedAt = entity.updatedAt
        )

    fun toEntity(model: MortalityRecord): MortalityRecordEntity =
        MortalityRecordEntity(
            deathId = model.id,
            productId = model.productId,
            quantity = model.quantity.toInt(),
            causeCategory = model.cause,
            occurredAt = model.date,
            circumstances = model.notes,
            updatedAt = model.updatedAt
        )
}
