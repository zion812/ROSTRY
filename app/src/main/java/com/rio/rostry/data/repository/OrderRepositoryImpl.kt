package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.marketplace.payment.PaymentGateway
import com.rio.rostry.marketplace.payment.PaymentMethod
import com.rio.rostry.marketplace.pricing.FeeCalculationEngine
import com.rio.rostry.marketplace.pricing.FeeBreakdown
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rich OrderRepository implementation that fulfills the minimal OrderRepository interface
 * and also exposes state transition helpers, fee calculation, and payment/refund hooks.
 */
@Singleton
class AdvancedOrderService @Inject constructor(
    private val orderDao: OrderDao,
    private val paymentRepository: PaymentRepository,
    private val paymentGateway: PaymentGateway,
) : OrderRepository {

    // ===== Existing interface fulfillment =====
    override fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)

    override fun getOrdersByBuyer(buyerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByBuyerId(buyerId)

    override fun getOrdersBySeller(sellerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersBySellerId(sellerId)

    override suspend fun upsert(order: OrderEntity) {
        val updated = order.copy(dirty = true, lastModifiedAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis())
        orderDao.insertOrUpdate(updated)
    }

    override suspend fun softDelete(orderId: String) {
        val current = orderDao.findById(orderId) ?: return
        val now = System.currentTimeMillis()
        orderDao.insertOrUpdate(current.copy(isDeleted = true, deletedAt = now, updatedAt = now, lastModifiedAt = now, dirty = true))
    }

    // ===== Extensions beyond the interface =====

    suspend fun calculateFees(subtotalCents: Long, userType: UserType, deliveryRequired: Boolean, promotionPercent: Int = 0, bulkQty: Int = 1): FeeBreakdown {
        return FeeCalculationEngine.calculate(subtotalCents, userType, deliveryRequired, promotionPercent, bulkQty)
    }

    suspend fun transition(orderId: String, newStatus: String, allowed: Set<String>): Resource<Unit> {
        return try {
            val current = orderDao.findById(orderId) ?: return Resource.Error("Order not found")
            if (!allowed.contains(current.status)) return Resource.Error("Invalid transition from ${current.status} to $newStatus")
            val now = System.currentTimeMillis()
            val patched = current.copy(status = newStatus, updatedAt = now, lastModifiedAt = now, dirty = true)
            orderDao.insertOrUpdate(patched)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Transition failed")
        }
    }

    suspend fun placeOrder(order: OrderEntity): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val created = order.copy(status = "PLACED", createdAt = now, updatedAt = now, lastModifiedAt = now, dirty = true)
            orderDao.insertOrUpdate(created)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to place order")
        }
    }

    suspend fun confirmOrder(orderId: String): Resource<Unit> = transition(orderId, "CONFIRMED", setOf("PLACED"))

    suspend fun startPreparing(orderId: String): Resource<Unit> = transition(orderId, "PROCESSING", setOf("CONFIRMED"))

    suspend fun shipOrder(orderId: String): Resource<Unit> = transition(orderId, "OUT_FOR_DELIVERY", setOf("PROCESSING"))

    suspend fun deliverOrder(orderId: String): Resource<Unit> = transition(orderId, "DELIVERED", setOf("OUT_FOR_DELIVERY"))

    suspend fun cancelOrder(orderId: String, reason: String? = null): Resource<Unit> {
        val current = orderDao.findById(orderId) ?: return Resource.Error("Order not found")
        
        // Cancellation Logic
        val canCancel = when {
            // Already cancelled or delivered
            current.status == "CANCELLED" || current.status == "DELIVERED" -> false
            
            // COD: 30 minute window
            current.paymentMethod == "COD" -> {
                val elapsed = System.currentTimeMillis() - current.createdAt
                elapsed <= 30 * 60 * 1000 // 30 minutes
            }
            
            // Other methods: Before payment/bill submission (assuming "pending" payment status means no slip/bill yet)
            else -> current.paymentStatus == "pending"
        }
        
        if (!canCancel) {
            return Resource.Error("Cancellation not allowed for this order state or time window.")
        }

        val now = System.currentTimeMillis()
        val updated = current.copy(
            status = "CANCELLED", 
            cancellationReason = reason,
            cancellationTime = now,
            updatedAt = now, 
            lastModifiedAt = now, 
            dirty = true
        )
        orderDao.insertOrUpdate(updated)
        return Resource.Success(Unit)
    }

    suspend fun processPayment(
        orderId: String,
        userId: String,
        amountCents: Long,
        method: PaymentMethod,
        currency: String = "INR"
    ): Resource<Unit> {
        return try {
            val req = com.rio.rostry.marketplace.payment.PaymentRequest(
                orderId = orderId,
                amountCents = amountCents,
                currency = currency,
                customerId = userId,
                paymentMethod = method
            )
            val init = paymentGateway.initialize()
            if (init.isFailure) return Resource.Error(init.exceptionOrNull()?.message ?: "Payment init failed")
            val result = paymentGateway.processPayment(req)
            if (result.isSuccess) Resource.Success(Unit) else Resource.Error(result.exceptionOrNull()?.message ?: "Payment failed")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Payment error")
        }
    }

    suspend fun refundOrderPayment(paymentId: String, reason: String?): Resource<Unit> = paymentRepository.refundPayment(paymentId, reason)

    // ===== Additional methods for General user flows =====

    override suspend fun updateOrderStatus(orderId: String, newStatus: String): Resource<Unit> {
        return try {
            val current = orderDao.findById(orderId) ?: return Resource.Error("Order not found")
            
            // Validate state transitions
            val validTransitions = mapOf(
                "PLACED" to setOf("CONFIRMED", "CANCELLED"),
                "CONFIRMED" to setOf("OUT_FOR_DELIVERY", "CANCELLED"),
                "OUT_FOR_DELIVERY" to setOf("DELIVERED", "CANCELLED"),
                "DELIVERED" to emptySet(),
                "CANCELLED" to emptySet()
            )
            
            val allowedNext = validTransitions[current.status] ?: emptySet()
            if (!allowedNext.contains(newStatus)) {
                return Resource.Error("Invalid transition from ${current.status} to $newStatus")
            }
            
            val now = System.currentTimeMillis()
            val updated = current.copy(
                status = newStatus, 
                updatedAt = now, 
                lastModifiedAt = now, 
                dirty = true,
                actualDeliveryDate = if (newStatus == "DELIVERED") now else current.actualDeliveryDate
            )
            orderDao.update(updated)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update order status")
        }
    }

    override fun getOrdersForNotification(userId: String, statuses: List<String>): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByStatus(userId, statuses)
    }

    override fun getRecentOrdersForUser(userId: String, limit: Int): Flow<List<OrderEntity>> {
        return orderDao.getRecentOrders(userId, limit)
    }
}
