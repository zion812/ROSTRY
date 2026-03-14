package com.rio.rostry.core.common.constants

/**
 * Centralized business constants for the ROSTRY application.
 * 
 * This object contains all magic numbers, thresholds, and configuration values
 * used throughout the codebase. Using centralized constants ensures:
 * - Consistency across features
 * - Easy configuration updates
 * - Better testability
 * - Clear documentation of business rules
 */
object BusinessConstants {

    // ═══════════════════════════════════════════════════════════════════
    // WEIGHT THRESHOLDS (grams)
    // ═══════════════════════════════════════════════════════════════════

    /** Minimum weight for a bird to be considered market-ready */
    const val MIN_BIRD_WEIGHT_GRAMS = 1500L

    /** Target weight for optimal sale price */
    const val TARGET_BIRD_WEIGHT_GRAMS = 2000L

    /** Minimum weight for breeding eligibility */
    const val MIN_BREEDING_WEIGHT_GRAMS = 1800L

    /** Average adult bird weight (for calculations) */
    const val AVERAGE_ADULT_BIRD_WEIGHT_GRAMS = 2500L

    // ═══════════════════════════════════════════════════════════════════
    // TEXT VALIDATION LIMITS
    // ═══════════════════════════════════════════════════════════════════

    /** Minimum length for text descriptions */
    const val MIN_TEXT_LENGTH = 10

    /** Maximum length for product descriptions */
    const val MAX_DESCRIPTION_LENGTH = 5000

    /** Maximum length for titles */
    const val MAX_TITLE_LENGTH = 200

    /** Maximum length for comments */
    const val MAX_COMMENT_LENGTH = 1000

    /** Maximum length for notes */
    const val MAX_NOTE_LENGTH = 500

    /** Minimum password length */
    const val MIN_PASSWORD_LENGTH = 8

    /** Maximum OTP length */
    const val MAX_OTP_LENGTH = 6

    // ═══════════════════════════════════════════════════════════════════
    // IMAGE & MEDIA CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Image quality for high quality uploads (0-100) */
    const val IMAGE_QUALITY_HIGH = 90

    /** Image quality for medium quality uploads (0-100) */
    const val IMAGE_QUALITY_MEDIUM = 70

    /** Image quality for thumbnails (0-100) */
    const val IMAGE_QUALITY_THUMBNAIL = 50

    /** Maximum image file size before compression (bytes) */
    const val MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024L // 5 MB

    /** Medium image size threshold (bytes) */
    const val MEDIUM_IMAGE_SIZE_THRESHOLD_BYTES = 50 * 1024L // 50 KB

    /** Large image size threshold (bytes) */
    const val LARGE_IMAGE_SIZE_THRESHOLD_BYTES = 100 * 1024L // 100 KB

    /** Thumbnail size in pixels */
    const val THUMBNAIL_SIZE_PX = 150

    /** Medium image size in pixels */
    const val MEDIUM_IMAGE_SIZE_PX = 1024

    /** Large image size in pixels */
    const val LARGE_IMAGE_SIZE_PX = 1440

    /** Maximum images per product listing */
    const val MAX_IMAGES_PER_LISTING = 10

    /** Maximum videos per listing */
    const val MAX_VIDEOS_PER_LISTING = 3

    // ═══════════════════════════════════════════════════════════════════
    // TRANSFER & TRANSACTION CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Transfer code expiry time in milliseconds (15 minutes) */
    const val TRANSFER_CODE_EXPIRY_MS = 15 * 60 * 1000L

    /** Transfer timeout in milliseconds (24 hours) */
    const val TRANSFER_TIMEOUT_MS = 24 * 60 * 60 * 1000L

    /** Minimum transfer amount */
    const val MIN_TRANSFER_AMOUNT = 1.0

    /** Maximum transfer amount per transaction */
    const val MAX_TRANSFER_AMOUNT = 1000000.0

    /** OTP expiry time in milliseconds (5 minutes) */
    const val OTP_EXPIRY_MS = 5 * 60 * 1000L

    // ═══════════════════════════════════════════════════════════════════
    // SESSION & AUTH CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Session timeout for farmers (days) */
    const val SESSION_TIMEOUT_FARMER_DAYS = 7L

    /** Session timeout for general users (days) */
    const val SESSION_TIMEOUT_GENERAL_DAYS = 30L

    /** Session timeout for guests (days) */
    const val SESSION_TIMEOUT_GUEST_DAYS = 7L

    /** Session timeout in milliseconds (calculated) */
    fun getSessionTimeoutMillis(userType: String): Long {
        val days = when (userType) {
            "FARMER" -> SESSION_TIMEOUT_FARMER_DAYS
            "GENERAL" -> SESSION_TIMEOUT_GENERAL_DAYS
            else -> SESSION_TIMEOUT_GUEST_DAYS
        }
        return days * 24 * 60 * 60 * 1000L
    }

    // ═══════════════════════════════════════════════════════════════════
    // SYNC & BACKGROUND WORK CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Sync interval in hours */
    const val SYNC_INTERVAL_HOURS = 8L

    /** Sync interval in milliseconds */
    const val SYNC_INTERVAL_MS = SYNC_INTERVAL_HOURS * 60 * 60 * 1000L

    /** Minimum sync interval in minutes */
    const val MIN_SYNC_INTERVAL_MINUTES = 30L

    /** Work manager backoff delay in minutes */
    const val WORK_BACKOFF_DELAY_MINUTES = 5L

    /** Maximum retry attempts for failed work */
    const val MAX_WORK_RETRY_ATTEMPTS = 3

    // ═══════════════════════════════════════════════════════════════════
    // PAGINATION & LIMITS
    // ═══════════════════════════════════════════════════════════════════

    /** Default page size for lists */
    const val DEFAULT_PAGE_SIZE = 20

    /** Maximum page size */
    const val MAX_PAGE_SIZE = 100

    /** Minimum page size */
    const val MIN_PAGE_SIZE = 10

    /** Recent activity window in days */
    const val RECENT_ACTIVITY_WINDOW_DAYS = 7L

    /** Analytics aggregation window in days */
    const val ANALYTICS_AGGREGATION_WINDOW_DAYS = 30L

    // ═══════════════════════════════════════════════════════════════════
    // FEES & PRICING
    // ═══════════════════════════════════════════════════════════════════

    /** Flat delivery fee in paise (Rs. 50) */
    const val DELIVERY_FEE_FLAT_PAISE = 5000L

    /** Platform fee percentage */
    const val PLATFORM_FEE_PERCENT = 2.5

    /** Minimum order value in paise (Rs. 100) */
    const val MIN_ORDER_VALUE_PAISE = 10000L

    /** Maximum discount percentage */
    const val MAX_DISCOUNT_PERCENT = 50.0

    /** Default delivery radius in kilometers */
    const val DEFAULT_DELIVERY_RADIUS_KM = 50.0

    /** Logistics fee for partial refunds (in rupees) */
    const val PARTIAL_REFUND_LOGISTICS_FEE = 50.0

    // ═══════════════════════════════════════════════════════════════════
    // NOTIFICATION CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Notification flush interval in minutes */
    const val NOTIFICATION_FLUSH_INTERVAL_MINUTES = 15L

    /** Maximum notifications per batch */
    const val MAX_NOTIFICATIONS_PER_BATCH = 50

    /** Notification retention days */
    const val NOTIFICATION_RETENTION_DAYS = 30L

    // ═══════════════════════════════════════════════════════════════════
    // VERIFICATION & KYC CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Maximum file size for verification documents (bytes) */
    const val MAX_VERIFICATION_FILE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB

    /** Maximum documents per verification */
    const val MAX_DOCUMENTS_PER_VERIFICATION = 5

    /** KYC review SLA in hours */
    const val KYC_REVIEW_SLA_HOURS = 48L

    // ═══════════════════════════════════════════════════════════════════
    // ANALYTICS & TRACKING CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Analytics event batch size */
    const val ANALYTICS_BATCH_SIZE = 100

    /** Analytics flush interval in minutes */
    const val ANALYTICS_FLUSH_INTERVAL_MINUTES = 30L

    /** Cache TTL for analytics data in minutes */
    const val ANALYTICS_CACHE_TTL_MINUTES = 60L

    // ═══════════════════════════════════════════════════════════════════
    // ERROR HANDLING & RETRY CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Network request timeout in seconds */
    const val NETWORK_TIMEOUT_SECONDS = 30L

    /** Connection timeout in seconds */
    const val CONNECTION_TIMEOUT_SECONDS = 10L

    /** Read timeout in seconds */
    const val READ_TIMEOUT_SECONDS = 30L

    /** Exponential backoff multiplier */
    const val BACKOFF_MULTIPLIER = 2.0

    /** Maximum backoff delay in seconds */
    const val MAX_BACKOFF_DELAY_SECONDS = 300L

    // ═══════════════════════════════════════════════════════════════════
    // STORAGE & CACHE CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Default storage quota per user in MB */
    const val DEFAULT_STORAGE_QUOTA_MB = 1024L // 1 GB

    /** Warning threshold for storage (percentage) */
    const val STORAGE_WARNING_THRESHOLD_PERCENT = 80

    /** Critical threshold for storage (percentage) */
    const val STORAGE_CRITICAL_THRESHOLD_PERCENT = 95

    /** Cache max age in seconds */
    const val CACHE_MAX_AGE_SECONDS = 3600L // 1 hour

    // ═══════════════════════════════════════════════════════════════════
    // LOCATION & GEO CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** GPS accuracy threshold in meters */
    const val GPS_ACCURACY_THRESHOLD_METERS = 50.0

    /** Location update interval in milliseconds */
    const val LOCATION_UPDATE_INTERVAL_MS = 10000L // 10 seconds

    /** Fastest location update interval in milliseconds */
    const val FASTEST_LOCATION_UPDATE_INTERVAL_MS = 5000L // 5 seconds

    /** Minimum distance for location update in meters */
    const val MIN_DISTANCE_FOR_LOCATION_UPDATE_METERS = 10.0f

    // ═══════════════════════════════════════════════════════════════════
    // SOCIAL & COMMUNITY CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Maximum followers to fetch in one query */
    const val MAX_FOLLOWERS_PER_QUERY = 1000

    /** Story expiry time in hours */
    const val STORY_EXPIRY_HOURS = 24L

    /** Maximum story duration in seconds */
    const val MAX_STORY_DURATION_SECONDS = 30

    /** Minimum follow interval in milliseconds */
    const val MIN_FOLLOW_INTERVAL_MS = 1000L // 1 second (rate limiting)

    // ═══════════════════════════════════════════════════════════════════
    // AUCTION CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Minimum bid increment percentage */
    const val MIN_BID_INCREMENT_PERCENT = 5.0

    /** Auction extension time in minutes (when bid in last minutes) */
    const val AUCTION_EXTENSION_TIME_MINUTES = 5L

    /** Minimum auction duration in hours */
    const val MIN_AUCTION_DURATION_HOURS = 1L

    /** Maximum auction duration in days */
    const val MAX_AUCTION_DURATION_DAYS = 7L

    // ═══════════════════════════════════════════════════════════════════
    // ADMIN & REVIEW CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Transfer amount threshold for admin review (rupees) */
    const val ADMIN_REVIEW_TRANSFER_THRESHOLD = 10000.0

    /** Maximum transfer amount for expedited processing (rupees) */
    const val EXPEDITED_TRANSFER_THRESHOLD = 5000.0

    // ═══════════════════════════════════════════════════════════════════
    // QR CODE & BARCODE CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** QR code generation size in pixels */
    const val QR_CODE_SIZE = 1024

    /** QR code display size in pixels */
    const val QR_CODE_DISPLAY_SIZE = 256

    /** Maximum QR code data length */
    const val MAX_QR_CODE_DATA_LENGTH = 500

    // ═══════════════════════════════════════════════════════════════════
    // DELIVERY & LOCATION CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Default delivery radius in meters */
    const val DEFAULT_DELIVERY_RADIUS_METERS = 50000.0

    /** Maximum delivery radius in meters */
    const val MAX_DELIVERY_RADIUS_METERS = 100000.0

    /** Proximity verification threshold in meters */
    const val PROXIMITY_THRESHOLD_METERS = 100.0

    // ═══════════════════════════════════════════════════════════════════
    // DIGITAL FARM SIMULATION CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Boid separation weight for flocking algorithm */
    const val BOID_SEPARATION_WEIGHT = 1.5f

    /** Boid alignment weight for flocking algorithm */
    const val BOID_ALIGNMENT_WEIGHT = 1.0f

    /** Boid cohesion weight for flocking algorithm */
    const val BOID_COHESION_WEIGHT = 1.0f

    /** Default weather effects intensity (0.0 - 1.0) */
    const val WEATHER_EFFECTS_INTENSITY = 0.5f

    // ═══════════════════════════════════════════════════════════════════
    // HARVEST & BATCH CONSTANTS
    // ═══════════════════════════════════════════════════════════════════

    /** Minimum quantity for batch operations */
    const val MIN_BATCH_QUANTITY = 1

    /** Maximum quantity for single harvest operation */
    const val MAX_HARVEST_QUANTITY = 10000

    /** Average weight regex pattern for parsing alerts */
    const val AVG_WEIGHT_REGEX_PATTERN = """(\d+)g\s*avg"""

    /** Quantity regex pattern for parsing alerts */
    const val QUANTITY_REGEX_PATTERN = """(\d+)\s*birds"""

    /** Age regex pattern for parsing alerts */
    const val AGE_REGEX_PATTERN = """(\d+)\s*weeks"""

    // ═══════════════════════════════════════════════════════════════════
    // HELPER FUNCTIONS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Convert grams to kilograms.
     */
    fun gramsToKilograms(grams: Long): Double = grams.toDouble() / 1000.0

    /**
     * Convert kilograms to grams.
     */
    fun kilogramsToGrams(kg: Double): Long = (kg * 1000.0).toLong()

    /**
     * Convert paise to rupees.
     */
    fun paiseToRupees(paise: Long): Double = paise.toDouble() / 100.0

    /**
     * Convert rupees to paise.
     */
    fun rupeesToPaise(rupees: Double): Long = (rupees * 100.0).toLong()

    /**
     * Convert milliseconds to days.
     */
    fun millisToDays(millis: Long): Long = millis / (24 * 60 * 60 * 1000L)

    /**
     * Convert days to milliseconds.
     */
    fun daysToMillis(days: Long): Long = days * 24 * 60 * 60 * 1000L

    /**
     * Check if weight is market-ready.
     */
    fun isMarketReady(weightGrams: Long): Boolean = weightGrams >= MIN_BIRD_WEIGHT_GRAMS

    /**
     * Check if weight meets breeding requirements.
     */
    fun isBreedingEligible(weightGrams: Long): Boolean = weightGrams >= MIN_BREEDING_WEIGHT_GRAMS

    /**
     * Calculate delivery fee based on distance and weight.
     */
    fun calculateDeliveryFee(distanceKm: Double, weightGrams: Long): Long {
        val baseFee = DELIVERY_FEE_FLAT_PAISE
        val distanceFee = (distanceKm * 1000).toLong() // Rs. 1 per km in paise
        val weightFee = (weightGrams / 1000) * 500 // Rs. 5 per kg in paise
        return baseFee + distanceFee + weightFee
    }

    /**
     * Parse harvest metadata from alert message using regex patterns.
     * Returns null if parsing fails.
     */
    fun parseHarvestMetadata(message: String): HarvestMetadata? {
        return try {
            val quantity = Regex(QUANTITY_REGEX_PATTERN)
                .find(message)?.groupValues?.getOrNull(1)?.toIntOrNull()
            val avgWeight = Regex(AVG_WEIGHT_REGEX_PATTERN)
                .find(message)?.groupValues?.getOrNull(1)?.toIntOrNull()
            val ageWeeks = Regex(AGE_REGEX_PATTERN)
                .find(message)?.groupValues?.getOrNull(1)?.toIntOrNull()

            if (quantity != null && quantity > 0) {
                HarvestMetadata(
                    batchId = "",
                    quantity = quantity,
                    avgWeight = avgWeight ?: 0,
                    ageWeeks = ageWeeks ?: 0
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if transfer requires admin review based on amount.
     */
    fun requiresAdminReview(amount: Double): Boolean = amount > ADMIN_REVIEW_TRANSFER_THRESHOLD

    /**
     * Check if transfer qualifies for expedited processing.
     */
    fun qualifiesForExpeditedProcessing(amount: Double): Boolean =
        amount in 0.0..EXPEDITED_TRANSFER_THRESHOLD
}

/**
 * Data class representing harvest metadata parsed from alert messages.
 */
data class HarvestMetadata(
    val batchId: String,
    val quantity: Int,
    val avgWeight: Int,
    val ageWeeks: Int
)
