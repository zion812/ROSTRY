package com.rio.rostry.domain.model.media

data class MediaItem(
    val mediaId: String,
    val assetId: String?,
    val url: String,
    val localPath: String?,
    val mediaType: MediaType,
    val tags: List<MediaTag>,
    val dateAdded: Long,
    val fileSize: Long,
    val width: Int?,
    val height: Int?,
    val duration: Int?, // For videos, in seconds
    val thumbnailUrl: String?,
    val uploadStatus: UploadStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val isCached: Boolean = false,
    val lastAccessedAt: Long? = null,
    val dirty: Boolean = false
)

enum class MediaType {
    IMAGE, VIDEO
}

enum class UploadStatus {
    PENDING, UPLOADING, COMPLETED, FAILED
}
