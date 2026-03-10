package com.rio.rostry.data.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.data.account.mapper.toRoleMigrationData
import com.rio.rostry.data.database.dao.RoleMigrationDao
import com.rio.rostry.data.database.entity.RoleMigrationEntity
import com.rio.rostry.domain.account.repository.RoleMigrationData
import com.rio.rostry.domain.account.repository.RoleMigrationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RoleMigrationRepository for managing role transitions.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Account Domain repository migration
 */
@Singleton
class RoleMigrationRepositoryImpl @Inject constructor(
    private val dao: RoleMigrationDao
) : RoleMigrationRepository {

    override suspend fun createMigration(
        userId: String,
        fromRole: String,
        toRole: String,
        totalItems: Int
    ): Result<String> {
        return try {
            val migrationId = java.util.UUID.randomUUID().toString()
            val entity = RoleMigrationEntity.create(
                migrationId = migrationId,
                userId = userId,
                fromRole = fromRole,
                toRole = toRole,
                totalItems = totalItems
            )
            dao.insert(entity)
            Result.Success(migrationId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getMigration(migrationId: String): RoleMigrationData? {
        return dao.getById(migrationId)?.toRoleMigrationData()
    }

    override fun observeMigration(migrationId: String): Flow<RoleMigrationData?> {
        return dao.observeById(migrationId).map { it?.toRoleMigrationData() }
    }

    override suspend fun getLatestMigration(userId: String): RoleMigrationData? {
        return dao.getLatestForUser(userId)?.toRoleMigrationData()
    }

    override fun observeLatestMigration(userId: String): Flow<RoleMigrationData?> {
        return dao.observeLatestForUser(userId).map { it?.toRoleMigrationData() }
    }

    override suspend fun updateProgress(
        migrationId: String,
        status: String,
        migratedItems: Int,
        phase: String?,
        entity: String?
    ) {
        dao.updateProgress(migrationId, status, migratedItems, phase, entity)
    }

    override suspend fun updateStatus(
        migrationId: String,
        status: String,
        errorMessage: String?
    ) {
        dao.updateStatus(migrationId, status, errorMessage)
    }

    override suspend fun hasActiveMigration(userId: String): Boolean {
        return dao.hasActiveMigration(userId)
    }

    override suspend fun updateMetadata(migrationId: String, metadataJson: String) {
        dao.updateMetadata(migrationId, metadataJson)
    }
}

