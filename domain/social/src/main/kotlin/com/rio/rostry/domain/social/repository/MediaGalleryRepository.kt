package com.rio.rostry.domain.social.repository

import com.rio.rostry.domain.model.media.MediaItem
import com.rio.rostry.domain.model.media.MediaTag
import com.rio.rostry.domain.model.media.MediaFilter
import com.rio.rostry.core.model.PaginationState
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Repository contract for media gallery management.
 * 
 * Handles media items (photos, videos) including storage, tagging,
 * filtering, and synchronization with remote storage.
 */
interface MediaGalleryRepository {
    
    /**
     * Observe media items with real-time updates based on filter and pagination.
     */
    fun observeMedia(filter: MediaFilter, pagination: PaginationState): Flow<List<MediaItem>>
    
    /**
     * Get a specific media item by ID.
     */
    suspend fun getMediaItem(mediaId: String): Result<MediaItem?>
    
    /**
     * Add new media item and its tags to the gallery.
     */
    suspend fun addMediaItem(mediaItem: MediaItem): Result<Unit>
    
    /**
     * Update an existing media item.
     */
    suspend fun updateMediaItem(mediaItem: MediaItem): Result<Unit>
    
    /**
     * Delete a media item (soft or hard delete).
     */
    suspend fun deleteMediaItem(mediaId: String): Result<Unit>
    
    /**
     * Add tags to an existing media item.
     */
    suspend fun addTagsToMedia(mediaId: String, tags: List<MediaTag>): Result<Unit>
    
    /**
     * Remove tags from a media item.
     */
    suspend fun removeTagsFromMedia(mediaId: String, tagIds: List<String>): Result<Unit>
    
    /**
     * Get distinct tag values for a specific tag type.
     */
    suspend fun getDistinctTagValues(tagType: String): Result<List<String>>
    
    /**
     * Get local cached file for a media item.
     */
    suspend fun getMediaFile(mediaItem: MediaItem): Result<File>
    
    /**
     * Export multiple media items to a destination.
     */
    suspend fun exportMediaItems(mediaIds: List<String>, destinationDir: File): Result<File>
    
    /**
     * Synchronize local cache with remote.
     */
    suspend fun synchronizeCache(): Result<Unit>
    
    /**
     * Sync state with backend.
     */
    suspend fun syncWithRemote(): Result<Unit>

    /**
     * Observe all media items for a specific asset.
     */
    fun getMediaForAsset(ownerId: String, assetId: String): Flow<List<MediaItem>>

    /**
     * Update caption and notes for a media item.
     */
    suspend fun updateCaption(mediaId: String, caption: String?, notes: String?): Result<Unit>
}
