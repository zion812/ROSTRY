package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.MediaMetadataEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for media metadata operations
 */
@Dao
interface MediaMetadataDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: MediaMetadataEntity)
    
    @Query("SELECT * FROM media_metadata WHERE mediaId = :mediaId")
    suspend fun getById(mediaId: String): MediaMetadataEntity?
    
    @Query("SELECT * FROM media_metadata WHERE mediaId = :mediaId")
    fun observeById(mediaId: String): Flow<MediaMetadataEntity?>
    
    @Query("DELETE FROM media_metadata WHERE mediaId = :mediaId")
    suspend fun delete(mediaId: String)
    
    @Query("SELECT COUNT(*) FROM media_metadata")
    suspend fun getCount(): Int
}
