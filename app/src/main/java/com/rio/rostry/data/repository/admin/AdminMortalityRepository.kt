package com.rio.rostry.data.repository.admin

import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AdminMortalityRepository {
    
    // Aggregated stats per region (e.g., City or State)
    fun getRegionalMortalityStats(): Flow<Resource<List<RegionalMortality>>>

    // Detect potential outbreaks based on logic (e.g., mortality rate > threshold)
    fun getPotentialOutbreaks(): Flow<Resource<List<OutbreakAlert>>>
}

data class RegionalMortality(
    val regionName: String, // City or State
    val totalFarms: Int,
    val reportedDeaths24h: Int,
    val averageMortalityRate: Double, // Percentage
    val riskLevel: MortalityRiskLevel
)

data class OutbreakAlert(
    val regionName: String,
    val affectedFarmsCount: Int,
    val detectedAt: Long,
    val severity: MortalityRiskLevel,
    val description: String
)

enum class MortalityRiskLevel {
    LOW,
    MODERATE,
    HIGH,
    CRITICAL
}
