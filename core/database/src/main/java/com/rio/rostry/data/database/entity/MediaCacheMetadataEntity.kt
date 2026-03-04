package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_cache_metadata",
    indices = [
        Index(value = ["lastAccessedAt"]),
        Index(value = ["fileSize"])
    ]
)
data class MediaCacheMetadataEntity(
    @PrimaryKey val mediaId: String,
    val localPath: String,
    val fileSize: Long,
    val downloadedAt: Long,
    val lastAccessedAt: Long,
    val accessCount: Int = 0
)
