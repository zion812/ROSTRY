package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.BatchSummaryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for BatchSummaryEntity.
 *
 * This entity is part of the "Split-Brain" data architecture where
 * only lightweight summaries sync to Firestore (not raw logs).
 */
@Dao
interface BatchSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: BatchSummaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(summaries: List<BatchSummaryEntity>)

    @Query("SELECT * FROM batch_summaries WHERE farmerId = :farmerId ORDER BY updatedAt DESC")
    fun observeForFarmer(farmerId: String): Flow<List<BatchSummaryEntity>>

    @Query("SELECT * FROM batch_summaries WHERE farmerId = :farmerId AND status = :status ORDER BY updatedAt DESC")
    fun observeByStatus(farmerId: String, status: String): Flow<List<BatchSummaryEntity>>

    @Query("SELECT * FROM batch_summaries WHERE batchId = :batchId LIMIT 1")
    suspend fun getById(batchId: String): BatchSummaryEntity?

    @Query("SELECT * FROM batch_summaries WHERE farmerId = :farmerId")
    suspend fun getAllForFarmer(farmerId: String): List<BatchSummaryEntity>

    // =========================================================================
    // SYNC METHODS (these summaries DO sync to Firestore)
    // =========================================================================

    @Query("SELECT * FROM batch_summaries WHERE dirty = 1")
    suspend fun getDirty(): List<BatchSummaryEntity>

    @Query("UPDATE batch_summaries SET dirty = 0, syncedAt = :syncedAt WHERE batchId IN (:batchIds)")
    suspend fun clearDirty(batchIds: List<String>, syncedAt: Long)

    // =========================================================================
    // AGGREGATE QUERIES
    // =========================================================================

    @Query("SELECT COUNT(*) FROM batch_summaries WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun getActiveBatchCount(farmerId: String): Int

    @Query("SELECT COALESCE(SUM(currentCount), 0) FROM batch_summaries WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun getTotalBirdCount(farmerId: String): Int

    @Query("SELECT COALESCE(AVG(fcr), 0.0) FROM batch_summaries WHERE farmerId = :farmerId AND status = 'ACTIVE' AND fcr > 0")
    suspend fun getAverageFcr(farmerId: String): Double

    @Query("DELETE FROM batch_summaries WHERE batchId = :batchId")
    suspend fun delete(batchId: String)
}
