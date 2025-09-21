package com.rio.rostry.data.sync

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.utils.Resource
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
    private val familyTreeDao: FamilyTreeDao,
    private val chatMessageDao: ChatMessageDao,
    private val transferDao: TransferDao,
    private val syncStateDao: SyncStateDao,
) {
    data class SyncStats(
        val pushed: Int = 0,
        val pulled: Int = 0
    )

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
                // TODO: fetch from remote where updatedAt > state.lastTrackingSyncAt
                val remoteTrackingUpdated: List<ProductTrackingEntity> = emptyList()
                if (remoteTrackingUpdated.isNotEmpty()) {
                    productTrackingDao.upsertAll(remoteTrackingUpdated)
                    pulls += remoteTrackingUpdated.size
                }

                // Push local dirty rows
                val localDirty = productTrackingDao.getUpdatedSince(state.lastTrackingSyncAt, limit = 1000)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // TODO: push to remote, resolve conflicts server-side, ACK
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
                // TODO: fetch from remote where updatedAt > state.lastProductSyncAt
                val remoteProductsUpdated: List<ProductEntity> = emptyList()
                if (remoteProductsUpdated.isNotEmpty()) {
                    productDao.insertProducts(remoteProductsUpdated)
                    pulls += remoteProductsUpdated.size
                }

                // Push (dirty only)
                val localDirty = productDao.getUpdatedSince(state.lastProductSyncAt, limit = 1000)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // TODO: push to remote, ACK back, then clear dirty
                    val cleaned = localDirty.map { it.copy(dirty = false, updatedAt = now) }
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
                // TODO: fetch from remote where updatedAt > state.lastOrderSyncAt
                val remoteOrdersUpdated: List<OrderEntity> = emptyList()
                if (remoteOrdersUpdated.isNotEmpty()) {
                    // Upsert each; OrderDao has insertOrUpdate
                    remoteOrdersUpdated.forEach { orderDao.insertOrUpdate(it) }
                    pulls += remoteOrdersUpdated.size
                }

                // Push dirties
                val localDirty = orderDao.getUpdatedSince(state.lastOrderSyncAt, limit = 1000)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // TODO: push to remote, then clear dirty
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
                // TODO: fetch from remote where updatedAt > state.lastTransferSyncAt
                val remoteTransfersUpdated: List<TransferEntity> = emptyList()
                if (remoteTransfersUpdated.isNotEmpty()) {
                    remoteTransfersUpdated.forEach { transferDao.upsert(it) }
                    pulls += remoteTransfersUpdated.size
                }

                // Push dirties
                val localDirty = transferDao.getUpdatedSince(state.lastTransferSyncAt, limit = 1000)
                    .filter { it.dirty }
                if (localDirty.isNotEmpty()) {
                    // TODO: push to remote, then clear dirty
                    val cleaned = localDirty.map { it.copy(dirty = false, updatedAt = now) }
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
                // TODO: fetch from remote where updatedAt > state.lastChatSyncAt
                val remoteMessagesUpdated: List<ChatMessageEntity> = emptyList()
                if (remoteMessagesUpdated.isNotEmpty()) {
                    chatMessageDao.upsertAll(remoteMessagesUpdated)
                    pulls += remoteMessagesUpdated.size
                }

                // Purge soft-deleted
                chatMessageDao.purgeDeleted()
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

            Resource.Success(SyncStats(pushes, pulls))
        } catch (e: Exception) {
            Timber.e(e, "syncAll failed")
            Resource.Error(e.message ?: "Sync failed")
        }
    }
}
