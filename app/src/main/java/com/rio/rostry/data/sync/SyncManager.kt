package com.rio.rostry.data.sync

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.ConnectivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.rio.rostry.data.repository.TraceabilityRepository

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
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val traceabilityRepository: TraceabilityRepository
) {
    data class SyncStats(
        val pushed: Int = 0,
        val pulled: Int = 0,
        val outboxProcessed: Int = 0
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

    private suspend fun mergeProductRemoteLocal(remote: ProductEntity, local: ProductEntity?): ProductEntity {
        if (local == null) return remote
        // Last-write-wins by updatedAt for general fields
        fun pickString(remoteVal: String?, localVal: String?, rUpdated: Long, lUpdated: Long): String? =
            if ((remoteVal != localVal) && (rUpdated > lUpdated)) remoteVal else localVal

        fun pickInt(remoteVal: Int?, localVal: Int?, rUpdated: Long, lUpdated: Long): Int? =
            if ((remoteVal != localVal) && (rUpdated > lUpdated)) remoteVal else localVal

        val rU = remote.updatedAt
        val lU = local.updatedAt

        // Lineage: if differs, verify before accepting remote. Skip if remote IDs are null/blank.
        val canVerify = !remote.parentMaleId.isNullOrBlank() && !remote.parentFemaleId.isNullOrBlank()
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

        val acceptParentMale = if (remote.parentMaleId != local.parentMaleId && rU > lU && verified) {
            remote.parentMaleId
        } else local.parentMaleId

        val acceptParentFemale = if (remote.parentFemaleId != local.parentFemaleId && rU > lU && verified) {
            remote.parentFemaleId
        } else local.parentFemaleId

        // Prefer stage based on lastStageTransitionAt recency (not updatedAt)
        val stageByTransition = when {
            remote.lastStageTransitionAt != null && (local.lastStageTransitionAt == null || remote.lastStageTransitionAt > local.lastStageTransitionAt) -> remote.stage
            else -> local.stage
        }

        // Prefer lifecycleStatus based on breederEligibleAt recency when applicable; otherwise fallback to LWW
        val lifecycleByEligibility = when {
            (remote.breederEligibleAt ?: 0L) > (local.breederEligibleAt ?: 0L) -> remote.lifecycleStatus
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
                (remote.breederEligibleAt ?: 0L) > (local.breederEligibleAt ?: 0L) -> remote.breederEligibleAt
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
     * Replace the TODO network calls with your remote API/Firestore logic.
     */
    suspend fun syncAll(now: Long = System.currentTimeMillis()): Resource<SyncStats> = withContext(Dispatchers.IO) {
        try {
            val state = syncStateDao.get() ?: SyncStateEntity()
            var pushes = 0
            var pulls = 0

            // ==============================
            // Product Tracking (incremental)
            // ==============================
            run {
                // Pull newer remote changes since lastTrackingSyncAt
                val remoteTrackingUpdated: List<ProductTrackingEntity> = withRetry {
                    firestoreService.fetchUpdatedTrackings(state.lastTrackingSyncAt)
                }
                if (remoteTrackingUpdated.isNotEmpty()) {
                    productTrackingDao.upsertAll(remoteTrackingUpdated)
                    pulls += remoteTrackingUpdated.size
                }

                // Push local dirty rows
                val localDirty = productTrackingDao.getUpdatedSince(state.lastTrackingSyncAt, limit = 1000)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // Push to remote, last-write-wins on server; then clear dirty
                    withRetry { firestoreService.pushTrackings(localDirty) }
                    val cleaned = localDirty.map { it.copy(dirty = false, updatedAt = now) }
                    productTrackingDao.upsertAll(cleaned)
                    pushes += cleaned.size
                }
            }

            // ============================
            // Enthusiast Entity Sync (UID)
            // ============================
            run {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    // Mating Logs (use enthusiast-specific window)
                    run {
                        val remote = withRetry {
                            firestoreService.fetchUpdatedMatingLogs(userId, state.lastEnthusiastBreedingSyncAt)
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
                            firestoreService.fetchUpdatedEggCollections(userId, state.lastEnthusiastBreedingSyncAt)
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
                            firestoreService.fetchUpdatedEnthusiastSnapshots(userId, state.lastEnthusiastDashboardSyncAt)
                        }
                        if (remote.isNotEmpty()) {
                            remote.forEach { enthusiastDashboardSnapshotDao.upsert(it) }
                            pulls += remote.size
                        }
                        val localDirty = enthusiastDashboardSnapshotDao.getDirty()
                        if (localDirty.isNotEmpty()) {
                            withRetry { firestoreService.pushEnthusiastSnapshots(userId, localDirty) }
                            enthusiastDashboardSnapshotDao.clearDirty(localDirty.map { it.snapshotId }, now)
                            pushes += localDirty.size
                        }
                    }
                }
            }

            // ==========
            // Products
            // ==========
            run {
                // Pull
                val remoteProductsUpdated: List<ProductEntity> = withRetry {
                    firestoreService.fetchUpdatedProducts(state.lastProductSyncAt)
                }
                if (remoteProductsUpdated.isNotEmpty()) {
                    // Merge with local on pull
                    val resolved = remoteProductsUpdated.map { remote ->
                        val local = productDao.findById(remote.productId)
                        if (local != null) mergeProductRemoteLocal(remote, local) else remote
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
                                if (remote.description != local.description) conflictFields.add("description")
                                if (remote.price != local.price) conflictFields.add("price")
                                if (remote.quantity != local.quantity) conflictFields.add("quantity")
                                if (remote.unit != local.unit) conflictFields.add("unit")
                                if (remote.location != local.location) conflictFields.add("location")
                                if (remote.stage != local.stage) conflictFields.add("stage")
                                if (remote.lifecycleStatus != local.lifecycleStatus) conflictFields.add("lifecycleStatus")
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

                // Push (dirty only)
                val localDirty = productDao.getUpdatedSince(state.lastProductSyncAt, limit = 1000)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // Protect lineage-related fields from being overwritten if remote has them newer/present
                    val payload = localDirty.map { local ->
                        val remote = remoteProductsUpdated.firstOrNull { it.productId == local.productId }
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
            }

            // =======
            // Orders
            // =======
            run {
                // Pull
                val remoteOrdersUpdated: List<OrderEntity> = withRetry {
                    firestoreService.fetchUpdatedOrders(state.lastOrderSyncAt)
                }
                if (remoteOrdersUpdated.isNotEmpty()) {
                    // Upsert each; OrderDao has insertOrUpdate
                    remoteOrdersUpdated.forEach { orderDao.insertOrUpdate(it) }
                    pulls += remoteOrdersUpdated.size
                }

                // Push dirties
                val localDirty = orderDao.getUpdatedSince(state.lastOrderSyncAt, limit = 1000)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    withRetry { firestoreService.pushOrders(localDirty) }
                    val cleaned = localDirty.map { it.copy(dirty = false, updatedAt = now) }
                    cleaned.forEach { orderDao.insertOrUpdate(it) }
                    pushes += cleaned.size
                }

                // Purge soft-deleted
                orderDao.purgeDeleted()
            }

            // ==========
            // Transfers
            // ==========
            run {
                // Pull
                val remoteTransfersUpdated: List<TransferEntity> = withRetry {
                    firestoreService.fetchUpdatedTransfers(state.lastTransferSyncAt)
                }
                if (remoteTransfersUpdated.isNotEmpty()) {
                    remoteTransfersUpdated.forEach { transferDao.upsert(it) }
                    pulls += remoteTransfersUpdated.size
                }

                // Push dirties
                val localDirty = transferDao.getUpdatedSince(state.lastTransferSyncAt, limit = 1000)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // If remote has terminal state, prefer remote over local
                    val toPush = localDirty.filter { local ->
                        val remote = remoteTransfersUpdated.firstOrNull { it.transferId == local.transferId }
                        val remoteTerminal = isTerminalTransferStatus(remote?.status)
                        !remoteTerminal
                    }
                    withRetry { firestoreService.pushTransfers(toPush) }
                    // When cleaning, keep remote terminal states by merging
                    val cleaned = localDirty.map { local ->
                        val remote = remoteTransfersUpdated.firstOrNull { it.transferId == local.transferId }
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
            }

            // =====
            // Chat
            // =====
            run {
                // Pull only (no dirty flag by design here); could push by updatedAt window if needed
                val remoteMessagesUpdated: List<ChatMessageEntity> = withRetry {
                    firestoreService.fetchUpdatedChats(state.lastChatSyncAt)
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
            }

            // ================
            // Outbox Processing
            // ================
            var outboxProcessed = 0
            run {
                val pendingEntries = outboxDao.getPendingPrioritized(limit = 50)
                val totalPending = pendingEntries.size
                var processed = 0
                val errors = mutableListOf<String>()

                // Group by entity type for batch processing
                val groupedEntries = pendingEntries.groupBy { it.entityType }

                for ((entityType, entries) in groupedEntries) {
                    syncProgressFlow.emit(SyncProgress(totalPending, processed, entityType, errors.toList()))

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
                                    withRetry { firestoreService.pushDailyLogs(userId, listOf(log)) }
                                    dailyLogDao.clearDirty(listOf(log.logId), now)
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(e.outboxId, if (willRetry) "PENDING" else "FAILED", now)
                                    errors.add("DailyLog push failed for ${log.logId}: ${t.message}")
                                }
                                processed++
                            }
                        }
                        OutboxEntity.TYPE_TRANSFER -> {
                            for (e in entries) {
                                val transfer = gson.fromJson(e.payloadJson, TransferEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushTransfers(listOf(transfer)) }
                                    transferDao.upsert(transfer.copy(dirty = false, updatedAt = now))
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(e.outboxId, if (willRetry) "PENDING" else "FAILED", now)
                                    errors.add("Transfer push failed for ${transfer.transferId}: ${t.message}")
                                }
                                processed++
                            }
                        }
                        OutboxEntity.TYPE_LISTING -> {
                            for (e in entries) {
                                val product = gson.fromJson(e.payloadJson, ProductEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushProducts(listOf(product)) }
                                    val cleaned = product.copy(dirty = false, updatedAt = now)
                                    productDao.insertProducts(listOf(cleaned))
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(e.outboxId, if (willRetry) "PENDING" else "FAILED", now)
                                    errors.add("Listing push failed for ${product.productId}: ${t.message}")
                                }
                                processed++
                            }
                        }
                        OutboxEntity.TYPE_CHAT_MESSAGE -> {
                            for (e in entries) {
                                val message = gson.fromJson(e.payloadJson, ChatMessageEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushChats(listOf(message)) }
                                    chatMessageDao.clearDirty(listOf(message.messageId), now)
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(e.outboxId, if (willRetry) "PENDING" else "FAILED", now)
                                    errors.add("Chat message push failed for ${message.messageId}: ${t.message}")
                                }
                                processed++
                            }
                        }
                        OutboxEntity.TYPE_GROUP_MESSAGE -> {
                            for (e in entries) {
                                // No remote API defined for group messages; mark as FAILED to surface
                                outboxDao.updateStatus(e.outboxId, "FAILED", now)
                                errors.add("No remote API for group messages; marked FAILED for ${e.entityId}")
                                processed++
                            }
                        }
                        OutboxEntity.TYPE_TASK -> {
                            val userId = firebaseAuth.currentUser?.uid ?: ""
                            for (e in entries) {
                                val task = gson.fromJson(e.payloadJson, TaskEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushTasks(userId, listOf(task)) }
                                    taskDao.clearDirty(listOf(task.taskId), now)
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(e.outboxId, if (willRetry) "PENDING" else "FAILED", now)
                                    errors.add("Task push failed for ${task.taskId}: ${t.message}")
                                }
                                processed++
                            }
                        }
                        OutboxEntity.TYPE_ORDER -> {
                            for (e in entries) {
                                val order = gson.fromJson(e.payloadJson, OrderEntity::class.java)
                                try {
                                    withRetry { firestoreService.pushOrders(listOf(order)) }
                                    orderDao.insertOrUpdate(order.copy(dirty = false, updatedAt = now))
                                    outboxDao.updateStatus(e.outboxId, "COMPLETED", now)
                                } catch (t: Throwable) {
                                    outboxDao.incrementRetry(e.outboxId, now)
                                    val willRetry = (e.retryCount + 1) < e.maxRetries
                                    outboxDao.updateStatus(e.outboxId, if (willRetry) "PENDING" else "FAILED", now)
                                    errors.add("Order push failed for ${order.orderId}: ${t.message}")
                                }
                                processed++
                            }
                        }
                        OutboxEntity.TYPE_POST -> {
                            for (e in entries) {
                                // No remote API defined in SyncRemote; mark as FAILED to surface
                                outboxDao.updateStatus(e.outboxId, "FAILED", now)
                                errors.add("No remote API for posts; marked FAILED for ${e.entityId}")
                                processed++
                            }
                        }
                        else -> {
                            Timber.w("Unknown outbox entity type: $entityType")
                            errors.add("Unknown type: $entityType")
                        }
                    }

                    // Emit after each batch completion
                    syncProgressFlow.emit(SyncProgress(totalPending, processed, entityType, errors.toList()))
                }

                outboxProcessed = processed

                // Purge completed entries older than 7 days
                val sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000)
                outboxDao.purgeCompleted(sevenDaysAgo)
            }

            // =============================
            // Farm Monitoring Entity Sync
            // =============================
            val farmerId = firebaseAuth.currentUser?.uid
            if (farmerId != null) {
                // Breeding Pairs
                run {
                    val remotePairs = withRetry {
                        firestoreService.fetchUpdatedBreedingPairs(farmerId, state.lastBreedingSyncAt)
                    }
                    if (remotePairs.isNotEmpty()) {
                        remotePairs.forEach { breedingPairDao.upsert(it) }
                        pulls += remotePairs.size
                    }
                    
                    val localDirty = breedingPairDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushBreedingPairs(farmerId, localDirty) }
                        breedingPairDao.clearDirty(localDirty.map { it.pairId }, now)
                        pushes += localDirty.size
                    }
                }
                
                // Farm Alerts
                run {
                    val remoteAlerts = withRetry {
                        firestoreService.fetchUpdatedAlerts(farmerId, state.lastAlertSyncAt)
                    }
                    if (remoteAlerts.isNotEmpty()) {
                        remoteAlerts.forEach { farmAlertDao.upsert(it) }
                        pulls += remoteAlerts.size
                    }
                    
                    val localDirty = farmAlertDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushAlerts(farmerId, localDirty) }
                        farmAlertDao.clearDirty(localDirty.map { it.alertId }, now)
                        pushes += localDirty.size
                    }
                    
                    // Clean up expired alerts
                    farmAlertDao.deleteExpired(now)
                }
                
                // Dashboard Snapshots
                run {
                    val remoteSnapshots = withRetry {
                        firestoreService.fetchUpdatedDashboardSnapshots(farmerId, state.lastDashboardSyncAt)
                    }
                    if (remoteSnapshots.isNotEmpty()) {
                        remoteSnapshots.forEach { farmerDashboardSnapshotDao.upsert(it) }
                        pulls += remoteSnapshots.size
                    }
                    
                    val localDirty = farmerDashboardSnapshotDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushDashboardSnapshots(farmerId, localDirty) }
                        farmerDashboardSnapshotDao.clearDirty(localDirty.map { it.snapshotId }, now)
                        pushes += localDirty.size
                    }
                }
                
                // Vaccination Records
                run {
                    val remoteVaccinations = withRetry {
                        firestoreService.fetchUpdatedVaccinations(farmerId, state.lastVaccinationSyncAt)
                    }
                    if (remoteVaccinations.isNotEmpty()) {
                        remoteVaccinations.forEach { vaccinationRecordDao.upsert(it) }
                        pulls += remoteVaccinations.size
                    }
                    
                    val localDirty = vaccinationRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushVaccinations(farmerId, localDirty) }
                        vaccinationRecordDao.clearDirty(localDirty.map { it.vaccinationId }, now)
                        pushes += localDirty.size
                    }
                }
                
                // Growth Records
                run {
                    val remoteGrowth = withRetry {
                        firestoreService.fetchUpdatedGrowthRecords(farmerId, state.lastGrowthSyncAt)
                    }
                    if (remoteGrowth.isNotEmpty()) {
                        remoteGrowth.forEach { growthRecordDao.upsert(it) }
                        pulls += remoteGrowth.size
                    }
                    
                    val localDirty = growthRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushGrowthRecords(farmerId, localDirty) }
                        growthRecordDao.clearDirty(localDirty.map { it.recordId }, now)
                        pushes += localDirty.size
                    }
                }
                
                // Quarantine Records
                run {
                    val remoteQuarantine = withRetry {
                        firestoreService.fetchUpdatedQuarantineRecords(farmerId, state.lastQuarantineSyncAt)
                    }
                    if (remoteQuarantine.isNotEmpty()) {
                        remoteQuarantine.forEach { quarantineRecordDao.upsert(it) }
                        pulls += remoteQuarantine.size
                    }
                    
                    val localDirty = quarantineRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushQuarantineRecords(farmerId, localDirty) }
                        quarantineRecordDao.clearDirty(localDirty.map { it.quarantineId }, now)
                        pushes += localDirty.size
                    }
                }
                
                // Mortality Records
                run {
                    val remoteMortality = withRetry {
                        firestoreService.fetchUpdatedMortalityRecords(farmerId, state.lastMortalitySyncAt)
                    }
                    if (remoteMortality.isNotEmpty()) {
                        remoteMortality.forEach { mortalityRecordDao.upsert(it) }
                        pulls += remoteMortality.size
                    }
                    
                    val localDirty = mortalityRecordDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushMortalityRecords(farmerId, localDirty) }
                        mortalityRecordDao.clearDirty(localDirty.map { it.deathId }, now)
                        pushes += localDirty.size
                    }
                }
                
                // Hatching Batches
                run {
                    val remoteHatching = withRetry {
                        firestoreService.fetchUpdatedHatchingBatches(farmerId, state.lastHatchingSyncAt)
                    }
                    if (remoteHatching.isNotEmpty()) {
                        remoteHatching.forEach { hatchingBatchDao.upsert(it) }
                        pulls += remoteHatching.size
                    }
                    
                    val localDirty = hatchingBatchDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushHatchingBatches(farmerId, localDirty) }
                        hatchingBatchDao.clearDirty(localDirty.map { it.batchId }, now)
                        pushes += localDirty.size
                    }
                }
                
                // Hatching Logs
                run {
                    val remoteLogs = withRetry {
                        firestoreService.fetchUpdatedHatchingLogs(farmerId, state.lastHatchingLogSyncAt)
                    }
                    if (remoteLogs.isNotEmpty()) {
                        remoteLogs.forEach { hatchingLogDao.upsert(it) }
                        pulls += remoteLogs.size
                    }
                    
                    val localDirty = hatchingLogDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushHatchingLogs(farmerId, localDirty) }
                        hatchingLogDao.clearDirty(localDirty.map { it.logId }, now)
                        pushes += localDirty.size
                    }
                }
            }

            // ===================
            // Daily Logs (Sprint 1)
            // ===================
            run {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    val remoteLogs: List<DailyLogEntity> = withRetry {
                        firestoreService.fetchUpdatedDailyLogs(userId, state.lastDailyLogSyncAt)
                    }
                    if (remoteLogs.isNotEmpty()) {
                        for (e in remoteLogs) {
                            dailyLogDao.upsert(e)
                        }
                        pulls += remoteLogs.size
                    }
                    val localDirty: List<DailyLogEntity> = dailyLogDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushDailyLogs(userId, localDirty) }
                        dailyLogDao.clearDirty(localDirty.map { it.logId }, now)
                        pushes += localDirty.size
                    }
                }
            }

            // ============
            // Tasks (Sprint 1)
            // ============
            run {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    val remoteTasks: List<TaskEntity> = withRetry {
                        firestoreService.fetchUpdatedTasks(userId, state.lastTaskSyncAt)
                    }
                    if (remoteTasks.isNotEmpty()) {
                        for (t in remoteTasks) {
                            taskDao.upsert(t)
                        }
                        pulls += remoteTasks.size
                    }
                    val localDirty: List<TaskEntity> = taskDao.getDirty()
                    if (localDirty.isNotEmpty()) {
                        withRetry { firestoreService.pushTasks(userId, localDirty) }
                        taskDao.clearDirty(localDirty.map { it.taskId }, now)
                        pushes += localDirty.size
                    }
                }
            }

            // Update sync state for all domains once at the end
            syncStateDao.upsert(
                state.copy(
                    lastSyncAt = now,
                    lastProductSyncAt = now,
                    lastOrderSyncAt = now,
                    lastTrackingSyncAt = now,
                    lastTransferSyncAt = now,
                    lastChatSyncAt = now,
                    lastBreedingSyncAt = now,
                    lastAlertSyncAt = now,
                    lastDashboardSyncAt = now,
                    lastVaccinationSyncAt = now,
                    lastGrowthSyncAt = now,
                    lastQuarantineSyncAt = now,
                    lastMortalitySyncAt = now,
                    lastHatchingSyncAt = now,
                    lastHatchingLogSyncAt = now,
                    lastEnthusiastBreedingSyncAt = now,
                    lastEnthusiastDashboardSyncAt = now,
                    lastDailyLogSyncAt = now,
                    lastTaskSyncAt = now
                )
            )

            Resource.Success(SyncStats(pushes, pulls, outboxProcessed))
        } catch (e: Exception) {
            Timber.e(e, "syncAll failed")
            Resource.Error(e.message ?: "Sync failed")
        }
    }
}