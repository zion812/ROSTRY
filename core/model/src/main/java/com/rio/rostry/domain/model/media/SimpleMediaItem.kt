package com.rio.rostry.domain.model.media

/**
 * Lightweight media item DTO matching the structure used in entity JSON serialization.
 * Used by database entities and converters for storing media attachments.
 *
 * The UI layer has its own fuller MediaItem in ui.components with the same base fields.
 */
data class SimpleMediaItem(
    val url: String,
    val caption: String? = null,
    val timestamp: Long? = null,
    val recordType: String? = null,
    val recordId: String? = null
)
