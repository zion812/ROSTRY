package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.RoleMigrationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for role migration tracking.
 */
@Dao
interface RoleMigrationDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(migration: RoleMigrationEntity)
    
    @Update
    suspend fun update(migration: RoleMigrationEntity)
    
    @Delete
    suspend fun delete(migration: RoleMigrationEntity)
    
    @Query("SELECT * FROM role_migrations WHERE migrationId = :migrationId")
    suspend fun getById(migrationId: String): RoleMigrationEntity?
    
    @Query("SELECT * FROM role_migrations WHERE migrationId = :migrationId")
    fun observeById(migrationId: String): Flow<RoleMigrationEntity?>
    
    @Query("SELECT * FROM role_migrations WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestForUser(userId: String): RoleMigrationEntity?
    
    @Query("SELECT * FROM role_migrations WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    fun observeLatestForUser(userId: String): Flow<RoleMigrationEntity?>
    
    @Query("SELECT * FROM role_migrations WHERE userId = :userId AND status IN (:statuses)")
    suspend fun getByUserAndStatuses(userId: String, statuses: List<String>): List<RoleMigrationEntity>
    
    @Query("SELECT * FROM role_migrations WHERE status IN ('PENDING', 'IN_PROGRESS', 'PAUSED')")
    suspend fun getActiveMigrations(): List<RoleMigrationEntity>
    
    @Query("SELECT * FROM role_migrations WHERE status IN ('PENDING', 'IN_PROGRESS', 'PAUSED')")
    fun observeActiveMigrations(): Flow<List<RoleMigrationEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM role_migrations WHERE userId = :userId AND status IN ('PENDING', 'IN_PROGRESS', 'PAUSED'))")
    suspend fun hasActiveMigration(userId: String): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM role_migrations WHERE userId = :userId AND status IN ('PENDING', 'IN_PROGRESS', 'PAUSED'))")
    fun observeHasActiveMigration(userId: String): Flow<Boolean>
    
    @Query("""
        UPDATE role_migrations 
        SET status = :status, 
            migratedItems = :migratedItems, 
            currentPhase = :phase,
            currentEntity = :entity,
            lastProgressAt = :timestamp,
            updatedAt = :timestamp
        WHERE migrationId = :migrationId
    """)
    suspend fun updateProgress(
        migrationId: String,
        status: String,
        migratedItems: Int,
        phase: String?,
        entity: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE role_migrations 
        SET status = :status, 
            errorMessage = :errorMessage,
            updatedAt = :timestamp
        WHERE migrationId = :migrationId
    """)
    suspend fun updateStatus(
        migrationId: String,
        status: String,
        errorMessage: String? = null,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("DELETE FROM role_migrations WHERE status = 'COMPLETED' AND completedAt < :beforeTimestamp")
    suspend fun deleteCompletedBefore(beforeTimestamp: Long): Int
    
    @Query("SELECT COUNT(*) FROM role_migrations WHERE userId = :userId AND status = 'COMPLETED'")
    suspend fun getCompletedMigrationCount(userId: String): Int
}
