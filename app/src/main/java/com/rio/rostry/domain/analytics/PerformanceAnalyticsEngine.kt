package com.rio.rostry.domain.analytics

import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.domain.repository.EnhancedDailyLogRepository
import javax.inject.Inject

class PerformanceAnalyticsEngine @Inject constructor(
    private val assetRepository: FarmAssetRepository,
    private val dailyLogRepository: EnhancedDailyLogRepository
) {
    /**
     * Calculates critical performance metrics like FCR across an individual asset's lifetime
     */
    suspend fun computeAssetLifetimeFcr(assetId: String): Double {
        val logs = dailyLogRepository.getDailyLogsForAsset(assetId)
        if (logs.isEmpty()) return 0.0
        
        var totalFeedIntake = 0.0
        var earliestWeight = logs.minByOrNull { it.logDate }?.weightGrams ?: 0.0
        var latestWeight = logs.maxByOrNull { it.logDate }?.weightGrams ?: 0.0
        
        logs.forEach { log ->
            totalFeedIntake += (log.feedKg ?: 0.0) * 1000 // Convert kg to grams for FCR
        }
        
        val weightGain = latestWeight - earliestWeight
        if (weightGain <= 0) return 0.0
        
        return totalFeedIntake / weightGain
    }
}
