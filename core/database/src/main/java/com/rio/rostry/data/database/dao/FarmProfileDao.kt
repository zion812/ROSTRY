package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.FarmProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmProfileDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: FarmProfileEntity)
    
    @Update
    suspend fun update(profile: FarmProfileEntity)
    
    @Query("SELECT * FROM farm_profiles WHERE farmerId = :farmerId")
    fun observeProfile(farmerId: String): Flow<FarmProfileEntity?>
    
    @Query("SELECT * FROM farm_profiles WHERE farmerId = :farmerId")
    suspend fun findById(farmerId: String): FarmProfileEntity?
    
    // ========================================
    // Trust Score & Metrics
    // ========================================
    
    @Query("UPDATE farm_profiles SET trustScore = :score, updatedAt = :updatedAt, dirty = 1 WHERE farmerId = :farmerId")
    suspend fun updateTrustScore(farmerId: String, score: Int, updatedAt: Long)
    
    @Query("UPDATE farm_profiles SET totalBirdsSold = totalBirdsSold + :count, totalOrdersCompleted = totalOrdersCompleted + 1, updatedAt = :updatedAt, dirty = 1 WHERE farmerId = :farmerId")
    suspend fun incrementSales(farmerId: String, count: Int, updatedAt: Long)
    
    @Query("UPDATE farm_profiles SET vaccinationRate = :rate, updatedAt = :updatedAt, dirty = 1 WHERE farmerId = :farmerId")
    suspend fun updateVaccinationRate(farmerId: String, rate: Int, updatedAt: Long)
    
    @Query("UPDATE farm_profiles SET badgesJson = :badges, updatedAt = :updatedAt, dirty = 1 WHERE farmerId = :farmerId")
    suspend fun updateBadges(farmerId: String, badges: String, updatedAt: Long)
    
    // ========================================
    // Verification
    // ========================================
    
    @Query("UPDATE farm_profiles SET isVerified = 1, verifiedAt = :verifiedAt, updatedAt = :updatedAt, dirty = 1 WHERE farmerId = :farmerId")
    suspend fun markVerified(farmerId: String, verifiedAt: Long, updatedAt: Long)
    
    // ========================================
    // Privacy Settings
    // ========================================
    
    @Query("UPDATE farm_profiles SET isPublic = :isPublic, updatedAt = :updatedAt, dirty = 1 WHERE farmerId = :farmerId")
    suspend fun setProfileVisibility(farmerId: String, isPublic: Boolean, updatedAt: Long)
    
    @Query("UPDATE farm_profiles SET showTimeline = :show, updatedAt = :updatedAt, dirty = 1 WHERE farmerId = :farmerId")
    suspend fun setTimelineVisibility(farmerId: String, show: Boolean, updatedAt: Long)
    
    // ========================================
    // Discovery (Public Profiles)
    // ========================================
    
    @Query("SELECT * FROM farm_profiles WHERE isPublic = 1 AND isVerified = 1 ORDER BY trustScore DESC LIMIT :limit")
    fun getTopFarms(limit: Int = 20): Flow<List<FarmProfileEntity>>
    
    @Query("SELECT * FROM farm_profiles WHERE isPublic = 1 AND province = :province ORDER BY trustScore DESC LIMIT :limit")
    fun getFarmsByProvince(province: String, limit: Int = 50): Flow<List<FarmProfileEntity>>
    
    @Query("SELECT * FROM farm_profiles WHERE isPublic = 1 AND farmName LIKE '%' || :query || '%' LIMIT :limit")
    suspend fun searchFarms(query: String, limit: Int = 20): List<FarmProfileEntity>
    
    // ========================================
    // Sync
    // ========================================
    
    @Query("SELECT * FROM farm_profiles WHERE dirty = 1 LIMIT :limit")
    suspend fun getDirtyProfiles(limit: Int = 50): List<FarmProfileEntity>
    
    @Query("UPDATE farm_profiles SET dirty = 0 WHERE farmerId = :farmerId")
    suspend fun clearDirty(farmerId: String)
}
