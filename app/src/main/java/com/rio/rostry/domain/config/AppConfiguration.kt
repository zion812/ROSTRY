package com.rio.rostry.domain.config

/**
 * Main application configuration containing all externalized settings.
 */
data class AppConfiguration(
    val security: SecurityConfig,
    val thresholds: ThresholdConfig,
    val timeouts: TimeoutConfig,
    val features: FeatureConfig
)

/**
 * Security-related configuration including admin identifiers and moderation settings.
 */
data class SecurityConfig(
    val adminIdentifiers: List<String>,
    val moderationBlocklist: List<String>,
    val allowedFileTypes: List<String>
)

/**
 * Threshold configuration for quotas, limits, and capacity settings.
 */
data class ThresholdConfig(
    val storageQuotaMB: Int,
    val maxBatchSize: Int,
    val circuitBreakerFailureRate: Double,
    val hubCapacity: Int,
    val deliveryRadiusKm: Double
)

/**
 * Timeout configuration for network requests and circuit breakers.
 */
data class TimeoutConfig(
    val networkRequestSeconds: Int,
    val circuitBreakerOpenSeconds: Int,
    val retryDelaysSeconds: List<Int>
)

/**
 * Feature flag configuration for enabling/disabling features.
 */
data class FeatureConfig(
    val enableRecommendations: Boolean,
    val enableDisputes: Boolean,
    val enableBreedingCompatibility: Boolean
)
