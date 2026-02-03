package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.OrderItemEntity
// You might want a POGO (Plain Old Java/Kotlin Object) to represent an Order with its items
// For example: data class OrderWithItems(val order: OrderEntity, val items: List<OrderItemEntity>)
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Transaction
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        insertOrder(order)
        insertOrderItems(items.map { it.copy(orderId = order.orderId) }) // Ensure orderId is set for items
    }

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun findById(orderId: String): OrderEntity?

    // If you create a POGO class OrderWithItems:
    // @Transaction
    // @Query("SELECT * FROM orders WHERE orderId = :orderId")
    // fun getOrderWithItemsById(orderId: String): Flow<OrderWithItems?>

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY orderDate DESC")
    fun getOrdersByBuyerId(buyerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY orderDate DESC")
    suspend fun getOrdersByBuyerIdSuspend(buyerId: String): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE sellerId = :sellerId ORDER BY orderDate DESC")
    fun getOrdersBySellerId(sellerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY orderDate DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsByOrderId(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsList(orderId: String): List<OrderItemEntity>

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrderById(orderId: String) // Cascading delete for OrderItems is handled by ForeignKey

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()

    // Incremental sync helpers
    @Query("SELECT * FROM orders WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int = 500): List<OrderEntity>

    @Query("DELETE FROM orders WHERE isDeleted = 1")
    suspend fun purgeDeleted()

    // Additional queries for General user flows
    @Query("SELECT * FROM orders WHERE buyerId = :buyerId AND status IN (:statuses)")
    fun getOrdersByStatus(buyerId: String, statuses: List<String>): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY orderDate DESC LIMIT :limit")
    fun getRecentOrders(buyerId: String, limit: Int): Flow<List<OrderEntity>>

    @Update
    suspend fun update(order: OrderEntity)

    // Aggregations for analytics
    @Query("SELECT IFNULL(SUM(totalAmount), 0) FROM orders WHERE sellerId = :sellerId AND status = 'DELIVERED' AND updatedAt BETWEEN :start AND :end")
    suspend fun sumDeliveredForSellerBetween(sellerId: String, start: Long, end: Long): Double

    @Query("SELECT COUNT(*) FROM orders WHERE sellerId = :sellerId AND status = 'DELIVERED' AND updatedAt BETWEEN :start AND :end")
    suspend fun countDeliveredForSellerBetween(sellerId: String, start: Long, end: Long): Int

    @Query("SELECT * FROM orders WHERE sellerId = :farmerId AND status = 'DELIVERED' AND updatedAt BETWEEN :start AND :end")
    suspend fun getDeliveredOrdersForFarmerBetween(farmerId: String, start: Long, end: Long): List<OrderEntity>

    // Note: Assuming orders are linked to batches/products via order items. This query is simplified and might need adjustment based on schema.
    // Ideally, we'd join with order_items, but for now, we'll assume a direct link or handle it in repository if needed.
    // However, since OrderEntity doesn't have batchId/productId directly, we need a JOIN.
    // But Room return types must match. Let's try a JOIN and return Orders.
    @Query("SELECT DISTINCT o.* FROM orders o INNER JOIN order_items oi ON o.orderId = oi.orderId WHERE oi.productId IN (SELECT productId FROM products WHERE batchId = :batchId)")
    suspend fun getOrdersForBatch(batchId: String): List<OrderEntity>

    @Query("SELECT DISTINCT o.* FROM orders o INNER JOIN order_items oi ON o.orderId = oi.orderId WHERE oi.productId = :productId")
    suspend fun getOrdersForProduct(productId: String): List<OrderEntity>

    @Query("SELECT * FROM orders ORDER BY orderDate DESC LIMIT :limit")
    suspend fun getAllOrdersSnapshot(limit: Int): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE dirty = 1")
    suspend fun getDirty(): List<OrderEntity>
}
