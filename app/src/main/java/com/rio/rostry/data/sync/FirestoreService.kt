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
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FirestoreService centralizes Firestore operations used by SyncManager.
 * Provides delta queries by updatedAt and batched writes with basic conflict handling.
 */
@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val products = firestore.collection("products")
    private val orders = firestore.collection("orders")
    private val transfers = firestore.collection("transfers")
    private val trackings = firestore.collection("productTrackings")
    private val chats = firestore.collection("chats")

    suspend fun fetchUpdatedProducts(since: Long, limit: Int = 500): List<ProductEntity> =
        products.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(ProductEntity::class.java) }

    suspend fun fetchUpdatedOrders(since: Long, limit: Int = 500): List<OrderEntity> =
        orders.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(OrderEntity::class.java) }

    suspend fun fetchUpdatedTransfers(since: Long, limit: Int = 500): List<TransferEntity> =
        transfers.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(TransferEntity::class.java) }

    suspend fun fetchUpdatedTrackings(since: Long, limit: Int = 500): List<ProductTrackingEntity> =
        trackings.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(ProductTrackingEntity::class.java) }

    suspend fun fetchUpdatedChats(since: Long, limit: Int = 1000): List<ChatMessageEntity> =
        chats.whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(ChatMessageEntity::class.java) }

    suspend fun pushProducts(entities: List<ProductEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = products.document(e.productId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    suspend fun pushOrders(entities: List<OrderEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = orders.document(e.orderId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    suspend fun pushTransfers(entities: List<TransferEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = transfers.document(e.transferId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    suspend fun pushTrackings(entities: List<ProductTrackingEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = trackings.document(e.trackingId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    suspend fun pushChats(entities: List<ChatMessageEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        entities.forEach { e ->
            val doc = chats.document(e.messageId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    // Farm monitoring entity sync methods (farmer-scoped subcollections)
    
    suspend fun fetchUpdatedBreedingPairs(farmerId: String, since: Long, limit: Int = 500): List<BreedingPairEntity> =
        firestore.collection("farmers").document(farmerId).collection("breeding_pairs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(BreedingPairEntity::class.java) }

    suspend fun pushBreedingPairs(farmerId: String, entities: List<BreedingPairEntity>): Int {
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

    suspend fun fetchUpdatedAlerts(farmerId: String, since: Long, limit: Int = 500): List<FarmAlertEntity> =
        firestore.collection("farmers").document(farmerId).collection("alerts")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(FarmAlertEntity::class.java) }

    suspend fun pushAlerts(farmerId: String, entities: List<FarmAlertEntity>): Int {
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

    suspend fun fetchUpdatedDashboardSnapshots(farmerId: String, since: Long, limit: Int = 100): List<FarmerDashboardSnapshotEntity> =
        firestore.collection("farmers").document(farmerId).collection("dashboard_snapshots")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(FarmerDashboardSnapshotEntity::class.java) }

    suspend fun pushDashboardSnapshots(farmerId: String, entities: List<FarmerDashboardSnapshotEntity>): Int {
        if (entities.isEmpty()) return 0
        val batch = firestore.batch()
        val collection = firestore.collection("farmers").document(farmerId).collection("dashboard_snapshots")
        entities.forEach { e ->
            val doc = collection.document(e.snapshotId)
            batch.set(doc, e, SetOptions.merge())
        }
        batch.commit().await()
        return entities.size
    }

    suspend fun fetchUpdatedVaccinations(farmerId: String, since: Long, limit: Int = 500): List<VaccinationRecordEntity> =
        firestore.collection("farmers").document(farmerId).collection("vaccinations")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(VaccinationRecordEntity::class.java) }

    suspend fun pushVaccinations(farmerId: String, entities: List<VaccinationRecordEntity>): Int {
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

    suspend fun fetchUpdatedGrowthRecords(farmerId: String, since: Long, limit: Int = 500): List<GrowthRecordEntity> =
        firestore.collection("farmers").document(farmerId).collection("growth_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(GrowthRecordEntity::class.java) }

    suspend fun pushGrowthRecords(farmerId: String, entities: List<GrowthRecordEntity>): Int {
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

    suspend fun fetchUpdatedQuarantineRecords(farmerId: String, since: Long, limit: Int = 500): List<QuarantineRecordEntity> =
        firestore.collection("farmers").document(farmerId).collection("quarantine_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(QuarantineRecordEntity::class.java) }

    suspend fun pushQuarantineRecords(farmerId: String, entities: List<QuarantineRecordEntity>): Int {
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

    suspend fun fetchUpdatedMortalityRecords(farmerId: String, since: Long, limit: Int = 500): List<MortalityRecordEntity> =
        firestore.collection("farmers").document(farmerId).collection("mortality_records")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(MortalityRecordEntity::class.java) }

    suspend fun pushMortalityRecords(farmerId: String, entities: List<MortalityRecordEntity>): Int {
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

    suspend fun fetchUpdatedHatchingBatches(farmerId: String, since: Long, limit: Int = 500): List<HatchingBatchEntity> =
        firestore.collection("farmers").document(farmerId).collection("hatching_batches")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(HatchingBatchEntity::class.java) }

    suspend fun pushHatchingBatches(farmerId: String, entities: List<HatchingBatchEntity>): Int {
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

    suspend fun fetchUpdatedHatchingLogs(farmerId: String, since: Long, limit: Int = 500): List<HatchingLogEntity> =
        firestore.collection("farmers").document(farmerId).collection("hatching_logs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(HatchingLogEntity::class.java) }

    suspend fun pushHatchingLogs(farmerId: String, entities: List<HatchingLogEntity>): Int {
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

    private fun log(msg: String) { Timber.d("[FirestoreService] $msg") }

    // ==============================
    // Enthusiast entity sync methods
    // ==============================

    suspend fun fetchUpdatedMatingLogs(userId: String, since: Long, limit: Int = 500): List<MatingLogEntity> =
        firestore.collection("enthusiasts").document(userId).collection("mating_logs")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(MatingLogEntity::class.java) }

    suspend fun pushMatingLogs(userId: String, entities: List<MatingLogEntity>): Int {
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

    suspend fun fetchUpdatedEggCollections(userId: String, since: Long, limit: Int = 500): List<EggCollectionEntity> =
        firestore.collection("enthusiasts").document(userId).collection("egg_collections")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(EggCollectionEntity::class.java) }

    suspend fun pushEggCollections(userId: String, entities: List<EggCollectionEntity>): Int {
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

    suspend fun fetchUpdatedEnthusiastSnapshots(userId: String, since: Long, limit: Int = 100): List<EnthusiastDashboardSnapshotEntity> =
        firestore.collection("enthusiasts").document(userId).collection("dashboard_snapshots")
            .whereGreaterThan("updatedAt", since)
            .orderBy("updatedAt", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .get().await()
            .documents.mapNotNull { it.toObject(EnthusiastDashboardSnapshotEntity::class.java) }

    suspend fun pushEnthusiastSnapshots(userId: String, entities: List<EnthusiastDashboardSnapshotEntity>): Int {
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
