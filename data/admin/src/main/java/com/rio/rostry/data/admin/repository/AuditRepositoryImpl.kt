package com.rio.rostry.data.admin.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.data.admin.mapper.toAuditLog
import com.rio.rostry.data.database.dao.AdminAuditDao
import com.rio.rostry.data.database.entity.AdminAuditLogEntity
import com.rio.rostry.domain.admin.repository.AuditLog
import com.rio.rostry.domain.admin.repository.AuditRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuditRepository for managing audit logs.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Admin Domain repository migration
 */
@Singleton
class AuditRepositoryImpl @Inject constructor(
    private val auditDao: AdminAuditDao
) : AuditRepository {

    override suspend fun logAction(
        adminId: String,
        actionType: String,
        targetId: String?,
        targetType: String?,
        details: String?,
        adminName: String?
    ): Result<Unit> {
        return try {
            val log = AdminAuditLogEntity(
                logId = UUID.randomUUID().toString(),
                adminId = adminId,
                adminName = adminName,
                actionType = actionType,
                targetId = targetId,
                targetType = targetType,
                details = details,
                timestamp = System.currentTimeMillis()
            )
            auditDao.insertLog(log)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getAllLogs(): Flow<List<AuditLog>> {
        return auditDao.getAllLogs().map { entities ->
            entities.map { it.toAuditLog() }
        }
    }

    override suspend fun getRecentLogsSnapshot(): Result<List<AuditLog>> {
        return try {
            val logs = auditDao.getRecentLogsSnapshot(100)
            Result.Success(logs.map { it.toAuditLog() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

