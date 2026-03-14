package com.rio.rostry.core.common.config

/**
 * Centralized storage configuration for Firebase Storage and media URLs.
 * 
 * This object provides environment-specific base URLs and configuration
 * for media storage operations. Using centralized configuration ensures:
 * - Environment-specific URLs (dev/staging/prod)
 * - Easy URL updates without code changes
 * - Consistent URL patterns across the app
 * 
 * Note: BuildConfig is not available in common module.
 * Debug/demo features are controlled via feature flags in the app module.
 */
object StorageConfig {

    // ═══════════════════════════════════════════════════════════════════
    // FIREBASE STORAGE BASE URLs
    // ═══════════════════════════════════════════════════════════════════

    /** Firebase Storage base URL for rostry-dev environment */
    private const val FIREBASE_STORAGE_DEV_URL = 
        "https://firebasestorage.googleapis.com/v0/b/rostry-dev.appspot.com/o"

    /** Firebase Storage base URL for rostry-staging environment */
    private const val FIREBASE_STORAGE_STAGING_URL = 
        "https://firebasestorage.googleapis.com/v0/b/rostry-staging.appspot.com/o"

    /** Firebase Storage base URL for production environment */
    private const val FIREBASE_STORAGE_PROD_URL = 
        "https://firebasestorage.googleapis.com/v0/b/rostry-prod.appspot.com/o"

    /** Google Cloud Storage base URL for production media */
    private const val GOOGLE_STORAGE_PROD_URL = 
        "https://storage.googleapis.com/rostry-media"

    /**
     * Get the appropriate Firebase Storage base URL based on build type.
     * Note: In common module, defaults to production. Override in app module if needed.
     */
    val firebaseStorageBaseUrl: String
        get() = FIREBASE_STORAGE_PROD_URL

    /**
     * Get the appropriate media storage URL based on build type.
     * Note: In common module, defaults to production. Override in app module if needed.
     */
    val mediaStorageBaseUrl: String
        get() = GOOGLE_STORAGE_PROD_URL

    // ═══════════════════════════════════════════════════════════════════
    // DEMO CONTENT URLs
    // ═══════════════════════════════════════════════════════════════════

    /** Demo content base path in Firebase Storage */
    private const val DEMO_CONTENT_PATH = "demo"

    /** Demo breed images path */
    private const val DEMO_BREEDS_PATH = "breeds"

    /** Demo educational content path */
    private const val DEMO_EDUCATIONAL_PATH = "educational"

    /**
     * Get demo content URL for educational content.
     * Note: Demo features should be controlled from app module.
     */
    fun getDemoContentUrl(fileName: String): String? {
        return null // Demo URLs disabled in common module
    }

    /**
     * Get demo breed image URL.
     * Note: Demo features should be controlled from app module.
     */
    fun getDemoBreedImageUrl(breedName: String): String? {
        return null // Demo URLs disabled in common module
    }

    // ═══════════════════════════════════════════════════════════════════
    // PRODUCTION MEDIA PATHS
    // ═══════════════════════════════════════════════════════════════════

    /** Path prefix for user-uploaded images */
    const val USER_IMAGES_PATH = "user-images"

    /** Path prefix for product images */
    const val PRODUCT_IMAGES_PATH = "product-images"

    /** Path prefix for farm asset photos */
    const val FARM_ASSET_PHOTOS_PATH = "farm-assets"

    /** Path prefix for verification documents */
    const val VERIFICATION_DOCS_PATH = "verification-docs"

    /** Path prefix for chat media */
    const val CHAT_MEDIA_PATH = "chat-media"

    /** Path prefix for educational content */
    const val EDUCATIONAL_CONTENT_PATH = "educational"

    /**
     * Build a complete media URL for a given remote path.
     */
    fun buildMediaUrl(remotePath: String): String {
        return "$mediaStorageBaseUrl/$remotePath"
    }

    /**
     * Build a user image URL.
     */
    fun buildUserImageUrl(userId: String, fileName: String): String {
        return buildMediaUrl("$USER_IMAGES_PATH/$userId/$fileName")
    }

    /**
     * Build a product image URL.
     */
    fun buildProductImageUrl(productId: String, fileName: String): String {
        return buildMediaUrl("$PRODUCT_IMAGES_PATH/$productId/$fileName")
    }

    /**
     * Build a farm asset photo URL.
     */
    fun buildFarmAssetPhotoUrl(assetId: String, fileName: String): String {
        return buildMediaUrl("$FARM_ASSET_PHOTOS_PATH/$assetId/$fileName")
    }

    /**
     * Build a verification document URL.
     */
    fun buildVerificationDocUrl(userId: String, documentId: String): String {
        return buildMediaUrl("$VERIFICATION_DOCS_PATH/$userId/$documentId")
    }

    // ═══════════════════════════════════════════════════════════════════
    // EXTERNAL API URLs
    // ═══════════════════════════════════════════════════════════════════

    /** Open-Meteo Weather API base URL */
    const val WEATHER_API_BASE_URL = "https://api.open-meteo.com/v1"

    /** Google Docs Viewer URL for PDF preview */
    const val GOOGLE_DOCS_VIEWER_URL = "https://docs.google.com/viewer"

    /** WhatsApp API base URL for support */
    const val WHATSAPP_SUPPORT_URL = "https://wa.me"

    /**
     * Build weather API URL for given coordinates.
     */
    fun buildWeatherApiUrl(latitude: Double, longitude: Double): String {
        return "$WEATHER_API_BASE_URL/forecast?latitude=$latitude&longitude=$longitude"
    }

    /**
     * Build Google Docs viewer URL for a document.
     */
    fun buildGoogleDocsViewerUrl(documentUrl: String): String {
        return "$GOOGLE_DOCS_VIEWER_URL?url=$documentUrl"
    }

    /**
     * Build WhatsApp support URL with pre-filled message.
     */
    fun buildWhatsAppSupportUrl(phoneNumber: String, message: String? = null): String {
        val encodedMessage = message?.let { java.net.URLEncoder.encode(it, "UTF-8") } ?: ""
        return "$WHATSAPP_SUPPORT_URL/$phoneNumber?text=$encodedMessage"
    }

    // ═══════════════════════════════════════════════════════════════════
    // DEEP LINK CONFIGURATION
    // ═══════════════════════════════════════════════════════════════════

    /** Base URL for rostry deep links */
    private const val ROSTRY_DEEP_LINK_BASE = "rostry://"

    /** Base URL for rostry web links */
    private const val ROSTRY_WEB_BASE = "https://rostry.app"

    /**
     * Get deep link URL for a specific route.
     */
    fun getDeepLink(route: String): String = "$ROSTRY_DEEP_LINK_BASE$route"

    /**
     * Get web link URL for a specific route.
     */
    fun getWebLink(route: String): String = "$ROSTRY_WEB_BASE$route"

    // ═══════════════════════════════════════════════════════════════════
    // HELPER FUNCTIONS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Check if a URL is a demo URL (should only be used in debug builds).
     */
    fun isDemoUrl(url: String): Boolean {
        return url.contains("rostry-dev.appspot.com") || url.contains("/demo/")
    }

    /**
     * Sanitize file name for storage paths.
     */
    fun sanitizeFileName(fileName: String): String {
        return fileName
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .take(100)
    }

    /**
     * Generate a unique file name with timestamp.
     */
    fun generateFileName(prefix: String, extension: String): String {
        val timestamp = System.currentTimeMillis()
        val sanitizedPrefix = sanitizeFileName(prefix)
        return "${sanitizedPrefix}_$timestamp.$extension"
    }
}