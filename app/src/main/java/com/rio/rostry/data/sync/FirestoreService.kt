package com.rio.rostry.data.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.rio.rostry.data.database.entity.ChatMessageEntity
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.ProductTrackingEntity
import com.rio.rostry.data.database.entity.TransferEntity
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

    private fun log(msg: String) { Timber.d("[FirestoreService] $msg") }
}
