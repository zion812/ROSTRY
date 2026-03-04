package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.RoleUpgradeRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoleUpgradeRequestDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: RoleUpgradeRequestEntity)
    
    @Update
    suspend fun update(request: RoleUpgradeRequestEntity)
    
    @Query("SELECT * FROM role_upgrade_requests WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeRequestsByUser(userId: String): Flow<List<RoleUpgradeRequestEntity>>
    
    @Query("SELECT * FROM role_upgrade_requests WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestRequestByUser(userId: String): RoleUpgradeRequestEntity?
    
    @Query("SELECT * FROM role_upgrade_requests WHERE userId = :userId AND status = 'PENDING' LIMIT 1")
    suspend fun getPendingRequestByUser(userId: String): RoleUpgradeRequestEntity?
    
    @Query("SELECT * FROM role_upgrade_requests WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun observePendingRequests(): Flow<List<RoleUpgradeRequestEntity>>
    
    @Query("SELECT * FROM role_upgrade_requests WHERE requestId = :requestId")
    suspend fun getRequestById(requestId: String): RoleUpgradeRequestEntity?
    
    @Query("SELECT COUNT(*) FROM role_upgrade_requests WHERE status = 'PENDING'")
    fun observePendingCount(): Flow<Int>
    
    @Query("SELECT * FROM role_upgrade_requests WHERE status IN ('APPROVED', 'REJECTED') ORDER BY reviewedAt DESC LIMIT 50")
    fun observeProcessedRequests(): Flow<List<RoleUpgradeRequestEntity>>
}
