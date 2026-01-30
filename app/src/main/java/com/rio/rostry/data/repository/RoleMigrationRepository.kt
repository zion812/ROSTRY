package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.RoleMigrationDao
import com.rio.rostry.data.database.entity.RoleMigrationEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface RoleMigrationRepository {
    suspend fun createMigration(userId: String, fromRole: String, toRole: String, totalItems: Int): Resource<String>
    suspend fun getMigration(migrationId: String): RoleMigrationEntity?
    fun observeMigration(migrationId: String): Flow<RoleMigrationEntity?>
    suspend fun getLatestMigration(userId: String): RoleMigrationEntity?
    fun observeLatestMigration(userId: String): Flow<RoleMigrationEntity?>
    suspend fun updateProgress(migrationId: String, status: String, migratedItems: Int, phase: String? = null, entity: String? = null)
    suspend fun updateStatus(migrationId: String, status: String, errorMessage: String? = null)
    suspend fun hasActiveMigration(userId: String): Boolean
}

@Singleton
class RoleMigrationRepositoryImpl @Inject constructor(
    private val dao: RoleMigrationDao
) : RoleMigrationRepository {

    override suspend fun createMigration(
        userId: String, 
        fromRole: String, 
        toRole: String, 
        totalItems: Int
    ): Resource<String> {
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
            Resource.Success(migrationId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create migration")
        }
    }

    override suspend fun getMigration(migrationId: String): RoleMigrationEntity? {
        return dao.getById(migrationId)
    }

    override fun observeMigration(migrationId: String): Flow<RoleMigrationEntity?> {
        return dao.observeById(migrationId)
    }
    
    override suspend fun getLatestMigration(userId: String): RoleMigrationEntity? {
        return dao.getLatestForUser(userId)
    }

    override fun observeLatestMigration(userId: String): Flow<RoleMigrationEntity?> {
        return dao.observeLatestForUser(userId)
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

    override suspend fun updateStatus(migrationId: String, status: String, errorMessage: String?) {
        dao.updateStatus(migrationId, status, errorMessage)
    }
    
    override suspend fun hasActiveMigration(userId: String): Boolean {
        return dao.hasActiveMigration(userId)
    }
}
