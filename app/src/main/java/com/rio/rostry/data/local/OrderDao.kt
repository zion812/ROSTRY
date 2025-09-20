package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: String): Order?

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId")
    fun getOrdersByBuyerId(buyerId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE farmerId = :farmerId")
    fun getOrdersByFarmerId(farmerId: String): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)
}