package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.AdminAuditDao
import com.rio.rostry.data.database.entity.AdminAuditLogEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

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
    ): Resource<Unit> {
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
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to log audit: ${e.message}")
        }
    }

    override fun getAllLogs(): Flow<Resource<List<AdminAuditLogEntity>>> {
        return auditDao.getAllLogs().map { Resource.Success(it) }
    }

    override suspend fun getRecentLogsSnapshot(): Resource<List<AdminAuditLogEntity>> {
        return try {
            val logs = auditDao.getRecentLogsSnapshot(100)
            Resource.Success(logs)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch logs")
        }
    }
}
