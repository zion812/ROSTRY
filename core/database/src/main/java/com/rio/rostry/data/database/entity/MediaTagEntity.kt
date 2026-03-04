package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "media_tags",
    primaryKeys = ["mediaId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = ["mediaId"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mediaId"]),
        Index(value = ["tagType", "value"])
    ]
)
data class MediaTagEntity(
    val mediaId: String,
    val tagId: String,
    val tagType: String, // ASSET_ID, AGE_GROUP, SOURCE_TYPE
    val value: String,
    val createdAt: Long
)
