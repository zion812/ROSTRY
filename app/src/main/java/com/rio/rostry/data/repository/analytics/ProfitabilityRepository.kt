package com.rio.rostry.data.repository.analytics

import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProfitabilityRepository {
    
    /**
     * getProfitabilitySummary
     * Calculates total revenue, total expenses, and net profit for a given period.
     */
    suspend fun getProfitabilitySummary(startDate: Long, endDate: Long): Resource<ProfitabilitySummary>

    /**
     * getAssetROI
     * Calculates the ROI for a specific asset (Revenue from Sales vs Expenses linked to Asset).
     */
    suspend fun getAssetROI(assetId: String): Resource<AssetROI>

    /**
     * getMonthlyTrends
     * Returns a list of monthly profit data points for charting.
     */
    suspend fun getMonthlyTrends(months: Int = 6): Resource<List<MonthlyProfit>>

    /**
     * getExpenseBreakdown
     * Returns total expenses grouped by category.
     */
    suspend fun getExpenseBreakdown(startDate: Long, endDate: Long): Resource<Map<String, Double>>

    /**
     * getProfitableBreeds
     * Returns ROI aggregated by breed.
     */
    suspend fun getProfitableBreeds(startDate: Long, endDate: Long): Resource<List<BreedROI>>
}

data class BreedROI(
    val breedName: String,
    val revenue: Double,
    val expenses: Double,
    val netProfit: Double,
    val roiPercent: Double,
    val assetCount: Int
)

data class ProfitabilitySummary(
    val totalRevenue: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val profitMarginPercent: Double
)

data class AssetROI(
    val assetId: String,
    val assetName: String,
    val revenue: Double,
    val expenses: Double,
    val netProfit: Double,
    val roiPercent: Double
)

data class MonthlyProfit(
    val monthLabel: String, // e.g., "Jan 2025" or "2025-01"
    val yearMonth: Long, // timestamp or int representation
    val revenue: Double,
    val expenses: Double
)
