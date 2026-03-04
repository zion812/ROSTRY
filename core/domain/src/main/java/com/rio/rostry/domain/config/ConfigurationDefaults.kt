package com.rio.rostry.domain.config

/**
 * Secure default configuration values used as fallback when remote config is unavailable.
 */
object ConfigurationDefaults {
    
    val DEFAULT_CONFIGURATION = AppConfiguration(
        security = SecurityConfig(
            adminIdentifiers = emptyList(), // No default admins for security
            moderationBlocklist = listOf(
                "spam",
                "scam",
                "fraud",
                "inappropriate"
            ),
            allowedFileTypes = listOf(
                "image/jpeg",
                "image/jpg",
                "image/png",
                "image/webp",
                "video/mp4"
            )
        ),
        thresholds = ThresholdConfig(
            storageQuotaMB = 500,
            maxBatchSize = 100,
            circuitBreakerFailureRate = 0.5,
            hubCapacity = 1000,
            deliveryRadiusKm = 100.0
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
