package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.MediaTagEntity

@Dao
interface MediaTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: MediaTagEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<MediaTagEntity>)
    
    @Query("SELECT * FROM media_tags WHERE mediaId = :mediaId")
    suspend fun getTagsForMedia(mediaId: String): List<MediaTagEntity>
    
    @Query("DELETE FROM media_tags WHERE mediaId = :mediaId")
    suspend fun deleteTagsForMedia(mediaId: String)
    
    @Query("""
        SELECT DISTINCT value FROM media_tags 
        WHERE tagType = :tagType
    """)
    suspend fun getDistinctTagValues(tagType: String): List<String>
}
