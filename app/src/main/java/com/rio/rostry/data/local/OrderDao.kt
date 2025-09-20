package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE isDeleted = 0")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id AND isDeleted = 0")
    suspend fun getOrderById(id: String): Order?

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId AND isDeleted = 0")
    fun getOrdersByBuyerId(buyerId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE farmerId = :farmerId AND isDeleted = 0")
    fun getOrdersByFarmerId(farmerId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status = :status AND isDeleted = 0")
    fun getOrdersByStatus(status: String): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun deleteOrder(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM orders WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    suspend fun purgeDeletedOrders(beforeTimestamp: Long)
}