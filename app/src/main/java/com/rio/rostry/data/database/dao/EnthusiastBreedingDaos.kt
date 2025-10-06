package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rio.rostry.data.database.entity.EggCollectionEntity
import com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity
import com.rio.rostry.data.database.entity.MatingLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatingLogDao {
    @Upsert
    suspend fun upsert(log: MatingLogEntity)

    @Query("SELECT * FROM mating_logs WHERE pairId = :pairId ORDER BY matedAt DESC")
    fun observeByPair(pairId: String): Flow<List<MatingLogEntity>>

    @Query("SELECT * FROM mating_logs WHERE farmerId = :farmerId ORDER BY matedAt DESC LIMIT :limit")
    suspend fun getRecentByFarmer(farmerId: String, limit: Int): List<MatingLogEntity>

    @Query("SELECT COUNT(*) FROM mating_logs WHERE pairId = :pairId AND matedAt BETWEEN :start AND :end")
    suspend fun countByPairBetween(pairId: String, start: Long, end: Long): Int

    // Pairs-to-mate aggregation
    data class MatingLast(val pairId: String, val lastMated: Long?)

    @Query("SELECT pairId AS pairId, MAX(matedAt) AS lastMated FROM mating_logs WHERE farmerId = :farmerId GROUP BY pairId")
    fun observeLastMatedByFarmer(farmerId: String): Flow<List<MatingLast>>

    @Query("SELECT * FROM mating_logs WHERE dirty = 1")
    suspend fun getDirty(): List<MatingLogEntity>

    @Query("UPDATE mating_logs SET dirty = 0, syncedAt = :syncedAt WHERE logId IN (:logIds)")
    suspend fun clearDirty(logIds: List<String>, syncedAt: Long)
}

@Dao
interface EggCollectionDao {
    @Upsert
    suspend fun upsert(collection: EggCollectionEntity)

    @Query("SELECT * FROM egg_collections WHERE pairId = :pairId ORDER BY collectedAt DESC")
    fun observeByPair(pairId: String): Flow<List<EggCollectionEntity>>

    @Query("SELECT IFNULL(SUM(eggsCollected), 0) FROM egg_collections WHERE pairId = :pairId")
    suspend fun getTotalEggsByPair(pairId: String): Int

    @Query("SELECT * FROM egg_collections WHERE collectedAt BETWEEN :start AND :end ORDER BY collectedAt DESC")
    suspend fun getCollectionsDueBetween(start: Long, end: Long): List<EggCollectionEntity>

    @Query("SELECT IFNULL(SUM(eggsCollected), 0) FROM egg_collections WHERE farmerId = :farmerId AND collectedAt BETWEEN :start AND :end")
    suspend fun countEggsForFarmerBetween(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT * FROM egg_collections WHERE farmerId = :farmerId ORDER BY collectedAt DESC LIMIT :limit")
    fun observeRecentByFarmer(farmerId: String, limit: Int): Flow<List<EggCollectionEntity>>

    @Query("SELECT * FROM egg_collections WHERE collectionId = :collectionId LIMIT 1")
    suspend fun getById(collectionId: String): EggCollectionEntity?

    @Query("SELECT * FROM egg_collections WHERE dirty = 1")
    suspend fun getDirty(): List<EggCollectionEntity>

    @Query("UPDATE egg_collections SET dirty = 0, syncedAt = :syncedAt WHERE collectionId IN (:collectionIds)")
    suspend fun clearDirty(collectionIds: List<String>, syncedAt: Long)
}

@Dao
interface EnthusiastDashboardSnapshotDao {
    @Upsert
    suspend fun upsert(snapshot: EnthusiastDashboardSnapshotEntity)

    @Query("SELECT * FROM enthusiast_dashboard_snapshots WHERE userId = :userId ORDER BY weekStartAt DESC LIMIT 1")
    suspend fun getLatest(userId: String): EnthusiastDashboardSnapshotEntity?

    @Query("SELECT * FROM enthusiast_dashboard_snapshots WHERE userId = :userId ORDER BY weekStartAt DESC LIMIT 1")
    fun observeLatest(userId: String): Flow<EnthusiastDashboardSnapshotEntity?>

    @Query("SELECT * FROM enthusiast_dashboard_snapshots WHERE userId = :userId AND weekStartAt = :weekStartAt LIMIT 1")
    suspend fun getByWeek(userId: String, weekStartAt: Long): EnthusiastDashboardSnapshotEntity?

    @Query("SELECT * FROM enthusiast_dashboard_snapshots WHERE dirty = 1")
    suspend fun getDirty(): List<EnthusiastDashboardSnapshotEntity>

    @Query("UPDATE enthusiast_dashboard_snapshots SET dirty = 0, syncedAt = :syncedAt WHERE snapshotId IN (:snapshotIds)")
    suspend fun clearDirty(snapshotIds: List<String>, syncedAt: Long)
}
