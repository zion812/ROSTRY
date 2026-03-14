package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Order
import com.rio.rostry.core.model.Result
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for order operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface OrderRepository {
    suspend fun getOrderById(orderId: String): Result<Order>
    suspend fun createOrder(order: Order): Result<Order>
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit>
    fun getOrdersByBuyer(buyerId: String): Flow<List<Order>>
    fun getOrdersBySeller(sellerId: String): Flow<List<Order>>
    suspend fun cancelOrder(orderId: String): Result<Unit>

    // ── Admin Features ──────────────────────────────────────────────

    suspend fun getAllOrdersAdmin(): Resource<List<OrderEntity>>
    suspend fun adminCancelOrder(orderId: String, reason: String): Resource<Unit>
    suspend fun adminRefundOrder(orderId: String, reason: String): Resource<Unit>
    suspend fun adminUpdateOrderStatus(orderId: String, newStatus: String): Resource<Unit>
    suspend fun adminForceComplete(orderId: String): Resource<Unit>
}
