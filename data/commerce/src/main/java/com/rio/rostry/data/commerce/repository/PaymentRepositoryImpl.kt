package com.rio.rostry.data.commerce.repository

import com.rio.rostry.data.database.dao.PaymentDao
import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.dao.RefundDao
import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.data.database.entity.RefundEntity
import com.rio.rostry.domain.model.PaymentStatus
import com.rio.rostry.domain.commerce.repository.PaymentRepository
import com.rio.rostry.core.model.Payment
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentDao: PaymentDao,
    private val orderDao: OrderDao,
    private val invoiceDao: InvoiceDao,
    private val refundDao: RefundDao,
    private val orderRepository: com.rio.rostry.domain.commerce.repository.OrderRepository
) : PaymentRepository {

    private fun PaymentEntity.toDomain(): Payment = Payment(
        paymentId = paymentId,
        orderId = orderId,
        userId = userId,
        method = method,
        amount = amount,
        status = PaymentStatus.fromString(status),
        providerRef = providerRef,
        upiUri = upiUri,
        idempotencyKey = idempotencyKey,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun buildUpiUri(vpa: String, name: String, amount: Double, note: String?): String {
        val sb = StringBuilder("upi://pay?pa=$vpa&pn=$name&am=$amount&cu=INR")
        if (!note.isNullOrBlank()) sb.append("&tn=$note")
        return sb.toString()
    }

    override fun observePaymentsByOrder(orderId: String): Flow<List<Payment>> =
        paymentDao.observeByOrder(orderId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun initiateUpiPayment(
        orderId: String, userId: String, amount: Double, vpa: String, name: String, note: String?
    ): Result<Payment> {
        val idempotencyKey = "UPI-$orderId-$amount"
        return try {
            require(amount > 0) { "Amount must be positive" }
            val existing = paymentDao.findByIdempotencyKey(idempotencyKey)
            if (existing != null) return Result.Success(existing.toDomain())
            val uri = buildUpiUri(vpa = vpa, name = name, amount = amount, note = note)
            val now = System.currentTimeMillis()
            val entity = PaymentEntity(
                paymentId = java.util.UUID.randomUUID().toString(),
                orderId = orderId,
                userId = userId,
                method = "UPI",
                amount = amount,
                status = PaymentStatus.PENDING.toStoredString(),
                providerRef = null,
                upiUri = uri,
                idempotencyKey = idempotencyKey,
                createdAt = now,
                updatedAt = now
            )
            paymentDao.insert(entity)
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun codReservation(orderId: String, userId: String, amount: Double): Result<Payment> {
        require(amount > 0) { "Amount must be positive" }
        return try {
            val idempotencyKey = "COD-$orderId"
            val existing = paymentDao.findByIdempotencyKey(idempotencyKey)
            if (existing != null) return Result.Success(existing.toDomain())
            val now = System.currentTimeMillis()
            val entity = PaymentEntity(
                paymentId = java.util.UUID.randomUUID().toString(),
                orderId = orderId,
                userId = userId,
                method = "COD",
                amount = amount,
                status = PaymentStatus.PENDING.toStoredString(),
                providerRef = null,
                upiUri = null,
                idempotencyKey = idempotencyKey,
                createdAt = now,
                updatedAt = now
            )
            paymentDao.insert(entity)
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun markPaymentResult(idempotencyKey: String, success: Boolean, providerRef: String?): Result<Unit> {
        return try {
            val current = paymentDao.findByIdempotencyKey(idempotencyKey) ?: return Result.Error(Exception("Payment not found"))
            val now = System.currentTimeMillis()
            val newStatus = if (success) PaymentStatus.SUCCESS else PaymentStatus.FAILED
            val updated = current.copy(status = newStatus.toStoredString(), providerRef = providerRef, updatedAt = now)
            paymentDao.update(updated)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun refundPayment(paymentId: String, reason: String?): Result<Unit> {
        return try {
            val current = paymentDao.findById(paymentId) ?: return Result.Error(Exception("Payment not found"))
            val order = orderDao.findById(current.orderId) ?: return Result.Error(Exception("Order not found"))
            val invoice = invoiceDao.findByOrder(order.orderId) ?: return Result.Error(Exception("Invoice not found"))

            val refundedSoFar = refundDao.totalRefundedForPayment(paymentId)
            val refundableRemaining = (invoice.total - refundedSoFar).coerceAtLeast(0.0)
            if (refundableRemaining <= 0.0) return Result.Success(Unit)

            val statuses = listOf("PLACED", "CONFIRMED", "PROCESSING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED")
            val idx = statuses.indexOf(order.status)
            val outIdx = statuses.indexOf("OUT_FOR_DELIVERY")

            val logisticsFee = 50.0
            val refundAmount = if (idx >= 0 && outIdx >= 0 && idx >= outIdx) {
                (refundableRemaining - logisticsFee).coerceAtLeast(0.0)
            } else {
                refundableRemaining
            }

            if (refundAmount <= 0.0) return Result.Success(Unit)

            val refund = RefundEntity(
                refundId = java.util.UUID.randomUUID().toString(),
                paymentId = paymentId,
                orderId = order.orderId,
                amount = refundAmount,
                reason = reason,
                createdAt = System.currentTimeMillis()
            )
            refundDao.insert(refund)

            val now = System.currentTimeMillis()
            val totalAfter = refundDao.totalRefundedForPayment(paymentId)
            val fullyRefunded = totalAfter >= invoice.total - 0.01
            val updatedPayment = current.copy(
                status = if (fullyRefunded) PaymentStatus.REFUNDED.toStoredString() else current.status,
                updatedAt = now
            )
            paymentDao.update(updatedPayment)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

