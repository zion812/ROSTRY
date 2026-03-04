package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asset_health_records")
data class AssetHealthRecordEntity(
    @PrimaryKey val recordId: String,
    val assetId: String,
    val farmerId: String,
    val recordType: String, // VACCINATION, TREATMENT, GROWTH, QUARANTINE, MORTALITY
    val recordData: String, // JSON containing type-specific data
    val healthScore: Int, // 0-100
    val veterinarianId: String?,
    val veterinarianNotes: String?,
    val followUpRequired: Boolean = false,
    val followUpDate: Long?,
    val costInr: Double?,
    val mediaItemsJson: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long?
)
