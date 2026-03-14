package com.rio.rostry.domain.admin.model

enum class MortalityRiskLevel {
    LOW,
    MODERATE,
    HIGH,
    CRITICAL
}

data class RegionalMortality(
    val regionName: String,
    val totalFarms: Int,
    val reportedDeaths24h: Int,
    val averageMortalityRate: Double,
    val riskLevel: MortalityRiskLevel
)

data class OutbreakAlert(
    val regionName: String,
    val affectedFarmsCount: Int,
    val detectedAt: Long,
    val severity: MortalityRiskLevel,
    val description: String
)
