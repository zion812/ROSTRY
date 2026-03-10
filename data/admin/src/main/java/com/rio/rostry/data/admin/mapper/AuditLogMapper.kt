package com.rio.rostry.data.admin.mapper

import com.rio.rostry.data.database.entity.AdminAuditLogEntity
import com.rio.rostry.domain.admin.repository.AuditLog

/**
 * Converts AdminAuditLogEntity to domain model.
 */
fun AdminAuditLogEntity.toAuditLog(): AuditLog {
    return AuditLog(
        logId = this.logId,
        adminId = this.adminId,
        adminName = this.adminName,
        actionType = this.actionType,
        targetId = this.targetId,
        targetType = this.targetType,
        details = this.details,
        timestamp = this.timestamp
    )
}
