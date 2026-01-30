package com.rio.rostry.data.sync

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.ConnectivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.session.SessionManager
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.first

/**
 * SyncManager coordinates incremental, offline-first sync across entities.
 * It demonstrates a pattern and leaves actual network I/O to the caller (e.g., repositories or API services).
 */
@Singleton
class SyncManager @Inject constructor(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val orderDao: OrderDao,
    private val productTrackingDao: ProductTrackingDao,
    private val chatMessageDao: ChatMessageDao,
    private val transferDao: TransferDao,
    private val syncStateDao: SyncStateDao,
    private val outboxDao: OutboxDao,
    private val firestoreService: SyncRemote,
    private val connectivityManager: ConnectivityManager,
    private val gson: Gson,
    // Farm monitoring DAOs
    private val breedingPairDao: com.rio.rostry.data.database.dao.BreedingPairDao,
    private val farmAlertDao: com.rio.rostry.data.database.dao.FarmAlertDao,
    private val farmerDashboardSnapshotDao: com.rio.rostry.data.database.dao.FarmerDashboardSnapshotDao,
    private val vaccinationRecordDao: com.rio.rostry.data.database.dao.VaccinationRecordDao,
    private val growthRecordDao: com.rio.rostry.data.database.dao.GrowthRecordDao,
    private val quarantineRecordDao: com.rio.rostry.data.database.dao.QuarantineRecordDao,
    private val mortalityRecordDao: com.rio.rostry.data.database.dao.MortalityRecordDao,
    private val hatchingBatchDao: com.rio.rostry.data.database.dao.HatchingBatchDao,
    private val hatchingLogDao: com.rio.rostry.data.database.dao.HatchingLogDao,
    // Sprint 1 DAOs
    private val dailyLogDao: com.rio.rostry.data.database.dao.DailyLogDao,
    private val taskDao: com.rio.rostry.data.database.dao.TaskDao,
    // Enthusiast DAOs
    private val matingLogDao: com.rio.rostry.data.database.dao.MatingLogDao,
    private val eggCollectionDao: com.rio.rostry.data.database.dao.EggCollectionDao,
    private val enthusiastDashboardSnapshotDao: com.rio.rostry.data.database.dao.EnthusiastDashboardSnapshotDao,
    private val roleMigrationDao: com.rio.rostry.data.database.dao.RoleMigrationDao,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val traceabilityRepository: TraceabilityRepository,
    private val sessionManager: SessionManager,
    // Split-Brain Data Architecture
    private val batchSummaryDao: com.rio.rostry.data.database.dao.BatchSummaryDao
) {
    companion object {
        // Overall sync timeout to prevent indefinite operations
        private const val OVERALL_SYNC_TIMEOUT_MS = 300_000L // 5 minutes

        // Per-domain timeouts
        private const val PRODUCTS_SYNC_TIMEOUT_MS = 60_000L // 1 minute
        private const val ORDERS_SYNC_TIMEOUT_MS = 45_000L
        private const val TRANSFERS_SYNC_TIMEOUT_MS = 45_000L
        private const val OUTBOX_SYNC_TIMEOUT_MS = 90_000L // 1.5 minutes
        private const val DEFAULT_DOMAIN_TIMEOUT_MS = 30_000L // 30 seconds
    }

    data class SyncStats(
        val pushed: Int = 0,
        val pulled: Int = 0,
        val outboxProcessed: Int = 0,
        val timedOutDomains: List<String> = emptyList()
    )

    data class SyncProgress(
        val totalPending: Int,
        val processed: Int,
        val currentEntityType: String,
        val errors: List<String>
    )

    val syncProgressFlow = MutableSharedFlow<SyncProgress>()

    data class ConflictEvent(
        val entityType: String,
        val entityId: String,
        val conflictFields: List<String>,
        val mergedAt: Long
    )

    val conflictEvents = MutableSharedFlow<ConflictEvent>(extraBufferCapacity = 64)
    
    // Sync state tracking
    @Volatile private var syncInProgress = false
    
    // Helper classes for refactoring syncAllInternal
    private data class SyncContext(
        val userId: String?,
        val role: UserType?,
        val now: Long,
        val state: SyncStateEntity,
        val timedOutDomains: MutableList<String>
    )

    private data class SyncResult(
        val pulls: Int = 0,
        val pushes: Int = 0,
        val processed: Int = 0, // For outbox
        val errors: List<String> = emptyList()
    ) {
        operator fun plus(other: SyncResult): SyncResult {
            return SyncResult(
                pulls = this.pulls + other.pulls,
                pushes = this.pushes + other.pushes,
                processed = this.processed + other.processed,
                errors = this.errors + other.errors
            )
        }
    }

    private fun isForeignKeyConstraintFailure(t: Throwable): Boolean {
        var cur: Throwable? = t
        while (cur != null) {
            val cn = cur::class.qualifiedName ?: cur::class.simpleName ?: ""
            val msg = cur.message ?: ""
            if ((cn.contains("SQLiteConstraintException")) && msg.contains(
                    "FOREIGN KEY constraint failed",
                    ignoreCase = true
                )
            ) {
                return true
            }
            cur = cur.cause
        }
        return false
    }

    private fun isTerminalTransferStatus(status: String?): Boolean {
        return when (status?.uppercase()) {
            "COMPLETED", "CANCELLED", "TIMED_OUT" -> true
            else -> false
        }
    }

    private fun protectLineageOnPush(local: ProductEntity, remote: ProductEntity?): ProductEntity {
        if (remote == null) return local
        // If remote lineage is newer or present, avoid overwriting lineage fields from local dirty
        val lineageKept = local.copy(
            familyTreeId = remote.familyTreeId ?: local.familyTreeId,
            parentIdsJson = remote.parentIdsJson ?: local.parentIdsJson,
            transferHistoryJson = remote.transferHistoryJson ?: local.transferHistoryJson,
            // Explicit lineage links: parentMaleId/parentFemaleId
            parentMaleId = remote.parentMaleId ?: local.parentMaleId,
            parentFemaleId = remote.parentFemaleId ?: local.parentFemaleId
        )
        return lineageKept
    }

    private suspend fun mergeProductRemoteLocal(
        remote: ProductEntity,
        local: ProductEntity?
    ): ProductEntity {
        if (local == null) return remote
        // Last-write-wins by updatedAt for general fields
        fun pickString(
            remoteVal: String?,
            localVal: String?,
            rUpdated: Long,
            lUpdated: Long
        ): String? =
            if ((remoteVal != localVal) && (rUpdated > lUpdated)) remoteVal else localVal

        fun pickInt(remoteVal: Int?, localVal: Int?, rUpdated: Long, lUpdated: Long): Int? =
            if ((remoteVal != localVal) && (rUpdated > lUpdated)) remoteVal else localVal

        val rU = remote.updatedAt
        val lU = local.updatedAt

        // Lineage: if differs, verify before accepting remote. Skip if remote IDs are null/blank.
        val canVerify =
            !remote.parentMaleId.isNullOrBlank() && !remote.parentFemaleId.isNullOrBlank()
        val verified: Boolean = if (canVerify && rU > lU) {
            when (val res = traceabilityRepository.verifyParentage(
                remote.productId,
                remote.parentMaleId!!,
                remote.parentFemaleId!!
            )) {
                is Resource.Success -> res.data == true
                else -> false
            }
        } else false

        val acceptParentMale =
            if (remote.parentMaleId != local.parentMaleId && rU > lU && verified) {
                remote.parentMaleId
            } else local.parentMaleId

        val acceptParentFemale =
            if (remote.parentFemaleId != local.parentFemaleId && rU > lU && verified) {
                remote.parentFemaleId
            } else local.parentFemaleId

        // Prefer stage based on lastStageTransitionAt recency (not updatedAt)
        val stageByTransition = when {
            remote.lastStageTransitionAt != null && (local.lastStageTransitionAt == null || remote.lastStageTransitionAt > local.lastStageTransitionAt) -> remote.stage
            else -> local.stage
        }

        // Prefer lifecycleStatus based on breederEligibleAt recency when applicable; otherwise fallback to LWW
        val lifecycleByEligibility = when {
            (remote.breederEligibleAt ?: 0L) > (local.breederEligibleAt
                ?: 0L) -> remote.lifecycleStatus

            else -> pickString(remote.lifecycleStatus, local.lifecycleStatus, rU, lU)
        }

        // Age weeks should be monotonic non-decreasing; do not use updatedAt to regress
        val ageWeeksResolved = listOfNotNull(remote.ageWeeks, local.ageWeeks).maxOrNull()

        return local.copy(
            // core simple fields via per-field strategies
            stage = stageByTransition,
            lifecycleStatus = lifecycleByEligibility,
            ageWeeks = ageWeeksResolved,
            lastStageTransitionAt = when {
                remote.lastStageTransitionAt != null && (local.lastStageTransitionAt == null || remote.lastStageTransitionAt > local.lastStageTransitionAt) -> remote.lastStageTransitionAt
                else -> local.lastStageTransitionAt
            },
            breederEligibleAt = when {
                (remote.breederEligibleAt ?: 0L) > (local.breederEligibleAt
                    ?: 0L) -> remote.breederEligibleAt

                else -> local.breederEligibleAt
            },
            parentMaleId = acceptParentMale,
            parentFemaleId = acceptParentFemale,
            updatedAt = maxOf(rU, lU),
            dirty = false
        )
    }

    private suspend fun <T> withRetry(
        attempts: Int = 3,
        initialDelayMs: Long = 750,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMs
        var lastError: Throwable? = null
        repeat(attempts) { attempt ->
            try {
                // Simple connectivity guard to avoid hammering on poor networks
                if (!connectivityManager.isOnline()) {
                    Timber.w("Network offline, delaying sync attempt ${attempt + 1}")
                }
                return block()
            } catch (t: Throwable) {
                lastError = t
                Timber.w(t, "Sync network operation failed on attempt ${attempt + 1}")
                if (attempt == attempts - 1) throw t
                kotlinx.coroutines.delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong()
            }
        }
        throw lastError ?: IllegalStateException("Unknown sync error")
    }

    /**
     * Main entry point to sync everything incrementally.
     * Enforces overall timeout and per-domain timeouts for resilience.
     */
    suspend fun syncAll(now: Long = System.currentTimeMillis()): Resource<SyncStats> =
        withContext(Dispatchers.IO) {
            val timedOutDomains = mutableListOf<String>()

            try {
                withTimeout(OVERALL_SYNC_TIMEOUT_MS) {
                    syncAllInternal(now, timedOutDomains)
                }
            } catch (e: TimeoutCancellationException) {
                Timber.e(e, "Overall sync timeout after ${OVERALL_SYNC_TIMEOUT_MS}ms")
                Resource.Error("Sync timed out. Partial sync may have completed.")
            }
        }

    private suspend fun syncAllInternal(
        now: Long,
        timedOutDomains: MutableList<String>
    ): Resource<SyncStats> {
        try {
            val state = syncStateDao.get() ?: SyncStateEntity()
            val userId = firebaseAuth.currentUser?.uid
            val role = sessionManager.sessionRole().first()
            
            val ctx = SyncContext(userId, role, now, state, timedOutDomains)
            
            // Execute sync blocks
            val r1 = syncProductTracking(ctx)
            val r2 = syncEnthusiastData(ctx)
            val r3 = syncUsers(ctx)
            val r4 = syncProducts(ctx)
            val r5 = syncOrders(ctx)
            val r6 = syncTransfers(ctx)
            val r7 = syncChat(ctx)
            val r8 = processOutbox(ctx)
            val r9 = syncFarmMonitoring(ctx)
            
            // Aggregate results
            val allResults = listOf(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            val totalPulls = allResults.sumOf { it.pulls }
            val totalPushes = allResults.sumOf { it.pushes }
            val totalProcessed = r8.processed
            val allErrors = allResults.flatMap { it.errors }
            
            // Update sync state for all domains once at the end
            syncStateDao.upsert(
                SyncStateEntity(
                    lastProductSyncAt = now,
                    lastOrderSyncAt = now,
                    lastTransferSyncAt = now,
                    lastTrackingSyncAt = now,
                    lastChatSyncAt = now,
                    lastBreedingSyncAt = now,
                    lastAlertSyncAt = now,
                    lastDashboardSyncAt = now,
                    lastVaccinationSyncAt = now,
                    lastGrowthSyncAt = now,
                    lastQuarantineSyncAt = now,
                    lastMortalitySyncAt = now,
                    lastHatchingSyncAt = now,
                    lastDailyLogSyncAt = now,
                    lastBatchSummarySyncAt = now, // Split-Brain
                    lastTaskSyncAt = now,
                    lastUserSyncAt = now,
                    lastEnthusiastBreedingSyncAt = now,
                    lastEnthusiastDashboardSyncAt = now
                )
            )

            // Final emission to report any errors collected during partial failures
            syncProgressFlow.emit(
                 SyncProgress(
                     0,
                     0,
                     "COMPLETED",
                     allErrors
                 )
            )

            val stats =
                SyncStats(totalPushes, totalPulls, outboxProcessed = totalProcessed, timedOutDomains = timedOutDomains)
            if (timedOutDomains.isNotEmpty()) {
                Timber.w("Sync completed with timeouts in: ${timedOutDomains.joinToString()}")
            }
            return Resource.Success(stats)
        } catch (e: Exception) {
            Timber.e(e, "syncAll failed")
            return Resource.Error(e.message ?: "Sync failed")
        }
    }
    
    // ==========================================
    // Refactored Helper Methods
    // ==========================================

    private suspend fun syncProductTracking(ctx: SyncContext): SyncResult {
        var pulls = 0
        var pushes = 0
        val errors = mutableListOf<String>()
        try {
            // Pull newer remote changes since lastTrackingSyncAt
            val remoteTrackingUpdated: List<ProductTrackingEntity> = withRetry {
                firestoreService.fetchUpdatedTrackings(ctx.userId, ctx.state.lastTrackingSyncAt)
            }
            if (remoteTrackingUpdated.isNotEmpty()) {
                productTrackingDao.upsertAll(remoteTrackingUpdated)
                pulls += remoteTrackingUpdated.size
            }

            // Push local dirty rows
            val localDirty = productTrackingDao.getUpdatedSince(ctx.state.lastTrackingSyncAt, limit = 50)
                .filter { it.dirty }
            if (localDirty.isNotEmpty()) {
                withRetry { firestoreService.pushTrackings(localDirty) }
                val cleaned = localDirty.map { it.copy(dirty = false, updatedAt = ctx.now) }
                productTrackingDao.upsertAll(cleaned)
                pushes += cleaned.size
            }
        } catch (e: Exception) {
            Timber.e(e, "Product Tracking sync failed")
            errors.add("Tracking: ${e.message}")
        }
        return SyncResult(pulls, pushes, 0, errors)
    }

    private suspend fun syncEnthusiastData(ctx: SyncContext): SyncResult {
        var pulls = 0
        var pushes = 0
        val errors = mutableListOf<String>()
        
        if (ctx.role == UserType.ENTHUSIAST && ctx.userId != null) {
            try {
                // Mating Logs
                run {
                    val remote = withRetry {
                        firestoreService.fetchUpdatedMatingLogs(ctx.userId, ctx.state.lastEnthusiastBreedingSyncAt)
                    }
                    if (remote.isNotEmpty()) {
                        val localDirtyIds = matingLogDao.getDirty().map { it.logId }.toHashSet()
                        val toUpsert = remote.filter { it.logId !in localDirtyIds }
                        toUpsert.forEach { matingLogDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = matingLogDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushMatingLogs(ctx.userId, localDirty) }
                        matingLogDao.clearDirty(localDirty.map { it.logId }, ctx.now)
                        pushes += localDirty.size
                    }
                }

                // Egg Collections
                run {
                    val remote = withRetry {
                        firestoreService.fetchUpdatedEggCollections(ctx.userId, ctx.state.lastEnthusiastBreedingSyncAt)
                    }
                    if (remote.isNotEmpty()) {
                        val localDirtyIds = eggCollectionDao.getDirty().map { it.collectionId }.toHashSet()
                        val toUpsert = remote.filter { it.collectionId !in localDirtyIds }
                        toUpsert.forEach { eggCollectionDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = eggCollectionDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushEggCollections(ctx.userId, localDirty) }
                        eggCollectionDao.clearDirty(localDirty.map { it.collectionId }, ctx.now)
                        pushes += localDirty.size
                    }
                }

                // Snapshots
                run {
                    val remote = withRetry {
                        firestoreService.fetchUpdatedEnthusiastSnapshots(ctx.userId, ctx.state.lastEnthusiastDashboardSyncAt)
                    }
                    if (remote.isNotEmpty()) {
                        val localDirtyIds = enthusiastDashboardSnapshotDao.getDirty().map { it.snapshotId }.toHashSet()
                        val toUpsert = remote.filter { it.snapshotId !in localDirtyIds }
                        toUpsert.forEach { enthusiastDashboardSnapshotDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = enthusiastDashboardSnapshotDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushEnthusiastSnapshots(ctx.userId, localDirty) }
                        enthusiastDashboardSnapshotDao.clearDirty(localDirty.map { it.snapshotId }, ctx.now)
                        pushes += localDirty.size
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Enthusiast sync failed")
                errors.add("Enthusiast Data: ${e.message}")
            }
        }
        return SyncResult(pulls, pushes, 0, errors)
    }

    private suspend fun syncUsers(ctx: SyncContext): SyncResult {
        var pulls = 0
        val errors = mutableListOf<String>()
        try {
            val remoteUsersUpdated: List<UserEntity> = withRetry {
                firestoreService.fetchUpdatedUsers(ctx.state.lastUserSyncAt)
            }
            if (remoteUsersUpdated.isNotEmpty()) {
                userDao.upsertUsers(remoteUsersUpdated)
                pulls += remoteUsersUpdated.size
            }
        } catch (e: Exception) {
            Timber.e(e, "Users sync failed")
            errors.add("Users: ${e.message}")
        }
        return SyncResult(pulls, 0, 0, errors)
    }

    private suspend fun syncProducts(ctx: SyncContext): SyncResult {
        var pulls = 0
        var pushes = 0
        val errors = mutableListOf<String>()
        var processedWithMissingUsers = false
        
        try {
            var remoteProductsUpdated: List<ProductEntity> = emptyList()
            try {
                withTimeout(PRODUCTS_SYNC_TIMEOUT_MS) {
                    // Pull
                    remoteProductsUpdated = withRetry {
                        firestoreService.fetchUpdatedProducts(ctx.state.lastProductSyncAt)
                    }
                    // Ensure all referenced users exist before inserting products
                    val sellerIds = remoteProductsUpdated.map { it.sellerId }.distinct()
                    if (sellerIds.isNotEmpty()) {
                        val existingSellers = userDao.getUsersByIds(sellerIds).map { it.userId }.toSet()
                        val missingSellerIds = sellerIds.filter { it !in existingSellers }
                        if (missingSellerIds.isNotEmpty()) {
                            val fetchedUsers = withRetry { firestoreService.fetchUsersByIds(missingSellerIds) }
                            if (fetchedUsers.isNotEmpty()) userDao.upsertUsers(fetchedUsers)
                            Timber.d("Auto-fetched ${fetchedUsers.size} users for product sync")
                            val fetchedIds = fetchedUsers.map { it.userId }.toSet()
                            val stillMissing = missingSellerIds.filter { it !in fetchedIds }
                            if (stillMissing.isNotEmpty()) {
                                Timber.w("Skipping ${stillMissing.size} products due to unresolved sellers: ${stillMissing.joinToString()}")
                            }
                            // Filter out products with unresolved sellers
                            val filtered = remoteProductsUpdated.filter { it.sellerId !in stillMissing }
                            if (filtered.isNotEmpty()) {
                                val resolved = mutableListOf<ProductEntity>()
                                for (remote in filtered) {
                                    val local = productDao.findById(remote.productId)
                                    resolved.add(if (local != null) mergeProductRemoteLocal(remote, local) else remote)
                                }
                                productDao.insertProducts(resolved)
                                // Emit conflict events
                                filtered.forEach { remote ->
                                    val local = productDao.findById(remote.productId)
                                    if (local != null) {
                                        val conflictFields = mutableListOf<String>()
                                        val rU = remote.updatedAt
                                        val lU = local.updatedAt
                                        if (rU > lU) {
                                            if (remote.name != local.name) conflictFields.add("name")
                                            if (remote.description != local.description) conflictFields.add("description")
                                            if (remote.price != local.price) conflictFields.add("price")
                                            if (remote.quantity != local.quantity) conflictFields.add("quantity")
                                            if (remote.unit != local.unit) conflictFields.add("unit")
                                            if (remote.location != local.location) conflictFields.add("location")
                                            if (remote.stage != local.stage) conflictFields.add("stage")
                                            if (remote.lifecycleStatus != local.lifecycleStatus) conflictFields.add("lifecycleStatus")
                                        }
                                        if (conflictFields.isNotEmpty()) {
                                            conflictEvents.emit(ConflictEvent(OutboxEntity.TYPE_LISTING, remote.productId, conflictFields, ctx.now))
                                        }
                                    }
                                }
                                pulls += resolved.size
                            }
                            processedWithMissingUsers = true
                        }
                    }
                    if (!processedWithMissingUsers && remoteProductsUpdated.isNotEmpty()) {
                        // Merge with local on pull
                        val resolved = mutableListOf<ProductEntity>()
                        for (remote in remoteProductsUpdated) {
                            val local = productDao.findById(remote.productId)
                            resolved.add(if (local != null) mergeProductRemoteLocal(remote, local) else remote)
                        }
                        productDao.insertProducts(resolved)
                        // Emit conflict events
                        remoteProductsUpdated.forEach { remote ->
                            val local = productDao.findById(remote.productId)
                            if (local != null) {
                                val conflictFields = mutableListOf<String>()
                                val rU = remote.updatedAt
                                val lU = local.updatedAt
                                if (rU > lU) {
                                    if (remote.name != local.name) conflictFields.add("name")
                                    if (remote.description != local.description) conflictFields.add("description")
                                    if (remote.price != local.price) conflictFields.add("price")
                                    if (remote.quantity != local.quantity) conflictFields.add("quantity")
                                    if (remote.unit != local.unit) conflictFields.add("unit")
                                    if (remote.location != local.location) conflictFields.add("location")
                                    if (remote.stage != local.stage) conflictFields.add("stage")
                                    if (remote.lifecycleStatus != local.lifecycleStatus) conflictFields.add("lifecycleStatus")
                                }
                                if (conflictFields.isNotEmpty()) {
                                    conflictEvents.emit(ConflictEvent(OutboxEntity.TYPE_LISTING, remote.productId, conflictFields, ctx.now))
                                }
                            }
                        }
                        pulls += resolved.size
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Timber.e(e, "Products sync timed out after ${PRODUCTS_SYNC_TIMEOUT_MS}ms")
                ctx.timedOutDomains.add("products")
                errors.add("Products: Timeout")
            }

            // Push (dirty only)
            val localDirty = productDao.getUpdatedSince(ctx.state.lastProductSyncAt, limit = 50).filter { it.dirty }
            if (localDirty.isNotEmpty()) {
                val payload = localDirty.map { local ->
                    val remote = remoteProductsUpdated.firstOrNull { it.productId == local.productId }
                    protectLineageOnPush(local, remote)
                }
                withRetry { firestoreService.pushProducts(payload) }
                val cleaned = payload.map { it.copy(dirty = false, updatedAt = ctx.now) }
                productDao.insertProducts(cleaned)
                pushes += cleaned.size
            }

            // Purge soft-deleted
            productDao.purgeDeleted()

            // Evict stale
            val fourteenDaysMillis = 14L * 24 * 60 * 60 * 1000
            val threshold = ctx.now - fourteenDaysMillis
            productDao.purgeStaleMarketplace(threshold)

        } catch (e: Exception) {
            Timber.e(e, "Products sync failed")
            errors.add("Products: ${e.message}")
        }
        return SyncResult(pulls, pushes, 0, errors)
    }

    private suspend fun syncOrders(ctx: SyncContext): SyncResult {
        var pulls = 0
        var pushes = 0
        val errors = mutableListOf<String>()
        try {
            if (ctx.userId != null) {
                val remoteOrdersUpdated: List<OrderEntity> = withRetry {
                    firestoreService.fetchUpdatedOrders(ctx.userId, ctx.state.lastOrderSyncAt)
                }
                if (remoteOrdersUpdated.isNotEmpty()) {
                    val localDirtyIds = orderDao.getDirty().map { it.orderId }.toHashSet()
                    val toUpsert = remoteOrdersUpdated.filter { it.orderId !in localDirtyIds }
                    toUpsert.forEach { orderDao.insertOrUpdate(it) }
                    pulls += toUpsert.size
                }
            }

            val localDirty = orderDao.getUpdatedSince(ctx.state.lastOrderSyncAt, limit = 50).filter { it.dirty }
            if (localDirty.isNotEmpty()) {
                withRetry { firestoreService.pushOrders(localDirty) }
                val cleaned = localDirty.map { it.copy(dirty = false, updatedAt = ctx.now) }
                cleaned.forEach { orderDao.insertOrUpdate(it) }
                pushes += cleaned.size
            }

            orderDao.purgeDeleted()
        } catch (e: Exception) {
            Timber.e(e, "Orders sync failed")
            errors.add("Orders: ${e.message}")
        }
        return SyncResult(pulls, pushes, 0, errors)
    }

    private suspend fun syncTransfers(ctx: SyncContext): SyncResult {
        var pulls = 0
        var pushes = 0
        val errors = mutableListOf<String>()
        try {
            val remoteTransfersUpdated: List<TransferEntity> = withRetry {
                firestoreService.fetchUpdatedTransfers(ctx.userId, ctx.state.lastTransferSyncAt)
            }
            if (remoteTransfersUpdated.isNotEmpty()) {
                remoteTransfersUpdated.forEach { transferDao.upsert(it) }
                pulls += remoteTransfersUpdated.size
            }

            val localDirty = transferDao.getUpdatedSince(ctx.state.lastTransferSyncAt, limit = 50).filter { it.dirty }
            if (localDirty.isNotEmpty()) {
                val toPush = localDirty.filter { local ->
                    val remote = remoteTransfersUpdated.firstOrNull { it.transferId == local.transferId }
                    !isTerminalTransferStatus(remote?.status)
                }
                withRetry { firestoreService.pushTransfers(toPush) }
                
                val cleaned = localDirty.map { local ->
                    val remote = remoteTransfersUpdated.firstOrNull { it.transferId == local.transferId }
                    if (remote != null && isTerminalTransferStatus(remote.status)) {
                        if (remote.status != local.status) {
                            conflictEvents.emit(ConflictEvent(OutboxEntity.TYPE_TRANSFER, local.transferId, listOf("status"), ctx.now))
                        }
                        remote.copy(dirty = false, updatedAt = ctx.now)
                    } else {
                        local.copy(dirty = false, updatedAt = ctx.now)
                    }
                }
                cleaned.forEach { transferDao.upsert(it) }
                pushes += cleaned.size
            }
            transferDao.purgeDeleted()
        } catch (e: Exception) {
            Timber.e(e, "Transfers sync failed")
            errors.add("Transfers: ${e.message}")
        }
        return SyncResult(pulls, pushes, 0, errors)
    }

    private suspend fun syncChat(ctx: SyncContext): SyncResult {
        var pulls = 0
        val errors = mutableListOf<String>()
        try {
            val remoteMessagesUpdated: List<ChatMessageEntity> = withRetry {
                firestoreService.fetchUpdatedChats(ctx.userId, ctx.state.lastChatSyncAt)
            }
            if (remoteMessagesUpdated.isNotEmpty()) {
                remoteMessagesUpdated.forEach { remote ->
                    val local = chatMessageDao.getById(remote.messageId)
                    if (local != null && remote.updatedAt > local.updatedAt) {
                        val fields = mutableListOf<String>()
                        if (remote.body != local.body) fields.add("body")
                        if (remote.mediaUrl != local.mediaUrl) fields.add("mediaUrl")
                        if (remote.deliveredAt != local.deliveredAt) fields.add("deliveredAt")
                        if (remote.readAt != local.readAt) fields.add("readAt")
                        if (fields.isNotEmpty()) {
                            conflictEvents.emit(ConflictEvent(OutboxEntity.TYPE_CHAT_MESSAGE, remote.messageId, fields, ctx.now))
                        }
                    }
                }
                chatMessageDao.upsertAll(remoteMessagesUpdated)
                pulls += remoteMessagesUpdated.size
            }
            chatMessageDao.purgeDeleted()
        } catch (e: Exception) {
            Timber.e(e, "Chat sync failed")
            errors.add("Chat: ${e.message}")
        }
        return SyncResult(pulls, 0, 0, errors)
    }
    
    private suspend fun processOutbox(ctx: SyncContext): SyncResult {
        var processedCount = 0
        val errors = mutableListOf<String>()
        
        try {
            val pendingEntries = outboxDao.getPendingPrioritized(limit = 50)
            val totalPending = pendingEntries.size
            if (totalPending == 0) return SyncResult(0, 0, 0, emptyList())
            
            val groupedEntries = pendingEntries.groupBy { it.entityType }
            
            for ((entityType, entries) in groupedEntries) {
                syncProgressFlow.emit(SyncProgress(totalPending, processedCount, entityType, errors.toList()))
                
                val batchIds = entries.map { it.outboxId }.filter { it.isNotBlank() }
                if (batchIds.isNotEmpty()) {
                    outboxDao.updateStatusBatch(batchIds, "IN_PROGRESS", ctx.now)
                }
                
                when (entityType) {
                    OutboxEntity.TYPE_DAILY_LOG -> {
                        for (e in entries) {
                            outboxDao.updateStatus(e.outboxId, "COMPLETED", ctx.now)
                            processedCount++
                        }
                        Timber.d("Skipped ${entries.size} daily log outbox entries (local-only)")
                    }
                    OutboxEntity.TYPE_TRANSFER -> {
                         for (e in entries) {
                            val transfer = gson.fromJson(e.payloadJson, TransferEntity::class.java)
                            try {
                                withRetry { firestoreService.pushTransfers(listOf(transfer)) }
                                transferDao.upsert(transfer.copy(dirty = false, updatedAt = ctx.now))
                                outboxDao.updateStatus(e.outboxId, "COMPLETED", ctx.now)
                            } catch (t: Throwable) {
                                outboxDao.incrementRetry(e.outboxId, ctx.now)
                                outboxDao.updateStatus(e.outboxId, if ((e.retryCount + 1) < e.maxRetries) "PENDING" else "FAILED", ctx.now)
                                errors.add("Transfer push failed for ${transfer.transferId}: ${t.message}")
                            }
                            processedCount++
                        }
                    }
                    OutboxEntity.TYPE_PRODUCT_TRACKING -> {
                        for (e in entries) {
                            val tracking = gson.fromJson(e.payloadJson, ProductTrackingEntity::class.java)
                            try {
                                withRetry { firestoreService.pushTrackings(listOf(tracking)) }
                                productTrackingDao.clearDirtyCustom(tracking.trackingId, ctx.now)
                                outboxDao.updateStatus(e.outboxId, "COMPLETED", ctx.now)
                            } catch (t: Throwable) {
                                outboxDao.incrementRetry(e.outboxId, ctx.now)
                                outboxDao.updateStatus(e.outboxId, if ((e.retryCount + 1) < e.maxRetries) "PENDING" else "FAILED", ctx.now)
                                errors.add("Tracking push failed: ${t.message}")
                            }
                            processedCount++
                        }
                    }
                    OutboxEntity.TYPE_LISTING -> {
                        for (e in entries) {
                            val product = gson.fromJson(e.payloadJson, ProductEntity::class.java)
                            try {
                                withRetry { firestoreService.pushProducts(listOf(product)) }
                                productDao.insertProducts(listOf(product.copy(dirty = false, updatedAt = ctx.now)))
                                outboxDao.updateStatus(e.outboxId, "COMPLETED", ctx.now)
                            } catch (t: Throwable) {
                                if (t.message?.contains("seller") == true) {
                                    if (product.sellerId.isNotBlank()) {
                                        val missingUser = withRetry { firestoreService.fetchUsersByIds(listOf(product.sellerId)).firstOrNull() }
                                        if (missingUser != null) {
                                            userDao.upsertUser(missingUser)
                                            Timber.d("Fetched and inserted missing seller ${product.sellerId}")
                                            productDao.insertProducts(listOf(product.copy(dirty = false, updatedAt = ctx.now)))
                                            outboxDao.updateStatus(e.outboxId, "COMPLETED", ctx.now)
                                        } else {
                                            Timber.e("Failed to fetch seller ${product.sellerId}")
                                            outboxDao.incrementRetry(e.outboxId, ctx.now)
                                            outboxDao.updateStatus(e.outboxId, "FAILED", ctx.now)
                                            errors.add("Listing push failed for ${product.productId}: missing seller")
                                        }
                                    } else {
                                        throw t
                                    }
                                } else {
                                    outboxDao.incrementRetry(e.outboxId, ctx.now)
                                    outboxDao.updateStatus(e.outboxId, if ((e.retryCount + 1) < e.maxRetries) "PENDING" else "FAILED", ctx.now)
                                    errors.add("Listing push failed: ${t.message}")
                                }
                            }
                            processedCount++
                        }
                    }
                    OutboxEntity.TYPE_CHAT_MESSAGE -> {
                         for (e in entries) {
                            val message = gson.fromJson(e.payloadJson, ChatMessageEntity::class.java)
                            try {
                                withRetry { firestoreService.pushChats(listOf(message)) }
                                chatMessageDao.clearDirty(listOf(message.messageId), ctx.now)
                                outboxDao.updateStatus(e.outboxId, "COMPLETED", ctx.now)
                            } catch (t: Throwable) {
                                outboxDao.incrementRetry(e.outboxId, ctx.now)
                                outboxDao.updateStatus(e.outboxId, if ((e.retryCount + 1) < e.maxRetries) "PENDING" else "FAILED", ctx.now)
                                errors.add("Chat message push failed: ${t.message}")
                            }
                            processedCount++
                        }
                    }
                    OutboxEntity.TYPE_GROUP_MESSAGE -> {
                         for (e in entries) {
                            outboxDao.updateStatus(e.outboxId, "FAILED", ctx.now)
                            errors.add("No remote API for group messages; marked FAILED")
                            processedCount++
                        }
                    }
                    OutboxEntity.TYPE_TASK -> {
                         for (e in entries) {
                            val task = gson.fromJson(e.payloadJson, TaskEntity::class.java)
                            try {
                                val userRole = ctx.role ?: UserType.GENERAL
                                withRetry { firestoreService.pushTasks(ctx.userId ?: "", userRole, listOf(task)) }
                                taskDao.clearDirty(listOf(task.taskId), ctx.now)
                                outboxDao.updateStatus(e.outboxId, "COMPLETED", ctx.now)
                            } catch (t: Throwable) {
                                outboxDao.incrementRetry(e.outboxId, ctx.now)
                                outboxDao.updateStatus(e.outboxId, if ((e.retryCount + 1) < e.maxRetries) "PENDING" else "FAILED", ctx.now)
                                errors.add("Task push failed: ${t.message}")
                            }
                            processedCount++
                        }
                    }
                    OutboxEntity.TYPE_ORDER -> {
                        for (e in entries) {
                            val order = gson.fromJson(e.payloadJson, OrderEntity::class.java)
                            try {
                                withRetry { firestoreService.pushOrders(listOf(order)) }
                                orderDao.insertOrUpdate(order.copy(dirty = false, updatedAt = ctx.now))
                                outboxDao.updateStatus(e.outboxId, "COMPLETED", ctx.now)
                            } catch (t: Throwable) {
                                outboxDao.incrementRetry(e.outboxId, ctx.now)
                                outboxDao.updateStatus(e.outboxId, if ((e.retryCount + 1) < e.maxRetries) "PENDING" else "FAILED", ctx.now)
                                errors.add("Order push failed: ${t.message}")
                            }
                            processedCount++
                        }
                    }
                    OutboxEntity.TYPE_POST -> {
                        for (e in entries) {
                            outboxDao.updateStatus(e.outboxId, "FAILED", ctx.now)
                            errors.add("No remote API for posts; marked FAILED")
                            processedCount++
                        }
                    }
                    else -> {
                        Timber.w("Unknown outbox entity type: $entityType")
                        errors.add("Unknown type: $entityType")
                    }
                }
                syncProgressFlow.emit(SyncProgress(totalPending, processedCount, entityType, errors.toList()))
            }
            // Purge completed
            val sevenDaysAgo = ctx.now - (7L * 24 * 60 * 60 * 1000)
            outboxDao.purgeCompleted(sevenDaysAgo)
            
        } catch (e: Exception) {
            Timber.e(e, "Outbox processing failed")
            errors.add("Outbox: ${e.message}")
        }
        return SyncResult(0, 0, processedCount, errors)
    }

    private suspend fun syncFarmMonitoring(ctx: SyncContext): SyncResult {
        var pulls = 0
        var pushes = 0
        val errors = mutableListOf<String>()
        
        val canSyncMonitoring = (ctx.role == UserType.FARMER || ctx.role == UserType.ENTHUSIAST) && ctx.userId != null
        val isMigrating = ctx.userId?.let { roleMigrationDao.hasActiveMigration(it) } ?: false
        
        if (canSyncMonitoring) {
            if (isMigrating) {
                Timber.d("Skipping monitoring sync due to active migration for ${ctx.userId}")
            } else {
                val farmerId = ctx.userId!!
                val role = ctx.role!!
                
                // Breeding Pairs
                try {
                    val remotePairs = withRetry { firestoreService.fetchUpdatedBreedingPairs(farmerId, role, ctx.state.lastBreedingSyncAt) }
                    if (remotePairs.isNotEmpty()) {
                        val localDirtyIds = breedingPairDao.getDirty().map { it.pairId }.toHashSet()
                        val toUpsert = remotePairs.filter { it.pairId !in localDirtyIds }
                        toUpsert.forEach { breedingPairDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = breedingPairDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushBreedingPairs(farmerId, role, localDirty) }
                        breedingPairDao.clearDirty(localDirty.map { it.pairId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Breeding Pairs sync failed")
                    errors.add("Breeding Pairs: ${e.message}")
                }
                
                // Farm Alerts
                try {
                    val remoteAlerts = withRetry { firestoreService.fetchUpdatedAlerts(farmerId, role, ctx.state.lastAlertSyncAt) }
                    if (remoteAlerts.isNotEmpty()) {
                        val localDirtyIds = farmAlertDao.getDirty().map { it.alertId }.toHashSet()
                        val toUpsert = remoteAlerts.filter { it.alertId !in localDirtyIds }
                        toUpsert.forEach { farmAlertDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = farmAlertDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushAlerts(farmerId, role, localDirty) }
                        farmAlertDao.clearDirty(localDirty.map { it.alertId }, ctx.now)
                        pushes += localDirty.size
                    }
                    farmAlertDao.deleteExpired(ctx.now)
                } catch (e: Exception) {
                    Timber.e(e, "Farm Alerts sync failed")
                    errors.add("Alerts: ${e.message}")
                }
                
                // Dashboard Snapshots
                try {
                    val remoteSnapshots = withRetry { firestoreService.fetchUpdatedDashboardSnapshots(farmerId, role, ctx.state.lastDashboardSyncAt) }
                    if (remoteSnapshots.isNotEmpty()) {
                        val localDirtyIds = farmerDashboardSnapshotDao.getDirty().map { it.snapshotId }.toHashSet()
                        val toUpsert = remoteSnapshots.filter { it.snapshotId !in localDirtyIds }
                        toUpsert.forEach { farmerDashboardSnapshotDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = farmerDashboardSnapshotDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushDashboardSnapshots(farmerId, role, localDirty) }
                        farmerDashboardSnapshotDao.clearDirty(localDirty.map { it.snapshotId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Dashboard Snapshots sync failed")
                    errors.add("Dashboard: ${e.message}")
                }
                
                // Vaccination Records
                try {
                    val remoteVaccinations = withRetry { firestoreService.fetchUpdatedVaccinations(farmerId, role, ctx.state.lastVaccinationSyncAt) }
                    if (remoteVaccinations.isNotEmpty()) {
                        val localDirtyIds = vaccinationRecordDao.getDirty().map { it.vaccinationId }.toHashSet()
                        val toUpsert = remoteVaccinations.filter { it.vaccinationId !in localDirtyIds }
                        toUpsert.forEach { vaccinationRecordDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = vaccinationRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushVaccinations(farmerId, role, localDirty) }
                        vaccinationRecordDao.clearDirty(localDirty.map { it.vaccinationId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Vaccination sync failed")
                    errors.add("Vaccinations: ${e.message}")
                }
                
                // Growth Records
                try {
                    val remoteGrowth = withRetry { firestoreService.fetchUpdatedGrowthRecords(farmerId, role, ctx.state.lastGrowthSyncAt) }
                    if (remoteGrowth.isNotEmpty()) {
                        val localDirtyIds = growthRecordDao.getDirty().map { it.recordId }.toHashSet()
                        val toUpsert = remoteGrowth.filter { it.recordId !in localDirtyIds }
                        toUpsert.forEach { growthRecordDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = growthRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushGrowthRecords(farmerId, role, localDirty) }
                        growthRecordDao.clearDirty(localDirty.map { it.recordId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Growth sync failed")
                    errors.add("Growth: ${e.message}")
                }
                
                // Quarantine Records
                try {
                    val remoteQuarantine = withRetry { firestoreService.fetchUpdatedQuarantineRecords(farmerId, role, ctx.state.lastQuarantineSyncAt) }
                    if (remoteQuarantine.isNotEmpty()) {
                        val localDirtyIds = quarantineRecordDao.getDirty().map { it.quarantineId }.toHashSet()
                        val toUpsert = remoteQuarantine.filter { it.quarantineId !in localDirtyIds }
                        toUpsert.forEach { quarantineRecordDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = quarantineRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushQuarantineRecords(farmerId, role, localDirty) }
                        quarantineRecordDao.clearDirty(localDirty.map { it.quarantineId }, ctx.now) // Removed unused now arg if not needed or verify usage
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Quarantine sync failed")
                    errors.add("Quarantine: ${e.message}")
                }
                
                // Mortality Records
                try {
                    val remoteMortality = withRetry { firestoreService.fetchUpdatedMortalityRecords(farmerId, role, ctx.state.lastMortalitySyncAt) }
                    if (remoteMortality.isNotEmpty()) {
                        val localDirtyIds = mortalityRecordDao.getDirty().map { it.deathId }.toHashSet()
                        val toUpsert = remoteMortality.filter { it.deathId !in localDirtyIds }
                        toUpsert.forEach { mortalityRecordDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = mortalityRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushMortalityRecords(farmerId, role, localDirty) }
                        mortalityRecordDao.clearDirty(localDirty.map { it.deathId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Mortality sync failed")
                    errors.add("Mortality: ${e.message}")
                }
                
                // Hatching Batches
                try {
                    val remoteHatching = withRetry { firestoreService.fetchUpdatedHatchingBatches(farmerId, role, ctx.state.lastHatchingSyncAt) }
                    if (remoteHatching.isNotEmpty()) {
                        val localDirtyIds = hatchingBatchDao.getDirty().map { it.batchId }.toHashSet()
                        val toUpsert = remoteHatching.filter { it.batchId !in localDirtyIds }
                        toUpsert.forEach { hatchingBatchDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = hatchingBatchDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushHatchingBatches(farmerId, role, localDirty) }
                        hatchingBatchDao.clearDirty(localDirty.map { it.batchId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Hatching Batches sync failed")
                    errors.add("Hatching: ${e.message}")
                }
                
                // Hatching Logs
                try {
                    val remoteLogs = withRetry { firestoreService.fetchUpdatedHatchingLogs(farmerId, role, ctx.state.lastHatchingLogSyncAt) }
                    if (remoteLogs.isNotEmpty()) {
                        val localDirtyIds = hatchingLogDao.getDirty().map { it.logId }.toHashSet()
                        val toUpsert = remoteLogs.filter { it.logId !in localDirtyIds }
                        toUpsert.forEach { hatchingLogDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = hatchingLogDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushHatchingLogs(farmerId, role, localDirty) }
                        hatchingLogDao.clearDirty(localDirty.map { it.logId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Hatching Logs sync failed")
                    errors.add("Hatching Logs: ${e.message}")
                }
                
                // Batch Summaries
                try {
                    val remoteSummaries = withRetry { firestoreService.fetchUpdatedBatchSummaries(ctx.userId, ctx.state.lastBatchSummarySyncAt) }
                    if (remoteSummaries.isNotEmpty()) {
                        val localDirtyIds = batchSummaryDao.getDirty().map { it.batchId }.toHashSet()
                        val toUpsert = remoteSummaries.filter { it.batchId !in localDirtyIds }
                        toUpsert.forEach { batchSummaryDao.upsert(it) }
                        pulls += toUpsert.size
                    }
                    val localDirty = batchSummaryDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushBatchSummaries(ctx.userId, localDirty) }
                        batchSummaryDao.clearDirty(localDirty.map { it.batchId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "BatchSummaries sync failed")
                    errors.add("BatchSummaries: ${e.message}")
                }
                
                // Tasks
                try {
                    val remoteTasks = withRetry { firestoreService.fetchUpdatedTasks(ctx.userId, role, ctx.state.lastTaskSyncAt) }
                    if (remoteTasks.isNotEmpty()) {
                         val localDirtyIds = taskDao.getDirty().map { it.taskId }.toHashSet()
                         val toUpsert = remoteTasks.filter { it.taskId !in localDirtyIds }
                         toUpsert.forEach { taskDao.upsert(it) }
                         pulls += toUpsert.size
                    }
                    val localDirty = taskDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushTasks(ctx.userId, role, localDirty) }
                        taskDao.clearDirty(localDirty.map { it.taskId }, ctx.now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Tasks sync failed")
                    errors.add("Tasks: ${e.message}")
                }
            }
        }
        return SyncResult(pulls, pushes, 0, errors)
    }

    fun isSyncInProgress(): Boolean = syncInProgress
    
    /**
     * Check if there are pending items to sync.
     */
    suspend fun hasPendingSync(): Boolean = withContext(Dispatchers.IO) {
        outboxDao.getPendingPrioritized(limit = 1).isNotEmpty()
    }
    
    /**
     * Detect conflicts between local and remote data.
     * Returns list of conflicts that need user resolution.
     * 
     * Note: This is a simplified implementation that looks at dirty local records.
     * Real conflict detection happens during syncAll() and emits ConflictEvents.
     */
    suspend fun detectConflicts(): List<SyncConflict> = withContext(Dispatchers.IO) {
        val conflicts = mutableListOf<SyncConflict>()
        
        try {
            // Check for dirty products that may have conflicts
            val dirtyProducts = productDao.getUpdatedSince(0L, limit = 100).filter { it.dirty }
            for (local in dirtyProducts) {
                // Simple heuristic: if dirty and last sync was recent, it's potentially conflicting
                val sinceLastSync = System.currentTimeMillis() - local.updatedAt
                if (sinceLastSync < 60_000) { // Less than 1 minute since update
                    conflicts.add(SyncConflict(
                        entityType = "Product",
                        entityId = local.productId,
                        conflictingFields = listOf("pending_sync"),
                        localTimestamp = local.updatedAt,
                        remoteTimestamp = 0L
                    ))
                }
            }
            
            // Check for dirty transfers
            val dirtyTransfers = transferDao.getUpdatedSince(0L, limit = 100).filter { it.dirty }
            for (local in dirtyTransfers) {
                val sinceLastSync = System.currentTimeMillis() - local.updatedAt
                if (sinceLastSync < 60_000) {
                    conflicts.add(SyncConflict(
                        entityType = "Transfer",
                        entityId = local.transferId,
                        conflictingFields = listOf("pending_sync"),
                        localTimestamp = local.updatedAt,
                        remoteTimestamp = 0L
                    ))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to detect conflicts")
        }
        
        conflicts
    }
    
    /**
     * Resolve a conflict by keeping either local or remote version.
     */
    suspend fun resolveConflict(
        entityId: String,
        entityType: String,
        useLocal: Boolean
    ): Unit = withContext(Dispatchers.IO) {
        try {
            when (entityType) {
                "Product" -> {
                    if (useLocal) {
                        // Push local to remote
                        val local = productDao.findById(entityId)
                        if (local != null) {
                            firestoreService.pushProducts(listOf(local))
                            productDao.insertProducts(listOf(local.copy(dirty = false, updatedAt = System.currentTimeMillis())))
                        }
                    } else {
                        // Re-fetch from remote by triggering a sync
                        // (Full implementation would fetch individual product but SyncRemote doesn't have that method)
                        val local = productDao.findById(entityId)
                        if (local != null) {
                            // Mark as not dirty to accept whatever comes from next sync
                            productDao.insertProducts(listOf(local.copy(dirty = false)))
                        }
                    }
                }
                "Transfer" -> {
                    if (useLocal) {
                        val local = transferDao.getById(entityId)
                        if (local != null) {
                            firestoreService.pushTransfers(listOf(local))
                            transferDao.upsert(local.copy(dirty = false, updatedAt = System.currentTimeMillis()))
                        }
                    } else {
                        val local = transferDao.getById(entityId)
                        if (local != null) {
                            transferDao.upsert(local.copy(dirty = false))
                        }
                    }
                }
                // Add more entity types as needed
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to resolve conflict for $entityType:$entityId")
            throw e
        }
    }
}
