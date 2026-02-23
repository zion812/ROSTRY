package com.rio.rostry.domain.manager

/**
 * Top-level application configuration, loaded from Firebase Remote Config
 * with local cache fallback and secure defaults.
 */
data class AppConfiguration(
    val security: SecurityConfig = SecurityConfig(),
    val thresholds: ThresholdConfig = ThresholdConfig(),
    val timeouts: TimeoutConfig = TimeoutConfig(),
    val features: FeatureConfig = FeatureConfig()
)

data class SecurityConfig(
    val adminIdentifiers: List<String> = emptyList(),
    val moderationBlocklist: List<String> = emptyList(),
    val allowedFileTypes: List<String> = listOf("image/jpeg", "image/png", "video/mp4")
)

data class ThresholdConfig(
    val storageQuotaMB: Int = 500,
    val maxBatchSize: Int = 100,
    val circuitBreakerFailureRate: Double = 0.5,
    val hubCapacity: Int = 1000,
    val deliveryRadiusKm: Double = 50.0
)

data class TimeoutConfig(
    val networkRequestSeconds: Int = 30,
    val circuitBreakerOpenSeconds: Int = 30,
    val retryDelaysSeconds: List<Int> = listOf(1, 2, 4)
)

data class FeatureConfig(
    val enableRecommendations: Boolean = true,
    val enableDisputes: Boolean = true,
    val enableBreedingCompatibility: Boolean = true
)
