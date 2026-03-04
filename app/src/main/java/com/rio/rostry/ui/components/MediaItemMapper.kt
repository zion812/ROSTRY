package com.rio.rostry.ui.components

import com.rio.rostry.domain.model.media.SimpleMediaItem

/** Convert a database [SimpleMediaItem] to its UI counterpart [MediaItem]. */
fun SimpleMediaItem.toUiMediaItem(): MediaItem = MediaItem(
    url = url,
    caption = caption,
    timestamp = timestamp,
    recordType = recordType,
    recordId = recordId
)

/** Convert a list of database [SimpleMediaItem]s to UI [MediaItem]s. */
fun List<SimpleMediaItem>.toUiMediaItems(): List<MediaItem> = map { it.toUiMediaItem() }

/** Convert a UI [MediaItem] to its database counterpart [SimpleMediaItem]. */
fun MediaItem.toSimpleMediaItem(): SimpleMediaItem = SimpleMediaItem(
    url = url,
    caption = caption,
    timestamp = timestamp,
    recordType = recordType,
    recordId = recordId
)
