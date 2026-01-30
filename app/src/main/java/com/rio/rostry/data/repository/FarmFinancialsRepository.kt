package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.FarmActivityLogDao
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class for Cost-Per-Bird Analysis
 */
data class CostPerBirdAnalysis(
    val totalFeedCost: Double,
    val totalVaccineCost: Double,
    val totalMedicationCost: Double,
    val totalMortalityImpact: Double,
    val totalOtherExpenses: Double,
    val totalCost: Double,
    val quantity: Double,
    val costPerBird: Double,
    val productionValue: Double = 0.0,
    val profitMargin: Double = 0.0,
    val profitabilityRating: ProfitabilityRating = ProfitabilityRating.BREAK_EVEN
)

enum class ProfitabilityRating {
    PROFITABLE,    // Green
    BREAK_EVEN,    // Yellow
    LOSS           // Red
}

/**
 * Data class for FCR (Feed Conversion Ratio) Analysis
 */
data class FCRAnalysis(
    val totalFeedKg: Double,
    val initialWeightKg: Double,
    val finalWeightKg: Double,
    val weightGainKg: Double,
    val fcr: Double,
    val rating: FCRRating,
    val ratingDescription: String,
    val industryBenchmark: Double = 2.0,
    val percentageVsBenchmark: Double = 0.0
)

enum class FCRRating {
    EXCELLENT,  // < 1.8
    GOOD,       // 1.8 - 2.2
    AVERAGE,    // 2.2 - 2.8
    POOR        // > 2.8
}

/**
 * Repository for financial calculations including cost-per-bird and FCR analysis.
 */
@Singleton
class FarmFinancialsRepository @Inject constructor(
    private val farmAssetDao: FarmAssetDao,
    private val farmActivityLogDao: FarmActivityLogDao,
    private val dailyLogDao: DailyLogDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val mortalityRecordDao: MortalityRecordDao
) {
    
    /**
     * Get comprehensive cost-per-bird analysis for a specific asset.
     */
    fun getCostPerBird(assetId: String): Flow<Resource<CostPerBirdAnalysis>> = flow {
        emit(Resource.Loading())
        try {
            // Get quantity
            val quantity = farmAssetDao.getCurrentQuantity(assetId) ?: 1.0
            
            // Get feed expenses
            val feedCost = farmActivityLogDao.getTotalFeedExpensesForAsset(assetId)
            
            // Get medication expenses from activity logs
            val medicationCostFromLogs = farmActivityLogDao.getTotalMedicationExpensesForAsset(assetId)
            
            // Get vaccination costs
            val vaccineCost = vaccinationRecordDao.getTotalVaccinationCostsForAsset(assetId)
            
            // Get mortality financial impact
            val mortalityImpact = mortalityRecordDao.getTotalMortalityImpactForAsset(assetId)
            
            // Get other expenses
            val otherExpenses = farmActivityLogDao.getTotalOtherExpensesForAsset(assetId)
            
            // Calculate total cost
            val totalCost = feedCost + vaccineCost + medicationCostFromLogs + mortalityImpact + otherExpenses
            
            // Calculate cost per bird
            val costPerBird = if (quantity > 0) totalCost / quantity else 0.0
            
            // Determine profitability rating
            // Assume break-even threshold is around ₹150/bird for layers
            val profitabilityRating = when {
                costPerBird < 120 -> ProfitabilityRating.PROFITABLE
                costPerBird < 180 -> ProfitabilityRating.BREAK_EVEN
                else -> ProfitabilityRating.LOSS
            }
            
            val analysis = CostPerBirdAnalysis(
                totalFeedCost = feedCost,
                totalVaccineCost = vaccineCost,
                totalMedicationCost = medicationCostFromLogs,
                totalMortalityImpact = mortalityImpact,
                totalOtherExpenses = otherExpenses,
                totalCost = totalCost,
                quantity = quantity,
                costPerBird = costPerBird,
                profitabilityRating = profitabilityRating
            )
            
            emit(Resource.Success(analysis))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to calculate cost per bird"))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get cull candidates based on cost threshold or production metrics.
     * Returns assets where cost-per-bird exceeds threshold or health status is poor.
     */
    fun getCullCandidates(farmerId: String): Flow<List<FarmAssetEntity>> {
        return farmAssetDao.getCullCandidates(farmerId)
    }
    
    /**
     * Get assets ready to lay (females aged 18-22 weeks).
     */
    fun getReadyToLayBirds(farmerId: String): Flow<List<FarmAssetEntity>> {
        return farmAssetDao.getReadyToLayBirds(farmerId)
    }
    
    /**
     * Get assets with vaccination due soon.
     */
    fun getVaccinationDueSoon(farmerId: String, daysAhead: Int = 3): Flow<List<FarmAssetEntity>> {
        val now = System.currentTimeMillis()
        val future = now + (daysAhead * 24 * 60 * 60 * 1000L)
        return farmAssetDao.getVaccinationDueSoon(farmerId, now, future)
    }
    
    /**
     * Calculate FCR (Feed Conversion Ratio) for a specific asset within a period.
     * FCR = Total Feed (kg) / Weight Gain (kg)
     * 
     * @param assetId The asset ID to calculate FCR for
     * @param periodDays Number of days to look back (7, 14, 30, 90)
     */
    fun calculateFCR(assetId: String, periodDays: Int = 30): Flow<Resource<FCRAnalysis>> = flow {
        emit(Resource.Loading())
        try {
            val endDate = System.currentTimeMillis()
            val startDate = endDate - (periodDays * 24 * 60 * 60 * 1000L)
            
            // Get total feed consumed
            val totalFeedKg = dailyLogDao.getTotalFeedForProduct(assetId, startDate, endDate)
            
            // Get initial and final weights
            val initialWeightGrams = dailyLogDao.getInitialWeightForProduct(assetId, startDate)
            val finalWeightGrams = dailyLogDao.getFinalWeightForProduct(assetId, endDate)
            
            if (initialWeightGrams == null || finalWeightGrams == null) {
                emit(Resource.Error("Insufficient weight data for FCR calculation. Please log weight measurements."))
                return@flow
            }
            
            val initialWeightKg = initialWeightGrams / 1000.0
            val finalWeightKg = finalWeightGrams / 1000.0
            val weightGainKg = finalWeightKg - initialWeightKg
            
            if (weightGainKg <= 0) {
                emit(Resource.Error("No weight gain detected in the selected period."))
                return@flow
            }
            
            // Calculate FCR
            val fcr = if (weightGainKg > 0) totalFeedKg / weightGainKg else 0.0
            
            // Determine rating
            val (rating, description) = when {
                fcr < 1.8 -> FCRRating.EXCELLENT to "Excellent - Better than industry standards"
                fcr < 2.2 -> FCRRating.GOOD to "Good - Meeting industry benchmarks"
                fcr < 2.8 -> FCRRating.AVERAGE to "Average - Room for improvement"
                else -> FCRRating.POOR to "Poor - Consider adjusting feed quality or management"
            }
            
            val industryBenchmark = 2.0
            val percentageVsBenchmark = ((industryBenchmark - fcr) / industryBenchmark) * 100
            
            val analysis = FCRAnalysis(
                totalFeedKg = totalFeedKg,
                initialWeightKg = initialWeightKg,
                finalWeightKg = finalWeightKg,
                weightGainKg = weightGainKg,
                fcr = fcr,
                rating = rating,
                ratingDescription = description,
                industryBenchmark = industryBenchmark,
                percentageVsBenchmark = percentageVsBenchmark
            )
            
            emit(Resource.Success(analysis))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to calculate FCR"))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get filter counts for quick filters.
     */
    fun getReadyToLayCount(farmerId: String): Flow<Int> {
        return farmAssetDao.countReadyToLayBirds(farmerId)
    }
    
    fun getCullCandidatesCount(farmerId: String): Flow<Int> {
        return farmAssetDao.countCullCandidates(farmerId)
    }
    
    fun getVaccinationDueSoonCount(farmerId: String, daysAhead: Int = 3): Flow<Int> {
        val now = System.currentTimeMillis()
        val future = now + (daysAhead * 24 * 60 * 60 * 1000L)
        return farmAssetDao.countVaccinationDueSoon(farmerId, now, future)
    }
    
    /**
     * Data class for overall farm cost analysis (aggregate across all assets).
     */
    data class OverallFarmCostAnalysis(
        val feedCost: Double,
        val vaccinationCost: Double,
        val medicationCost: Double,
        val mortalityCost: Double,
        val otherCost: Double,
        val totalCost: Double,
        val assetCount: Int
    )
    
    /**
     * Get overall farm cost analysis aggregated across all assets for a farmer.
     * This is used by the Farmer Dashboard for profitability metrics.
     */
    /**
     * Get overall farm cost analysis aggregated across all assets for a farmer.
     * This is used by the Farmer Dashboard for profitability metrics.
     * @param range Optional date range (start, end)
     */
    fun getOverallFarmCostAnalysis(farmerId: String, range: Pair<Long, Long>? = null): Flow<Resource<OverallFarmCostAnalysis>> = flow {
        emit(Resource.Loading())
        try {
            // Get aggregate costs across all farmer's assets
            val feedCost = if (range != null) {
                farmActivityLogDao.getTotalFeedExpensesByFarmerBetween(farmerId, range.first, range.second)
            } else {
                farmActivityLogDao.getTotalFeedExpensesByFarmer(farmerId)
            }
            
            val medicationCost = if (range != null) {
                farmActivityLogDao.getTotalMedicationExpensesByFarmerBetween(farmerId, range.first, range.second)
            } else {
                farmActivityLogDao.getTotalMedicationExpensesByFarmer(farmerId)
            }
            
            val vaccinationCost = if (range != null) {
                vaccinationRecordDao.getTotalVaccinationCostsByFarmerBetween(farmerId, range.first, range.second)
            } else {
                vaccinationRecordDao.getTotalVaccinationCostsByFarmer(farmerId)
            }
            
            val mortalityCost = if (range != null) {
                mortalityRecordDao.getTotalMortalityImpactByFarmerBetween(farmerId, range.first, range.second)
            } else {
                mortalityRecordDao.getTotalMortalityImpactByFarmer(farmerId)
            }
            
            val otherCost = if (range != null) {
                farmActivityLogDao.getTotalOtherExpensesByFarmerBetween(farmerId, range.first, range.second)
            } else {
                farmActivityLogDao.getTotalOtherExpensesByFarmer(farmerId)
            }
            
            // Count active assets
            val assetCount = farmAssetDao.countActiveByFarmer(farmerId)
            
            val totalCost = feedCost + medicationCost + vaccinationCost + mortalityCost + otherCost
            
            emit(Resource.Success(
                OverallFarmCostAnalysis(
                    feedCost = feedCost,
                    vaccinationCost = vaccinationCost,
                    medicationCost = medicationCost,
                    mortalityCost = mortalityCost,
                    otherCost = otherCost,
                    totalCost = totalCost,
                    assetCount = assetCount
                )
            ))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to calculate farm costs"))
        }
    }.flowOn(Dispatchers.IO)
}

