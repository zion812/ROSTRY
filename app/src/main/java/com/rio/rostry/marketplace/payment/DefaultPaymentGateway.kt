package com.rio.rostry.marketplace.payment

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Default simple PaymentGateway implementation used for development/testing.
 * Simulates success paths and exposes a basic transaction status stream.
 */
class DefaultPaymentGateway : PaymentGateway {
    override suspend fun initialize(): Result<Unit> = Result.success(Unit)

    override suspend fun processPayment(request: PaymentRequest): Result<PaymentResult> {
        // Simulate network/payment processing latency
        delay(150)
        val txId = "tx_${System.currentTimeMillis()}"
        return Result.success(
            PaymentResult(
                transactionId = txId,
                status = PaymentStatus.SUCCEEDED,
                provider = "default",
                message = "Payment processed successfully"
            )
        )
    }

    override suspend fun refund(refundRequest: RefundRequest): Result<RefundResult> {
        delay(100)
        return Result.success(
            RefundResult(
                refundId = "rf_${System.currentTimeMillis()}",
                status = RefundStatus.SUCCEEDED,
                message = "Refund issued"
            )
        )
    }

    override fun observeTransactionStatus(transactionId: String): Flow<PaymentStatus> = flow {
        emit(PaymentStatus.INITIATED)
        delay(100)
        emit(PaymentStatus.SUCCEEDED)
    }
}
