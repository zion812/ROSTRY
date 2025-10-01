package com.rio.rostry.marketplace.payment

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for payment gateways to support multiple providers with a consistent API.
 */
interface PaymentGateway {
    suspend fun initialize(): Result<Unit>
    suspend fun processPayment(request: PaymentRequest): Result<PaymentResult>
    suspend fun refund(refundRequest: RefundRequest): Result<RefundResult>
    fun observeTransactionStatus(transactionId: String): Flow<PaymentStatus>
}

data class PaymentRequest(
    val orderId: String,
    val amountCents: Long,
    val currency: String = "INR",
    val customerId: String,
    val paymentMethod: PaymentMethod,
    val allowCod: Boolean = false,
)

data class PaymentResult(
    val transactionId: String,
    val status: PaymentStatus,
    val provider: String,
    val message: String? = null,
)

data class RefundRequest(
    val transactionId: String,
    val amountCents: Long,
    val reason: String? = null,
)

data class RefundResult(
    val refundId: String,
    val status: RefundStatus,
    val message: String? = null,
)

enum class PaymentMethod { CARD, UPI, NETBANKING, COD }

enum class PaymentStatus { INITIATED, REQUIRES_ACTION, SUCCEEDED, FAILED, CANCELLED }

enum class RefundStatus { REQUESTED, PROCESSING, SUCCEEDED, FAILED }

/**
 * Basic pass-through adapter that can be used for mocks or a single provider.
 */
class PaymentGatewayAdapter(
    private val delegate: PaymentGateway
) : PaymentGateway by delegate
