package com.rio.rostry.data.farm.mapper

import com.rio.rostry.core.model.AuditLog
import com.rio.rostry.data.database.entity.AuditLogEntity

/**
 * Maps AuditLogEntity to AuditLog domain model.
 */
fun AuditLogEntity.toAuditLog(): AuditLog {
    return AuditLog(
        logId = this.logId,
        type = this.type,
        refId = this.refId,
        action = this.action,
        actorUserId = this.actorUserId,
        detailsJson = this.detailsJson,
        createdAt = this.createdAt
    )
}

/**
 * Maps AuditLog domain model to AuditLogEntity.
 */
fun AuditLog.toEntity(): AuditLogEntity {
    return AuditLogEntity(
        logId = this.logId,
        type = this.type,
        refId = this.refId,
        action = this.action,
        actorUserId = this.actorUserId,
        detailsJson = this.detailsJson,
        createdAt = this.createdAt
    )
}
