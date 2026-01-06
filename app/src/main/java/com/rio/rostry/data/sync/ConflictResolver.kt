package com.rio.rostry.data.sync

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Conflict resolution strategy
 */
enum class ConflictStrategy {
    SERVER_WINS,      // Always use server version (safe default)
    CLIENT_WINS,      // Always use client version (offline-first)
    NEWEST_WINS,      // Use version with latest timestamp
    MERGE,            // Attempt to merge changes (field-level)
    ASK_USER          // Defer to user for resolution
}

/**
 * Result of conflict resolution
 */
sealed class ConflictResult<T> {
    data class Resolved<T>(val value: T, val strategy: ConflictStrategy) : ConflictResult<T>()
    data class NeedsUserInput<T>(val local: T, val remote: T, val conflictFields: List<String>) : ConflictResult<T>()
    data class Error<T>(val message: String) : ConflictResult<T>()
}

/**
 * Conflict resolver for handling data sync conflicts.
 * Implements multiple resolution strategies based on entity type and field importance.
 */
@Singleton
class ConflictResolver @Inject constructor() {

    companion object {
        private const val TAG = "ConflictResolver"
    }

    /**
     * Get default strategy for entity type
     */
    fun getDefaultStrategy(entityType: String): ConflictStrategy = when (entityType) {
        // Critical data: server wins to prevent data loss
        "order", "transfer", "payment" -> ConflictStrategy.SERVER_WINS
        
        // User-generated content: newest wins
        "product", "farm_asset" -> ConflictStrategy.NEWEST_WINS
        
        // Records: attempt merge for different fields
        "vaccination_record", "growth_record", "activity_log" -> ConflictStrategy.MERGE
        
        // Preferences: client wins (user's local choices)
        "user_preferences", "settings" -> ConflictStrategy.CLIENT_WINS
        
        // Default: use server version
        else -> ConflictStrategy.SERVER_WINS
    }

    /**
     * Resolve conflict between local and remote entities
     */
    fun <T : SyncableEntity> resolve(
        local: T,
        remote: T,
        strategy: ConflictStrategy = getDefaultStrategy(local.entityType)
    ): ConflictResult<T> {
        Timber.d("Resolving conflict for ${local.entityType}:${local.entityId} using $strategy")

        return when (strategy) {
            ConflictStrategy.SERVER_WINS -> {
                Timber.d("Server wins: using remote version")
                ConflictResult.Resolved(remote, strategy)
            }

            ConflictStrategy.CLIENT_WINS -> {
                Timber.d("Client wins: using local version")
                ConflictResult.Resolved(local, strategy)
            }

            ConflictStrategy.NEWEST_WINS -> {
                val winner = if (local.updatedAt >= remote.updatedAt) local else remote
                Timber.d("Newest wins: using ${if (winner == local) "local" else "remote"} version")
                ConflictResult.Resolved(winner, strategy)
            }

            ConflictStrategy.MERGE -> {
                Timber.d("Attempting merge resolution")
                attemptMerge(local, remote)
            }

            ConflictStrategy.ASK_USER -> {
                val conflictFields = findConflictingFields(local, remote)
                Timber.d("Deferring to user: ${conflictFields.size} conflicting fields")
                ConflictResult.NeedsUserInput(local, remote, conflictFields)
            }
        }
    }

    /**
     * Attempt to merge local and remote by combining non-conflicting changes
     */
    private fun <T : SyncableEntity> attemptMerge(local: T, remote: T): ConflictResult<T> {
        val conflictFields = findConflictingFields(local, remote)
        
        return if (conflictFields.isEmpty()) {
            // No actual conflicts, safe to merge (prefer newest)
            val merged = if (local.updatedAt >= remote.updatedAt) local else remote
            ConflictResult.Resolved(merged, ConflictStrategy.MERGE)
        } else if (conflictFields.size <= 2 && !hasImportantFieldConflict(conflictFields)) {
            // Minor conflicts, use newest version
            val merged = if (local.updatedAt >= remote.updatedAt) local else remote
            Timber.d("Minor conflicts in $conflictFields, using newest")
            ConflictResult.Resolved(merged, ConflictStrategy.MERGE)
        } else {
            // Too many conflicts or important fields, ask user
            ConflictResult.NeedsUserInput(local, remote, conflictFields)
        }
    }

    /**
     * Find fields that differ between local and remote
     */
    private fun <T : SyncableEntity> findConflictingFields(local: T, remote: T): List<String> {
        // This is a simplified implementation
        // In production, you'd use reflection or entity-specific comparison
        val conflicts = mutableListOf<String>()
        
        // Compare common fields
        if (local.updatedAt != remote.updatedAt) conflicts.add("updatedAt")
        
        // Additional field-level comparison would be entity-specific
        // This is a placeholder for actual implementation
        return conflicts
    }

    /**
     * Check if conflicting fields include important/critical ones
     */
    private fun hasImportantFieldConflict(fields: List<String>): Boolean {
        val importantFields = setOf(
            "status", "quantity", "price", "amount",
            "paymentStatus", "healthStatus", "administeredAt"
        )
        return fields.any { it in importantFields }
    }
}

/**
 * Interface for entities that can be synced and may have conflicts
 */
interface SyncableEntity {
    val entityType: String
    val entityId: String
    val updatedAt: Long
    val syncedAt: Long?
    val dirty: Boolean
}
