package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * FarmActivityLogEntity represents farm-level activities like expenses, sanitation,
 * maintenance, and other general farm operations.
 * 
 * This is separate from DailyLogEntity which tracks product-specific metrics.
 */
@Entity(
    tableName = "farm_activity_logs",
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["activityType"]),
        Index(value = ["createdAt"]),
        Index(value = ["productId"])
    ]
)
data class FarmActivityLogEntity(
    @PrimaryKey val activityId: String,
    val farmerId: String,
    val productId: String? = null,        // Optional: specific bird/batch
    val activityType: String,              // EXPENSE, SANITATION, MAINTENANCE, MEDICATION, MORTALITY, FEED, WEIGHT, OTHER
    val amountInr: Double? = null,         // For expenses
    val quantity: Double? = null,          // For feed (kg), weight (g), mortality count
    val category: String? = null,          // Feed, Medicine, Equipment, Labor, Cleaning, etc.
    val description: String? = null,
    val notes: String? = null,
    val photoUrls: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true,
    val syncedAt: Long? = null
) {
    // No-arg constructor for Firestore deserialization
    constructor() : this(
        activityId = "",
        farmerId = "",
        activityType = ""
    )
}
