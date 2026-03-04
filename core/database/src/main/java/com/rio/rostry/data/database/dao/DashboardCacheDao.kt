package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.DashboardCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for DashboardCacheEntity.
 *
 * Provides access to pre-computed dashboard statistics for instant loading.
 * Cache is populated by LifecycleWorker running in the background.
 */
@Dao
interface DashboardCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: DashboardCacheEntity)

    @Query("SELECT * FROM dashboard_cache WHERE farmerId = :farmerId LIMIT 1")
    suspend fun getForFarmer(farmerId: String): DashboardCacheEntity?

    @Query("SELECT * FROM dashboard_cache WHERE farmerId = :farmerId LIMIT 1")
    fun observeForFarmer(farmerId: String): Flow<DashboardCacheEntity?>

    @Query("SELECT computedAt FROM dashboard_cache WHERE farmerId = :farmerId LIMIT 1")
    suspend fun getLastComputedAt(farmerId: String): Long?

    @Query("DELETE FROM dashboard_cache WHERE farmerId = :farmerId")
    suspend fun deleteForFarmer(farmerId: String)

    @Query("DELETE FROM dashboard_cache WHERE computedAt < :olderThan")
    suspend fun deleteStale(olderThan: Long)
}
