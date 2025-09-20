package com.rio.rostry.domain.model

import java.util.Date

data class Transfer(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val orderId: String? = null,
    val amount: Double,
    val type: String, // payment, refund
    val status: String, // pending, completed, failed
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)