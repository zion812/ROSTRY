package com.rio.rostry.core.model

/**
 * Domain model for financial transactions.
 * 
 * Represents a payment transaction associated with an order.
 */
data class Transaction(
    val transactionId: String,
    val orderId: String,
    val userId: String,
    val amount: Double,
    val currency: String = "INR",
    val status: String,
    val paymentMethod: String,
    val gatewayReference: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null
)
