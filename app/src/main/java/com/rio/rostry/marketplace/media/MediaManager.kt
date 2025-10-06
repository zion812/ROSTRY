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

    // ===== Compose helpers to standardize media entrypoints =====
    // These helpers return lambdas that the UI can invoke from buttons, keeping policies consistent.
    @androidx.compose.runtime.Composable
    fun rememberImagePicker(onPicked: (android.net.Uri) -> Unit): () -> Unit {
        val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
        ) { uri: android.net.Uri? ->
            uri?.let(onPicked)
        }
        return { launcher.launch("image/*") }
    }

    @androidx.compose.runtime.Composable
    fun rememberImageCapture(
        context: android.content.Context,
        fileNamePrefix: String,
        onCaptured: (android.net.Uri) -> Unit
    ): () -> Unit {
        // Create a persistent output Uri for this capture session
        val outputUri = androidx.compose.runtime.remember(fileNamePrefix) {
            createImageUri(context, fileNamePrefix)
        }
        val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.TakePicture()
        ) { success: Boolean ->
            if (success && outputUri != null) onCaptured(outputUri)
        }
        return { outputUri?.let(launcher::launch) }
    }

    private fun createImageUri(context: android.content.Context, name: String): android.net.Uri? {
        return try {
            val imagesDir = java.io.File(context.cacheDir, "images").apply { mkdirs() }
            val file = java.io.File(imagesDir, "$name-${System.currentTimeMillis()}.jpg")
            val authority = "${context.packageName}.fileprovider"
            androidx.core.content.FileProvider.getUriForFile(context, authority, file)
        } catch (_: Exception) { null }
    }
}
