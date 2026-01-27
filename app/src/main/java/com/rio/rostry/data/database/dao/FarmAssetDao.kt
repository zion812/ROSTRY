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

    @Query("SELECT * FROM farm_assets WHERE status = 'ACTIVE' AND isDeleted = 0")
    suspend fun getActiveAssetsOneShot(): List<FarmAssetEntity>

    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getAssetsByFarmer(farmerId: String): Flow<List<FarmAssetEntity>>

    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND assetType = :type AND isDeleted = 0")
    fun getAssetsByType(farmerId: String, type: String): Flow<List<FarmAssetEntity>>

    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND isShowcase = 1 AND isDeleted = 0")
    fun getShowcaseAssets(farmerId: String): Flow<List<FarmAssetEntity>>

    @Query("UPDATE farm_assets SET quantity = :quantity, updatedAt = :updatedAt, dirty = 1 WHERE assetId = :assetId")
    suspend fun updateQuantity(assetId: String, quantity: Double, updatedAt: Long)

    @Query("UPDATE farm_assets SET status = :status, updatedAt = :timestamp WHERE assetId = :assetId")
    suspend fun updateStatus(assetId: String, status: String, timestamp: Long)

    @Query("SELECT * FROM farm_assets WHERE birdCode = :code LIMIT 1")
    suspend fun findByBirdCode(code: String): FarmAssetEntity?

    @Query("UPDATE farm_assets SET healthStatus = :status, updatedAt = :updatedAt, dirty = 1 WHERE assetId = :assetId")
    suspend fun updateHealthStatus(assetId: String, status: String, updatedAt: Long)
    
    // Sync Support
    @Query("SELECT * FROM farm_assets WHERE updatedAt > :sinceTime LIMIT :limit")
    suspend fun getUpdatedSince(sinceTime: Long, limit: Int = 100): List<FarmAssetEntity>

    @Query("SELECT COUNT(*) FROM farm_assets WHERE farmerId = :farmerId AND isDeleted = 0")
    suspend fun getAssetCountForFarmer(farmerId: String): Int

    @Query("DELETE FROM farm_assets WHERE isDeleted = 1")
    suspend fun purgeDeleted()
    
    // ========================================
    // Marketplace Lifecycle Operations
    // ========================================
    
    /** Mark asset as listed for sale */
    @Query("UPDATE farm_assets SET status = 'LISTED', listedAt = :listedAt, listingId = :listingId, updatedAt = :updatedAt, dirty = 1 WHERE assetId = :assetId")
    suspend fun markAsListed(assetId: String, listingId: String, listedAt: Long, updatedAt: Long)
    
    /** Delist asset (return to ACTIVE) */
    @Query("UPDATE farm_assets SET status = 'ACTIVE', listedAt = NULL, listingId = NULL, updatedAt = :updatedAt, dirty = 1 WHERE assetId = :assetId")
    suspend fun markAsDeListed(assetId: String, updatedAt: Long)
    
    /** Mark asset as sold */
    @Query("UPDATE farm_assets SET status = 'SOLD', soldAt = :soldAt, soldToUserId = :buyerId, soldPrice = :price, listedAt = NULL, listingId = NULL, updatedAt = :updatedAt, dirty = 1 WHERE assetId = :assetId")
    suspend fun markAsSold(assetId: String, buyerId: String, price: Double, soldAt: Long, updatedAt: Long)
    
    /** Get assets by status */
    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND status = :status AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun getAssetsByStatus(farmerId: String, status: String): Flow<List<FarmAssetEntity>>
    
    /** Get sold history */
    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND status = 'SOLD' AND isDeleted = 0 ORDER BY soldAt DESC")
    fun getSoldAssets(farmerId: String): Flow<List<FarmAssetEntity>>
    
    /** Check if asset is already listed */
    @Query("SELECT listingId FROM farm_assets WHERE assetId = :assetId")
    suspend fun getListingId(assetId: String): String?
    
    // ========================================
    // Lifecycle Stage Queries
    // ========================================
    
    /** Get dirty assets for sync */
    @Query("SELECT * FROM farm_assets WHERE dirty = 1 LIMIT :limit")
    suspend fun getDirtyAssets(limit: Int = 100): List<FarmAssetEntity>
    
    /** Clear dirty flag after sync */
    @Query("UPDATE farm_assets SET dirty = 0 WHERE assetId = :assetId")
    suspend fun clearDirty(assetId: String)
    
    /** Get assets for age update (have birthDate) */
    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND birthDate IS NOT NULL AND isDeleted = 0")
    suspend fun getAssetsWithBirthDate(farmerId: String): List<FarmAssetEntity>
    
    /** Update age weeks for asset */
    @Query("UPDATE farm_assets SET ageWeeks = :ageWeeks, updatedAt = :updatedAt WHERE assetId = :assetId")
    suspend fun updateAgeWeeks(assetId: String, ageWeeks: Int, updatedAt: Long)
    
    /** Update lifecycle sub-stage */
    @Query("UPDATE farm_assets SET lifecycleSubStage = :subStage, updatedAt = :updatedAt WHERE assetId = :assetId")
    suspend fun updateLifecycleSubStage(assetId: String, subStage: String, updatedAt: Long)
    
    /** Get market-ready assets (weight >= 1500g and not listed) */
    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND weightGrams >= :minWeight AND status = 'ACTIVE' AND listingId IS NULL AND isDeleted = 0")
    suspend fun getMarketReadyAssets(farmerId: String, minWeight: Double = 1500.0): List<FarmAssetEntity>
    
    /** Get assets needing vaccination (nextVaccinationDate is past or today) */
    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND nextVaccinationDate IS NOT NULL AND nextVaccinationDate <= :now AND isDeleted = 0")
    suspend fun getAssetsNeedingVaccination(farmerId: String, now: Long): List<FarmAssetEntity>
    
    /** Get all active farmer IDs (for batch processing) */
    @Query("SELECT DISTINCT farmerId FROM farm_assets WHERE isDeleted = 0")
    suspend fun getAllFarmerIds(): List<String>
    
    // ========================================
    // Cost-Per-Bird Analysis Queries
    // ========================================
    
    /** Get current quantity for an asset */
    @Query("SELECT quantity FROM farm_assets WHERE assetId = :assetId")
    suspend fun getCurrentQuantity(assetId: String): Double?
    
    /** Get assets ready to lay (females aged 18-22 weeks) */
    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND gender = 'FEMALE' AND ageWeeks >= 18 AND ageWeeks <= 22 AND isDeleted = 0")
    fun getReadyToLayBirds(farmerId: String): Flow<List<FarmAssetEntity>>
    
    /** Get cull candidates (aged > 72 weeks OR sick) */
    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND (ageWeeks > 72 OR healthStatus = 'SICK') AND isDeleted = 0")
    fun getCullCandidates(farmerId: String): Flow<List<FarmAssetEntity>>
    
    /** Get assets with vaccination due within the next N days */
    @Query("SELECT * FROM farm_assets WHERE farmerId = :farmerId AND nextVaccinationDate IS NOT NULL AND nextVaccinationDate BETWEEN :now AND :future AND isDeleted = 0")
    fun getVaccinationDueSoon(farmerId: String, now: Long, future: Long): Flow<List<FarmAssetEntity>>
    
    /** Count ready to lay birds */
    @Query("SELECT COUNT(*) FROM farm_assets WHERE farmerId = :farmerId AND gender = 'FEMALE' AND ageWeeks >= 18 AND ageWeeks <= 22 AND isDeleted = 0")
    fun countReadyToLayBirds(farmerId: String): Flow<Int>
    
    /** Count cull candidates */
    @Query("SELECT COUNT(*) FROM farm_assets WHERE farmerId = :farmerId AND (ageWeeks > 72 OR healthStatus = 'SICK') AND isDeleted = 0")
    fun countCullCandidates(farmerId: String): Flow<Int>
    
    /** Count vaccination due soon */
    @Query("SELECT COUNT(*) FROM farm_assets WHERE farmerId = :farmerId AND nextVaccinationDate IS NOT NULL AND nextVaccinationDate BETWEEN :now AND :future AND isDeleted = 0")
    fun countVaccinationDueSoon(farmerId: String, now: Long, future: Long): Flow<Int>
    
    /** Count active assets for a farmer (for profitability dashboard) */
    @Query("SELECT COUNT(*) FROM farm_assets WHERE farmerId = :farmerId AND status = 'ACTIVE' AND isDeleted = 0")
    suspend fun countActiveByFarmer(farmerId: String): Int
}

