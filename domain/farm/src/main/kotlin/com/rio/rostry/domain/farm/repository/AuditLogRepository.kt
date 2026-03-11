package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.AuditLog
import kotlinx.coroutines.flow.Flow

/**
 * Repository for audit log operations.
 * 
 * Handles writing and querying audit logs for traceability and compliance.
 */
interface AuditLogRepository {
    /**
     * Insert an audit log entry.
     */
    suspend fun insert(log: AuditLog)
    
    /**
     * Get all audit logs ordered by creation time descending.
     */
    suspend fun getAll(): List<AuditLog>
    
    /**
     * Get audit logs by reference ID.
     */
    suspend fun getByRef(refId: String): List<AuditLog>
    
    /**
     * Stream audit logs by reference ID.
     */
    fun streamByRef(refId: String): Flow<List<AuditLog>>
    
    /**
     * Get audit logs by type.
     */
    suspend fun getByType(type: String, limit: Int = 50): List<AuditLog>
    
    /**
     * Get audit logs by actor user ID.
     */
    suspend fun getByActor(userId: String, limit: Int = 50): List<AuditLog>
}
