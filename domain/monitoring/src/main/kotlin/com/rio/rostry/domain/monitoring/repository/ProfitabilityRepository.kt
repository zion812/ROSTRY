package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.ProfitabilitySummary
import com.rio.rostry.core.model.AssetROI
import com.rio.rostry.core.model.MonthlyProfit
import com.rio.rostry.core.model.BreedROI
import com.rio.rostry.core.common.Result

/**
 * Repository contract for profitability analytics.
 * 
 * Provides financial analytics including revenue, expenses, ROI calculations,
 * and profitability trends for farm operations.
 */
interface ProfitabilityRepository {
    
    /**
     * Calculate total revenue, expenses, and net profit for a given period.
     */
    suspend fun getProfitabilitySummary(startDate: Long, endDate: Long): Result<ProfitabilitySummary>

    /**
     * Calculate the ROI for a specific asset (revenue from sales vs expenses).
     */
    suspend fun getAssetROI(assetId: String): Result<AssetROI>

    /**
     * Get monthly profit data points for charting trends.
     */
    suspend fun getMonthlyTrends(months: Int = 6): Result<List<MonthlyProfit>>

    /**
     * Get total expenses grouped by category.
     */
    suspend fun getExpenseBreakdown(startDate: Long, endDate: Long): Result<Map<String, Double>>

    /**
     * Get ROI aggregated by breed.
     */
    suspend fun getProfitableBreeds(startDate: Long, endDate: Long): Result<List<BreedROI>>
}
