package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.ui.components.MediaItem

/**
 * FarmActivityLogEntity represents farm-level activities like expenses, sanitation,
 * maintenance, and other general farm operations.
 * 
 * This is separate from DailyLogEntity which tracks product-specific metrics.
 * 
 * Media storage:
 * - photoUrls: Legacy comma-separated URLs (backwards compatibility)
 * - mediaItemsJson: Structured JSON for rich metadata (caption, timestamp, etc.)
 * Use getMediaItems() helper to get the best available data.
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
    val photoUrls: String? = null,         // Legacy: comma-separated URLs
    val mediaItemsJson: String? = null,    // Structured: JSON array of MediaItem
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

    /**
     * Get media items from structured JSON, falling back to parsing photoUrls
     */
    @Ignore
    fun getMediaItems(): List<MediaItem> {
        // Prefer structured mediaItemsJson
        if (!mediaItemsJson.isNullOrBlank()) {
            try {
                val type = object : TypeToken<List<MediaItem>>() {}.type
                val items: List<MediaItem>? = Gson().fromJson(mediaItemsJson, type)
                if (!items.isNullOrEmpty()) return items
            } catch (_: Exception) { }
        }
        
        // Fallback: parse legacy photoUrls
        return photoUrls?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.mapIndexed { index, url ->
                MediaItem(
                    url = url,
                    caption = "Photo ${index + 1}",
                    recordType = "ACTIVITY",
                    recordId = activityId
                )
            } ?: emptyList()
    }
}
