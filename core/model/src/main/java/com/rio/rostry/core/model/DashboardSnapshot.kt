package com.rio.rostry.core.model

/**
 * Domain model for farmer dashboard statistics and KPIs.
 */
data class DashboardSnapshot(
    val snapshotId: String = "",
    val farmerId: String = "",
    val weekStartAt: Long = 0L,
    val weekEndAt: Long = 0L,
    val revenueInr: Double = 0.0,
    val ordersCount: Int = 0,
    val hatchSuccessRate: Double = 0.0,
    val mortalityRate: Double = 0.0,
    val deathsCount: Int = 0,
    val vaccinationCompletionRate: Double = 0.0,
    val growthRecordsCount: Int = 0,
    val quarantineActiveCount: Int = 0,
    val productsReadyToListCount: Int = 0,
    val avgFeedKg: Double? = null,
    val medicationUsageCount: Int? = null,
    val dailyLogComplianceRate: Double? = null,
    val actionSuggestions: String? = null,
    val transfersInitiatedCount: Int = 0,
    val transfersCompletedCount: Int = 0,
    val complianceScore: Double = 0.0,
    val onboardingCount: Int = 0,
    val dailyGoalsCompletedCount: Int = 0,
    val analyticsInsightsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
