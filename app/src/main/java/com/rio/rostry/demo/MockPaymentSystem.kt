package com.rio.rostry.demo

import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.dao.PaymentDao
import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockPaymentSystem @Inject constructor(
    private val paymentDao: PaymentDao,
    @Suppress("UNUSED_PARAMETER") private val invoiceDao: InvoiceDao,
) {
    enum class Method { UPI, COD, CARD }

    suspend fun initiate(
        orderId: String,
        userId: String,
        amount: Double,
        method: Method,
        idempotencyKey: String,
    ): Resource<PaymentEntity> {
        // Idempotency: if a payment with this key already exists, return it
        val existing = paymentDao.findByIdempotencyKey(idempotencyKey)
        if (existing != null) return Resource.Success(existing)

        val now = System.currentTimeMillis()
        val entity = PaymentEntity(
            paymentId = java.util.UUID.randomUUID().toString(),
            orderId = orderId,
            userId = userId,
            method = when (method) {
                Method.UPI -> "UPI"
                Method.COD -> "COD"
                Method.CARD -> "CARD"
            },
            amount = amount,
            status = "PENDING", // Demo system can mark as PENDING; tests only verify idempotency
            providerRef = null,
            upiUri = null,
            idempotencyKey = idempotencyKey,
            createdAt = now,
            updatedAt = now,
        )
        paymentDao.insert(entity)
        return Resource.Success(entity)
    }
}
