package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tracks role upgrade migration state for FARMER→ENTHUSIAST transitions.
 * 
 * Enables:
 * - Progress tracking during long-running migrations
 * - Resume capability if migration is interrupted
 * - Rollback support if migration fails
 * - Audit trail for data migration events
 */
@Entity(
    tableName = "role_migrations",
    indices = [
        Index("userId"),
        Index("status"),
        Index("createdAt")
    ]
)
data class RoleMigrationEntity(
    @PrimaryKey
    val migrationId: String,
    
    val userId: String,
    val fromRole: String,
    val toRole: String,
    
    /**
     * Migration status:
     * - PENDING: Migration initiated, waiting to start
     * - IN_PROGRESS: Actively copying data
     * - PAUSED: Temporarily paused (e.g., network loss)
     * - COMPLETED: Successfully finished
     * - FAILED: Error occurred, may need rollback
     * - ROLLED_BACK: Reverted to original state
     */
    val status: String,
    
    // Progress tracking
    val totalItems: Int,
    val migratedItems: Int,
    val currentPhase: String? = null,
    val currentEntity: String? = null,
    
    // Timestamps
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val pausedAt: Long? = null,
    val lastProgressAt: Long? = null,
    
    // Error handling
    val errorMessage: String? = null,
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    
    // Metadata
    val snapshotPath: String? = null, // Local backup path for rollback
    val metadataJson: String? = null, // Additional migration metadata
    
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        // Status constants
        const val STATUS_PENDING = "PENDING"
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val STATUS_PAUSED = "PAUSED"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_FAILED = "FAILED"
        const val STATUS_ROLLED_BACK = "ROLLED_BACK"
        
        // Phase constants
        const val PHASE_VALIDATION = "VALIDATION"
        const val PHASE_SNAPSHOT = "SNAPSHOT"
        const val PHASE_CLOUD_COPY = "CLOUD_COPY"
        const val PHASE_ROLE_UPDATE = "ROLE_UPDATE"
        const val PHASE_RECONCILIATION = "RECONCILIATION"
        const val PHASE_VERIFICATION = "VERIFICATION"
        const val PHASE_CLEANUP = "CLEANUP"
        
        fun create(
            migrationId: String,
            userId: String,
            fromRole: String,
            toRole: String,
            totalItems: Int
        ): RoleMigrationEntity {
            val now = System.currentTimeMillis()
            return RoleMigrationEntity(
                migrationId = migrationId,
                userId = userId,
                fromRole = fromRole,
                toRole = toRole,
                status = STATUS_PENDING,
                totalItems = totalItems,
                migratedItems = 0,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    val progressPercentage: Float
        get() = if (totalItems > 0) (migratedItems.toFloat() / totalItems) * 100f else 0f
    
    val isActive: Boolean
        get() = status in listOf(STATUS_PENDING, STATUS_IN_PROGRESS, STATUS_PAUSED)
    
    val canRetry: Boolean
        get() = status == STATUS_FAILED && retryCount < maxRetries
    
    val canRollback: Boolean
        get() = status in listOf(STATUS_FAILED, STATUS_IN_PROGRESS, STATUS_PAUSED) && snapshotPath != null
    
    fun withProgress(
        migratedItems: Int,
        phase: String? = null,
        entity: String? = null
    ): RoleMigrationEntity = copy(
        migratedItems = migratedItems,
        currentPhase = phase ?: currentPhase,
        currentEntity = entity ?: currentEntity,
        lastProgressAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
    
    fun withStatus(newStatus: String, error: String? = null): RoleMigrationEntity = copy(
        status = newStatus,
        errorMessage = error,
        completedAt = if (newStatus == STATUS_COMPLETED) System.currentTimeMillis() else completedAt,
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Migration progress data for UI observation.
 */
data class MigrationProgress(
    val migrationId: String,
    val status: String,
    val progressPercentage: Float,
    val currentPhase: String?,
    val currentEntity: String?,
    val migratedItems: Int,
    val totalItems: Int,
    val errorMessage: String?
) {
    companion object {
        fun from(entity: RoleMigrationEntity): MigrationProgress = MigrationProgress(
            migrationId = entity.migrationId,
            status = entity.status,
            progressPercentage = entity.progressPercentage,
            currentPhase = entity.currentPhase,
            currentEntity = entity.currentEntity,
            migratedItems = entity.migratedItems,
            totalItems = entity.totalItems,
            errorMessage = entity.errorMessage
        )
    }
}
