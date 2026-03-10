package com.rio.rostry.core.model

/**
 * Domain model representing a media item (photo or video).
 */
data class MediaItem(
    val mediaId: String,
    val ownerId: String,
    val assetId: String? = null,
    val mediaType: MediaType,
    val localUri: String? = null,
    val remoteUrl: String? = null,
    val caption: String? = null,
    val notes: String? = null,
    val tags: List<MediaTag> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Media type enumeration.
 */
enum class MediaType {
    IMAGE,
    VIDEO
}

/**
 * Domain model representing a media tag.
 */
data class MediaTag(
    val tagId: String,
    val tagType: String,
    val tagValue: String
)

/**
 * Filter criteria for media queries.
 */
data class MediaFilter(
    val ownerId: String,
    val assetId: String? = null,
    val mediaTypes: List<MediaType> = listOf(MediaType.IMAGE, MediaType.VIDEO)
)

/**
 * Pagination state for media queries.
 */
data class PaginationState(
    val currentPage: Int = 0,
    val pageSize: Int = 20
)
