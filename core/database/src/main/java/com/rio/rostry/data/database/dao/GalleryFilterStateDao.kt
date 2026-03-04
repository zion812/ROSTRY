package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.GalleryFilterStateEntity

@Dao
interface GalleryFilterStateDao {
    @Query("SELECT * FROM gallery_filter_state WHERE id = :id")
    suspend fun getFilterState(id: String = "default"): GalleryFilterStateEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFilterState(state: GalleryFilterStateEntity)
}
