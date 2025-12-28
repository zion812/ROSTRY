package com.rio.rostry.data.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.QuerySnapshot
import com.rio.rostry.data.database.entity.ChatMessageEntity
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.ProductTrackingEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.BreedingPairEntity
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import com.rio.rostry.data.database.entity.QuarantineRecordEntity
import com.rio.rostry.data.database.entity.MortalityRecordEntity
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.database.entity.HatchingLogEntity
import com.rio.rostry.data.database.entity.MatingLogEntity
import com.rio.rostry.data.database.entity.EggCollectionEntity
import com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity
import com.rio.rostry.data.database.entity.DailyLogEntity
import com.rio.rostry.data.database.entity.BatchSummaryEntity
import com.rio.rostry.data.database.entity.TaskEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
/**
 * SyncRemote abstracts the remote API used by SyncManager. Implemented by FirestoreService in prod
 * and by fakes in androidTest.
 */
interface SyncRemote {
    suspend fun fetchUpdatedProducts(since: Long, limit: Int = 500): List<ProductEntity>
    suspend fun fetchUpdatedOrders(userId: String, since: Long, limit: Int = 500): List<OrderEntity>
    suspend fun fetchUpdatedTransfers(userId: String?, since: Long, limit: Int = 500): List<TransferEntity>
    suspend fun fetchUpdatedTrackings(userId: String?, since: Long, limit: Int = 500): List<ProductTrackingEntity>
    suspend fun fetchUpdatedChats(userId: String?, since: Long, limit: Int = 1000): List<ChatMessageEntity>
    suspend fun pushProducts(entities: List<ProductEntity>): Int
    suspend fun fetchUpdatedDailyLogs(userId: String, role: UserType, since: Long, limit: Int = 1000): List<DailyLogEntity>
    suspend fun pushDailyLogs(userId: String, role: UserType, entities: List<DailyLogEntity>): Int
    suspend fun fetchUpdatedTasks(userId: String, role: UserType, since: Long, limit: Int = 1000): List<TaskEntity>
    suspend fun pushTasks(userId: String, role: UserType, entities: List<TaskEntity>): Int
    suspend fun pushOrders(entities: List<OrderEntity>): Int
    suspend fun pushTransfers(entities: List<TransferEntity>): Int
    suspend fun pushTrackings(entities: List<ProductTrackingEntity>): Int
    suspend fun pushChats(entities: List<ChatMessageEntity>): Int
    suspend fun fetchUpdatedBreedingPairs(userId: String, role: UserType, since: Long, limit: Int = 500): List<BreedingPairEntity>
    suspend fun pushBreedingPairs(userId: String, role: UserType, entities: List<BreedingPairEntity>): Int
    suspend fun fetchUpdatedAlerts(userId: String, role: UserType, since: Long, limit: Int = 500): List<FarmAlertEntity>
    suspend fun pushAlerts(userId: String, role: UserType, entities: List<FarmAlertEntity>): Int
    suspend fun fetchUpdatedDashboardSnapshots(userId: String, role: UserType, since: Long, limit: Int = 100): List<FarmerDashboardSnapshotEntity>
    suspend fun pushDashboardSnapshots(userId: String, role: UserType, entities: List<FarmerDashboardSnapshotEntity>): Int
    suspend fun fetchUpdatedVaccinations(userId: String, role: UserType, since: Long, limit: Int = 500): List<VaccinationRecordEntity>
    suspend fun pushVaccinations(userId: String, role: UserType, entities: List<VaccinationRecordEntity>): Int
    suspend fun fetchUpdatedGrowthRecords(userId: String, role: UserType, since: Long, limit: Int = 500): List<GrowthRecordEntity>
    suspend fun pushGrowthRecords(userId: String, role: UserType, entities: List<GrowthRecordEntity>): Int
    suspend fun fetchUpdatedQuarantineRecords(userId: String, role: UserType, since: Long, limit: Int = 500): List<QuarantineRecordEntity>
    suspend fun pushQuarantineRecords(userId: String, role: UserType, entities: List<QuarantineRecordEntity>): Int
    suspend fun fetchUpdatedMortalityRecords(userId: String, role: UserType, since: Long, limit: Int = 500): List<MortalityRecordEntity>
    suspend fun pushMortalityRecords(userId: String, role: UserType, entities: List<MortalityRecordEntity>): Int
    suspend fun fetchUpdatedHatchingBatches(userId: String, role: UserType, since: Long, limit: Int = 500): List<HatchingBatchEntity>
    suspend fun pushHatchingBatches(userId: String, role: UserType, entities: List<HatchingBatchEntity>): Int
    suspend fun fetchUpdatedHatchingLogs(userId: String, role: UserType, since: Long, limit: Int = 500): List<HatchingLogEntity>
    suspend fun pushHatchingLogs(userId: String, role: UserType, entities: List<HatchingLogEntity>): Int
    suspend fun fetchUpdatedMatingLogs(userId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.MatingLogEntity>
    suspend fun pushMatingLogs(userId: String, entities: List<com.rio.rostry.data.database.entity.MatingLogEntity>): Int
    suspend fun fetchUpdatedEggCollections(userId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.EggCollectionEntity>
    suspend fun pushEggCollections(userId: String, entities: List<com.rio.rostry.data.database.entity.EggCollectionEntity>): Int
    suspend fun fetchUpdatedEnthusiastSnapshots(userId: String, since: Long, limit: Int = 100): List<com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity>
    suspend fun pushEnthusiastSnapshots(userId: String, entities: List<com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity>): Int
    suspend fun fetchUpdatedUsers(since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.UserEntity>
    suspend fun fetchUsersByIds(ids: List<String>): List<com.rio.rostry.data.database.entity.UserEntity>
    // Split-Brain Data Architecture: BatchSummary sync (replaces DailyLogs for cloud sync)
    suspend fun fetchUpdatedBatchSummaries(userId: String, since: Long, limit: Int = 500): List<BatchSummaryEntity>
    suspend fun pushBatchSummaries(userId: String, entities: List<BatchSummaryEntity>): Int
}

/**
 * FirestoreService centralizes Firestore operations used by SyncManager.
 * Provides delta queries by updatedAt and batched writes with basic conflict handling.
 */
@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) : SyncRemote {

    companion object {
        // Timeout constants for different operation types
        private const val READ_TIMEOUT_MS = 15_000L // 15 seconds for reads
        private const val WRITE_TIMEOUT_MS = 30_000L // 30 seconds for writes
        private const val BATCH_TIMEOUT_MS = 60_000L // 60 seconds for batch operations
    }
    private val products = firestore.collection("products")
    private val orders = firestore.collection("orders")
    private val transfers = firestore.collection("transfers")
    private val trackings = firestore.collection("productTrackings")
    private val chats = firestore.collection("chats")
    // collectionGroup() takes a subcollection ID only (no '/'). These exist under multiple parents.
    private val dailyLogs = firestore.collectionGroup("daily_logs")
    private val tasks = firestore.collectionGroup("tasks")
    private val users = firestore.collection("users")

    private fun getRootCollection(role: UserType): String {
        return if (role == UserType.ENTHUSIAST) "enthusiasts" else "farmers"
    }

    override suspend fun fetchUpdatedProducts(since: Long, limit: Int): List<ProductEntity> = try {
        withTimeout(READ_TIMEOUT_MS) {
            products.whereGreaterThan("updatedAt", since)
                .orderBy("updatedAt", Query.Direction.ASCENDING)
                .limit(limit.toLong())
                .get().await()
                .documents.mapNotNull { it.toObject(ProductEntity::class.java) }
        }
    } catch (e: TimeoutCancellationException) {
        Timber.e(e, "Timeout fetching products since=$since")
        emptyList()
    }

    override suspend fun fetchUpdatedOrders(userId: String, since: Long, limit: Int): List<OrderEntity> = try {
        withTimeout(READ_TIMEOUT_MS) {
            // Firestore rules require filtering by buyerId or sellerId.
            // We must perform two queries and merge them because OR queries with inequality filters are restricted.
            // Actually, we can't do OR with inequality on different fields easily.
            // But we can do two separate queries.
            
            val buyerQueryTask = orders
                .whereEqualTo("buyerId", userId)
                .whereGreaterThan("updatedAt", since)
                .orderBy("updatedAt", Query.Direction.ASCENDING)
                .limit(limit.toLong())
                .get()

            val sellerQueryTask = orders
                .whereEqualTo("sellerId", userId)
                .whereGreaterThan("updatedAt", since)
                .orderBy("updatedAt", Query.Direction.ASCENDING)
                .limit(limit.toLong())
                .get()

            val buyerSnapshot = buyerQueryTask.await()
            val sellerSnapshot = sellerQueryTask.await()
            
            val buyerOrders = buyerSnapshot.documents.mapNotNull { it.toObject(OrderEntity::class.java) }
            val sellerOrders = sellerSnapshot.documents.mapNotNull { it.toObject(OrderEntity::class.java) }
            
            (buyerOrders + sellerOrders).distinctBy { it.orderId }
        }
    } catch (e: TimeoutCancellationException) {
        Timber.e(e, "Timeout fetching orders since=$since")
        emptyList()
    }

    override suspend fun fetchUpdatedTransfers(userId: String?, since: Long, limit: Int): List<TransferEntity> = try {
        withTimeout(READ_TIMEOUT_MS) {
            if (userId != null) {
                val senderQuery = transfers.whereEqualTo("senderId", userId)
                    .whereGreaterThan("updatedAt", since)
                    .orderBy("updatedAt", Query.Direction.ASCENDING)
                    .limit(limit.toLong())
                    .get()
                val receiverQuery = transfers.whereEqualTo("receiverId", userId)
                    .whereGreaterThan("updatedAt", since)
                    .orderBy("updatedAt", Query.Direction.ASCENDING)
                    .limit(limit.toLong())
                    .get()
                val tasks = Tasks.whenAllSuccess<QuerySnapshot>(senderQuery, receiverQuery)
                val results = tasks.await()
                val senderSnap = results[0]
                val receiverSnap = results[1]
                val senderDocs = senderSnap.documents.mapNotNull { it.toObject(TransferEntity::class.java) }
                val receiverDocs = receiverSnap.documents.mapNotNull { it.toObject(TransferEntity::class.java) }
                (senderDocs + receiverDocs).distinctBy { it.transferId }
            } else {
                 transfers.whereGreaterThan("updatedAt", since)
                    .orderBy("updatedAt", Query.Direction.ASCENDING)
                    .limit(limit.toLong())
                    .get().await()
                    .documents.mapNotNull { it.toObject(TransferEntity::class.java) }
            }
        }
    } catch (e: Exception) {
        Timber.e(e, "Error fetching transfers since=$since")
        emptyList()
    }

    override suspend fun fetchUpdatedTrackings(userId: String?, since: Long, limit: Int): List<ProductTrackingEntity> = try {
        withTimeout(READ_TIMEOUT_MS) {
            if (userId != null) {
                val buyerQuery = trackings.whereEqualTo("buyerId", userId)
                    .whereGreaterThan("updatedAt", since)
                    .orderBy("updatedAt", Query.Direction.ASCENDING)
                    .limit(limit.toLong())
                    .get()
                val sellerQuery = trackings.whereEqualTo("sellerId", userId)
                    .whereGreaterThan("updatedAt", since)
                    .orderBy("updatedAt", Query.Direction.ASCENDING)
                    .limit(limit.toLong())
                    .get()
                
                val tasks = Tasks.whenAllSuccess<QuerySnapshot>(buyerQuery, sellerQuery)
                val results = tasks.await()
                val buyerSnap = results[0]
                val sellerSnap = results[1]
                val buyerDocs = buyerSnap.documents.mapNotNull { it.toObject(ProductTrackingEntity::class.java) }
                val sellerDocs = sellerSnap.documents.mapNotNull { it.toObject(ProductTrackingEntity::class.java) }
                (buyerDocs + sellerDocs).distinctBy { it.trackingId }
            } else {
                // Do not attempt global query if user is not authenticated/identified
                Timber.w("Skipping global tracking fetch without userId")
                emptyList()
            }
        }
    } catch (e: Exception) {
        Timber.e(e, "Error fetching trackings since=$since")
        emptyList()
    }

    override suspend fun fetchUpdatedChats(userId: String?, since: Long, limit: Int): List<ChatMessageEntity> = try {
        withTimeout(READ_TIMEOUT_MS) {
            if (userId != null) {
                val senderQuery = chats.whereEqualTo("senderId", userId)
                    .whereGreaterThan("updatedAt", since)
                    .orderBy("updatedAt", Query.Direction.ASCENDING)
                    .limit(limit.toLong())
                    .get()
                val receiverQuery = chats.whereEqualTo("receiverId", userId)
                    .whereGreaterThan("updatedAt", since)
                    .orderBy("updatedAt", Query.Direction.ASCENDING)
                    .limit(limit.toLong())
                    .get()
                val tasks = Tasks.whenAllSuccess<QuerySnapshot>(senderQuery, receiverQuery)
                val results = tasks.await()
                val senderSnap = results[0]
                val receiverSnap = results[1]
                val senderDocs = senderSnap.documents.mapNotNull { it.toObject(ChatMessageEntity::class.java) }
                val receiverDocs = receiverSnap.documents.mapNotNull { it.toObject(ChatMessageEntity::class.java) }
                (senderDocs + receiverDocs).distinctBy { it.messageId }
            } else {
                chats.whereGreaterThan("updatedAt", since)
                    .orderBy("updatedAt", Query.Direction.ASCENDING)
                    .limit(limit.toLong())
                    .get().await()
                    .documents.mapNotNull { it.toObject(ChatMessageEntity::class.java) }
            }
        }
    } catch (e: Exception) {
        Timber.e(e, "Error fetching chats since=$since")
        emptyList()
    }

    override suspend fun pushProducts(entities: List<ProductEntity>): Int {
        if (entities.isEmpty()) return 0
        return try {
            withTimeout(BATCH_TIMEOUT_MS) {
                val batch = firestore.batch()
                entities.forEach { e ->
                    val doc = products.document(e.productId)
                    batch.set(doc, e, SetOptions.merge())
                }
                batch.commit().await()
                entities.size
            }
        } catch (e: TimeoutCancellationException) {
            Timber.e(e, "Timeout pushing ${entities.size} products")
            0
        }
    }

    // ==============================
    // Sprint 1: Daily Logs & Tasks
    // ==============================

    override suspend fun fetchUpdatedDailyLogs(userId: String, role: UserType, since: Long, limit: Int): List<DailyLogEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("daily_logs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(DailyLogEntity::class.java) }

    /**
     * @deprecated Daily logs are now LOCAL-ONLY in Split-Brain architecture.
     * Use pushBatchSummaries() instead for cloud sync.
     */
    @Deprecated("Use pushBatchSummaries for Split-Brain architecture", ReplaceWith("pushBatchSummaries(userId, batchSummaries)"))
    override suspend fun pushDailyLogs(userId: String, role: UserType, entities: List<DailyLogEntity>): Int {
        // FREE TIER: Daily logs no longer sync to cloud
        Timber.w("pushDailyLogs called but disabled in Split-Brain architecture. Use BatchSummary instead.")
        return 0 // Return 0 to indicate no sync happened
    }

    override suspend fun fetchUpdatedTasks(userId: String, role: UserType, since: Long, limit: Int): List<TaskEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("tasks")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(TaskEntity::class.java) }

    override suspend fun pushTasks(userId: String, role: UserType, entities: List<TaskEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val col = firestore.collection(getRootCollection(role)).document(userId).collection("tasks")
        val now = System.currentTimeMillis()
        entities.forEach { e ->
            val doc = col.document(e.taskId)
            batch.set(doc, e, SetOptions.merge())
            batch.update(doc, mapOf("updatedAt" to now))
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun pushOrders(entities: List<OrderEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = orders.document(e.orderId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun pushTransfers(entities: List<TransferEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = transfers.document(e.transferId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun pushTrackings(entities: List<ProductTrackingEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = trackings.document(e.trackingId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun pushChats(entities: List<ChatMessageEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = chats.document(e.messageId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedUsers(since: Long, limit: Int): List<UserEntity> = try {
        users.whereGreaterThan("updatedAt", java.util.Date(since))
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(UserEntity::class.java) }
    } catch (e: Exception) {
        Timber.e(e, "Error fetching users since=$since")
        emptyList()
    }

    override suspend fun fetchUsersByIds(ids: List<String>): List<UserEntity> {
        if (ids.isEmpty()) return emptyList()
        // Firestore whereIn supports up to 10 elements; batch accordingly
        val batches = ids.distinct().chunked(10)
        val results = mutableListOf<UserEntity>()
        for (chunk in batches) {
            val snapshot = users.whereIn("userId", chunk).get().await()
            results += snapshot.documents.mapNotNull { it.toObject(UserEntity::class.java) }
        }
        // Deduplicate by userId in case of overlaps
        return results.distinctBy { it.userId }
    }

    // Farm monitoring entity sync methods (farmer-scoped subcollections)
    
    override suspend fun fetchUpdatedBreedingPairs(userId: String, role: UserType, since: Long, limit: Int): List<BreedingPairEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("breeding_pairs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(BreedingPairEntity::class.java) }

    override suspend fun pushBreedingPairs(userId: String, role: UserType, entities: List<BreedingPairEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("breeding_pairs")
        entities.forEach { e ->
            val doc = collection.document(e.pairId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedAlerts(userId: String, role: UserType, since: Long, limit: Int): List<FarmAlertEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("alerts")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(FarmAlertEntity::class.java) }

    override suspend fun pushAlerts(userId: String, role: UserType, entities: List<FarmAlertEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("alerts")
        entities.forEach { e ->
            val doc = collection.document(e.alertId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedDashboardSnapshots(userId: String, role: UserType, since: Long, limit: Int): List<FarmerDashboardSnapshotEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("dashboard_snapshots")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(FarmerDashboardSnapshotEntity::class.java) }

    override suspend fun pushDashboardSnapshots(userId: String, role: UserType, entities: List<FarmerDashboardSnapshotEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("dashboard_snapshots")
        val now = System.currentTimeMillis()
        entities.forEach { e ->
            val doc = collection.document(e.snapshotId)
            batch.set(doc, e, SetOptions.merge())
            // Ensure updatedAt is written for delta queries
            batch.update(doc, mapOf("updatedAt" to now))
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedVaccinations(userId: String, role: UserType, since: Long, limit: Int): List<VaccinationRecordEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("vaccinations")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(VaccinationRecordEntity::class.java) }

    override suspend fun pushVaccinations(userId: String, role: UserType, entities: List<VaccinationRecordEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("vaccinations")
        entities.forEach { e ->
            val doc = collection.document(e.vaccinationId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedGrowthRecords(userId: String, role: UserType, since: Long, limit: Int): List<GrowthRecordEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("growth_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(GrowthRecordEntity::class.java) }

    override suspend fun pushGrowthRecords(userId: String, role: UserType, entities: List<GrowthRecordEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("growth_records")
        entities.forEach { e ->
            val doc = collection.document(e.recordId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedQuarantineRecords(userId: String, role: UserType, since: Long, limit: Int): List<QuarantineRecordEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("quarantine_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(QuarantineRecordEntity::class.java) }

    override suspend fun pushQuarantineRecords(userId: String, role: UserType, entities: List<QuarantineRecordEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("quarantine_records")
        entities.forEach { e ->
            val doc = collection.document(e.quarantineId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedMortalityRecords(userId: String, role: UserType, since: Long, limit: Int): List<MortalityRecordEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("mortality_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(MortalityRecordEntity::class.java) }

    override suspend fun pushMortalityRecords(userId: String, role: UserType, entities: List<MortalityRecordEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("mortality_records")
        entities.forEach { e ->
            val doc = collection.document(e.deathId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedHatchingBatches(userId: String, role: UserType, since: Long, limit: Int): List<HatchingBatchEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("hatching_batches")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(HatchingBatchEntity::class.java) }

    override suspend fun pushHatchingBatches(userId: String, role: UserType, entities: List<HatchingBatchEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("hatching_batches")
        entities.forEach { e ->
            val doc = collection.document(e.batchId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedHatchingLogs(userId: String, role: UserType, since: Long, limit: Int): List<HatchingLogEntity> =
        firestore.collection(getRootCollection(role)).document(userId).collection("hatching_logs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(HatchingLogEntity::class.java) }

    override suspend fun pushHatchingLogs(userId: String, role: UserType, entities: List<HatchingLogEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection(getRootCollection(role)).document(userId).collection("hatching_logs")
        entities.forEach { e ->
            val doc = collection.document(e.logId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    // Enthusiast-scoped
    override suspend fun fetchUpdatedMatingLogs(userId: String, since: Long, limit: Int): List<MatingLogEntity> =
        firestore.collection("enthusiasts").document(userId).collection("mating_logs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(MatingLogEntity::class.java) }

    override suspend fun pushMatingLogs(userId: String, entities: List<MatingLogEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val col = firestore.collection("enthusiasts").document(userId).collection("mating_logs")
        val now = System.currentTimeMillis()
        entities.forEach { e ->
            val doc = col.document(e.logId)
            batch.set(doc, e, SetOptions.merge())
            batch.update(doc, mapOf("updatedAt" to now))
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedEggCollections(userId: String, since: Long, limit: Int): List<EggCollectionEntity> =
        firestore.collection("enthusiasts").document(userId).collection("egg_collections")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(EggCollectionEntity::class.java) }

    override suspend fun pushEggCollections(userId: String, entities: List<EggCollectionEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val col = firestore.collection("enthusiasts").document(userId).collection("egg_collections")
        val now = System.currentTimeMillis()
        entities.forEach { e ->
            val doc = col.document(e.collectionId)
            batch.set(doc, e, SetOptions.merge())
            batch.update(doc, mapOf("updatedAt" to now))
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedEnthusiastSnapshots(userId: String, since: Long, limit: Int): List<EnthusiastDashboardSnapshotEntity> =
        firestore.collection("enthusiasts").document(userId).collection("dashboard_snapshots")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(EnthusiastDashboardSnapshotEntity::class.java) }

    override suspend fun pushEnthusiastSnapshots(userId: String, entities: List<EnthusiastDashboardSnapshotEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val col = firestore.collection("enthusiasts").document(userId).collection("dashboard_snapshots")
        val now = System.currentTimeMillis()
        entities.forEach { e ->
            val doc = col.document(e.snapshotId)
            // Merge entity
            batch.set(doc, e, SetOptions.merge())
            // Ensure updatedAt is written for delta queries
            batch.update(doc, mapOf("updatedAt" to now))
        }
        batch.commit().await()
        return entities.size
    }

    // ==============================
    // Split-Brain: BatchSummary Sync
    // ==============================

    override suspend fun fetchUpdatedBatchSummaries(userId: String, since: Long, limit: Int): List<BatchSummaryEntity> =
        firestore.collection("farmers").document(userId).collection("batch_summaries")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(BatchSummaryEntity::class.java) }

    override suspend fun pushBatchSummaries(userId: String, entities: List<BatchSummaryEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val col = firestore.collection("farmers").document(userId).collection("batch_summaries")
        val now = System.currentTimeMillis()
        entities.forEach { e ->
            val doc = col.document(e.batchId)
            batch.set(doc, e, SetOptions.merge())
            batch.update(doc, mapOf("updatedAt" to now))
        }
        batch.commit().await()
        Timber.d("Pushed ${entities.size} batch summaries for user=$userId")
        return entities.size
    }

}
