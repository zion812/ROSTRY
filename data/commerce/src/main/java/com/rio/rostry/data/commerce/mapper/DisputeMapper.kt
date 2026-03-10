package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Dispute
import com.rio.rostry.core.model.DisputeStatus
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus as EntityDisputeStatus

fun DisputeEntity.toDomainModel(): Dispute {
    return Dispute(
        id = disputeId,
        transferId = transferId,
        reporterId = reporterId,
        reportedUserId = reportedUserId,
        reason = reason,
        description = description,
        evidenceUrls = evidenceUrls,
        status = DisputeStatus.valueOf(status.name),
        resolution = resolution,
        resolvedByAdminId = resolvedByAdminId,
        createdAt = createdAt,
        resolvedAt = resolvedAt
    )
}

fun Dispute.toEntity(): DisputeEntity {
    return DisputeEntity(
        disputeId = id,
        transferId = transferId,
        reporterId = reporterId,
        reportedUserId = reportedUserId,
        reason = reason,
        description = description,
        evidenceUrls = evidenceUrls,
        status = EntityDisputeStatus.valueOf(status.name),
        resolution = resolution,
        resolvedByAdminId = resolvedByAdminId,
        createdAt = createdAt,
        resolvedAt = resolvedAt
    )
}
