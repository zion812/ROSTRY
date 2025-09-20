package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a lifecycle milestone for a poultry
 */
@Entity(tableName = "lifecycle_milestones")
data class LifecycleMilestone(
    @PrimaryKey
    val id: String,
    val poultryId: String,
    val weekNumber: Int, // 1-52+
    val stage: String, // CHICK, GROWTH, ADULT, BREEDER
    val title: String, // e.g., "First Vaccination", "Sexing Completed"
    val description: String,
    val date: Date, // Actual date when milestone was achieved
    val isCompleted: Boolean = false,
    val notes: String? = null,
    val alertDate: Date? = null, // When to send alert/reminder
    val isAlertSent: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)