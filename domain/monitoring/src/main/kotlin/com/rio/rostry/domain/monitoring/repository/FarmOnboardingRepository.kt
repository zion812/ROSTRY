package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.utils.Resource
import com.rio.rostry.core.model.OnboardingActivity
import com.rio.rostry.core.model.OnboardingStats
import kotlinx.coroutines.flow.Flow

interface FarmOnboardingRepository {
    suspend fun addProductToFarmMonitoring(productId: String, farmerId: String, healthStatus: String = "OK"): Resource<List<String>>
    fun observeRecentOnboardingActivity(farmerId: String, days: Int = 7): Flow<List<OnboardingActivity>>
    suspend fun getOnboardingStats(farmerId: String): OnboardingStats
}
