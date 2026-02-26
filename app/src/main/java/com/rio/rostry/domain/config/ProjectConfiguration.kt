package com.rio.rostry.domain.config

/**
 * Single source of truth for business logic constants and thresholds.
 * 
 * This object centralizes all hardcoded values that govern the application's
 * behavior, limits, and security policies.
 * 
 * Requirement 18: Configuration Value Centralization
 */
object ProjectConfiguration {
    
    // --- Authentication & Session ---
    const val SESSION_EXPIRY_CHECK_INTERVAL_MS = 60_000L // 1 minute
    const val SESSION_TIMEOUT_MS = 3_600_000L // 1 hour
    const val TOKEN_REFRESH_THRESHOLD_MS = 300_000L // 5 minutes before expiry
    
    // --- Free Tier Limits (Spark Plan) ---
    const val FREE_TIER_BIRD_LIMIT = 50
    const val FREE_TIER_MAX_ASSETS = 10
    const val FREE_TIER_MEDIA_STORAGE_MB = 100
    
    // --- Sync & Network ---
    const val MAX_SYNC_RETRIES = 3
    const val SYNC_INITIAL_BACKOFF_MS = 2_000L
    const val NETWORK_TIMEOUT_SECONDS = 30L
    
    // --- Marketplace & Logistics ---
    const val MAX_HUB_DISTANCE_KM = 100.0
    const val RECOMMENDATION_MIN_COUNT = 5
    const val ORDER_BATCH_SIZE = 100
    
    // --- Media Processing ---
    const val THUMBNAIL_SIZE_PX = 300
    const val IMAGE_COMPRESSION_QUALITY = 85
    const val MAX_IMAGE_SIZE_MB = 5
    
    // --- Genetics ---
    const val INBREEDING_WARNING_THRESHOLD = 0.125
    
    // --- Performance ---
    const val ANALYTICS_REFRESH_INTERVAL_HOURS = 24
    const val CACHE_EXPIRY_MS = 86_400_000L // 24 hours
    
    // --- Security ---
    val ADMIN_EMAILS = listOf("admin@rostry.com", "system@rostry.com")
    const val RATE_LIMIT_OTP_WINDOW_MS = 60_000L
}
