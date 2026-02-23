package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_items",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["dateAdded"]),
        Index(value = ["uploadStatus"]),
        Index(value = ["mediaType"])
    ]
)
data class MediaItemEntity(
    @PrimaryKey val mediaId: String,
    val assetId: String?,
    val url: String,
    val localPath: String?,
    val mediaType: String, // IMAGE, VIDEO
    val dateAdded: Long,
    val fileSize: Long,
    val width: Int?,
    val height: Int?,
    val duration: Int?,
    val thumbnailUrl: String?,
    val uploadStatus: String, // PENDING, UPLOADING, COMPLETED, FAILED
    val createdAt: Long,
    val updatedAt: Long,
    val isCached: Boolean = false,
    val lastAccessedAt: Long? = null,
    val dirty: Boolean = false
)
