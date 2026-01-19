package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for FarmActivityLogEntity - farm-level activity tracking.
 */
@Dao
interface FarmActivityLogDao {
    
    @Upsert
    suspend fun upsert(log: FarmActivityLogEntity)
    
    @Upsert
    suspend fun upsertAll(logs: List<FarmActivityLogEntity>)
    
    @Query("SELECT * FROM farm_activity_logs WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLogEntity>>

    @Query("SELECT * FROM farm_activity_logs WHERE farmerId = :farmerId ORDER BY createdAt DESC LIMIT 1")
    fun observeLatestForFarmer(farmerId: String): Flow<FarmActivityLogEntity?>
    
    @Query("SELECT * FROM farm_activity_logs WHERE farmerId = :farmerId AND activityType = :type ORDER BY createdAt DESC")
    fun observeForFarmerByType(farmerId: String, type: String): Flow<List<FarmActivityLogEntity>>
    
    @Query("SELECT * FROM farm_activity_logs WHERE farmerId = :farmerId AND createdAt BETWEEN :start AND :end ORDER BY createdAt DESC")
    fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<FarmActivityLogEntity>>
    
    @Query("SELECT * FROM farm_activity_logs WHERE productId = :productId ORDER BY createdAt DESC")
    fun observeForProduct(productId: String): Flow<List<FarmActivityLogEntity>>
    
    @Query("SELECT SUM(amountInr) FROM farm_activity_logs WHERE farmerId = :farmerId AND activityType = 'EXPENSE' AND createdAt BETWEEN :start AND :end")
    suspend fun getTotalExpensesBetween(farmerId: String, start: Long, end: Long): Double?
    
    @Query("SELECT * FROM farm_activity_logs WHERE activityId = :activityId")
    suspend fun getById(activityId: String): FarmActivityLogEntity?
    
    @Query("SELECT * FROM farm_activity_logs WHERE dirty = 1 AND farmerId = :farmerId")
    suspend fun getDirty(farmerId: String): List<FarmActivityLogEntity>
    
    @Query("UPDATE farm_activity_logs SET dirty = 0, syncedAt = :syncedAt WHERE activityId IN (:ids)")
    suspend fun markSynced(ids: List<String>, syncedAt: Long)
    
    @Query("SELECT COUNT(*) FROM farm_activity_logs WHERE farmerId = :farmerId")
    suspend fun getCountForFarmer(farmerId: String): Int
    
    @Query("DELETE FROM farm_activity_logs WHERE activityId = :activityId")
    suspend fun delete(activityId: String)
    
    // ========================================
    // Cost-Per-Bird Analysis Queries
    // ========================================
    
    /** Get total expenses for a specific asset (bird/batch) within a date range */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = :assetId AND activityType IN ('EXPENSE', 'FEED', 'MEDICATION') AND createdAt BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpensesForAsset(assetId: String, startDate: Long, endDate: Long): Double
    
    /** Get total expenses for a specific asset (all time) */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = :assetId AND activityType IN ('EXPENSE', 'FEED', 'MEDICATION')")
    suspend fun getTotalExpensesForAssetAllTime(assetId: String): Double
    
    /** Get feed expenses for a specific asset */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = :assetId AND activityType = 'FEED'")
    suspend fun getTotalFeedExpensesForAsset(assetId: String): Double
    
    /** Get medication expenses for a specific asset */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = :assetId AND activityType = 'MEDICATION'")
    suspend fun getTotalMedicationExpensesForAsset(assetId: String): Double
    
    /** Get general expenses for a specific asset */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE productId = :assetId AND activityType = 'EXPENSE'")
    suspend fun getTotalOtherExpensesForAsset(assetId: String): Double
    
    // ========================================
    // Farm-Level Aggregate Queries (Profitability Dashboard)
    // ========================================
    
    /** Get total feed expenses across all assets for a farmer */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = :farmerId AND activityType = 'FEED'")
    suspend fun getTotalFeedExpensesByFarmer(farmerId: String): Double
    
    /** Get total medication expenses across all assets for a farmer */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = :farmerId AND activityType = 'MEDICATION'")
    suspend fun getTotalMedicationExpensesByFarmer(farmerId: String): Double
    
    /** Get total other expenses across all assets for a farmer */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = :farmerId AND activityType = 'EXPENSE'")
    suspend fun getTotalOtherExpensesByFarmer(farmerId: String): Double
    
    /** Get total expenses between dates for a farmer (for monthly reports) */
    @Query("SELECT COALESCE(SUM(amountInr), 0.0) FROM farm_activity_logs WHERE farmerId = :farmerId AND createdAt BETWEEN :start AND :end")
    suspend fun getTotalExpensesByFarmerBetween(farmerId: String, start: Long, end: Long): Double
}

