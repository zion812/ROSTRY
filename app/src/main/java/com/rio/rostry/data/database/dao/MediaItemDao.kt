package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.MediaItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {
    @Query("""
        SELECT * FROM media_items 
        WHERE (:assetId IS NULL OR assetId = :assetId)
        AND uploadStatus = 'COMPLETED'
        ORDER BY dateAdded DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryMedia(
        assetId: String?,
        limit: Int,
        offset: Int
    ): List<MediaItemEntity>
    
    @Query("""
        SELECT mi.* FROM media_items mi
        INNER JOIN media_tags mt ON mi.mediaId = mt.mediaId
        WHERE mt.tagType = :tagType AND mt.value IN (:values)
        AND uploadStatus = 'COMPLETED'
        GROUP BY mi.mediaId
        ORDER BY mi.dateAdded DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryMediaByTags(
        tagType: String,
        values: List<String>,
        limit: Int,
        offset: Int
    ): List<MediaItemEntity>
    
    @Query("""
        SELECT COUNT(*) FROM media_items 
        WHERE (:assetId IS NULL OR assetId = :assetId)
        AND uploadStatus = 'COMPLETED'
    """)
    suspend fun getMediaCount(assetId: String?): Int
    
    @Query("SELECT * FROM media_items WHERE mediaId = :mediaId")
    suspend fun getMediaById(mediaId: String): MediaItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaItemEntity)
    
    @Update
    suspend fun updateMedia(media: MediaItemEntity)
    
    @Delete
    suspend fun deleteMedia(media: MediaItemEntity)
    
    @Query("UPDATE media_items SET uploadStatus = :status, updatedAt = :updatedAt, dirty = 1 WHERE mediaId = :mediaId")
    suspend fun updateMediaStatus(mediaId: String, status: String, updatedAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE media_items SET url = :url, uploadStatus = :status, updatedAt = :updatedAt, dirty = 1 WHERE mediaId = :mediaId")
    suspend fun updateMediaUrlAndStatus(mediaId: String, url: String, status: String, updatedAt: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM media_items WHERE mediaId IN (:mediaIds)")
    suspend fun batchDeleteMedia(mediaIds: List<String>)
    
    @Query("UPDATE media_items SET isCached = :isCached WHERE mediaId = :mediaId")
    suspend fun updateCacheStatus(mediaId: String, isCached: Boolean)
    
    @Query("UPDATE media_items SET lastAccessedAt = :timestamp WHERE mediaId = :mediaId")
    suspend fun updateLastAccessed(mediaId: String, timestamp: Long)
    
    @Query("""
        SELECT * FROM media_items 
        WHERE uploadStatus = 'COMPLETED'
        ORDER BY dateAdded DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun observeMedia(limit: Int, offset: Int): Flow<List<MediaItemEntity>>
}
