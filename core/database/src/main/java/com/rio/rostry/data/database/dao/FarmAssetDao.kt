package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.FarmAssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmAssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: FarmAssetEntity)

    @Update
    suspend fun update(asset: FarmAssetEntity)

    @Delete
    suspend fun delete(asset: FarmAssetEntity)

    @Query("SELECT * FROM farm_assets WHERE id = :id")
    suspend fun getById(id: String): FarmAssetEntity?

    @Query("SELECT * FROM farm_assets WHERE id = :id")
    suspend fun findById(id: String): FarmAssetEntity? = getById(id)

    @Query("SELECT * FROM farm_assets WHERE userId = :userId ORDER BY name ASC")
    fun getAssetsForUser(userId: String): Flow<List<FarmAssetEntity>>

    @Query("SELECT * FROM farm_assets WHERE type = :type AND userId = :userId")
    fun getAssetsByType(userId: String, type: String): Flow<List<FarmAssetEntity>>

    @Query("SELECT COUNT(*) FROM farm_assets WHERE userId = :userId")
    suspend fun getAssetCount(userId: String): Int
}