package com.rio.rostry.core.model

/**
 * Admin metrics model for admin dashboard.
 */
data class AdminMetrics(
    val totalUsers: Int = 0,
    val totalFarmers: Int = 0,
    val totalEnthusiasts: Int = 0,
    val totalPosts: Int = 0,
    val totalListings: Int = 0,
    val totalOrders: Int = 0,
    val pendingVerifications: Int = 0,
    val pendingModerations: Int = 0,
    val activeDisputes: Int = 0,
    val revenue: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)
