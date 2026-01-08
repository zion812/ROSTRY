package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for DailyLogEntity.
 *
 * NOTE: Daily logs are LOCAL-ONLY and do not sync to Firestore.
 * This is part of the "Split-Brain" data architecture where heavy
 * logging data stays on-device for efficiency.
 */
@Dao
interface DailyLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: DailyLogEntity)

    @Query("SELECT * FROM daily_logs WHERE productId = :productId ORDER BY logDate DESC")
    fun observeForProduct(productId: String): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE farmerId = :farmerId AND logDate BETWEEN :start AND :end ORDER BY logDate DESC")
    fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE farmerId = :farmerId AND logDate = :date")
    suspend fun getByFarmerAndDate(farmerId: String, date: Long): List<DailyLogEntity>

    @Query("SELECT * FROM daily_logs WHERE productId = :productId AND logDate = :date LIMIT 1")
    suspend fun getByProductAndDate(productId: String, date: Long): DailyLogEntity?

    @Query("SELECT COUNT(*) FROM daily_logs WHERE farmerId = :farmerId AND logDate BETWEEN :start AND :end")
    fun observeCountForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<Int>

    @Query("DELETE FROM daily_logs WHERE logId = :logId")
    suspend fun delete(logId: String)

    @Query("SELECT * FROM daily_logs WHERE logId = :logId LIMIT 1")
    suspend fun getById(logId: String): DailyLogEntity?

    @Query("SELECT COUNT(*) FROM daily_logs WHERE farmerId = :farmerId")
    suspend fun getLogCountForFarmer(farmerId: String): Int

    // =========================================================================
    // SUGGESTION QUERIES (for pre-filling Quick Log values)
    // =========================================================================

    /** Get the most recent feed amount logged by this farmer (for pre-filling Quick Log). */
    @Query("SELECT feedKg FROM daily_logs WHERE farmerId = :farmerId AND feedKg IS NOT NULL ORDER BY logDate DESC LIMIT 1")
    suspend fun getLastFeedAmount(farmerId: String): Double?

    // =========================================================================
    // AGGREGATE QUERIES (for BatchSummary generation by LifecycleWorker)
    // =========================================================================

    /** Total feed consumed (kg) in the date range for a farmer. */
    @Query("SELECT COALESCE(SUM(feedKg), 0.0) FROM daily_logs WHERE farmerId = :farmerId AND logDate BETWEEN :start AND :end")
    suspend fun getTotalFeedBetween(farmerId: String, start: Long, end: Long): Double

    /** Average weight (grams) of logged entries in the date range for a farmer. */
    @Query("SELECT COALESCE(AVG(weightGrams), 0.0) FROM daily_logs WHERE farmerId = :farmerId AND logDate BETWEEN :start AND :end AND weightGrams IS NOT NULL")
    suspend fun getAverageWeightBetween(farmerId: String, start: Long, end: Long): Double

    /** Total number of unique products logged in the date range. */
    @Query("SELECT COUNT(DISTINCT productId) FROM daily_logs WHERE farmerId = :farmerId AND logDate BETWEEN :start AND :end")
    suspend fun getActiveProductCountBetween(farmerId: String, start: Long, end: Long): Int
}

