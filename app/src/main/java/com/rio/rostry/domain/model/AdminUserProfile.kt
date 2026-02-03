package com.rio.rostry.domain.model

import com.rio.rostry.data.database.entity.UserEntity
import java.util.Date

/**
 * Aggregated user profile for Admin View.
 * Contains user entity, recent orders, product stats, etc.
 */
data class AdminUserProfile(
    val user: UserEntity,
    val totalOrdersCount: Int = 0,
    val totalSpend: Double = 0.0,
    val activeListingsCount: Int = 0,
    val lastActive: Date? = null,
    val recentOrders: List<AdminOrderSummary> = emptyList(),
    val riskScore: Int = 0 // 0-100, calculated based on disputes/reports
)

data class AdminOrderSummary(
    val orderId: String,
    val date: Date,
    val amount: Double,
    val status: String,
    val itemCount: Int
)
