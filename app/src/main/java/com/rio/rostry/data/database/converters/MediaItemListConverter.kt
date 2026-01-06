package com.rio.rostry.data.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.ui.components.MediaItem

/**
 * Room TypeConverter for List<MediaItem> to/from JSON.
 * Used to store structured media metadata in entities.
 */
class MediaItemListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromMediaItemList(mediaItems: List<MediaItem>?): String? {
        return mediaItems?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toMediaItemList(json: String?): List<MediaItem>? {
        if (json.isNullOrBlank()) return null
        val type = object : TypeToken<List<MediaItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            // Fallback: try to parse as comma-separated URLs for backwards compatibility
            parseCommaSeparatedUrls(json)
        }
    }

    /**
     * Backward compatibility: parse old comma-separated URL format
     */
    private fun parseCommaSeparatedUrls(urls: String): List<MediaItem>? {
        return urls.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapIndexed { index, url ->
                MediaItem(
                    url = url,
                    caption = "Photo ${index + 1}",
                    timestamp = null,
                    recordType = null,
                    recordId = null
                )
            }
            .takeIf { it.isNotEmpty() }
    }
}

/**
 * Context passed during media upload to associate with record
 */
data class MediaUploadContext(
    val recordId: String,
    val recordType: String,    // VACCINATION, GROWTH, MORTALITY, ACTIVITY, DAILY_LOG
    val caption: String? = null,
    val farmerId: String? = null
)
