package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.ProductDao
import timber.log.Timber
import javax.inject.Inject

/**
 * Predicts market readiness based on target weight and current growth rate.
 */
class MarketReadyPredictionUseCase @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val productDao: ProductDao
) {
    data class PredictionResult(
        val currentWeight: Double,
        val projectedDaysToTarget: Int?,
        val isReady: Boolean,
        val confidence: String // HIGH, MEDIUM, LOW
    )

    suspend fun execute(productId: String, targetWeightGrams: Double): PredictionResult {
        val now = System.currentTimeMillis()
        val product = productDao.findById(productId) ?: return PredictionResult(0.0, null, false, "LOW")
        
        val latestWeight = dailyLogDao.getFinalWeightForProduct(productId, now) ?: 0.0
        if (latestWeight >= targetWeightGrams) {
            return PredictionResult(latestWeight, 0, true, "HIGH")
        }
        
        // Calculate growth rate over last 7 days
        val sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000)
        val weightStart = dailyLogDao.getInitialWeightForProduct(productId, sevenDaysAgo)
        
        if (weightStart == null || weightStart >= latestWeight) {
            return PredictionResult(latestWeight, null, false, "LOW")
        }
        
        val gainPerDay = (latestWeight - weightStart) / 7.0
        val remainingWeight = targetWeightGrams - latestWeight
        val daysToTarget = (remainingWeight / gainPerDay).toInt()
        
        return PredictionResult(
            currentWeight = latestWeight,
            projectedDaysToTarget = daysToTarget,
            isReady = false,
            confidence = if (gainPerDay > 0) "MEDIUM" else "LOW"
        )
    }
}
