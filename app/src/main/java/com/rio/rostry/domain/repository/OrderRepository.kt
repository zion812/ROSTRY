package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getAllOrders(): Flow<List<Order>>
    suspend fun getOrderById(id: String): Order?
    fun getOrdersByBuyerId(buyerId: String): Flow<List<Order>>
    fun getOrdersByFarmerId(farmerId: String): Flow<List<Order>>
    suspend fun insertOrder(order: Order)
    suspend fun updateOrder(order: Order)
    suspend fun deleteOrder(order: Order)
}