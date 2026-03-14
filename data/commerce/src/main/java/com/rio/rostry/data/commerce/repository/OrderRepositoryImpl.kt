package com.rio.rostry.data.commerce.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rio.rostry.core.model.Order
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.commerce.repository.OrderRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of OrderRepository using Firebase Firestore.
 *
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 * 
 * ARCHITECTURE NOTE:
 * This repository handles domain-level Order operations via Firestore.
 * Admin operations requiring database entities (OrderEntity) are delegated
 * to OrderManagementRepository in the app module, which has access to Room DAOs.
 * This separation keeps domain/data layers clean while supporting complex admin workflows.
 */
@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : OrderRepository {

    private val ordersCollection = firestore.collection("orders")

    // ═══════════════════════════════════════════════════════════════════
    // CORE ORDER OPERATIONS
    // ═══════════════════════════════════════════════════════════════════

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val document = ordersCollection.document(orderId).get().await()
            if (document.exists()) {
                val order = document.toObject(Order::class.java)
                if (order != null) {
                    Result.Success(order)
                } else {
                    Result.Error(Exception("Failed to parse order data"))
                }
            } else {
                Result.Error(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get order: $orderId")
            Result.Error(e)
        }
    }

    override suspend fun createOrder(order: Order): Result<Order> {
        return try {
            ordersCollection.document(order.id).set(order).await()
            Timber.i("Created order: ${order.id}")
            Result.Success(order)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create order")
            Result.Error(e)
        }
    }

    override suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", status, "updatedAt", System.currentTimeMillis())
                .await()
            Timber.d("Updated order $orderId status to: $status")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update order status")
            Result.Error(e)
        }
    }

    override fun getOrdersByBuyer(buyerId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection
            .whereEqualTo("buyerId", buyerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error observing buyer orders")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents?.mapNotNull {
                    it.toObject(Order::class.java)
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    override fun getOrdersBySeller(sellerId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection
            .whereEqualTo("sellerId", sellerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error observing seller orders")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents?.mapNotNull {
                    it.toObject(Order::class.java)
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun cancelOrder(orderId: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update(
                    "status", "CANCELLED",
                    "cancelledAt", System.currentTimeMillis()
                )
                .await()
            Timber.i("Cancelled order: $orderId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to cancel order")
            Result.Error(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ADMIN OPERATIONS - DELEGATED TO APP MODULE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Admin operation: Get all orders for admin dashboard.
     * 
     * ARCHITECTURE DECISION:
     * This method requires access to OrderEntity (Room database entity) for complex
     * queries, joins, and offline-first operations. The data:commerce module focuses
     * on Firestore-based domain models (Order). Admin operations are handled by
     * OrderManagementRepository in the app module, which has access to Room DAOs.
     * 
     * @see com.rio.rostry.data.repository.OrderManagementRepository
     */
    override suspend fun getAllOrdersAdmin(): com.rio.rostry.utils.Resource<List<com.rio.rostry.data.database.entity.OrderEntity>> {
        Timber.w("getAllOrdersAdmin called on OrderRepositoryImpl - this is delegated to OrderManagementRepository in app module")
        return com.rio.rostry.utils.Resource.Error(
            "Admin order operations handled by OrderManagementRepository. " +
            "Inject OrderManagementRepository instead of OrderRepository for admin features."
        )
    }

    /**
     * Admin operation: Cancel order with reason logging.
     * 
     * ARCHITECTURE DECISION:
     * Admin cancellations require audit logging, notification triggers, and potential
     * refund processing. These complex workflows are managed by OrderManagementRepository
     * which has access to notification systems and audit logging infrastructure.
     * 
     * @see com.rio.rostry.data.repository.OrderManagementRepository
     */
    override suspend fun adminCancelOrder(orderId: String, reason: String): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("adminCancelOrder called on OrderRepositoryImpl - delegated to OrderManagementRepository")
        return com.rio.rostry.utils.Resource.Error(
            "Admin cancel operations handled by OrderManagementRepository. " +
            "Reason: $reason"
        )
    }

    /**
     * Admin operation: Process refund for order.
     * 
     * ARCHITECTURE DECISION:
     * Refunds require payment gateway integration, transaction records, and audit trails.
     * This complexity belongs in OrderManagementRepository which has access to payment
     * systems and financial transaction logging.
     * 
     * @see com.rio.rostry.data.repository.OrderManagementRepository
     */
    override suspend fun adminRefundOrder(orderId: String, reason: String): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("adminRefundOrder called on OrderRepositoryImpl - delegated to OrderManagementRepository")
        return com.rio.rostry.utils.Resource.Error(
            "Admin refund operations handled by OrderManagementRepository. " +
            "Reason: $reason"
        )
    }

    /**
     * Admin operation: Force update order status (bypass state machine).
     * 
     * ARCHITECTURE DECISION:
     * Admin status overrides require audit logging and notification to both parties.
     * This is handled by OrderManagementRepository which implements the full workflow.
     * 
     * @see com.rio.rostry.data.repository.OrderManagementRepository
     */
    override suspend fun adminUpdateOrderStatus(orderId: String, newStatus: String): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("adminUpdateOrderStatus called on OrderRepositoryImpl - delegated to OrderManagementRepository")
        return com.rio.rostry.utils.Resource.Error(
            "Admin status update handled by OrderManagementRepository. " +
            "New status: $newStatus"
        )
    }

    /**
     * Admin operation: Force complete order (bypass normal workflow).
     * 
     * ARCHITECTURE DECISION:
     * Force completion requires verification, audit logging, and potential dispute
     * prevention checks. OrderManagementRepository handles these complex workflows.
     * 
     * @see com.rio.rostry.data.repository.OrderManagementRepository
     */
    override suspend fun adminForceComplete(orderId: String): com.rio.rostry.utils.Resource<Unit> {
        Timber.w("adminForceComplete called on OrderRepositoryImpl - delegated to OrderManagementRepository")
        return com.rio.rostry.utils.Resource.Error(
            "Admin force complete handled by OrderManagementRepository"
        )
    }
}
