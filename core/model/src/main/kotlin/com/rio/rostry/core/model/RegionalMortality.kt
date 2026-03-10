package com.rio.rostry.core.model

/**
 * Domain model for regional mortality statistics.
 */
data class RegionalMortality(
    val regionName: String,
    val totalFarms: Int,
    val reportedDeaths24h: Int,
    val averageMortalityRate: Double,
    val riskLevel: MortalityRiskLevel
)

/**
 * Domain model for outbreak alert.
 */
data class OutbreakAlert(
    val regionName: String,
    val affectedFarmsCount: Int,
    val detectedAt: Long,
    val severity: MortalityRiskLevel,
    val description: String
)

/**
 * Mortality risk level.
 */
enum class MortalityRiskLevel {
    LOW,
    MODERATE,
    HIGH,
    CRITICAL
}
