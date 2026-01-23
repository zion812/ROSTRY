package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.AdminAuditLogEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuditRepository {
    suspend fun logAction(
        adminId: String, 
        actionType: String, 
        targetId: String? = null, 
        targetType: String? = null, 
        details: String? = null,
        adminName: String? = null
    ): Resource<Unit>

    fun getAllLogs(): Flow<Resource<List<AdminAuditLogEntity>>>
    
    suspend fun getRecentLogsSnapshot(): Resource<List<AdminAuditLogEntity>>
}
