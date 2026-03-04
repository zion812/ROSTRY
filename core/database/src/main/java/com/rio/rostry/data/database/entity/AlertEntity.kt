package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "alerts")
@Keep
data class AlertEntity(
    @PrimaryKey val id: String,
    val userId: String, // The user (Farmer) the alert is for
    val title: String,
    val message: String,
    /**
     * "LOW", "MEDIUM", "HIGH", "CRITICAL"
     */
    val severity: String,
    val type: String, // "VACCINATION", "QUARANTINE", "MORTALITY", "HATCHING"
    val relatedId: String? = null, // ID of the related entity (e.g., product, batch)
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isDismissed: Boolean = false
)
