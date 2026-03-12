package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result

/** Domain interface for predicting growth trajectories based on historical weights. */
interface GrowthPredictionService {
    suspend fun predictGrowthTrajectory(weights: List<Int>, breed: String): List<Map<String, Any>>
    suspend fun calculateExpectedWeight(ageDays: Int, breed: String): Result<Int>
}
