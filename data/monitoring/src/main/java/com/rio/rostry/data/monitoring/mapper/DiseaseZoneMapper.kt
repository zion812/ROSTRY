package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.DiseaseZone
import com.rio.rostry.core.model.ZoneSeverity as DomainSeverity
import com.rio.rostry.data.database.entity.DiseaseZoneEntity
import com.rio.rostry.data.database.entity.ZoneSeverity as EntitySeverity

fun DiseaseZoneEntity.toDiseaseZone(): DiseaseZone =
    DiseaseZone(
        zoneId = zoneId,
        latitude = latitude,
        longitude = longitude,
        radiusMeters = radiusMeters.toFloat(),
        reason = reason,
        severity = when (severity) {
            EntitySeverity.WARNING -> DomainSeverity.WARNING
            EntitySeverity.RESTRICTED -> DomainSeverity.QUARANTINE
            EntitySeverity.LOCKDOWN -> DomainSeverity.LOCKDOWN
        },
        isActive = isActive,
        createdAt = createdAt.time
    )

fun DiseaseZone.toEntity(): DiseaseZoneEntity =
    DiseaseZoneEntity(
        zoneId = zoneId,
        latitude = latitude,
        longitude = longitude,
        radiusMeters = radiusMeters.toDouble(),
        reason = reason,
        severity = when (severity) {
            DomainSeverity.WARNING -> EntitySeverity.WARNING
            DomainSeverity.QUARANTINE -> EntitySeverity.RESTRICTED
            DomainSeverity.LOCKDOWN -> EntitySeverity.LOCKDOWN
        },
        isActive = isActive,
        createdAt = java.util.Date(createdAt)
    )
