package com.rio.rostry.marketplace.payment

import com.rio.rostry.marketplace.model.PaymentType
import com.rio.rostry.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * High-level demo payment flows to exercise the MockPaymentManager across methods.
 */
@Singleton
class DemoPaymentService @Inject constructor(
    private val mockPaymentManager: MockPaymentManager
) {
    suspend fun payFixedPrice(orderId: String, userId: String, amount: Double, failNext: Boolean = false): Resource<MockPaymentManager.MockResult> {
        mockPaymentManager.forceNextFailure = failNext
        return mockPaymentManager.process(orderId, userId, amount, PaymentType.FIXED_PRICE)
    }

    suspend fun payAuctionBid(orderId: String, userId: String, amount: Double, failNext: Boolean = false): Resource<MockPaymentManager.MockResult> {
        mockPaymentManager.forceNextFailure = failNext
        return mockPaymentManager.process(orderId, userId, amount, PaymentType.AUCTION_BID)
    }

    suspend fun payCashOnDelivery(orderId: String, userId: String, amount: Double): Resource<MockPaymentManager.MockResult> {
        // COD is reservation flow, success means reservation created
        return mockPaymentManager.process(orderId, userId, amount, PaymentType.COD)
    }

    suspend fun payAdvance(orderId: String, userId: String, totalAmount: Double, failNext: Boolean = false): Resource<MockPaymentManager.MockResult> {
        mockPaymentManager.forceNextFailure = failNext
        return mockPaymentManager.process(orderId, userId, totalAmount, PaymentType.ADVANCE_PAYMENT)
    }
}
