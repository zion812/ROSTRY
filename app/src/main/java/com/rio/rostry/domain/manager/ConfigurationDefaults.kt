package com.rio.rostry.domain.manager

/**
 * Secure default configuration values.
 * Used as the last resort when both remote and cache are unavailable.
 */
object ConfigurationDefaults {
    val DEFAULT = AppConfiguration(
        security = SecurityConfig(
            adminIdentifiers = emptyList(),
            moderationBlocklist = emptyList(),
            allowedFileTypes = listOf("image/jpeg", "image/png", "image/webp", "video/mp4")
        ),
        thresholds = ThresholdConfig(
            storageQuotaMB = 500,
            maxBatchSize = 100,
            circuitBreakerFailureRate = 0.5,
            hubCapacity = 1000,
            deliveryRadiusKm = 50.0
        ),
        timeouts = TimeoutConfig(
            networkRequestSeconds = 30,
            circuitBreakerOpenSeconds = 30,
            retryDelaysSeconds = listOf(1, 2, 4)
        ),
        features = FeatureConfig(
            enableRecommendations = true,
            enableDisputes = true,
            enableBreedingCompatibility = true
        )
    )
}
