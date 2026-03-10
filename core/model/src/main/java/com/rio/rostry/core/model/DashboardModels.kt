package com.rio.rostry.core.model

data class GeneralDashboard(
    val totalOrders: Int,
    val totalSpend: Double,
    val favoriteProducts: List<String>, // placeholder
    val recentEngagement: Int,
    val suggestions: List<String> = emptyList(),
)

data class FarmAnalyticsDashboard(
    val revenue: Double,
    val orders: Int,
    val productViews: Int,
    val engagementScore: Double,
    val suggestions: List<String> = emptyList(),
)

data class EnthusiastAnalyticsDashboard(
    val breedingSuccessRate: Double,
    val transfers: Int,
    val engagementScore: Double,
    val suggestions: List<String> = emptyList(),
)
