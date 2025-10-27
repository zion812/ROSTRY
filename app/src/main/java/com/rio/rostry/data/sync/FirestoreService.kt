package com.rio.rostry.data.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
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
import com.rio.rostry.data.database.entity.TaskEntity
import com.rio.rostry.data.database.entity.UserEntity
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
/**
 * SyncRemote abstracts the remote API used by SyncManager. Implemented by FirestoreService in prod
 * and by fakes in androidTest.
 */
interface SyncRemote {
    suspend fun fetchUpdatedProducts(since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.ProductEntity>
    suspend fun fetchUpdatedOrders(since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.OrderEntity>
    suspend fun fetchUpdatedTransfers(since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.TransferEntity>
    suspend fun fetchUpdatedTrackings(since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.ProductTrackingEntity>
    suspend fun fetchUpdatedChats(since: Long, limit: Int = 1000): List<com.rio.rostry.data.database.entity.ChatMessageEntity>
    suspend fun pushProducts(entities: List<com.rio.rostry.data.database.entity.ProductEntity>): Int
    suspend fun fetchUpdatedDailyLogs(farmerId: String, since: Long, limit: Int = 1000): List<com.rio.rostry.data.database.entity.DailyLogEntity>
    suspend fun pushDailyLogs(farmerId: String, entities: List<com.rio.rostry.data.database.entity.DailyLogEntity>): Int
    suspend fun fetchUpdatedTasks(farmerId: String, since: Long, limit: Int = 1000): List<com.rio.rostry.data.database.entity.TaskEntity>
    suspend fun pushTasks(farmerId: String, entities: List<com.rio.rostry.data.database.entity.TaskEntity>): Int
    suspend fun pushOrders(entities: List<com.rio.rostry.data.database.entity.OrderEntity>): Int
    suspend fun pushTransfers(entities: List<com.rio.rostry.data.database.entity.TransferEntity>): Int
    suspend fun pushTrackings(entities: List<com.rio.rostry.data.database.entity.ProductTrackingEntity>): Int
    suspend fun pushChats(entities: List<com.rio.rostry.data.database.entity.ChatMessageEntity>): Int
    suspend fun fetchUpdatedBreedingPairs(farmerId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.BreedingPairEntity>
    suspend fun pushBreedingPairs(farmerId: String, entities: List<com.rio.rostry.data.database.entity.BreedingPairEntity>): Int
    suspend fun fetchUpdatedAlerts(farmerId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.FarmAlertEntity>
    suspend fun pushAlerts(farmerId: String, entities: List<com.rio.rostry.data.database.entity.FarmAlertEntity>): Int
    suspend fun fetchUpdatedDashboardSnapshots(farmerId: String, since: Long, limit: Int = 100): List<com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity>
    suspend fun pushDashboardSnapshots(farmerId: String, entities: List<com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity>): Int
    suspend fun fetchUpdatedVaccinations(farmerId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.VaccinationRecordEntity>
    suspend fun pushVaccinations(farmerId: String, entities: List<com.rio.rostry.data.database.entity.VaccinationRecordEntity>): Int
    suspend fun fetchUpdatedGrowthRecords(farmerId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.GrowthRecordEntity>
    suspend fun pushGrowthRecords(farmerId: String, entities: List<com.rio.rostry.data.database.entity.GrowthRecordEntity>): Int
    suspend fun fetchUpdatedQuarantineRecords(farmerId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.QuarantineRecordEntity>
    suspend fun pushQuarantineRecords(farmerId: String, entities: List<com.rio.rostry.data.database.entity.QuarantineRecordEntity>): Int
    suspend fun fetchUpdatedMortalityRecords(farmerId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.MortalityRecordEntity>
    suspend fun pushMortalityRecords(farmerId: String, entities: List<com.rio.rostry.data.database.entity.MortalityRecordEntity>): Int
    suspend fun fetchUpdatedHatchingBatches(farmerId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.HatchingBatchEntity>
    suspend fun pushHatchingBatches(farmerId: String, entities: List<com.rio.rostry.data.database.entity.HatchingBatchEntity>): Int
    suspend fun fetchUpdatedHatchingLogs(farmerId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.HatchingLogEntity>
    suspend fun pushHatchingLogs(farmerId: String, entities: List<com.rio.rostry.data.database.entity.HatchingLogEntity>): Int
    suspend fun fetchUpdatedMatingLogs(userId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.MatingLogEntity>
    suspend fun pushMatingLogs(userId: String, entities: List<com.rio.rostry.data.database.entity.MatingLogEntity>): Int
    suspend fun fetchUpdatedEggCollections(userId: String, since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.EggCollectionEntity>
    suspend fun pushEggCollections(userId: String, entities: List<com.rio.rostry.data.database.entity.EggCollectionEntity>): Int
    suspend fun fetchUpdatedEnthusiastSnapshots(userId: String, since: Long, limit: Int = 100): List<com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity>
    suspend fun pushEnthusiastSnapshots(userId: String, entities: List<com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity>): Int
    suspend fun fetchUpdatedUsers(since: Long, limit: Int = 500): List<com.rio.rostry.data.database.entity.UserEntity>
    suspend fun fetchUsersByIds(ids: List<String>): List<com.rio.rostry.data.database.entity.UserEntity>
}

/**
 * FirestoreService centralizes Firestore operations used by SyncManager.
 * Provides delta queries by updatedAt and batched writes with basic conflict handling.
 */
@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) : SyncRemote {
    private val products = firestore.collection("products")
    private val orders = firestore.collection("orders")
    private val transfers = firestore.collection("transfers")
    private val trackings = firestore.collection("productTrackings")
    private val chats = firestore.collection("chats")
    // collectionGroup() takes a subcollection ID only (no '/'). These exist under multiple parents.
    private val dailyLogs = firestore.collectionGroup("daily_logs")
    private val tasks = firestore.collectionGroup("tasks")
    private val users = firestore.collection("users")

    override suspend fun fetchUpdatedProducts(since: Long, limit: Int): List<ProductEntity> =
        products.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(ProductEntity::class.java) }

    override suspend fun fetchUpdatedOrders(since: Long, limit: Int): List<OrderEntity> =
        orders.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(OrderEntity::class.java) }

    override suspend fun fetchUpdatedTransfers(since: Long, limit: Int): List<TransferEntity> =
        transfers.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(TransferEntity::class.java) }

    override suspend fun fetchUpdatedTrackings(since: Long, limit: Int): List<ProductTrackingEntity> =
        trackings.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(ProductTrackingEntity::class.java) }

    override suspend fun fetchUpdatedChats(since: Long, limit: Int): List<ChatMessageEntity> =
        chats.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(ChatMessageEntity::class.java) }

    override suspend fun pushProducts(entities: List<ProductEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = products.document(e.productId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    // ==============================
    // Sprint 1: Daily Logs & Tasks
    // ==============================

    override suspend fun fetchUpdatedDailyLogs(farmerId: String, since: Long, limit: Int): List<DailyLogEntity> =
        firestore.collection("farmers").document(farmerId).collection("daily_logs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(DailyLogEntity::class.java) }

    override suspend fun pushDailyLogs(farmerId: String, entities: List<DailyLogEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val col = firestore.collection("farmers").document(farmerId).collection("daily_logs")
        val now = System.currentTimeMillis()
        entities.forEach { e ->
            val doc = col.document(e.logId)
            batch.set(doc, e, SetOptions.merge())
            batch.update(doc, mapOf("updatedAt" to now))
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedTasks(farmerId: String, since: Long, limit: Int): List<TaskEntity> =
        firestore.collection("farmers").document(farmerId).collection("tasks")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(TaskEntity::class.java) }

    override suspend fun pushTasks(farmerId: String, entities: List<TaskEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val col = firestore.collection("farmers").document(farmerId).collection("tasks")
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

    override suspend fun fetchUpdatedUsers(since: Long, limit: Int): List<UserEntity> =
        users.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(UserEntity::class.java) }

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
    
    override suspend fun fetchUpdatedBreedingPairs(farmerId: String, since: Long, limit: Int): List<BreedingPairEntity> =
        firestore.collection("farmers").document(farmerId).collection("breeding_pairs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(BreedingPairEntity::class.java) }

    override suspend fun pushBreedingPairs(farmerId: String, entities: List<BreedingPairEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("breeding_pairs")
        entities.forEach { e ->
            val doc = collection.document(e.pairId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedAlerts(farmerId: String, since: Long, limit: Int): List<FarmAlertEntity> =
        firestore.collection("farmers").document(farmerId).collection("alerts")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(FarmAlertEntity::class.java) }

    override suspend fun pushAlerts(farmerId: String, entities: List<FarmAlertEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("alerts")
        entities.forEach { e ->
            val doc = collection.document(e.alertId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedDashboardSnapshots(farmerId: String, since: Long, limit: Int): List<FarmerDashboardSnapshotEntity> =
        firestore.collection("farmers").document(farmerId).collection("dashboard_snapshots")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(FarmerDashboardSnapshotEntity::class.java) }

    override suspend fun pushDashboardSnapshots(farmerId: String, entities: List<FarmerDashboardSnapshotEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("dashboard_snapshots")
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

    override suspend fun fetchUpdatedVaccinations(farmerId: String, since: Long, limit: Int): List<VaccinationRecordEntity> =
        firestore.collection("farmers").document(farmerId).collection("vaccinations")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(VaccinationRecordEntity::class.java) }

    override suspend fun pushVaccinations(farmerId: String, entities: List<VaccinationRecordEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("vaccinations")
        entities.forEach { e ->
            val doc = collection.document(e.vaccinationId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedGrowthRecords(farmerId: String, since: Long, limit: Int): List<GrowthRecordEntity> =
        firestore.collection("farmers").document(farmerId).collection("growth_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(GrowthRecordEntity::class.java) }

    override suspend fun pushGrowthRecords(farmerId: String, entities: List<GrowthRecordEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("growth_records")
        entities.forEach { e ->
            val doc = collection.document(e.recordId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedQuarantineRecords(farmerId: String, since: Long, limit: Int): List<QuarantineRecordEntity> =
        firestore.collection("farmers").document(farmerId).collection("quarantine_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(QuarantineRecordEntity::class.java) }

    override suspend fun pushQuarantineRecords(farmerId: String, entities: List<QuarantineRecordEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("quarantine_records")
        entities.forEach { e ->
            val doc = collection.document(e.quarantineId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedMortalityRecords(farmerId: String, since: Long, limit: Int): List<MortalityRecordEntity> =
        firestore.collection("farmers").document(farmerId).collection("mortality_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(MortalityRecordEntity::class.java) }

    override suspend fun pushMortalityRecords(farmerId: String, entities: List<MortalityRecordEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("mortality_records")
        entities.forEach { e ->
            val doc = collection.document(e.deathId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedHatchingBatches(farmerId: String, since: Long, limit: Int): List<HatchingBatchEntity> =
        firestore.collection("farmers").document(farmerId).collection("hatching_batches")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(HatchingBatchEntity::class.java) }

    override suspend fun pushHatchingBatches(farmerId: String, entities: List<HatchingBatchEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("hatching_batches")
        entities.forEach { e ->
            val doc = collection.document(e.batchId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    override suspend fun fetchUpdatedHatchingLogs(farmerId: String, since: Long, limit: Int): List<HatchingLogEntity> =
        firestore.collection("farmers").document(farmerId).collection("hatching_logs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(HatchingLogEntity::class.java) }

    override suspend fun pushHatchingLogs(farmerId: String, entities: List<HatchingLogEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("hatching_logs")
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

}
