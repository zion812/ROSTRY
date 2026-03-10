package com.rio.rostry.core.model

/**
 * Domain model representing farm financials.
 */
data class FarmFinancials(
    val farmerId: String,
    val totalRevenue: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netProfit: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)
