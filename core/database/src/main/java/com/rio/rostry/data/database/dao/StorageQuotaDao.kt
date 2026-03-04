package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.StorageQuotaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for storage quota entity.
 */
@Dao
interface StorageQuotaDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quota: StorageQuotaEntity)
    
    @Update
    suspend fun update(quota: StorageQuotaEntity)
    
    @Delete
    suspend fun delete(quota: StorageQuotaEntity)
    
    @Query("SELECT * FROM storage_quota WHERE userId = :userId")
    suspend fun getByUserId(userId: String): StorageQuotaEntity?
    
    @Query("SELECT * FROM storage_quota WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<StorageQuotaEntity?>
    
    @Query("""
        UPDATE storage_quota 
        SET usedBytes = :usedBytes,
            publicUsedBytes = :publicUsedBytes,
            privateUsedBytes = :privateUsedBytes,
            imageBytes = :imageBytes,
            documentBytes = :documentBytes,
            dataBytes = :dataBytes,
            warningLevel = :warningLevel,
            lastCalculatedAt = :timestamp,
            updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateUsage(
        userId: String,
        usedBytes: Long,
        publicUsedBytes: Long,
        privateUsedBytes: Long,
        imageBytes: Long,
        documentBytes: Long,
        dataBytes: Long,
        warningLevel: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE storage_quota 
        SET quotaBytes = :quotaBytes,
            publicLimitBytes = :publicLimitBytes,
            privateLimitBytes = :privateLimitBytes,
            updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateQuotaLimits(
        userId: String,
        quotaBytes: Long,
        publicLimitBytes: Long,
        privateLimitBytes: Long,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("UPDATE storage_quota SET lastSyncedAt = :timestamp, updatedAt = :timestamp WHERE userId = :userId")
    suspend fun updateSyncTime(userId: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT warningLevel FROM storage_quota WHERE userId = :userId")
    suspend fun getWarningLevel(userId: String): String?
    
    @Query("SELECT usedBytes FROM storage_quota WHERE userId = :userId")
    suspend fun getUsedBytes(userId: String): Long?
    
    @Query("DELETE FROM storage_quota WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)
}
