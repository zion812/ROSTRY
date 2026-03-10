package com.rio.rostry.core.model

import com.rio.rostry.domain.model.PaymentStatus

/**
 * Domain model representing a payment in the ROSTRY system.
 */
data class Payment(
    val paymentId: String,
    val orderId: String,
    val userId: String,
    val method: String,
    val amount: Double,
    val currency: String = "INR",
    val status: PaymentStatus,
    val providerRef: String? = null,
    val upiUri: String? = null,
    val idempotencyKey: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
