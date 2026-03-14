package com.rio.rostry.data.sync

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * Conflict resolution strategy
 */
enum class ConflictStrategy {
    SERVER_WINS,      // Always use server version (safe default)
    CLIENT_WINS,      // Always use client version (offline-first)
    NEWEST_WINS,      // Use version with latest timestamp (last-write-wins)
    MERGE,            // Attempt to merge changes (field-level)
    ASK_USER          // Defer to user for resolution
}

/**
 * Result of conflict resolution
 */
sealed class ConflictResult<T> {
    data class Resolved<T>(val value: T, val strategy: ConflictStrategy, val mergedFields: List<String> = emptyList()) : ConflictResult<T>()
    data class NeedsUserInput<T>(val local: T, val remote: T, val conflictFields: List<FieldConflict>) : ConflictResult<T>()
    data class Error<T>(val message: String) : ConflictResult<T>()
}

/**
 * Represents a conflict on a specific field
 */
data class FieldConflict(
    val fieldName: String,
    val localValue: Any?,
    val remoteValue: Any?,
    val localTimestamp: Long,
    val remoteTimestamp: Long
) {
    val winner: FieldWinner = when {
        localTimestamp > remoteTimestamp -> FieldWinner.LOCAL
        remoteTimestamp > localTimestamp -> FieldWinner.REMOTE
        else -> FieldWinner.TIE
    }
}

enum class FieldWinner {
    LOCAL, REMOTE, TIE
}

/**
 * Metadata about a field for conflict resolution
 */
data class FieldMetadata(
    val name: String,
    val importance: FieldImportance,
    val mergeable: Boolean = true
)

enum class FieldImportance {
    CRITICAL,   // Must be resolved manually (amounts, status)
    IMPORTANT,  // Should be resolved with strategy
    MINOR       // Can be auto-merged
}

/**
 * Conflict resolver for handling data sync conflicts.
 * Implements multiple resolution strategies including field-level last-write-wins.
 */
@Singleton
class ConflictResolver @Inject constructor() {

    companion object {
        private const val TAG = "ConflictResolver"
    }

    // Cache for entity field metadata
    private val fieldMetadataCache = mutableMapOf<String, Map<String, FieldMetadata>>()

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
                val winnerSource = if (winner == local) "local" else "remote"
                Timber.d("Newest wins: using $winnerSource version (local=${local.updatedAt}, remote=${remote.updatedAt})")
                ConflictResult.Resolved(winner, strategy)
            }

            ConflictStrategy.MERGE -> {
                Timber.d("Attempting merge resolution with field-level conflict detection")
                attemptMerge(local, remote)
            }

            ConflictStrategy.ASK_USER -> {
                val conflictFields = detectFieldConflicts(local, remote)
                Timber.d("Deferring to user: ${conflictFields.size} conflicting fields detected")
                ConflictResult.NeedsUserInput(local, remote, conflictFields)
            }
        }
    }

    /**
     * Resolve using last-write-wins strategy for each field individually
     */
    fun <T : SyncableEntity> resolveWithLastWriteWins(
        local: T,
        remote: T
    ): ConflictResult<T> {
        Timber.d("Applying last-write-wins strategy for ${local.entityType}:${local.entityId}")

        val conflicts = detectFieldConflicts(local, remote)
        if (conflicts.isEmpty()) {
            // No conflicts, use newest version
            val winner = if (local.updatedAt >= remote.updatedAt) local else remote
            return ConflictResult.Resolved(winner, ConflictStrategy.NEWEST_WINS, emptyList())
        }

        // Apply last-write-wins per field
        val merged = applyLastWriteWins(local, remote, conflicts)
        val mergedFields = conflicts.map { it.fieldName }

        Timber.d("Last-write-wins resolved ${mergedFields.size} fields")
        return ConflictResult.Resolved(merged, ConflictStrategy.NEWEST_WINS, mergedFields)
    }

    /**
     * Detect all fields that differ between local and remote
     */
    fun <T : SyncableEntity> detectFieldConflicts(local: T, remote: T): List<FieldConflict> {
        val conflicts = mutableListOf<FieldConflict>()

        // Get entity-specific field comparison
        val entityConflicts = when (local.entityType) {
            "order" -> detectOrderConflicts(local, remote)
            "vaccination_record" -> detectVaccinationConflicts(local, remote)
            "expense" -> detectExpenseConflicts(local, remote)
            else -> detectGenericFieldConflicts(local, remote)
        }

        conflicts.addAll(entityConflicts)
        return conflicts
    }

    /**
     * Generic field conflict detection using reflection
     */
    private fun <T : SyncableEntity> detectGenericFieldConflicts(local: T, remote: T): List<FieldConflict> {
        val conflicts = mutableListOf<FieldConflict>()

        // Use reflection to compare common fields
        val localClass = local::class
        val remoteClass = remote::class

        if (localClass != remoteClass) return conflicts

        // Get timestamp fields for comparison
        val localUpdatedAt = local.updatedAt
        val remoteUpdatedAt = remote.updatedAt

        // Compare common string/number fields that might differ
        val comparableFields = listOf("status", "quantity", "amount", "name", "description")

        for (fieldName in comparableFields) {
            val localValue = getFieldValue(local, fieldName)
            val remoteValue = getFieldValue(remote, fieldName)

            if (localValue != null && remoteValue != null && localValue != remoteValue) {
                // Both have values and they differ
                conflicts.add(FieldConflict(
                    fieldName = fieldName,
                    localValue = localValue,
                    remoteValue = remoteValue,
                    localTimestamp = localUpdatedAt,
                    remoteTimestamp = remoteUpdatedAt
                ))
            }
        }

        return conflicts
    }

    /**
     * Order-specific conflict detection
     */
    private fun <T : SyncableEntity> detectOrderConflicts(local: T, remote: T): List<FieldConflict> {
        val conflicts = mutableListOf<FieldConflict>()

        // Order-specific field comparisons
        val orderFields = listOf("status", "totalAmount", "shippingAddress", "notes")

        for (fieldName in orderFields) {
            val localValue = getFieldValue(local, fieldName)
            val remoteValue = getFieldValue(remote, fieldName)

            if (localValue != null && remoteValue != null && localValue != remoteValue) {
                conflicts.add(FieldConflict(
                    fieldName = fieldName,
                    localValue = localValue,
                    remoteValue = remoteValue,
                    localTimestamp = local.updatedAt,
                    remoteTimestamp = remote.updatedAt
                ))
            }
        }

        return conflicts
    }

    /**
     * Vaccination record-specific conflict detection
     */
    private fun <T : SyncableEntity> detectVaccinationConflicts(local: T, remote: T): List<FieldConflict> {
        val conflicts = mutableListOf<FieldConflict>()

        // Vaccination-specific fields
        val vaccineFields = listOf("vaccineType", "administeredAt", "dosage", "administeredBy", "notes")

        for (fieldName in vaccineFields) {
            val localValue = getFieldValue(local, fieldName)
            val remoteValue = getFieldValue(remote, fieldName)

            if (localValue != null && remoteValue != null && localValue != remoteValue) {
                conflicts.add(FieldConflict(
                    fieldName = fieldName,
                    localValue = localValue,
                    remoteValue = remoteValue,
                    localTimestamp = local.updatedAt,
                    remoteTimestamp = remote.updatedAt
                ))
            }
        }

        return conflicts
    }

    /**
     * Expense-specific conflict detection
     */
    private fun <T : SyncableEntity> detectExpenseConflicts(local: T, remote: T): List<FieldConflict> {
        val conflicts = mutableListOf<FieldConflict>()

        // Expense-specific fields
        val expenseFields = listOf("amount", "category", "description", "date", "receiptUrl")

        for (fieldName in expenseFields) {
            val localValue = getFieldValue(local, fieldName)
            val remoteValue = getFieldValue(remote, fieldName)

            if (localValue != null && remoteValue != null && localValue != remoteValue) {
                conflicts.add(FieldConflict(
                    fieldName = fieldName,
                    localValue = localValue,
                    remoteValue = remoteValue,
                    localTimestamp = local.updatedAt,
                    remoteTimestamp = remote.updatedAt
                ))
            }
        }

        return conflicts
    }

    /**
     * Apply last-write-wins strategy to merge conflicting fields
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : SyncableEntity> applyLastWriteWins(
        local: T,
        remote: T,
        conflicts: List<FieldConflict>
    ): T {
        // Create a mutable copy from the newer version
        val base = if (local.updatedAt >= remote.updatedAt) {
            cloneEntity(local)
        } else {
            cloneEntity(remote)
        }

        // For each conflict, take the value from the newer version
        for (conflict in conflicts) {
            val winner = if (conflict.localTimestamp >= conflict.remoteTimestamp) local else remote
            setFieldValue(base, conflict.fieldName, getFieldValue(winner, conflict.fieldName))
        }

        return base
    }

    /**
     * Attempt to merge local and remote by combining non-conflicting changes
     */
    private fun <T : SyncableEntity> attemptMerge(local: T, remote: T): ConflictResult<T> {
        val conflicts = detectFieldConflicts(local, remote)

        return if (conflicts.isEmpty()) {
            // No actual conflicts, safe to merge (prefer newest)
            val merged = if (local.updatedAt >= remote.updatedAt) local else remote
            Timber.d("No conflicts detected, using newest version")
            ConflictResult.Resolved(merged, ConflictStrategy.MERGE)
        } else if (conflicts.size <= 2 && !hasImportantFieldConflict(conflicts)) {
            // Minor conflicts, use newest version with last-write-wins
            val merged = applyLastWriteWins(local, remote, conflicts)
            Timber.d("Minor conflicts in ${conflicts.map { it.fieldName }}, applying last-write-wins")
            ConflictResult.Resolved(merged, ConflictStrategy.MERGE, conflicts.map { it.fieldName })
        } else {
            // Too many conflicts or important fields, ask user
            Timber.d("Major conflicts detected: ${conflicts.size} fields, asking user")
            ConflictResult.NeedsUserInput(local, remote, conflicts)
        }
    }

    /**
     * Check if conflicting fields include important/critical ones
     */
    private fun hasImportantFieldConflict(fields: List<FieldConflict>): Boolean {
        val importantFields = setOf(
            "status", "quantity", "price", "amount",
            "paymentStatus", "healthStatus", "administeredAt"
        )
        return fields.any { it.fieldName in importantFields }
    }

    /**
     * Get field value using reflection
     */
    @Suppress("UNCHECKED_CAST")
    private fun getFieldValue(entity: Any, fieldName: String): Any? {
        return try {
            val property = entity::class.memberProperties
                .find { it.name == fieldName }
            property?.get(entity)
        } catch (e: Exception) {
            Timber.w(e, "Failed to get field value: $fieldName")
            null
        }
    }

    /**
     * Set field value using reflection
     */
    private fun setFieldValue(entity: Any, fieldName: String, value: Any?) {
        try {
            val property = entity::class.memberProperties
                .find { it.name == fieldName } as? KProperty1<Any, Any?>
            property?.set(entity, value)
        } catch (e: Exception) {
            Timber.w(e, "Failed to set field value: $fieldName")
        }
    }

    /**
     * Clone entity (simplified - creates a new instance with same values)
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : SyncableEntity> cloneEntity(entity: T): T {
        // For entities with copy method (data classes)
        return try {
            (entity as? com.rio.rostry.data.database.entity.OrderEntity)?.copy()
                ?: (entity as? com.rio.rostry.data.database.entity.ExpenseEntity)?.copy()
                ?: entity
        } catch (e: Exception) {
            entity
        } as T
    }

    /**
     * Resolve conflict with automatic last-write-wins for non-critical fields
     */
    fun <T : SyncableEntity> autoResolve(
        local: T,
        remote: T,
        criticalFields: List<String> = emptyList()
    ): ConflictResult<T> {
        val conflicts = detectFieldConflicts(local, remote)
        val criticalConflicts = conflicts.filter { it.fieldName in criticalFields }

        return if (criticalConflicts.isEmpty()) {
            // No critical conflicts, apply last-write-wins
            resolveWithLastWriteWins(local, remote)
        } else {
            // Has critical conflicts, ask user
            ConflictResult.NeedsUserInput(local, remote, conflicts)
        }
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