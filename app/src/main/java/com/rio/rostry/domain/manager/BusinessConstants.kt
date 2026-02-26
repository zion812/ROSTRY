package com.rio.rostry.domain.manager

/**
 * Centralized business constants for the ROSTRY application.
 * 
 * All magic numbers and threshold values that were previously
 * hardcoded throughout the codebase are consolidated here for
 * maintainability and configurability.
 * 
 * For runtime-configurable values, use ConfigurationManager.get().
 * These constants serve as compile-time defaults and fallbacks.
 * 
 * Requirements: 18.1-18.8
 */
object BusinessConstants {

    // ─── Media Upload ───────────────────────────────────────────────────
    const val MAX_IMAGE_SIZE_MB = 10
    const val MAX_VIDEO_SIZE_MB = 100
    const val THUMBNAIL_WIDTH = 200
    const val THUMBNAIL_HEIGHT = 200
    const val IMAGE_COMPRESSION_QUALITY = 85
    const val MAX_MEDIA_RETRIES = 3

    // ─── Marketplace & Recommendations ──────────────────────────────────
    const val DEFAULT_RECOMMENDATION_COUNT = 5
    const val RECOMMENDATION_RESPONSE_TARGET_MS = 500L
    const val BROWSING_HISTORY_DAYS = 30L

    // ─── Hub Assignment ─────────────────────────────────────────────────
    const val DEFAULT_MAX_DELIVERY_RADIUS_KM = 100.0
    const val DEFAULT_HUB_CAPACITY = 1000
    const val EARTH_RADIUS_KM = 6371.0

    // ─── Transfer ───────────────────────────────────────────────────────
    const val TRANSFER_TIMEOUT_SECONDS = 30
    const val MAX_TRANSFER_RETRIES = 3

    // ─── Verification ───────────────────────────────────────────────────
    const val MIN_LATITUDE = -90.0
    const val MAX_LATITUDE = 90.0
    const val MIN_LONGITUDE = -180.0
    const val MAX_LONGITUDE = 180.0

    // ─── Analytics ──────────────────────────────────────────────────────
    const val ANALYTICS_AGGREGATION_INTERVAL_HOURS = 24L
    const val ANALYTICS_PERFORMANCE_TARGET_MS = 2000L
    const val DAY_MS = 24 * 60 * 60 * 1000L
    const val WEEK_MS = 7 * DAY_MS
    const val MONTH_MS = 30 * DAY_MS

    // ─── Breeding ───────────────────────────────────────────────────────
    const val MAX_BREEDING_SUGGESTIONS = 5
    const val BREEDING_CALC_TIMEOUT_MS = 2000L
    const val INBREEDING_LOW_THRESHOLD = 0.01     // 1%
    const val INBREEDING_MODERATE_THRESHOLD = 0.0625 // 6.25%
    const val INBREEDING_HIGH_THRESHOLD = 0.125    // 12.5%
    const val INBREEDING_CRITICAL_THRESHOLD = 0.25  // 25%
    const val MAX_PEDIGREE_DEPTH = 4

    // ─── Circuit Breaker ────────────────────────────────────────────────
    const val DEFAULT_CB_FAILURE_THRESHOLD = 5
    const val DEFAULT_CB_RESET_TIMEOUT_MS = 30_000L
    const val DEFAULT_CB_HALF_OPEN_TIMEOUT_MS = 10_000L

    // ─── Lifecycle ──────────────────────────────────────────────────────
    const val LIFECYCLE_CHECK_INTERVAL_HOURS = 24L
    const val STAGE_TRANSITION_WARNING_DAYS = 3

    // ─── Notifications ──────────────────────────────────────────────────
    const val MAX_NOTIFICATION_BATCH_SIZE = 5
    const val NOTIFICATION_BATCH_WINDOW_MS = 60_000L
    const val NOTIFICATION_DELIVERY_TARGET_SECONDS = 60
    const val QUIET_HOURS_START = 22 // 10 PM
    const val QUIET_HOURS_END = 7    // 7 AM

    // ─── Accessibility ──────────────────────────────────────────────────
    const val MIN_TOUCH_TARGET_DP = 48
    const val MIN_CONTRAST_RATIO = 4.5
    const val MAX_TEXT_SCALE = 2.0f

    // ─── Dispute Resolution ─────────────────────────────────────────────
    const val DISPUTE_RESPONSE_WINDOW_HOURS = 72L
    const val DISPUTE_AUTO_ESCALATION_HOURS = 168L // 7 days

    // ─── Sync ───────────────────────────────────────────────────────────
    const val SYNC_INTERVAL_MINUTES = 15L
    const val MAX_SYNC_RETRIES = 5
    const val STALE_DATA_THRESHOLD_HOURS = 24L
}
