package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.ProfitabilityMetricsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for profitability metrics operations.
 */
@Dao
interface ProfitabilityMetricsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metrics: ProfitabilityMetricsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(metrics: List<ProfitabilityMetricsEntity>)

    @Query("""
        SELECT * FROM profitability_metrics 
        WHERE entity_id = :entityId 
        AND entity_type = :entityType 
        AND period_start >= :periodStart 
        AND period_end <= :periodEnd
        ORDER BY period_start DESC
    """)
    suspend fun getMetrics(
        entityId: String,
        entityType: String,
        periodStart: Long,
        periodEnd: Long
    ): List<ProfitabilityMetricsEntity>

    @Query("""
        SELECT * FROM profitability_metrics 
        WHERE entity_id = :entityId 
        AND entity_type = :entityType 
        ORDER BY period_start DESC
        LIMIT 1
    """)
    suspend fun getLatestMetrics(
        entityId: String,
        entityType: String
    ): ProfitabilityMetricsEntity?

    @Query("""
        SELECT * FROM profitability_metrics 
        WHERE entity_type = :entityType 
        AND period_start >= :periodStart 
        AND period_end <= :periodEnd
        ORDER BY profit DESC
        LIMIT :limit
    """)
    suspend fun getTopPerformers(
        entityType: String,
        periodStart: Long,
        periodEnd: Long,
        limit: Int = 10
    ): List<ProfitabilityMetricsEntity>

    @Query("""
        SELECT * FROM profitability_metrics 
        WHERE entity_id = :entityId 
        AND entity_type = :entityType
        ORDER BY period_start DESC
    """)
    fun observeMetrics(
        entityId: String,
        entityType: String
    ): Flow<List<ProfitabilityMetricsEntity>>

    @Query("DELETE FROM profitability_metrics WHERE calculated_at < :beforeTimestamp")
    suspend fun deleteOldMetrics(beforeTimestamp: Long)

    @Query("DELETE FROM profitability_metrics")
    suspend fun deleteAll()
}
