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
    private val sessionManager: SessionManager
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
            var pushes = 0
            var pulls = 0
            val syncErrors = mutableListOf<String>()

            val userId = firebaseAuth.currentUser?.uid
            val role = sessionManager.sessionRole().first()

            // ==============================
            // Product Tracking (incremental)
            // ==============================
            try {
                // Pull newer remote changes since lastTrackingSyncAt
                val remoteTrackingUpdated: List<ProductTrackingEntity> = withRetry {
                    firestoreService.fetchUpdatedTrackings(userId, state.lastTrackingSyncAt)
                }
                if (remoteTrackingUpdated.isNotEmpty()) {
                    productTrackingDao.upsertAll(remoteTrackingUpdated)
                    pulls += remoteTrackingUpdated.size
                }

                // Push local dirty rows
                val localDirty =
                    productTrackingDao.getUpdatedSince(state.lastTrackingSyncAt, limit = 50)
                        .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // Push to remote, last-write-wins on server; then clear dirty
                    withRetry { firestoreService.pushTrackings(localDirty) }
                    val cleaned = localDirty.map { it.copy(dirty = false, updatedAt = now) }
                    productTrackingDao.upsertAll(cleaned)
                    pushes += cleaned.size
                }
            } catch (e: Exception) {
                Timber.e(e, "Product Tracking sync failed")
                syncErrors.add("Tracking: ${e.message}")
            }

            // ============================
            // Enthusiast Entity Sync (UID)
            // ============================
            if (role == UserType.ENTHUSIAST && userId != null) {
                try {
                    // Mating Logs (use enthusiast-specific window)
                    run {
                        val remote = withRetry {
                            firestoreService.fetchUpdatedMatingLogs(
                                userId,
                                state.lastEnthusiastBreedingSyncAt
                            )
                        }
                        if (remote.isNotEmpty()) {
                            remote.forEach { matingLogDao.upsert(it) }
                            pulls += remote.size
                        }
                        val localDirty = matingLogDao.getDirty()
                        if (localDirty.isNotEmpty()) {
                            withRetry { firestoreService.pushMatingLogs(userId, localDirty) }
                            matingLogDao.clearDirty(localDirty.map { it.logId }, now)
                            pushes += localDirty.size
                        }
                    }

                    // Egg Collections (use enthusiast-specific window)
                    run {
                        val remote = withRetry {
                            firestoreService.fetchUpdatedEggCollections(
                                userId,
                                state.lastEnthusiastBreedingSyncAt
                            )
                        }
                        if (remote.isNotEmpty()) {
                            remote.forEach { eggCollectionDao.upsert(it) }
                            pulls += remote.size
                        }
                        val localDirty = eggCollectionDao.getDirty()
                        if (localDirty.isNotEmpty()) {
                            withRetry { firestoreService.pushEggCollections(userId, localDirty) }
                            eggCollectionDao.clearDirty(localDirty.map { it.collectionId }, now)
                            pushes += localDirty.size
                        }
                    }

                    // Enthusiast Dashboard Snapshots (use enthusiast-specific window)
                    run {
                        val remote = withRetry {
                            firestoreService.fetchUpdatedEnthusiastSnapshots(
                                userId,
                                state.lastEnthusiastDashboardSyncAt
                            )
                        }
                        if (remote.isNotEmpty()) {
                            remote.forEach { enthusiastDashboardSnapshotDao.upsert(it) }
                            pulls += remote.size
                        }
                        val localDirty = enthusiastDashboardSnapshotDao.getDirty()
                        if (localDirty.isNotEmpty()) {
                            withRetry {
                                firestoreService.pushEnthusiastSnapshots(
                                    userId,
                                    localDirty
                                )
                            }
                            enthusiastDashboardSnapshotDao.clearDirty(
                                localDirty.map { it.snapshotId },
                                now
                            )
                            pushes += localDirty.size
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Enthusiast sync failed")
                    syncErrors.add("Enthusiast Data: ${e.message}")
                }
            }

            // =====
            // Users
            // =====
            try {
                // Pull updated users to ensure sellers exist before product sync
                val remoteUsersUpdated: List<UserEntity> = withRetry {
                    firestoreService.fetchUpdatedUsers(state.lastUserSyncAt)
                }
                if (remoteUsersUpdated.isNotEmpty()) {
                    userDao.upsertUsers(remoteUsersUpdated)
                    pulls += remoteUsersUpdated.size
                }
                // Users are not pushed as dirty in this context; handled elsewhere if needed
            } catch (e: Exception) {
                Timber.e(e, "Users sync failed")
                syncErrors.add("Users: ${e.message}")
            }

            // ==========
            // Products
            // ==========
            try {
                var remoteProductsUpdated: List<ProductEntity> = emptyList()
                var processedWithMissingUsers = false
                try {
                    withTimeout(PRODUCTS_SYNC_TIMEOUT_MS) {
                        // Pull
                        remoteProductsUpdated = withRetry {
                            firestoreService.fetchUpdatedProducts(state.lastProductSyncAt)
                        }
                        // Ensure all referenced users exist before inserting products
                        val sellerIds = remoteProductsUpdated.map { it.sellerId }.distinct()
                        if (sellerIds.isNotEmpty()) {
                            val existingSellers =
                                userDao.getUsersByIds(sellerIds).map { it.userId }.toSet()
                            val missingSellerIds = sellerIds.filter { it !in existingSellers }
                            if (missingSellerIds.isNotEmpty()) {
                                val fetchedUsers =
                                    withRetry { firestoreService.fetchUsersByIds(missingSellerIds) }
                                if (fetchedUsers.isNotEmpty()) userDao.upsertUsers(fetchedUsers)
                                Timber.d("Auto-fetched ${fetchedUsers.size} users for product sync")
                                val fetchedIds = fetchedUsers.map { it.userId }.toSet()
                                val stillMissing = missingSellerIds.filter { it !in fetchedIds }
                                if (stillMissing.isNotEmpty()) {
                                    Timber.w("Skipping ${stillMissing.size} products due to unresolved sellers: ${stillMissing.joinToString()}")
                                }
                                // Filter out products with unresolved sellers
                                val filtered =
                                    remoteProductsUpdated.filter { it.sellerId !in stillMissing }
                                if (filtered.isNotEmpty()) {
                                    val resolved = mutableListOf<ProductEntity>()
                                    for (remote in filtered) {
                                        val local = productDao.findById(remote.productId)
                                        resolved.add(
                                            if (local != null) mergeProductRemoteLocal(
                                                remote,
                                                local
                                            ) else remote
                                        )
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
                                                if (remote.description != local.description) conflictFields.add(
                                                    "description"
                                                )
                                                if (remote.price != local.price) conflictFields.add(
                                                    "price"
                                                )
                                                if (remote.quantity != local.quantity) conflictFields.add(
                                                    "quantity"
                                                )
                                                if (remote.unit != local.unit) conflictFields.add("unit")
                                                if (remote.location != local.location) conflictFields.add(
                                                    "location"
                                                )
                                                if (remote.stage != local.stage) conflictFields.add(
                                                    "stage"
                                                )
                                                if (remote.lifecycleStatus != local.lifecycleStatus) conflictFields.add(
                                                    "lifecycleStatus"
                                                )
                                            }
                                            if (conflictFields.isNotEmpty()) {
                                                conflictEvents.emit(
                                                    ConflictEvent(
                                                        entityType = OutboxEntity.TYPE_LISTING,
                                                        entityId = remote.productId,
                                                        conflictFields = conflictFields,
                                                        mergedAt = now
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    pulls += resolved.size
                                }
                                // Mark that we processed products with missing users
                                processedWithMissingUsers = true
                            }
                        }
                        if (!processedWithMissingUsers && remoteProductsUpdated.isNotEmpty()) {
                            // Merge with local on pull (explicit loop)
                            val resolved = mutableListOf<ProductEntity>()
                            for (remote in remoteProductsUpdated) {
                                val local = productDao.findById(remote.productId)
                                resolved.add(
                                    if (local != null) mergeProductRemoteLocal(
                                        remote,
                                        local
                                    ) else remote
                                )
                            }
                            productDao.insertProducts(resolved)
                            // Emit conflict events for fields that changed due to remote taking precedence
                            remoteProductsUpdated.forEach { remote ->
                                val local = productDao.findById(remote.productId)
                                if (local != null) {
                                    val conflictFields = mutableListOf<String>()
                                    val rU = remote.updatedAt
                                    val lU = local.updatedAt
                                    if (rU > lU) {
                                        if (remote.name != local.name) conflictFields.add("name")
                                        if (remote.description != local.description) conflictFields.add(
                                            "description"
                                        )
                                        if (remote.price != local.price) conflictFields.add("price")
                                        if (remote.quantity != local.quantity) conflictFields.add("quantity")
                                        if (remote.unit != local.unit) conflictFields.add("unit")
                                        if (remote.location != local.location) conflictFields.add("location")
                                        if (remote.stage != local.stage) conflictFields.add("stage")
                                        if (remote.lifecycleStatus != local.lifecycleStatus) conflictFields.add(
                                            "lifecycleStatus"
                                        )
                                    }
                                    if (conflictFields.isNotEmpty()) {
                                        conflictEvents.emit(
                                            ConflictEvent(
                                                entityType = OutboxEntity.TYPE_LISTING,
                                                entityId = remote.productId,
                                                conflictFields = conflictFields,
                                                mergedAt = now
                                            )
                                        )
                                    }
                                }
                            }
                            pulls += resolved.size
                        }
                    }
                } catch (e: TimeoutCancellationException) {
                    Timber.e(e, "Products sync timed out after ${PRODUCTS_SYNC_TIMEOUT_MS}ms")
                    timedOutDomains.add("products")
                    syncErrors.add("Products: Timeout")
                }

                // Push (dirty only)
                val localDirty = productDao.getUpdatedSince(state.lastProductSyncAt, limit = 50)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // Protect lineage-related fields from being overwritten if remote has them newer/present
                    val payload = localDirty.map { local ->
                        val remote =
                            remoteProductsUpdated.firstOrNull { it.productId == local.productId }
                        protectLineageOnPush(local, remote)
                    }
                    withRetry { firestoreService.pushProducts(payload) }
                    // Also apply the protected payload locally when clearing dirty to avoid clobbering lineage fields
                    val cleaned = payload.map { it.copy(dirty = false, updatedAt = now) }
                    productDao.insertProducts(cleaned)
                    pushes += cleaned.size
                }

                // Purge soft-deleted
                productDao.purgeDeleted()

                // Evict stale marketplace cache older than 14 days (unless referenced by tracking)
                val fourteenDaysMillis = 14L * 24 * 60 * 60 * 1000
                val threshold = now - fourteenDaysMillis
                productDao.purgeStaleMarketplace(threshold)
            } catch (e: Exception) {
                Timber.e(e, "Products sync failed")
                syncErrors.add("Products: ${e.message}")
            }

            // =======
            // Orders
            // =======
            try {
                // Pull
                if (userId != null) {
                    val remoteOrdersUpdated: List<OrderEntity> = withRetry {
                        firestoreService.fetchUpdatedOrders(userId, state.lastOrderSyncAt)
                    }
                    if (remoteOrdersUpdated.isNotEmpty()) {
                        // Upsert each; OrderDao has insertOrUpdate
                        remoteOrdersUpdated.forEach { orderDao.insertOrUpdate(it) }
                        pulls += remoteOrdersUpdated.size
                    }
                }

                // Push dirties
                val localDirty = orderDao.getUpdatedSince(state.lastOrderSyncAt, limit = 50)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    withRetry { firestoreService.pushOrders(localDirty) }
                    val cleaned = localDirty.map { it.copy(dirty = false, updatedAt = now) }
                    cleaned.forEach { orderDao.insertOrUpdate(it) }
                    pushes += cleaned.size
                }

                // Purge soft-deleted
                orderDao.purgeDeleted()
            } catch (e: Exception) {
                Timber.e(e, "Orders sync failed")
                syncErrors.add("Orders: ${e.message}")
            }

            // =========
            // Transfers
            // =========
            try {
                // Pull
                val remoteTransfersUpdated: List<TransferEntity> = withRetry {
                    firestoreService.fetchUpdatedTransfers(userId, state.lastTransferSyncAt)
                }
                if (remoteTransfersUpdated.isNotEmpty()) {
                    remoteTransfersUpdated.forEach { transferDao.upsert(it) }
                    pulls += remoteTransfersUpdated.size
                }

                // Push dirties
                val localDirty = transferDao.getUpdatedSince(state.lastTransferSyncAt, limit = 50)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // If remote has terminal state, prefer remote over local
                    val toPush = localDirty.filter { local ->
                        val remote =
                            remoteTransfersUpdated.firstOrNull { it.transferId == local.transferId }
                        val remoteTerminal = isTerminalTransferStatus(remote?.status)
                        !remoteTerminal
                    }
                    withRetry { firestoreService.pushTransfers(toPush) }
                    // When cleaning, keep remote terminal states by merging
                    val cleaned = localDirty.map { local ->
                        val remote =
                            remoteTransfersUpdated.firstOrNull { it.transferId == local.transferId }
                        if (remote != null && isTerminalTransferStatus(remote.status)) {
                            // Emit conflict on status override
                            if (remote.status != local.status) {
                                conflictEvents.emit(
                                    ConflictEvent(
                                        entityType = OutboxEntity.TYPE_TRANSFER,
                                        entityId = local.transferId,
                                        conflictFields = listOf("status"),
                                        mergedAt = now
                                    )
                                )
                            }
                            remote.copy(dirty = false, updatedAt = now)
                        } else {
                            local.copy(dirty = false, updatedAt = now)
                        }
                    }
                    cleaned.forEach { transferDao.upsert(it) }
                    pushes += cleaned.size
                }

                // Purge soft-deleted
                transferDao.purgeDeleted()
            } catch (e: Exception) {
                Timber.e(e, "Transfers sync failed")
                syncErrors.add("Transfers: ${e.message}")
            }

            // =====
            // Chat
            // =====
            try {
                // Pull only (no dirty flag by design here); could push by updatedAt window if needed
                val remoteMessagesUpdated: List<ChatMessageEntity> = withRetry {
                    firestoreService.fetchUpdatedChats(userId, state.lastChatSyncAt)
                }
                if (remoteMessagesUpdated.isNotEmpty()) {
                    // Emit conflicts where remote overwrote different local fields by recency
                    remoteMessagesUpdated.forEach { remote ->
                        val local = chatMessageDao.getById(remote.messageId)
                        if (local != null && remote.updatedAt > local.updatedAt) {
                            val fields = mutableListOf<String>()
                            if (remote.body != local.body) fields.add("body")
                            if (remote.mediaUrl != local.mediaUrl) fields.add("mediaUrl")
                            if (remote.deliveredAt != local.deliveredAt) fields.add("deliveredAt")
                            if (remote.readAt != local.readAt) fields.add("readAt")
                            if (fields.isNotEmpty()) {
                                conflictEvents.emit(
                                    ConflictEvent(
                                        entityType = OutboxEntity.TYPE_CHAT_MESSAGE,
                                        entityId = remote.messageId,
                                        conflictFields = fields,
                                        mergedAt = now
                                    )
                                )
                            }
                        }
                    }
                    chatMessageDao.upsertAll(remoteMessagesUpdated)
                    pulls += remoteMessagesUpdated.size
                }

                // Purge soft-deleted
                chatMessageDao.purgeDeleted()
            } catch (e: Exception) {
                Timber.e(e, "Chat sync failed")
                syncErrors.add("Chat: ${e.message}")
            }

            // ================
            // Outbox Processing
            // ================
            var outboxProcessed = 0
            run {
                val pendingEntries = outboxDao.getPendingPrioritized(limit = 50)
                val totalPending = pendingEntries.size
                var processed = 0
                // Use global syncErrors list

                // Group by entity type for batch processing
                val groupedEntries = pendingEntries.groupBy { it.entityType }

                for ((entityType, entries) in groupedEntries) {
                    syncProgressFlow.emit(
                        SyncProgress(
                            totalPending,
                            processed,
                            entityType,
                            syncErrors.toList()
                        )
                    )

                    // Mark batch IN_PROGRESS to prevent duplicates in concurrent runs
                    val batchIds = entries.map { it.outboxId }.filter { it.isNotBlank() }
                    if (batchIds.isNotEmpty()) {
                        outboxDao.updateStatusBatch(batchIds, "IN_PROGRESS", now)
                    }

                    // Map entityId -> OutboxEntity to avoid empty lookups
                    val outboxByEntityId = entries.associateBy { it.entityId }

                    when (entityType) {
                        OutboxEntity.TYPE_DAILY_LOG -> {
                            val userId = firebaseAuth.currentUser?.uid ?: ""
                            for (e in entries) {
                                val log = gson.fromJson(e.payloadJson, DailyLogEntity::class.java)
                                try {
                                    withRetry {
                                        firestoreService.pushDailyLogs(
                                            userId,
                                            role ?: UserType.GENERAL,
                                            listOf(log)
                                        )
                                    }
                                    dailyLogDao.clearDirty(listOf(log.logId), now)
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(
                                        e.outboxId,
                                        if (willRetry) "PENDING" else "FAILED",
                                        now
                                    )
                                    syncErrors.add("DailyLog push failed: ${t.message}")
                                }
                                processed++
                            }
                        }

                        OutboxEntity.TYPE_TRANSFER -> {
                            for (e in entries) {
                                val transfer =
                                    gson.fromJson(e.payloadJson, TransferEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushTransfers(listOf(transfer)) }
                                    transferDao.upsert(
                                        transfer.copy(
                                            dirty = false,
                                            updatedAt = now
                                        )
                                    )
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(
                                        e.outboxId,
                                        if (willRetry) "PENDING" else "FAILED",
                                        now
                                    )
                                    syncErrors.add("Transfer push failed for ${transfer.transferId}: ${t.message}")
                                }
                                processed++
                            }
                        }

                        OutboxEntity.TYPE_PRODUCT_TRACKING -> {
                            val userId = firebaseAuth.currentUser?.uid ?: ""
                            for (e in entries) {
                                val tracking =
                                    gson.fromJson(e.payloadJson, ProductTrackingEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushTrackings(listOf(tracking)) }
                                    productTrackingDao.clearDirtyCustom(tracking.trackingId, now)
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(
                                        e.outboxId,
                                        if (willRetry) "PENDING" else "FAILED",
                                        now
                                    )
                                    syncErrors.add("Tracking push failed: ${t.message}")
                                }
                                processed++
                            }
                        }

                        OutboxEntity.TYPE_LISTING -> {
                            for (e in entries) {
                                val product = gson.fromJson(e.payloadJson, ProductEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushProducts(listOf(product)) }
                                    productDao.insertProducts(
                                        listOf(
                                            product.copy(
                                                dirty = false,
                                                updatedAt = now
                                            )
                                        )
                                    )
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    // Check if it's the "missing seller" error
                                    if (t.message?.contains("seller") == true) {
                                        if (product.sellerId.isNotBlank()) {
                                            // Attempt to fetch missing seller
                                            val missingUser = withRetry {
                                                firestoreService.fetchUsersByIds(
                                                    listOf(product.sellerId)
                                                ).firstOrNull()
                                            }
                                            if (missingUser != null) {
                                                userDao.upsertUser(missingUser)
                                                Timber.d("Fetched and inserted missing seller ${product.sellerId} for product ${product.productId}")
                                                // Retry insert
                                                productDao.insertProducts(listOf(product.copy(dirty = false, updatedAt = now)))
                                                outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                            } else {
                                                Timber.e("Failed to fetch seller ${product.sellerId} for product ${product.productId}")
                                                outboxDao.incrementRetry(e.outboxId, now)
                                                outboxDao.updateStatus(e.outboxId, "FAILED", now)
                                                syncErrors.add("Listing push failed for ${product.productId}: missing seller ${product.sellerId}")
                                            }
                                        } else {
                                            throw t
                                        }
                                    } else {
                                        outboxDao.incrementRetry(e.outboxId, now)
                                        val willRetry = (e.retryCount + 1) < e.maxRetries
                                        outboxDao.updateStatus(
                                            e.outboxId,
                                            if (willRetry) "PENDING" else "FAILED",
                                            now
                                        )
                                        syncErrors.add("Listing push failed for ${product.productId}: ${t.message}")
                                    }
                                }
                                processed++
                            }
                        }

                        OutboxEntity.TYPE_CHAT_MESSAGE -> {
                            for (e in entries) {
                                val message =
                                    gson.fromJson(e.payloadJson, ChatMessageEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushChats(listOf(message)) }
                                    chatMessageDao.clearDirty(listOf(message.messageId), now)
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(
                                        e.outboxId,
                                        if (willRetry) "PENDING" else "FAILED",
                                        now
                                    )
                                    syncErrors.add("Chat message push failed for ${message.messageId}: ${t.message}")
                                }
                                processed++
                            }
                        }

                        OutboxEntity.TYPE_GROUP_MESSAGE -> {
                            for (e in entries) {
                                // No remote API defined for group messages; mark as FAILED to surface
                                outboxDao.updateStatus(e.outboxId, "FAILED", now)
                                syncErrors.add("No remote API for group messages; marked FAILED for ${e.entityId}")
                                processed++
                            }
                        }

                        OutboxEntity.TYPE_TASK -> {
                            val userId = firebaseAuth.currentUser?.uid ?: ""
                            for (e in entries) {
                                val task = gson.fromJson(e.payloadJson, TaskEntity::class.java)
                                try {
                                    val userRole = role ?: UserType.GENERAL
                                    withRetry { firestoreService.pushTasks(userId, userRole, listOf(task)) }
                                    taskDao.clearDirty(listOf(task.taskId), now)
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(
                                        e.outboxId,
                                        if (willRetry) "PENDING" else "FAILED",
                                        now
                                    )
                                    syncErrors.add("Task push failed for ${task.taskId}: ${t.message}")
                                }
                                processed++
                            }
                        }

                        OutboxEntity.TYPE_ORDER -> {
                            for (e in entries) {
                                val order = gson.fromJson(e.payloadJson, OrderEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushOrders(listOf(order)) }
                                    orderDao.insertOrUpdate(
                                        order.copy(
                                            dirty = false,
                                            updatedAt = now
                                        )
                                    )
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(
                                        e.outboxId,
                                        if (willRetry) "PENDING" else "FAILED",
                                        now
                                    )
                                    syncErrors.add("Order push failed for ${order.orderId}: ${t.message}")
                                }
                                processed++
                            }
                        }

                        OutboxEntity.TYPE_POST -> {
                            for (e in entries) {
                                // No remote API defined in SyncRemote; mark as FAILED to surface
                                outboxDao.updateStatus(e.outboxId, "FAILED", now)
                                syncErrors.add("No remote API for posts; marked FAILED for ${e.entityId}")
                                processed++
                            }
                        }

                        else -> {
                            Timber.w("Unknown outbox entity type: $entityType")
                            syncErrors.add("Unknown type: $entityType")
                        }
                    }

                    // Emit after each batch completion
                    syncProgressFlow.emit(
                        SyncProgress(
                            totalPending,
                            processed,
                            entityType,
                            syncErrors.toList()
                        )
                    )
                }

                outboxProcessed = processed

                // Purge completed entries older than 7 days
                val sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000)
                outboxDao.purgeCompleted(sevenDaysAgo)
            }

            // =============================
            // Farm Monitoring Entity Sync
            // =============================
            val canSyncMonitoring = (role == UserType.FARMER || role == UserType.ENTHUSIAST) && userId != null
            val isMigrating = userId?.let { roleMigrationDao.hasActiveMigration(it) } ?: false
            
            if (canSyncMonitoring) {
                if (isMigrating) {
                    Timber.d("Skipping monitoring sync due to active migration for $userId")
                } else {
                    val farmerId = userId!!
                    // Breeding Pairs
                try {
                    val remotePairs = withRetry {
                        firestoreService.fetchUpdatedBreedingPairs(
                            farmerId,
                            role,
                            state.lastBreedingSyncAt
                        )
                    }
                    if (remotePairs.isNotEmpty()) {
                        remotePairs.forEach { breedingPairDao.upsert(it) }
                        pulls += remotePairs.size
                    }

                    val localDirty = breedingPairDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushBreedingPairs(farmerId, role, localDirty) }
                        breedingPairDao.clearDirty(localDirty.map { it.pairId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Breeding Pairs sync failed")
                    syncErrors.add("Breeding Pairs: ${e.message}")
                }

                // Farm Alerts
                try {
                    val remoteAlerts = withRetry {
                        firestoreService.fetchUpdatedAlerts(farmerId, role, state.lastAlertSyncAt)
                    }
                    if (remoteAlerts.isNotEmpty()) {
                        remoteAlerts.forEach { farmAlertDao.upsert(it) }
                        pulls += remoteAlerts.size
                    }

                    val localDirty = farmAlertDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushAlerts(farmerId, role, localDirty) }
                        farmAlertDao.clearDirty(localDirty.map { it.alertId }, now)
                        pushes += localDirty.size
                    }

                    // Clean up expired alerts
                    farmAlertDao.deleteExpired(now)
                } catch (e: Exception) {
                    Timber.e(e, "Farm Alerts sync failed")
                    syncErrors.add("Alerts: ${e.message}")
                }

                // Dashboard Snapshots
                try {
                    val remoteSnapshots = withRetry {
                        firestoreService.fetchUpdatedDashboardSnapshots(
                            farmerId,
                            role,
                            state.lastDashboardSyncAt
                        )
                    }
                    if (remoteSnapshots.isNotEmpty()) {
                        remoteSnapshots.forEach { farmerDashboardSnapshotDao.upsert(it) }
                        pulls += remoteSnapshots.size
                    }

                    val localDirty = farmerDashboardSnapshotDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushDashboardSnapshots(farmerId, role, localDirty) }
                        farmerDashboardSnapshotDao.clearDirty(localDirty.map { it.snapshotId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Dashboard Snapshots sync failed")
                    syncErrors.add("Dashboard: ${e.message}")
                }

                // Vaccination Records
                try {
                    val remoteVaccinations = withRetry {
                        firestoreService.fetchUpdatedVaccinations(
                            farmerId,
                            role,
                            state.lastVaccinationSyncAt
                        )
                    }
                    if (remoteVaccinations.isNotEmpty()) {
                        remoteVaccinations.forEach { vaccinationRecordDao.upsert(it) }
                        pulls += remoteVaccinations.size
                    }

                    val localDirty = vaccinationRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushVaccinations(farmerId, role, localDirty) }
                        vaccinationRecordDao.clearDirty(localDirty.map { it.vaccinationId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Vaccination sync failed")
                    syncErrors.add("Vaccinations: ${e.message}")
                }

                // Growth Records
                try {
                    val remoteGrowth = withRetry {
                        firestoreService.fetchUpdatedGrowthRecords(farmerId, role, state.lastGrowthSyncAt)
                    }
                    if (remoteGrowth.isNotEmpty()) {
                        remoteGrowth.forEach { growthRecordDao.upsert(it) }
                        pulls += remoteGrowth.size
                    }

                    val localDirty = growthRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushGrowthRecords(farmerId, role, localDirty) }
                        growthRecordDao.clearDirty(localDirty.map { it.recordId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Growth sync failed")
                    syncErrors.add("Growth: ${e.message}")
                }

                // Quarantine Records
                try {
                    val remoteQuarantine = withRetry {
                        firestoreService.fetchUpdatedQuarantineRecords(
                            farmerId,
                            role,
                            state.lastQuarantineSyncAt
                        )
                    }
                    if (remoteQuarantine.isNotEmpty()) {
                        remoteQuarantine.forEach { quarantineRecordDao.upsert(it) }
                        pulls += remoteQuarantine.size
                    }

                    val localDirty = quarantineRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushQuarantineRecords(farmerId, role, localDirty) }
                        quarantineRecordDao.clearDirty(localDirty.map { it.quarantineId })
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Quarantine sync failed")
                    syncErrors.add("Quarantine: ${e.message}")
                }

                // Mortality Records
                try {
                    val remoteMortality = withRetry {
                        firestoreService.fetchUpdatedMortalityRecords(
                            farmerId,
                            role,
                            state.lastMortalitySyncAt
                        )
                    }
                    if (remoteMortality.isNotEmpty()) {
                        remoteMortality.forEach { mortalityRecordDao.upsert(it) }
                        pulls += remoteMortality.size
                    }

                    val localDirty = mortalityRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushMortalityRecords(farmerId, role, localDirty) }
                        mortalityRecordDao.clearDirty(localDirty.map { it.deathId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Mortality sync failed")
                    syncErrors.add("Mortality: ${e.message}")
                }

                // Hatching Batches
                try {
                    val remoteHatching = withRetry {
                        firestoreService.fetchUpdatedHatchingBatches(
                            farmerId,
                            role,
                            state.lastHatchingSyncAt
                        )
                    }
                    if (remoteHatching.isNotEmpty()) {
                        remoteHatching.forEach { hatchingBatchDao.upsert(it) }
                        pulls += remoteHatching.size
                    }

                    val localDirty = hatchingBatchDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushHatchingBatches(farmerId, role, localDirty) }
                        hatchingBatchDao.clearDirty(localDirty.map { it.batchId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Hatching Batches sync failed")
                    syncErrors.add("Hatching: ${e.message}")
                }

                // Hatching Logs
                try {
                    val remoteLogs = withRetry {
                        firestoreService.fetchUpdatedHatchingLogs(
                            farmerId,
                            role,
                            state.lastHatchingLogSyncAt
                        )
                    }
                    if (remoteLogs.isNotEmpty()) {
                        remoteLogs.forEach { hatchingLogDao.upsert(it) }
                        pulls += remoteLogs.size
                    }

                    val localDirty = hatchingLogDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushHatchingLogs(farmerId, role, localDirty) }
                        hatchingLogDao.clearDirty(localDirty.map { it.logId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Hatching Logs sync failed")
                    syncErrors.add("Hatching Logs: ${e.message}")
                }

                // ===================
                // Daily Logs (Sprint 1)
                // ===================
                try {
                    val remoteLogs: List<DailyLogEntity> = withRetry {
                        firestoreService.fetchUpdatedDailyLogs(userId, role, state.lastDailyLogSyncAt)
                    }
                    if (remoteLogs.isNotEmpty()) {
                        for (e in remoteLogs) {
                            dailyLogDao.upsert(e)
                        }
                        pulls += remoteLogs.size
                    }
                    val localDirty: List<DailyLogEntity> = dailyLogDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushDailyLogs(userId, role, localDirty) }
                        dailyLogDao.clearDirty(localDirty.map { it.logId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Daily Logs sync failed")
                    syncErrors.add("Daily Logs: ${e.message}")
                }

                // ============
                // Tasks (Sprint 1)
                // ============
                try {
                    val remoteTasks: List<TaskEntity> = withRetry {
                        firestoreService.fetchUpdatedTasks(userId, role, state.lastTaskSyncAt)
                    }
                    if (remoteTasks.isNotEmpty()) {
                        for (t in remoteTasks) {
                            taskDao.upsert(t)
                        }
                        pulls += remoteTasks.size
                    }
                    val localDirty: List<TaskEntity> = taskDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushTasks(userId, role, localDirty) }
                        taskDao.clearDirty(localDirty.map { it.taskId }, now)
                        pushes += localDirty.size
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Tasks sync failed")
                    syncErrors.add("Tasks: ${e.message}")
                }
            } // End of else (not migrating)
        } // End of if (canSyncMonitoring)

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
                     syncErrors.toList()
                 )
            )

            val stats =
                SyncStats(pushes, pulls, outboxProcessed = 0, timedOutDomains = timedOutDomains)
            if (timedOutDomains.isNotEmpty()) {
                Timber.w("Sync completed with timeouts in: ${timedOutDomains.joinToString()}")
            }
            return Resource.Success(stats)
        } catch (e: Exception) {
            Timber.e(e, "syncAll failed")
            return Resource.Error(e.message ?: "Sync failed")
        }
    }
}
