package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.MediaCacheMetadataEntity

@Dao
interface MediaCacheMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheMetadata(metadata: MediaCacheMetadataEntity)
    
    @Query("SELECT * FROM media_cache_metadata WHERE mediaId = :mediaId")
    suspend fun getCacheMetadata(mediaId: String): MediaCacheMetadataEntity?
    
    @Query("UPDATE media_cache_metadata SET lastAccessedAt = :timestamp, accessCount = accessCount + 1 WHERE mediaId = :mediaId")
    suspend fun updateAccess(mediaId: String, timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM media_cache_metadata")
    suspend fun getCacheCount(): Int
    
    @Query("SELECT SUM(fileSize) FROM media_cache_metadata")
    suspend fun getTotalCacheSize(): Long?
    
    @Query("""
        SELECT * FROM media_cache_metadata 
        ORDER BY lastAccessedAt ASC 
        LIMIT :count
    """)
    suspend fun getLeastRecentlyUsed(count: Int): List<MediaCacheMetadataEntity>
    
    @Delete
    suspend fun deleteCacheMetadata(metadata: MediaCacheMetadataEntity)
}
