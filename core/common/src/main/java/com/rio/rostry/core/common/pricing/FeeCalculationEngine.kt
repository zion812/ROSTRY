package com.rio.rostry.core.common.pricing

import com.rio.rostry.core.common.constants.BusinessConstants
import com.rio.rostry.domain.model.UserType

data class FeeBreakdown(
    val subtotalCents: Long,
    val platformFeeCents: Long,
    val paymentProcessingFeeCents: Long,
    val deliveryFeeCents: Long,
    val discountCents: Long,
    val totalCents: Long
)

object FeeCalculationEngine {
    fun calculate(
        subtotalCents: Long,
        userType: UserType,
        deliveryRequired: Boolean,
        promotionPercent: Int = 0,
        bulkQty: Int = 1
    ): FeeBreakdown {
        val platformRate = when (userType) {
            UserType.FARMER -> 0.02
            UserType.GENERAL -> 0.05
            else -> 0.04
        }
        val platformFee = (subtotalCents * platformRate).toLong()
        val processingFee = (subtotalCents * 0.015).toLong()
        
        // Use centralized constant for delivery fee
        val deliveryFee = if (deliveryRequired) BusinessConstants.DELIVERY_FEE_FLAT_PAISE else 0L
        
        val bulkDiscount = if (bulkQty >= 10) (subtotalCents * 0.03).toLong() else 0L
        val promoDiscount = (subtotalCents * (promotionPercent / 100.0)).toLong()
        val discount = bulkDiscount + promoDiscount
        val total = (subtotalCents + platformFee + processingFee + deliveryFee - discount).coerceAtLeast(0)
        
        return FeeBreakdown(
            subtotalCents = subtotalCents,
            platformFeeCents = platformFee,
            paymentProcessingFeeCents = processingFee,
            deliveryFeeCents = deliveryFee,
            discountCents = discount,
            totalCents = total
        )
    }
}
