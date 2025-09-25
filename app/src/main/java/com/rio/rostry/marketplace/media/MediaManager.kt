package com.rio.rostry.marketplace.media

/**
 * Handles media size checks and (placeholder) compression for rural networks.
 * Integrate with actual pickers/recorders at UI layer.
 */
object MediaManager {
    data class MediaStats(
        val photos: Int,
        val videos: Int,
        val audios: Int,
        val documents: Int
    )

    data class CheckResult(val withinLimits: Boolean, val reasons: List<String>)

    fun checkLimits(stats: MediaStats, maxPhotos: Int = 12, maxVideos: Int = 2, maxAudios: Int = 3, maxDocs: Int = 5): CheckResult {
        val errors = mutableListOf<String>()
        if (stats.photos > maxPhotos) errors += "Too many photos (max $maxPhotos)"
        if (stats.videos > maxVideos) errors += "Too many videos (max $maxVideos)"
        if (stats.audios > maxAudios) errors += "Too many audio notes (max $maxAudios)"
        if (stats.documents > maxDocs) errors += "Too many documents (max $maxDocs)"
        return CheckResult(errors.isEmpty(), errors)
    }

    // Placeholder for image compression; integrate with CompressionUtils as needed at repository level.
    fun compressImage(bytes: ByteArray): ByteArray = bytes // no-op here; repository compresses already
}
