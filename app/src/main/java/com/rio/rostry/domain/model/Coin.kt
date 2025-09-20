package com.rio.rostry.domain.model

import java.util.Date

data class Coin(
    val id: String,
    val userId: String,
    val balance: Double,
    val totalEarned: Double,
    val totalSpent: Double,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)