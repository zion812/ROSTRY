package com.rio.rostry.data.account.mapper

import com.rio.rostry.data.database.entity.RoleMigrationEntity
import com.rio.rostry.domain.account.repository.RoleMigrationData

/**
 * Converts RoleMigrationEntity to domain model.
 */
fun RoleMigrationEntity.toRoleMigrationData(): RoleMigrationData {
    return RoleMigrationData(
        migrationId = this.migrationId,
        userId = this.userId,
        fromRole = this.fromRole,
        toRole = this.toRole,
        status = this.status,
        totalItems = this.totalItems,
        migratedItems = this.migratedItems,
        currentPhase = this.currentPhase,
        currentEntity = this.currentEntity,
        errorMessage = this.errorMessage,
        metadataJson = this.metadataJson,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
