package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_logs",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["farmerId"]),
        Index(value = ["logDate"]),
        Index(value = ["createdAt"]) 
    ]
)
data class DailyLogEntity(
    @PrimaryKey val logId: String,
    val productId: String,
    val farmerId: String,
    // date at midnight (UTC millis) for grouping
    val logDate: Long,
    val weightGrams: Double? = null,
    val feedKg: Double? = null,
    val medicationJson: String? = null,
    val symptomsJson: String? = null,
    val activityLevel: String? = null, // LOW, NORMAL, HIGH
    val photoUrls: String? = null,
    val notes: String? = null,
    val temperature: Double? = null,
    val humidity: Double? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null,
    // offline audit
    val deviceTimestamp: Long = System.currentTimeMillis(),
    val author: String? = null
)
