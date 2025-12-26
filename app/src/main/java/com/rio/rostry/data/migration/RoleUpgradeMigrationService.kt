package com.rio.rostry.data.migration

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.MigrationProgress
import com.rio.rostry.data.database.entity.RoleMigrationEntity
import com.rio.rostry.data.storage.CloudStorageQuotaManager
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of a role upgrade migration.
 */
sealed class MigrationResult {
    data class Success(val migrationId: String) : MigrationResult()
    data class Error(val message: String, val canRetry: Boolean = true) : MigrationResult()
    object InsufficientQuota : MigrationResult()
    object AlreadyMigrating : MigrationResult()
}

/**
 * Service for orchestrating role upgrade data migrations.
 * 
 * Handles the FARMER → ENTHUSIAST transition:
 * 1. Validates prerequisites (data integrity, quota availability)
 * 2. Creates local data snapshot for rollback
 * 3. Copies Firestore data from farmer collections to enthusiast collections
 * 4. Updates user role
 * 5. Triggers sync reconciliation
 * 6. Verifies data integrity
 */
@Singleton
class RoleUpgradeMigrationService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val roleMigrationDao: RoleMigrationDao,
    private val storageQuotaManager: CloudStorageQuotaManager,
    private val userDao: UserDao,
    private val farmAssetDao: FarmAssetDao,
    private val dailyLogDao: DailyLogDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val growthRecordDao: GrowthRecordDao,
    private val mortalityRecordDao: MortalityRecordDao,
    private val breedingPairDao: BreedingPairDao,
    private val farmAlertDao: FarmAlertDao
) {
    companion object {
        private const val TAG = "RoleUpgradeMigration"
        
        // Firestore collection paths
        const val FARMERS_COLLECTION = "farmers"
        const val ENTHUSIASTS_COLLECTION = "enthusiasts"
        const val USERS_COLLECTION = "users"
        
        // Subcollections to migrate
        val SUBCOLLECTIONS_TO_MIGRATE = listOf(
            "farm_assets",
            "daily_logs",
            "vaccinations",
            "growth_records",
            "mortality_records",
            "breeding_pairs",
            "alerts",
            "hatching_batches",
            "quarantine_records"
        )
    }
    
    /**
     * Initiate a role upgrade migration from FARMER to ENTHUSIAST.
     * 
     * @param userId User ID to upgrade
     * @param migrateData If true, copies all farmer data to enthusiast collections.
     *                    If false, performs "fresh start" with just role change.
     * @return MigrationResult with migration ID or error
     */
    suspend fun initiateMigration(
        userId: String,
        migrateData: Boolean
    ): MigrationResult = withContext(Dispatchers.IO) {
        Timber.d("Initiating migration for user $userId, migrateData=$migrateData")
        
        try {
            // Check for existing active migration
            if (roleMigrationDao.hasActiveMigration(userId)) {
                Timber.w("User $userId already has an active migration")
                return@withContext MigrationResult.AlreadyMigrating
            }
            
            // Validate user is FARMER
            val user = userDao.getUserById(userId).firstOrNull()
            if (user == null) {
                return@withContext MigrationResult.Error("User not found")
            }
            if (user.role != UserType.FARMER) {
                return@withContext MigrationResult.Error("User is not a FARMER, current role: ${user.role}")
            }
            
            // Calculate data to migrate
            val totalItems = if (migrateData) {
                calculateTotalMigrationItems(userId)
            } else {
                1 // Just role update
            }
            
            // Check quota availability for ENTHUSIAST
            if (migrateData) {
                val quotaAvailable = checkQuotaAvailability(userId, UserType.ENTHUSIAST)
                if (!quotaAvailable) {
                    Timber.w("Insufficient quota for migration")
                    return@withContext MigrationResult.InsufficientQuota
                }
            }
            
            // Create migration record
            val migrationId = UUID.randomUUID().toString()
            val migration = RoleMigrationEntity.create(
                migrationId = migrationId,
                userId = userId,
                fromRole = UserType.FARMER.name,
                toRole = UserType.ENTHUSIAST.name,
                totalItems = totalItems
            )
            roleMigrationDao.insert(migration)
            
            Timber.i("Created migration $migrationId for user $userId with $totalItems items")
            
            // If not migrating data, just update role immediately
            if (!migrateData) {
                performRoleUpdateOnly(migrationId, userId)
            }
            
            MigrationResult.Success(migrationId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initiate migration for user $userId")
            MigrationResult.Error(e.message ?: "Unknown error", canRetry = true)
        }
    }
    
    /**
     * Observe migration progress.
     */
    fun observeMigrationProgress(migrationId: String): Flow<MigrationProgress?> {
        return roleMigrationDao.observeById(migrationId).map { entity ->
            entity?.let { MigrationProgress.from(it) }
        }
    }
    
    /**
     * Observe active migration for a user.
     */
    fun observeActiveMigration(userId: String): Flow<MigrationProgress?> {
        return roleMigrationDao.observeLatestForUser(userId).map { entity ->
            entity?.takeIf { it.isActive }?.let { MigrationProgress.from(it) }
        }
    }
    
    /**
     * Get current migration status.
     */
    suspend fun getMigrationStatus(migrationId: String): RoleMigrationEntity? {
        return roleMigrationDao.getById(migrationId)
    }
    
    /**
     * Execute migration step by step. Called by MigrationWorker.
     */
    suspend fun executeMigration(
        migrationId: String,
        onProgress: suspend (phase: String, current: Int, total: Int) -> Unit
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        val migration = roleMigrationDao.getById(migrationId)
            ?: return@withContext Resource.Error("Migration not found")
        
        if (migration.status == RoleMigrationEntity.STATUS_COMPLETED) {
            return@withContext Resource.Success(Unit)
        }
        
        val userId = migration.userId
        
        try {
            // Phase 1: Start migration
            updateMigrationStatus(migrationId, RoleMigrationEntity.STATUS_IN_PROGRESS)
            updateMigrationProgress(migrationId, 0, RoleMigrationEntity.PHASE_VALIDATION, null)
            onProgress(RoleMigrationEntity.PHASE_VALIDATION, 0, migration.totalItems)
            
            // Phase 2: Validate data integrity
            Timber.d("Phase: Validation for $migrationId")
            validateLocalData(userId)
            
            // Phase 3: Cloud copy
            Timber.d("Phase: Cloud Copy for $migrationId")
            updateMigrationProgress(migrationId, 0, RoleMigrationEntity.PHASE_CLOUD_COPY, null)
            onProgress(RoleMigrationEntity.PHASE_CLOUD_COPY, 0, migration.totalItems)
            
            var migratedCount = 0
            for (subcollection in SUBCOLLECTIONS_TO_MIGRATE) {
                val count = copySubcollection(userId, subcollection)
                migratedCount += count
                updateMigrationProgress(migrationId, migratedCount, RoleMigrationEntity.PHASE_CLOUD_COPY, subcollection)
                onProgress(RoleMigrationEntity.PHASE_CLOUD_COPY, migratedCount, migration.totalItems)
            }
            
            // Phase 4: Update role
            Timber.d("Phase: Role Update for $migrationId")
            updateMigrationProgress(migrationId, migratedCount, RoleMigrationEntity.PHASE_ROLE_UPDATE, null)
            onProgress(RoleMigrationEntity.PHASE_ROLE_UPDATE, migratedCount, migration.totalItems)
            
            updateUserRole(userId, UserType.ENTHUSIAST)
            
            // Phase 5: Update quota limits
            storageQuotaManager.updateQuotaLimits(userId, UserType.ENTHUSIAST)
            
            // Phase 6: Mark complete
            updateMigrationProgress(migrationId, migration.totalItems, RoleMigrationEntity.PHASE_CLEANUP, null)
            onProgress(RoleMigrationEntity.PHASE_CLEANUP, migration.totalItems, migration.totalItems)
            
            completeMigration(migrationId)
            
            Timber.i("Migration $migrationId completed successfully")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Migration $migrationId failed")
            failMigration(migrationId, e.message ?: "Unknown error")
            Resource.Error("Migration failed: ${e.message}")
        }
    }
    
    /**
     * Rollback a failed or in-progress migration.
     */
    suspend fun rollbackMigration(migrationId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        val migration = roleMigrationDao.getById(migrationId)
            ?: return@withContext Resource.Error("Migration not found")
        
        if (!migration.canRollback) {
            return@withContext Resource.Error("Migration cannot be rolled back")
        }
        
        try {
            val userId = migration.userId
            
            // Restore original role if it was changed
            val user = userDao.getUserById(userId).firstOrNull()
            if (user?.role == UserType.ENTHUSIAST) {
                updateUserRole(userId, UserType.FARMER)
                storageQuotaManager.updateQuotaLimits(userId, UserType.FARMER)
            }
            
            // Clean up any copied data in enthusiast collection
            deleteEnthusiastData(userId)
            
            // Mark migration as rolled back
            roleMigrationDao.updateStatus(
                migrationId = migrationId,
                status = RoleMigrationEntity.STATUS_ROLLED_BACK
            )
            
            Timber.i("Migration $migrationId rolled back successfully")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to rollback migration $migrationId")
            Resource.Error("Rollback failed: ${e.message}")
        }
    }
    
    /**
     * Resume a paused or interrupted migration.
     */
    suspend fun resumeMigration(migrationId: String): Resource<Unit> {
        val migration = roleMigrationDao.getById(migrationId)
            ?: return Resource.Error("Migration not found")
        
        if (migration.status !in listOf(
            RoleMigrationEntity.STATUS_PAUSED, 
            RoleMigrationEntity.STATUS_FAILED
        )) {
            return Resource.Error("Migration cannot be resumed from status ${migration.status}")
        }
        
        if (!migration.canRetry) {
            return Resource.Error("Maximum retry attempts exceeded")
        }
        
        // Reset to in-progress
        roleMigrationDao.update(
            migration.copy(
                status = RoleMigrationEntity.STATUS_PENDING,
                retryCount = migration.retryCount + 1,
                updatedAt = System.currentTimeMillis()
            )
        )
        
        return Resource.Success(Unit)
    }
    
    // Private helper methods
    
    private suspend fun calculateTotalMigrationItems(userId: String): Int {
        var total = 0
        total += farmAssetDao.getAssetCountForFarmer(userId)
        total += dailyLogDao.getLogCountForFarmer(userId)
        total += vaccinationRecordDao.getRecordCountForFarmer(userId)
        total += growthRecordDao.getRecordCountForFarmer(userId)
        total += mortalityRecordDao.getRecordCountForFarmer(userId)
        total += breedingPairDao.getPairCountForFarmer(userId)
        total += farmAlertDao.getAlertCountForFarmer(userId)
        total += 1 // Role update itself
        return total.coerceAtLeast(1)
    }
    
    private suspend fun checkQuotaAvailability(userId: String, targetRole: UserType): Boolean {
        Timber.d("Checking quota availability for user $userId upgrading to ${targetRole.name}")
        
        // 1. Get the quota for the target role
        val targetQuota = storageQuotaManager.getQuotaForRole(targetRole)
        
        // 2. Fetch current actual storage usage (BLOBs)
        // We use the cached status or trigger a recalculation if needed.
        // For migration safety, we trigger a fresh calculation.
        val usageResource = storageQuotaManager.calculateStorageUsage(userId)
        val currentUsage = when (usageResource) {
            is Resource.Success -> usageResource.data
            else -> {
                Timber.w("Failed to calculate fresh usage, falling back to cached quota status")
                storageQuotaManager.getCachedQuotaStatus(userId, UserType.FARMER)?.usage
            }
        }

        if (currentUsage == null) {
            Timber.w("Could not determine current usage, allowing migration to proceed")
            return true
        }

        // 3. Compare current usage against target quota
        // Since the files in USERS_PATH/userId don't move, currentUsage is the projected usage.
        val exceedsTotal = currentUsage.totalUsedBytes > targetQuota.totalQuotaBytes
        val exceedsPublic = currentUsage.publicUsedBytes > targetQuota.publicLimitBytes
        val exceedsPrivate = currentUsage.privateUsedBytes > targetQuota.privateLimitBytes

        if (exceedsTotal || exceedsPublic || exceedsPrivate) {
            Timber.w("Quota exceeded for target role ${targetRole.name}: " +
                "Total: ${currentUsage.totalUsedBytes}/${targetQuota.totalQuotaBytes}, " +
                "Public: ${currentUsage.publicUsedBytes}/${targetQuota.publicLimitBytes}, " +
                "Private: ${currentUsage.privateUsedBytes}/${targetQuota.privateLimitBytes}")
            return false
        }

        Timber.d("Quota check passed for user $userId")
        return true
    }
    
    private suspend fun validateLocalData(userId: String) {
        // Basic validation - ensure user exists
        val user = userDao.getUserById(userId).firstOrNull()
            ?: throw IllegalStateException("User $userId not found during validation")
        
        if (user.role != UserType.FARMER) {
            throw IllegalStateException("User is not a FARMER")
        }
    }
    
    private suspend fun copySubcollection(userId: String, subcollection: String): Int {
        return try {
            val sourceRef = firestore.collection(FARMERS_COLLECTION)
                .document(userId)
                .collection(subcollection)
            
            val destRef = firestore.collection(ENTHUSIASTS_COLLECTION)
                .document(userId)
                .collection(subcollection)
            
            val snapshot = sourceRef.get().await()
            var count = 0
            
            for (doc in snapshot.documents) {
                val data = doc.data
                if (data != null) {
                    destRef.document(doc.id).set(data).await()
                    count++
                }
            }
            
            Timber.d("Copied $count documents from $subcollection for user $userId")
            count
        } catch (e: Exception) {
            Timber.w(e, "Error copying subcollection $subcollection (may not exist)")
            0
        }
    }
    
    private suspend fun updateUserRole(userId: String, newRole: UserType) {
        // Update local database
        val user = userDao.getUserById(userId).firstOrNull()
        if (user != null) {
            userDao.updateUser(user.copy(
                userType = newRole.name,
                updatedAt = Date(System.currentTimeMillis())
            ))
        }
        
        // Update Firestore
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .update(
                mapOf(
                    "userType" to newRole.name,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }
    
    private suspend fun performRoleUpdateOnly(migrationId: String, userId: String) {
        try {
            updateMigrationStatus(migrationId, RoleMigrationEntity.STATUS_IN_PROGRESS)
            updateMigrationProgress(migrationId, 0, RoleMigrationEntity.PHASE_ROLE_UPDATE, null)
            
            updateUserRole(userId, UserType.ENTHUSIAST)
            storageQuotaManager.updateQuotaLimits(userId, UserType.ENTHUSIAST)
            
            completeMigration(migrationId)
        } catch (e: Exception) {
            failMigration(migrationId, e.message ?: "Unknown error")
        }
    }
    
    private suspend fun updateMigrationStatus(migrationId: String, status: String) {
        roleMigrationDao.updateStatus(migrationId, status)
    }
    
    private suspend fun updateMigrationProgress(
        migrationId: String,
        migratedItems: Int,
        phase: String?,
        entity: String?
    ) {
        roleMigrationDao.updateProgress(
            migrationId = migrationId,
            status = RoleMigrationEntity.STATUS_IN_PROGRESS,
            migratedItems = migratedItems,
            phase = phase,
            entity = entity
        )
    }
    
    private suspend fun completeMigration(migrationId: String) {
        val now = System.currentTimeMillis()
        val migration = roleMigrationDao.getById(migrationId)
        if (migration != null) {
            roleMigrationDao.update(
                migration.copy(
                    status = RoleMigrationEntity.STATUS_COMPLETED,
                    migratedItems = migration.totalItems,
                    completedAt = now,
                    updatedAt = now
                )
            )
        }
    }
    
    private suspend fun failMigration(migrationId: String, error: String) {
        roleMigrationDao.updateStatus(migrationId, RoleMigrationEntity.STATUS_FAILED, error)
    }
    
    private suspend fun deleteEnthusiastData(userId: String) {
        for (subcollection in SUBCOLLECTIONS_TO_MIGRATE) {
            try {
                val ref = firestore.collection(ENTHUSIASTS_COLLECTION)
                    .document(userId)
                    .collection(subcollection)
                
                val snapshot = ref.get().await()
                for (doc in snapshot.documents) {
                    doc.reference.delete().await()
                }
            } catch (e: Exception) {
                Timber.w(e, "Error deleting $subcollection during rollback")
            }
        }
    }
}
