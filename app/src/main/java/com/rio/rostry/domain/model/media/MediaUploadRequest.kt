package com.rio.rostry.domain.model.media

import android.net.Uri

data class MediaUploadRequest(
    val assetId: String?,
    val sourceUri: Uri,
    val mediaType: MediaType,
    val tags: List<MediaTag> = emptyList(),
    val generateThumbnail: Boolean = true
)
