package com.rio.rostry.data.mapper

import com.rio.rostry.data.database.entity.MediaItemEntity
import com.rio.rostry.data.database.entity.MediaTagEntity
import com.rio.rostry.domain.model.media.MediaItem
import com.rio.rostry.domain.model.media.MediaTag
import com.rio.rostry.domain.model.media.MediaType
import com.rio.rostry.domain.model.media.TagType
import com.rio.rostry.domain.model.media.UploadStatus

fun MediaItemEntity.toDomainModel(tags: List<MediaTagEntity> = emptyList()): MediaItem {
    return MediaItem(
        mediaId = mediaId,
        assetId = assetId,
        ownerId = ownerId,
        url = url,
        localPath = localPath,
        mediaType = runCatching { MediaType.valueOf(mediaType) }.getOrDefault(MediaType.IMAGE),
        tags = tags.map { it.toDomainModel() },
        caption = caption,
        notes = notes,
        dateAdded = dateAdded,
        fileSize = fileSize,
        width = width,
        height = height,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        uploadStatus = runCatching { UploadStatus.valueOf(uploadStatus) }.getOrDefault(UploadStatus.COMPLETED),
        createdAt = createdAt,
        updatedAt = updatedAt,
        isCached = isCached,
        lastAccessedAt = lastAccessedAt,
        dirty = dirty
    )
}

fun MediaItem.toEntity(): MediaItemEntity {
    return MediaItemEntity(
        mediaId = mediaId,
        assetId = assetId,
        ownerId = ownerId,
        url = url,
        localPath = localPath,
        mediaType = mediaType.name,
        caption = caption,
        notes = notes,
        dateAdded = dateAdded,
        fileSize = fileSize,
        width = width,
        height = height,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        uploadStatus = uploadStatus.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isCached = isCached,
        lastAccessedAt = lastAccessedAt,
        dirty = dirty
    )
}

fun MediaTagEntity.toDomainModel(): MediaTag {
    return MediaTag(
        tagId = tagId,
        tagType = runCatching { TagType.valueOf(tagType) }.getOrDefault(TagType.ASSET_ID),
        value = value
    )
}

fun MediaTag.toEntity(mediaId: String): MediaTagEntity {
    return MediaTagEntity(
        mediaId = mediaId,
        tagId = tagId,
        tagType = tagType.name,
        value = value,
        createdAt = System.currentTimeMillis()
    )
}
