package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.FarmAlert
import com.rio.rostry.data.database.entity.FarmAlertEntity

/**
 * Maps FarmAlertEntity to FarmAlert domain model.
 */
fun FarmAlertEntity.toFarmAlert(): FarmAlert {
    return FarmAlert(
        alertId = this.alertId,
        farmerId = this.farmerId,
        alertType = this.alertType,
        severity = this.severity,
        message = this.message,
        actionRoute = this.actionRoute,
        isRead = this.isRead,
        createdAt = this.createdAt,
        expiresAt = this.expiresAt
    )
}

/**
 * Maps FarmAlert domain model to FarmAlertEntity.
 */
fun FarmAlert.toEntity(): FarmAlertEntity {
    return FarmAlertEntity(
        alertId = this.alertId,
        farmerId = this.farmerId,
        alertType = this.alertType,
        severity = this.severity,
        message = this.message,
        actionRoute = this.actionRoute,
        isRead = this.isRead,
        createdAt = this.createdAt,
        expiresAt = this.expiresAt,
        dirty = false,
        syncedAt = null
    )
}
