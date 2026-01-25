package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.AdminAuditLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminAuditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AdminAuditLogEntity)

    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AdminAuditLogEntity>>

    @Query("SELECT * FROM admin_audit_logs WHERE adminId = :adminId ORDER BY timestamp DESC")
    fun getLogsByAdmin(adminId: String): Flow<List<AdminAuditLogEntity>>

    @Query("SELECT * FROM admin_audit_logs WHERE targetId = :targetId ORDER BY timestamp DESC")
    fun getLogsForTarget(targetId: String): Flow<List<AdminAuditLogEntity>>

    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogsSnapshot(limit: Int = 100): List<AdminAuditLogEntity>
    
    @Query("DELETE FROM admin_audit_logs WHERE timestamp < :threshold")
    suspend fun purgeOldLogs(threshold: Long)
}
