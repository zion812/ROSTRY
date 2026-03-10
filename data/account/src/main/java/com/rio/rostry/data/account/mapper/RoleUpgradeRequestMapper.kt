package com.rio.rostry.data.account.mapper

import com.rio.rostry.data.database.entity.RoleUpgradeRequestEntity
import com.rio.rostry.domain.account.repository.RoleUpgradeRequestData

/**
 * Converts RoleUpgradeRequestEntity to domain model.
 */
fun RoleUpgradeRequestEntity.toRoleUpgradeRequestData(): RoleUpgradeRequestData {
    return RoleUpgradeRequestData(
        requestId = this.requestId,
        userId = this.userId,
        currentRole = this.currentRole,
        requestedRole = this.requestedRole,
        status = this.status,
        reviewedBy = this.reviewedBy,
        adminNotes = this.adminNotes,
        reviewedAt = this.reviewedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
