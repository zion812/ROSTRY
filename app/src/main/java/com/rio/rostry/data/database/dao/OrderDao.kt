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

    // If you create a POGO class OrderWithItems:
    // @Transaction
    // @Query("SELECT * FROM orders WHERE orderId = :orderId")
    // fun getOrderWithItemsById(orderId: String): Flow<OrderWithItems?>

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY orderDate DESC")
    fun getOrdersByBuyerId(buyerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE sellerId = :sellerId ORDER BY orderDate DESC")
    fun getOrdersBySellerId(sellerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY orderDate DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsByOrderId(orderId: String): Flow<List<OrderItemEntity>>

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrderById(orderId: String) // Cascading delete for OrderItems is handled by ForeignKey

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()
}
