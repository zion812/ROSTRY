package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity storing metadata about uploaded media files
 */
@Entity(
    tableName = "media_metadata",
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = ["mediaId"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mediaId")]
)
data class MediaMetadataEntity(
    @PrimaryKey
    val mediaId: String,
    val originalUrl: String,
    val thumbnailUrl: String,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val format: String,
    val duration: Int? = null,
    val compressionQuality: Int? = null,
    val createdAt: Long
)
