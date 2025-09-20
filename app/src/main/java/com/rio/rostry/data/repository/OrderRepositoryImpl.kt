package com.rio.rostry.data.repository

import com.rio.rostry.data.local.OrderDao
import com.rio.rostry.data.model.Order as DataOrder
import com.rio.rostry.domain.model.Order as DomainOrder
import com.rio.rostry.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao
) : OrderRepository {

    override fun getAllOrders(): Flow<List<DomainOrder>> {
        return orderDao.getAllOrders().map { orders ->
            orders.map { it.toDomainModel() }
        }
    }

    override suspend fun getOrderById(id: String): DomainOrder? {
        return orderDao.getOrderById(id)?.toDomainModel()
    }

    override fun getOrdersByBuyerId(buyerId: String): Flow<List<DomainOrder>> {
        return orderDao.getOrdersByBuyerId(buyerId).map { orders ->
            orders.map { it.toDomainModel() }
        }
    }

    override fun getOrdersByFarmerId(farmerId: String): Flow<List<DomainOrder>> {
        return orderDao.getOrdersByFarmerId(farmerId).map { orders ->
            orders.map { it.toDomainModel() }
        }
    }

    override suspend fun insertOrder(order: DomainOrder) {
        orderDao.insertOrder(order.toDataModel())
    }

    override suspend fun updateOrder(order: DomainOrder) {
        orderDao.updateOrder(order.toDataModel())
    }

    override suspend fun deleteOrder(order: DomainOrder) {
        orderDao.deleteOrder(order.id)
    }

    private fun DataOrder.toDomainModel(): DomainOrder {
        return DomainOrder(
            id = id,
            productId = productId,
            buyerId = buyerId,
            farmerId = farmerId,
            quantity = quantity,
            totalPrice = totalPrice,
            status = status,
            deliveryAddress = deliveryAddress,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainOrder.toDataModel(): DataOrder {
        return DataOrder(
            id = id,
            productId = productId,
            buyerId = buyerId,
            farmerId = farmerId,
            quantity = quantity,
            totalPrice = totalPrice,
            status = status,
            deliveryAddress = deliveryAddress,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}