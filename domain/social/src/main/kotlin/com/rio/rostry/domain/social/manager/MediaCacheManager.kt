package com.rio.rostry.domain.social.manager

import com.rio.rostry.core.common.Result
import java.io.File

interface MediaCacheManager {
    /** 
     * Stores media locally and track with MediaCacheMetadataEntity
     * Returns the local File if successful.
     */
    suspend fun cacheMedia(mediaId: String, sourceUri: String): Result<File>
    
    /**
     * Retrieves cached media if it exists. Returns null otherwise.
     * Updates LRU last access timestamp.
     */
    suspend fun getCachedMedia(mediaId: String): File?
    
    /**
     * Delete media from cache and its metadata.
     */
    suspend fun removeCachedMedia(mediaId: String): Result<Unit>
    
    /**
     * Clear cache based on LRU policy if total size exceeds limit.
     */
    suspend fun enforceLruPolicy(maxSpaceBytes: Long): Result<Unit>

    /**
     * Clear all cached media.
     */
    suspend fun clearCache(): Result<Unit>
}
