package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.entity.OrderEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface OrderRepository {
    fun getOrderById(orderId: String): Flow<OrderEntity?>
    fun getOrdersByBuyer(buyerId: String): Flow<List<OrderEntity>>
    suspend fun upsert(order: OrderEntity)
    suspend fun softDelete(orderId: String)
}

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao
) : OrderRepository {
    override fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)

    override fun getOrdersByBuyer(buyerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByBuyerId(buyerId)

    override suspend fun upsert(order: OrderEntity) {
        // Mark dirty for sync and bump lastModifiedAt
        val updated = order.copy(dirty = true, lastModifiedAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis())
        orderDao.insertOrUpdate(updated)
    }

    override suspend fun softDelete(orderId: String) {
        // In a full impl we would fetch current row; here rely on DAO helpers (not present). Placeholder no-op.
    }
}
