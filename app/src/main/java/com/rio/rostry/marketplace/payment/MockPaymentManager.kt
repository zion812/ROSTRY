package com.rio.rostry.marketplace.payment

import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.data.repository.PaymentRepository
import com.rio.rostry.marketplace.model.PaymentType
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Mock payment manager for demo/testing in field conditions.
 * Simulates success/failure, generates mock transaction IDs, and handles COD.
 */
class MockPaymentManager @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    data class MockResult(
        val success: Boolean,
        val transactionId: String?,
        val message: String
    )

    var forceNextFailure: Boolean = false
    var artificialDelayMs: Long = 1200L

    suspend fun process(
        orderId: String,
        userId: String,
        amount: Double,
        type: PaymentType,
        metadata: Map<String, String?> = emptyMap()
    ): Resource<MockResult> {
        // Simulate network/gateway latency
        if (artificialDelayMs > 0) delay(artificialDelayMs)

        return when (type) {
            PaymentType.COD -> handleCod(orderId, userId, amount)
            PaymentType.FIXED_PRICE -> handleCardWalletDemo(orderId, userId, amount)
            PaymentType.AUCTION_BID -> handleAuctionBidDemo(orderId, userId, amount)
            PaymentType.ADVANCE_PAYMENT -> handleAdvancePayment(orderId, userId, amount)
        }
    }

    private suspend fun handleCod(orderId: String, userId: String, amount: Double): Resource<MockResult> {
        val reservation = paymentRepository.codReservation(orderId, userId, amount)
        return when (reservation) {
            is Resource.Success -> {
                val payment = reservation.data
                if (payment == null) {
                    Resource.Error("COD reservation missing payment data")
                } else {
                    Resource.Success(
                        MockResult(
                            success = true,
                            transactionId = "COD-${payment.paymentId.takeLast(6)}",
                            message = "COD reservation created"
                        )
                    )
                }
            }
            is Resource.Error -> Resource.Error(reservation.message ?: "COD failed")
            is Resource.Loading -> Resource.Error("Unexpected loading state")
        }
    }

    private suspend fun handleCardWalletDemo(orderId: String, userId: String, amount: Double): Resource<MockResult> {
        val idempotencyKey = "CARD-$orderId-$amount"
        val start = paymentRepository.cardWalletDemo(orderId, userId, amount, idempotencyKey)
        return when (start) {
            is Resource.Success -> finalizeMock(idempotencyKey)
            is Resource.Error -> Resource.Error(start.message ?: "Card demo failed")
            is Resource.Loading -> Resource.Error("Unexpected loading state")
        }
    }

    private suspend fun handleAuctionBidDemo(orderId: String, userId: String, amount: Double): Resource<MockResult> {
        // For demo, we treat winning bid payment same path as card demo
        val idempotencyKey = "BID-$orderId-$amount"
        val start = paymentRepository.cardWalletDemo(orderId, userId, amount, idempotencyKey)
        return when (start) {
            is Resource.Success -> finalizeMock(idempotencyKey)
            is Resource.Error -> Resource.Error(start.message ?: "Auction payment failed")
            is Resource.Loading -> Resource.Error("Unexpected loading state")
        }
    }

    private suspend fun handleAdvancePayment(orderId: String, userId: String, amount: Double): Resource<MockResult> {
        // For demo: partial 30% advance using the same demo pipeline
        val advance = (amount * 0.3).coerceAtLeast(1.0)
        val idempotencyKey = "ADV-$orderId-$advance"
        val start = paymentRepository.cardWalletDemo(orderId, userId, advance, idempotencyKey)
        return when (start) {
            is Resource.Success -> finalizeMock(idempotencyKey)
            is Resource.Error -> Resource.Error(start.message ?: "Advance payment failed")
            is Resource.Loading -> Resource.Error("Unexpected loading state")
        }
    }

    private suspend fun finalizeMock(idempotencyKey: String): Resource<MockResult> {
        val success = !forceNextFailure
        val providerRef = if (success) mockTxnId() else null
        val mark = paymentRepository.markPaymentResult(idempotencyKey, success, providerRef)
        // reset failure flag only for one attempt
        forceNextFailure = false
        return when (mark) {
            is Resource.Success -> Resource.Success(
                MockResult(success = success, transactionId = providerRef, message = if (success) "Payment success" else "Payment failed")
            )
            is Resource.Error -> Resource.Error(mark.message ?: "Finalize failed")
            is Resource.Loading -> Resource.Error("Unexpected loading state")
        }
    }

    private fun mockTxnId(): String = buildString {
        append("TXN")
        append("-")
        append(System.currentTimeMillis().toString().takeLast(8))
        append("-")
        append((100000..999999).random())
    }
}
