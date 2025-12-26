package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.FarmAssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmAssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: FarmAssetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(asset: FarmAssetEntity)

    @Update
    suspend fun updateAsset(asset: FarmAssetEntity)

    @Query("SELECT * FROM farm_assets WHERE assetId = :assetId")
    fun getAssetById(assetId: String): Flow<FarmAssetEntity?>

    @Query("SELECT * FROM farm_assets WHERE assetId = :assetId")
    suspend fun findById(assetId: String): FarmAssetEntity?

    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getAssetsByFarmer(farmerId: String): Flow<List<FarmAssetEntity>>

    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND assetType = :type AND isDeleted = 0")
    fun getAssetsByType(farmerId: String, type: String): Flow<List<FarmAssetEntity>>

    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND isShowcase = 1 AND isDeleted = 0")
    fun getShowcaseAssets(farmerId: String): Flow<List<FarmAssetEntity>>

    @Query("UPDATE farm_assets SET quantity = :quantity, updatedAt = :updatedAt, dirty = 1 WHERE assetId = :assetId")
    suspend fun updateQuantity(assetId: String, quantity: Double, updatedAt: Long)

    @Query("UPDATE farm_assets SET healthStatus = :status, updatedAt = :updatedAt, dirty = 1 WHERE assetId = :assetId")
    suspend fun updateHealthStatus(assetId: String, status: String, updatedAt: Long)
    
    // Sync Support
    @Query("SELECT * FROM farm_assets WHERE updatedAt > :sinceTime LIMIT :limit")
    suspend fun getUpdatedSince(sinceTime: Long, limit: Int = 100): List<FarmAssetEntity>

    @Query("SELECT COUNT(*) FROM farm_assets WHERE farmerId = :farmerId AND isDeleted = 0")
    suspend fun getAssetCountForFarmer(farmerId: String): Int

    @Query("DELETE FROM farm_assets WHERE isDeleted = 1")
    suspend fun purgeDeleted()
}
