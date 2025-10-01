package com.rio.rostry.data.sync

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.ConnectivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

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
    private val firestoreService: FirestoreService,
    private val connectivityManager: ConnectivityManager,
    private val gson: Gson
    ) {
        data class SyncStats(
            val pushed: Int = 0,
            val pulled: Int = 0,
            val outboxProcessed: Int = 0
        )

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
            transferHistoryJson = remote.transferHistoryJson ?: local.transferHistoryJson
        )
        return lineageKept
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

            // ==========
            // Products
            // ==========
            run {
                // Pull
                val remoteProductsUpdated: List<ProductEntity> = withRetry {
                    firestoreService.fetchUpdatedProducts(state.lastProductSyncAt)
                }
                if (remoteProductsUpdated.isNotEmpty()) {
                    productDao.insertProducts(remoteProductsUpdated)
                    pulls += remoteProductsUpdated.size
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
                val pendingEntries = outboxDao.getPending(limit = 50)
                for (entry in pendingEntries) {
                    try {
                        // Mark as in progress
                        outboxDao.updateStatus(entry.outboxId, "IN_PROGRESS", now)
                        
                        // Process based on entity type
                        when (entry.entityType) {
                            "ORDER" -> {
                                val order = gson.fromJson(entry.payloadJson, OrderEntity::class.java)
                                withRetry { firestoreService.pushOrders(listOf(order)) }
                            }
                            "POST" -> {
                                val post = gson.fromJson(entry.payloadJson, PostEntity::class.java)
                                // Assuming posts have a push method - add if needed
                                Timber.d("Outbox: Processing POST entity (implement push if needed)")
                            }
                            else -> {
                                Timber.w("Unknown outbox entity type: ${entry.entityType}")
                            }
                        }
                        
                        // Mark as completed
                        outboxDao.updateStatus(entry.outboxId, "COMPLETED", now)
                        pushes++
                        outboxProcessed++
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to process outbox entry ${entry.outboxId}")
                        outboxDao.incrementRetry(entry.outboxId, now)
                        
                        // Mark as FAILED if exceeded retry threshold
                        if (entry.retryCount >= 2) { // Will be 3 after increment
                            outboxDao.updateStatus(entry.outboxId, "FAILED", now)
                        }
                    }
                }
                
                // Purge completed entries older than 7 days
                val sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000)
                outboxDao.purgeCompleted(sevenDaysAgo)
            }

            // Update sync state for all domains
            syncStateDao.upsert(
                state.copy(
                    lastSyncAt = now,
                    lastProductSyncAt = now,
                    lastOrderSyncAt = now,
                    lastTrackingSyncAt = now,
                    lastTransferSyncAt = now,
                    lastChatSyncAt = now
                )
            )

            Resource.Success(SyncStats(pushes, pulls, outboxProcessed))
        } catch (e: Exception) {
            Timber.e(e, "syncAll failed")
            Resource.Error(e.message ?: "Sync failed")
        }
    }
}
