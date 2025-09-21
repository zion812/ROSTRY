package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.PaymentDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.dao.RefundDao
import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.data.database.entity.RefundEntity
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.UpiUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface PaymentRepository {
    fun observePaymentsByOrder(orderId: String): Flow<List<PaymentEntity>>
    suspend fun initiateUpiPayment(orderId: String, userId: String, amount: Double, vpa: String, name: String, note: String?): Resource<PaymentEntity>
    suspend fun codReservation(orderId: String, userId: String, amount: Double): Resource<PaymentEntity>
    suspend fun cardWalletDemo(orderId: String, userId: String, amount: Double, idempotencyKey: String): Resource<PaymentEntity>
    suspend fun markPaymentResult(idempotencyKey: String, success: Boolean, providerRef: String? = null): Resource<Unit>
    suspend fun refundPayment(paymentId: String, reason: String?): Resource<Unit>
}

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentDao: PaymentDao,
    private val orderDao: OrderDao,
    private val invoiceDao: InvoiceDao,
    private val refundDao: RefundDao
) : PaymentRepository {

    override fun observePaymentsByOrder(orderId: String): Flow<List<PaymentEntity>> = paymentDao.observeByOrder(orderId)

    override suspend fun initiateUpiPayment(orderId: String, userId: String, amount: Double, vpa: String, name: String, note: String?): Resource<PaymentEntity> {
        return try {
            require(amount > 0) { "Amount must be positive" }
            val idempotencyKey = "UPI-$orderId-$amount"
            val existing = paymentDao.findByIdempotencyKey(idempotencyKey)
            if (existing != null) return Resource.Success(existing)
            val uri = UpiUtils.buildUpiUri(vpa = vpa, name = name, amount = amount, note = note)
            val now = System.currentTimeMillis()
            val entity = PaymentEntity(
                paymentId = java.util.UUID.randomUUID().toString(),
                orderId = orderId,
                userId = userId,
                method = "UPI",
                amount = amount,
                status = "PENDING",
                providerRef = null,
                upiUri = uri,
                idempotencyKey = idempotencyKey,
                createdAt = now,
                updatedAt = now
            )
            paymentDao.insert(entity)
            Resource.Success(entity)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to initiate UPI payment")
        }
    }

    override suspend fun codReservation(orderId: String, userId: String, amount: Double): Resource<PaymentEntity> {
        return try {
            val idempotencyKey = "COD-$orderId"
            val existing = paymentDao.findByIdempotencyKey(idempotencyKey)
            if (existing != null) return Resource.Success(existing)
            val now = System.currentTimeMillis()
            val entity = PaymentEntity(
                paymentId = java.util.UUID.randomUUID().toString(),
                orderId = orderId,
                userId = userId,
                method = "COD",
                amount = amount,
                status = "PENDING",
                providerRef = null,
                upiUri = null,
                idempotencyKey = idempotencyKey,
                createdAt = now,
                updatedAt = now
            )
            paymentDao.insert(entity)
            Resource.Success(entity)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create COD reservation")
        }
    }

    override suspend fun cardWalletDemo(orderId: String, userId: String, amount: Double, idempotencyKey: String): Resource<PaymentEntity> {
        return try {
            val exist = paymentDao.findByIdempotencyKey(idempotencyKey)
            if (exist != null) return Resource.Success(exist)
            val now = System.currentTimeMillis()
            val entity = PaymentEntity(
                paymentId = java.util.UUID.randomUUID().toString(),
                orderId = orderId,
                userId = userId,
                method = "CARD",
                amount = amount,
                status = "PENDING",
                providerRef = null,
                upiUri = null,
                idempotencyKey = idempotencyKey,
                createdAt = now,
                updatedAt = now
            )
            paymentDao.insert(entity)
            Resource.Success(entity)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to start demo card payment")
        }
    }

    override suspend fun markPaymentResult(idempotencyKey: String, success: Boolean, providerRef: String?): Resource<Unit> {
        return try {
            val current = paymentDao.findByIdempotencyKey(idempotencyKey) ?: return Resource.Error("Payment not found")
            val now = System.currentTimeMillis()
            val updated = current.copy(status = if (success) "SUCCESS" else "FAILED", providerRef = providerRef, updatedAt = now)
            paymentDao.update(updated)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update payment result")
        }
    }

    override suspend fun refundPayment(paymentId: String, reason: String?): Resource<Unit> {
        return try {
            val current = paymentDao.findById(paymentId) ?: return Resource.Error("Payment not found")
            val order = orderDao.findById(current.orderId) ?: return Resource.Error("Order not found")
            val invoice = invoiceDao.findByOrder(order.orderId) ?: return Resource.Error("Invoice not found")

            // Compute already refunded and remaining refundable amount
            val refundedSoFar = refundDao.totalRefundedForPayment(paymentId)
            val refundableRemaining = (invoice.total - refundedSoFar).coerceAtLeast(0.0)
            if (refundableRemaining <= 0.0) return Resource.Success(Unit) // nothing left to refund

            // Determine full vs partial
            val statuses = listOf("PLACED", "CONFIRMED", "PROCESSING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED")
            val idx = statuses.indexOf(order.status)
            val outIdx = statuses.indexOf("OUT_FOR_DELIVERY")

            val logisticsFee = 50.0 // default logistics fee for partial refunds
            val refundAmount = if (idx >= 0 && outIdx >= 0 && idx >= outIdx) {
                // On/after OUT_FOR_DELIVERY => partial refund
                (refundableRemaining - logisticsFee).coerceAtLeast(0.0)
            } else {
                // Before OUT_FOR_DELIVERY => full refund
                refundableRemaining
            }

            if (refundAmount <= 0.0) return Resource.Success(Unit)

            val refund = RefundEntity(
                refundId = java.util.UUID.randomUUID().toString(),
                paymentId = paymentId,
                orderId = order.orderId,
                amount = refundAmount,
                reason = reason,
                createdAt = System.currentTimeMillis()
            )
            refundDao.insert(refund)

            // If fully refunded, mark payment REFUNDED; else leave status as SUCCESS/PENDING (depending on flow)
            val now = System.currentTimeMillis()
            val totalAfter = refundDao.totalRefundedForPayment(paymentId)
            val fullyRefunded = totalAfter >= invoice.total - 0.01 // epsilon
            val updated = current.copy(status = if (fullyRefunded) "REFUNDED" else current.status, updatedAt = now)
            paymentDao.update(updated)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Refund failed")
        }
    }
}
