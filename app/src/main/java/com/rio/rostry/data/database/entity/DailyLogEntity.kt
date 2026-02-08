package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * DailyLogEntity represents a daily log entry for a product.
 *
 * Merge strategy: Last-write-wins based on updatedAt. Conflicts are resolved by preferring the most recent update
 * (highest updatedAt timestamp). For critical fields like weightGrams and feedKg, the most recent deviceTimestamp
 * is used if available. Arrays (e.g., medication, symptoms) are deduplicated, and notes are merged with timestamps.
 */
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
        Index(value = ["createdAt"]),
        Index(value = ["mergedAt"])
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
    val author: String? = null,
    // merge tracking
    /** Timestamp of the last merge operation, null if never merged. */
    val mergedAt: Long? = null,
    /** Number of times this log has been merged during conflict resolution. */
    val mergeCount: Int = 0,
    /** Flag indicating if a conflict was resolved during the last merge. */
    val conflictResolved: Boolean = false,
    /** Structured media items (photos, videos) with metadata. Overrides photoUrls if present. */
    val mediaItemsJson: String? = null
) {
    // No-arg constructor for Firestore deserialization
    constructor() : this(
        logId = "",
        productId = "",
        farmerId = "",
        logDate = 0L
    )

    /**
     * Get media items from structured JSON, falling back to parsing photoUrls (legacy).
     */
    fun getMediaItems(): List<com.rio.rostry.ui.components.MediaItem> {
        // Prefer structured mediaItemsJson
        if (!mediaItemsJson.isNullOrBlank()) {
            try {
                val type = object : com.google.gson.reflect.TypeToken<List<com.rio.rostry.ui.components.MediaItem>>() {}.type
                val items: List<com.rio.rostry.ui.components.MediaItem>? = com.google.gson.Gson().fromJson(mediaItemsJson, type)
                if (!items.isNullOrEmpty()) return items
            } catch (_: Exception) { }
        }
        
        // Fallback: parse legacy photoUrls
        return photoUrls?.let { urls ->
            val listType = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
            // Try parsing as JSON array first
            try {
                 val parsed: List<String> = com.google.gson.Gson().fromJson(urls, listType)
                 parsed.mapIndexed { index, url ->
                     com.rio.rostry.ui.components.MediaItem(
                         url = url,
                         caption = "Photo ${index + 1}",
                         recordType = "DAILY_LOG",
                         recordId = logId
                     )
                 }
            } catch (e: Exception) {
                // Fallback to comma-separated
                urls.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .mapIndexed { index, url ->
                        com.rio.rostry.ui.components.MediaItem(
                            url = url,
                            caption = "Photo ${index + 1}",
                            recordType = "DAILY_LOG",
                            recordId = logId
                        )
                    }
            }
        } ?: emptyList()
    }
}
