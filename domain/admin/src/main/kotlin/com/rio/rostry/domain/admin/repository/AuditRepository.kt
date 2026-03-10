package com.rio.rostry.domain.admin.repository

import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing audit logs.
 * 
 * Handles logging and retrieving admin actions for audit purposes.
 */
interface AuditRepository {
    /**
     * Logs an admin action.
     * 
     * @param adminId The admin user ID
     * @param actionType The type of action performed
     * @param targetId Optional target entity ID
     * @param targetType Optional target entity type
     * @param details Optional action details
     * @param adminName Optional admin name
     * @return Result indicating success or failure
     */
    suspend fun logAction(
        adminId: String,
        actionType: String,
        targetId: String? = null,
        targetType: String? = null,
        details: String? = null,
        adminName: String? = null
    ): Result<Unit>
    
    /**
     * Gets all audit logs.
     * 
     * @return Flow emitting list of audit logs
     */
    fun getAllLogs(): Flow<List<AuditLog>>
    
    /**
     * Gets recent audit logs snapshot.
     * 
     * @return Result containing list of recent audit logs
     */
    suspend fun getRecentLogsSnapshot(): Result<List<AuditLog>>
}

/**
 * Domain model for audit log.
 */
data class AuditLog(
    val logId: String,
    val adminId: String,
    val adminName: String?,
    val actionType: String,
    val targetId: String?,
    val targetType: String?,
    val details: String?,
    val timestamp: Long
)

