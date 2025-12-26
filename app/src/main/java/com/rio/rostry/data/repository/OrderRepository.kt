package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrderById(orderId: String): Flow<OrderEntity?>
    fun getOrdersByBuyer(buyerId: String): Flow<List<OrderEntity>>
    fun getOrdersBySeller(sellerId: String): Flow<List<OrderEntity>>
    suspend fun upsert(order: OrderEntity)
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<com.rio.rostry.data.database.entity.OrderItemEntity>)
    suspend fun softDelete(orderId: String)
    
    fun getOrderItems(orderId: String): Flow<List<com.rio.rostry.data.database.entity.OrderItemEntity>>
    
    // Additional methods for General user flows
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Resource<Unit>
    fun getOrdersForNotification(userId: String, statuses: List<String>): Flow<List<OrderEntity>>
    fun getRecentOrdersForUser(userId: String, limit: Int = 10): Flow<List<OrderEntity>>
}

