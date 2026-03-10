package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing role migration operations.
 * 
 * Handles the migration of user data when transitioning between roles
 * (e.g., from Enthusiast to Farmer).
 */
interface RoleMigrationRepository {
    /**
     * Creates a new role migration record.
     * 
     * @param userId The user ID
     * @param fromRole The source role
     * @param toRole The target role
     * @param totalItems Total number of items to migrate
     * @return Result containing the migration ID
     */
    suspend fun createMigration(
        userId: String,
        fromRole: String,
        toRole: String,
        totalItems: Int
    ): Result<String>
    
    /**
     * Gets a migration by ID.
     * 
     * @param migrationId The migration ID
     * @return The migration data or null if not found
     */
    suspend fun getMigration(migrationId: String): RoleMigrationData?
    
    /**
     * Observes a migration by ID.
     * 
     * @param migrationId The migration ID
     * @return Flow emitting migration updates
     */
    fun observeMigration(migrationId: String): Flow<RoleMigrationData?>
    
    /**
     * Gets the latest migration for a user.
     * 
     * @param userId The user ID
     * @return The latest migration or null
     */
    suspend fun getLatestMigration(userId: String): RoleMigrationData?
    
    /**
     * Observes the latest migration for a user.
     * 
     * @param userId The user ID
     * @return Flow emitting the latest migration
     */
    fun observeLatestMigration(userId: String): Flow<RoleMigrationData?>
    
    /**
     * Updates migration progress.
     * 
     * @param migrationId The migration ID
     * @param status The current status
     * @param migratedItems Number of items migrated
     * @param phase Optional current phase
     * @param entity Optional current entity being migrated
     */
    suspend fun updateProgress(
        migrationId: String,
        status: String,
        migratedItems: Int,
        phase: String? = null,
        entity: String? = null
    )
    
    /**
     * Updates migration status.
     * 
     * @param migrationId The migration ID
     * @param status The new status
     * @param errorMessage Optional error message
     */
    suspend fun updateStatus(
        migrationId: String,
        status: String,
        errorMessage: String? = null
    )
    
    /**
     * Checks if a user has an active migration.
     * 
     * @param userId The user ID
     * @return True if there's an active migration
     */
    suspend fun hasActiveMigration(userId: String): Boolean
    
    /**
     * Updates migration metadata.
     * 
     * @param migrationId The migration ID
     * @param metadataJson JSON metadata
     */
    suspend fun updateMetadata(migrationId: String, metadataJson: String)
}

/**
 * Domain model for role migration data.
 */
data class RoleMigrationData(
    val migrationId: String,
    val userId: String,
    val fromRole: String,
    val toRole: String,
    val status: String,
    val totalItems: Int,
    val migratedItems: Int,
    val currentPhase: String?,
    val currentEntity: String?,
    val errorMessage: String?,
    val metadataJson: String?,
    val createdAt: Long,
    val updatedAt: Long
)

