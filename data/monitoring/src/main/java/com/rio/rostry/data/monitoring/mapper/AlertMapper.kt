package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.Alert
import com.rio.rostry.data.database.entity.AlertEntity

/**
 * Maps AlertEntity to Alert domain model.
 */
fun AlertEntity.toAlert(): Alert {
    return Alert(
        id = this.id,
        userId = this.userId,
        title = this.title,
        message = this.message,
        severity = this.severity,
        type = this.type,
        relatedId = this.relatedId,
        isRead = this.isRead,
        isDismissed = this.isDismissed,
        createdAt = this.createdAt
    )
}

/**
 * Maps Alert domain model to AlertEntity.
 */
fun Alert.toEntity(): AlertEntity {
    return AlertEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        message = this.message,
        severity = this.severity,
        type = this.type,
        relatedId = this.relatedId,
        isRead = this.isRead,
        isDismissed = this.isDismissed,
        createdAt = this.createdAt
    )
}
