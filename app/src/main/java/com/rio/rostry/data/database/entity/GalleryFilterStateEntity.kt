package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gallery_filter_state")
data class GalleryFilterStateEntity(
    @PrimaryKey val id: String = "default",
    val ageGroupsJson: String, // JSON array of selected age groups
    val sourceTypesJson: String, // JSON array of selected source types
    val updatedAt: Long
)
