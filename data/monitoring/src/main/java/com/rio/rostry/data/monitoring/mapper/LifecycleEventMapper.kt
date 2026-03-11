package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.LifecycleEvent
import com.rio.rostry.data.database.entity.LifecycleEventEntity

object LifecycleEventMapper {
    fun toDomain(entity: LifecycleEventEntity): LifecycleEvent =
        LifecycleEvent(
            eventId = entity.eventId,
            productId = entity.productId,
            eventType = entity.type,
            eventDate = entity.timestamp,
            description = entity.notes,
            metadata = entity.stage,
            createdAt = entity.timestamp
        )

    fun toEntity(model: LifecycleEvent): LifecycleEventEntity =
        LifecycleEventEntity(
            eventId = model.eventId,
            productId = model.productId,
            type = model.eventType,
            timestamp = model.eventDate,
            notes = model.description,
            stage = model.metadata ?: "",
            week = 0 // Week not in model
        )
}
