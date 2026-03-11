package com.rio.rostry.data.farm.repository

import com.rio.rostry.core.model.AuditLog
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.farm.mapper.toAuditLog
import com.rio.rostry.data.farm.mapper.toEntity
import com.rio.rostry.domain.farm.repository.AuditLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuditLogRepository using Room database.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class AuditLogRepositoryImpl @Inject constructor(
    private val auditLogDao: AuditLogDao
) : AuditLogRepository {

    override suspend fun insert(log: AuditLog) {
        auditLogDao.insert(log.toEntity())
    }

    override suspend fun getAll(): List<AuditLog> {
        return auditLogDao.getAll()
            .map { it.toAuditLog() }
    }

    override suspend fun getByRef(refId: String): List<AuditLog> {
        return auditLogDao.getByRef(refId)
            .map { it.toAuditLog() }
    }

    override fun streamByRef(refId: String): Flow<List<AuditLog>> {
        return auditLogDao.streamByRef(refId)
            .map { entities -> entities.map { it.toAuditLog() } }
    }

    override suspend fun getByType(type: String, limit: Int): List<AuditLog> {
        return auditLogDao.getByType(type, limit)
            .map { it.toAuditLog() }
    }

    override suspend fun getByActor(userId: String, limit: Int): List<AuditLog> {
        return auditLogDao.getByActor(userId, limit)
            .map { it.toAuditLog() }
    }
}
