package com.rio.rostry.domain.media

import java.io.File

/**
 * Media upload request containing file and metadata
 */
data class MediaUploadRequest(
    val file: File,
    val mediaType: MediaType,
    val ownerId: String,
    val entityType: String,
    val entityId: String
)

/**
 * Type of media being uploaded
 */
enum class MediaType {
    IMAGE,
    VIDEO
}

/**
 * Result of media upload operation
 */
sealed class UploadResult {
    data class Success(
        val mediaUrl: String,
        val thumbnailUrl: String,
        val metadata: MediaMetadata
    ) : UploadResult()
    
    data class Failure(val error: Throwable) : UploadResult()
}

/**
 * Metadata about uploaded media
 */
data class MediaMetadata(
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val format: String,
    val duration: Int? = null // For videos only
)
