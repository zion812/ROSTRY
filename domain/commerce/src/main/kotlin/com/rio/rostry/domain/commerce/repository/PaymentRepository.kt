package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Payment
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for payment operations.
 */
interface PaymentRepository {
    /**
     * Observes payments for a specific order.
     */
    fun observePaymentsByOrder(orderId: String): Flow<List<Payment>>

    /**
     * Initiates a UPI payment.
     */
    suspend fun initiateUpiPayment(
        orderId: String, 
        userId: String, 
        amount: Double, 
        vpa: String, 
        name: String, 
        note: String? = null
    ): Result<Payment>

    /**
     * Reserves a Cash on Delivery (COD) payment.
     */
    suspend fun codReservation(
        orderId: String, 
        userId: String, 
        amount: Double
    ): Result<Payment>

    /**
     * Marks the result of a payment from a provider callback or local confirmation.
     */
    suspend fun markPaymentResult(
        idempotencyKey: String, 
        success: Boolean, 
        providerRef: String? = null
    ): Result<Unit>

    /**
     * Initiates a refund for a payment.
     */
    suspend fun refundPayment(
        paymentId: String, 
        reason: String? = null
    ): Result<Unit>
}
