package com.rio.rostry.core.model

/**
 * Summary of profitability metrics for a given period.
 */
data class ProfitabilitySummary(
    val totalRevenue: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val profitMarginPercent: Double
)

/**
 * Return on Investment (ROI) for a specific farm asset.
 */
data class AssetROI(
    val assetId: String,
    val assetName: String,
    val revenue: Double,
    val expenses: Double,
    val netProfit: Double,
    val roiPercent: Double
)

/**
 * Monthly profit data point for trend analysis.
 */
data class MonthlyProfit(
    val monthLabel: String,
    val yearMonth: Long,
    val revenue: Double,
    val expenses: Double
)

/**
 * ROI metrics aggregated by breed.
 */
data class BreedROI(
    val breedName: String,
    val revenue: Double,
    val expenses: Double,
    val netProfit: Double,
    val roiPercent: Double,
    val assetCount: Int
)
