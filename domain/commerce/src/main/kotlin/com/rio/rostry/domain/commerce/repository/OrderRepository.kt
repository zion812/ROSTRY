package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Order
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for order operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface OrderRepository {
    /**
     * Get order by ID.
     * @param orderId The order ID
     * @return Result containing the order or error
     */
    suspend fun getOrderById(orderId: String): Result<Order>

    /**
     * Create a new order.
     * @param order The order to create
     * @return Result containing the created order or error
     */
    suspend fun createOrder(order: Order): Result<Order>

    /**
     * Update order status.
     * @param orderId The order ID
     * @param status The new status
     * @return Result indicating success or error
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit>

    /**
     * Get orders by buyer ID.
     * @param buyerId The buyer ID
     * @return Flow of buyer's orders
     */
    fun getOrdersByBuyer(buyerId: String): Flow<List<Order>>

    /**
     * Get orders by seller ID.
     * @param sellerId The seller ID
     * @return Flow of seller's orders
     */
    fun getOrdersBySeller(sellerId: String): Flow<List<Order>>

    /**
     * Cancel an order.
     * @param orderId The order ID to cancel
     * @return Result indicating success or error
     */
    suspend fun cancelOrder(orderId: String): Result<Unit>
}
