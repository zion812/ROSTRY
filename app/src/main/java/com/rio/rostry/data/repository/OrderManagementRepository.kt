package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.OrderTrackingEventDao
import com.rio.rostry.data.database.dao.PaymentDao
import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.dao.RefundDao
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.OrderTrackingEventEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface OrderManagementRepository {
    fun getOrder(orderId: String): Flow<OrderEntity?>
    suspend fun placeOrder(order: OrderEntity): Resource<String>
    suspend fun advanceState(orderId: String, newStatus: String, hubId: String? = null, note: String? = null): Resource<Unit>
    suspend fun cancelOrder(orderId: String, reason: String?): Resource<Unit>
    suspend fun onPaymentStatusChanged(idempotencyKey: String, paymentStatus: String): Resource<Unit>
    suspend fun onRefundCompleted(paymentId: String, refundAmount: Double): Resource<Unit>
}

@Singleton
class OrderManagementRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val trackingDao: OrderTrackingEventDao,
    private val paymentDao: PaymentDao,
    private val invoiceDao: InvoiceDao,
    private val refundDao: RefundDao,
) : OrderManagementRepository {

    override fun getOrder(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)

    override suspend fun placeOrder(order: OrderEntity): Resource<String> = try {
        val now = System.currentTimeMillis()
        val id = order.orderId
        val placed = order.copy(status = "PLACED", updatedAt = now, lastModifiedAt = now, dirty = true)
        orderDao.insertOrUpdate(placed)
        trackingDao.insert(
            OrderTrackingEventEntity(
                eventId = java.util.UUID.randomUUID().toString(),
                orderId = id,
                status = "PLACED",
                note = "Order placed",
                timestamp = now
            )
        )
        Resource.Success(id)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to place order")
    }

    override suspend fun advanceState(orderId: String, newStatus: String, hubId: String?, note: String?): Resource<Unit> = try {
        val now = System.currentTimeMillis()
        val current = orderDao.getOrderById(orderId)
        // We can't collect Flow here; in real impl, add a DAO find() similar to ProductDao.findById
        // As a fallback, insert a tracking event and rely on a higher-level caller to upsert order state.
        trackingDao.insert(
            OrderTrackingEventEntity(
                eventId = java.util.UUID.randomUUID().toString(),
                orderId = orderId,
                status = newStatus,
                hubId = hubId,
                note = note,
                timestamp = now
            )
        )
        // Best-effort bump order state using update
        // In absence of direct find, we synthesize minimal order row (server will reconcile on sync)
        val synthetic = OrderEntity(
            orderId = orderId,
            buyerId = null,
            sellerId = "",
            totalAmount = 0.0,
            status = newStatus,
            shippingAddress = "",
            createdAt = now,
            updatedAt = now,
            lastModifiedAt = now,
            isDeleted = false,
            dirty = true
        )
        orderDao.insertOrUpdate(synthetic)
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to advance state")
    }

    override suspend fun cancelOrder(orderId: String, reason: String?): Resource<Unit> {
        val order = orderDao.findById(orderId) ?: return Resource.Error("Order not found")

        // Validation logic
        if (order.status == "CANCELLED" || order.status == "DELIVERED") {
            return Resource.Error("Order cannot be cancelled")
        }

        val canCancel = if (order.paymentMethod == "COD") {
            val elapsed = System.currentTimeMillis() - order.createdAt
            elapsed <= 30 * 60 * 1000 // 30 minutes
        } else {
            order.paymentStatus == "pending"
        }

        if (!canCancel) {
            return Resource.Error("Cancellation window expired or payment processed")
        }

        return advanceState(orderId, "CANCELLED", null, reason)
    }

    override suspend fun onPaymentStatusChanged(idempotencyKey: String, paymentStatus: String): Resource<Unit> {
        return try {
            val payment = paymentDao.findByIdempotencyKey(idempotencyKey) ?: return Resource.Error("Payment not found")
            val orderId = payment.orderId
            val order = orderDao.findById(orderId) ?: return Resource.Error("Order not found")
            val result = when (paymentStatus) {
                "SUCCESS" -> advanceState(orderId, "CONFIRMED", null, "Payment successful")
                "FAILED" -> advanceState(orderId, "PAYMENT_FAILED", null, "Payment failed")
                "REFUNDED" -> advanceState(orderId, "REFUNDED", null, "Payment refunded")
                else -> return Resource.Error("Unknown payment status: $paymentStatus")
            }
            if (result is Resource.Error) {
                return result // Propagate the error from advanceState
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to handle payment status change")
        }
    }

    override suspend fun onRefundCompleted(paymentId: String, refundAmount: Double): Resource<Unit> {
        return try {
            val payment = paymentDao.findById(paymentId) ?: return Resource.Error("Payment not found")
            val orderId = payment.orderId
            val order = orderDao.findById(orderId) ?: return Resource.Error("Order not found")
            // Determine full vs partial refund using cumulative refund amounts from refundDao
            val totalRefundedSoFar = refundDao.totalRefundedForPayment(paymentId) // Use injected refundDao
            val cumulativeRefundAmount = totalRefundedSoFar + refundAmount
            val invoice = invoiceDao.findByOrder(orderId) // Use injected invoiceDao
            val invoiceTotal = invoice?.total ?: order.totalAmount // Fallback to order total if invoice not found
            val isFullRefund = cumulativeRefundAmount >= invoiceTotal

            val result = if (isFullRefund) {
                advanceState(orderId, "REFUNDED", null, "Full refund processed")
            } else {
                advanceState(orderId, "PARTIALLY_REFUNDED", null, "Partial refund of $refundAmount processed")
            }

            if (result is Resource.Error) {
                return result // Propagate the error from advanceState
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to handle refund completion")
        }
    }
}
