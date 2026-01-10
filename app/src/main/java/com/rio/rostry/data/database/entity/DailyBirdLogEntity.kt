package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_bird_logs",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["birdId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["birdId"]), Index(value = ["date"])]
)
data class DailyBirdLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val birdId: String,
    val date: Long, // Start of day timestamp
    val activityType: String, // e.g., "Training", "Rest", "Sparring", "Illness"
    val weight: Double? = null,
    val feedIntakeGrams: Double? = null,
    val notes: String? = null,
    val performanceRating: Int? = null, // Legacy: 1-10
    val performanceScoreJson: String? = null, // Structured: {"speed":8,"stamina":7,"temperament":9}
    val createdAt: Long = System.currentTimeMillis()
)
