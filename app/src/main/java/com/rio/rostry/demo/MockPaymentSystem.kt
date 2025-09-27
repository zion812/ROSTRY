package com.rio.rostry.demo

import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.dao.PaymentDao
import com.rio.rostry.data.database.entity.InvoiceEntity
import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockPaymentSystem @Inject constructor(
    private val paymentDao: PaymentDao,
    private val invoiceDao: InvoiceDao
) {
    enum class Method { UPI, CARD, COD }

    data class Scenario(
        val forceFailure: Boolean = false,
        val delayMs: Long = 0L,
        val providerPrefix: String = "MOCK"
    )

    suspend fun initiate(
        orderId: String,
        userId: String,
        amount: Double,
        method: Method,
        idempotencyKey: String,
        scenario: Scenario = Scenario()
    ): Resource<PaymentEntity> {
        val existing = paymentDao.findByIdempotencyKey(idempotencyKey)
        if (existing != null) return Resource.Success(existing)
        val now = System.currentTimeMillis()
        val entity = PaymentEntity(
            paymentId = java.util.UUID.randomUUID().toString(),
            orderId = orderId,
            userId = userId,
            method = method.name,
            amount = amount,
            status = if (scenario.forceFailure) "FAILED" else "SUCCESS",
            providerRef = "${scenario.providerPrefix}-${method.name}-${orderId}",
            upiUri = null,
            idempotencyKey = idempotencyKey,
            createdAt = now,
            updatedAt = now
        )
        paymentDao.insert(entity)
        // Generate a simple invoice in demo
        val invoice = InvoiceEntity(
            invoiceId = java.util.UUID.randomUUID().toString(),
            orderId = orderId,
            subtotal = amount,
            gstPercent = 0.0,
            gstAmount = 0.0,
            total = amount,
            createdAt = now
        )
        runCatching { invoiceDao.insertInvoice(invoice) }
        return Resource.Success(entity)
    }
}
