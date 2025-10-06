package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingPairDao {
    @Upsert
    suspend fun upsert(pair: BreedingPairEntity)

    @Query("SELECT * FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE' ORDER BY pairedAt DESC")
    fun observeActive(farmerId: String): Flow<List<BreedingPairEntity>>

    @Query("SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun countActive(farmerId: String): Int

    @Query("SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE' AND maleProductId = :maleProductId")
    suspend fun countActiveByMale(farmerId: String, maleProductId: String): Int

    @Query("SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE' AND femaleProductId = :femaleProductId")
    suspend fun countActiveByFemale(farmerId: String, femaleProductId: String): Int

    @Query("SELECT * FROM breeding_pairs WHERE pairId = :pairId")
    suspend fun getById(pairId: String): BreedingPairEntity?

    @Query("SELECT * FROM breeding_pairs WHERE dirty = 1")
    suspend fun getDirty(): List<BreedingPairEntity>

    @Query("UPDATE breeding_pairs SET dirty = 0, syncedAt = :syncedAt WHERE pairId IN (:pairIds)")
    suspend fun clearDirty(pairIds: List<String>, syncedAt: Long)
}

@Dao
interface FarmAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: FarmAlertEntity)

    @Upsert
    suspend fun upsert(alert: FarmAlertEntity)

    @Query("SELECT * FROM farm_alerts WHERE farmerId = :farmerId AND isRead = 0 ORDER BY createdAt DESC")
    fun observeUnread(farmerId: String): Flow<List<FarmAlertEntity>>

    @Query("SELECT COUNT(*) FROM farm_alerts WHERE farmerId = :farmerId AND isRead = 0")
    suspend fun countUnread(farmerId: String): Int

    @Query("UPDATE farm_alerts SET isRead = 1 WHERE alertId = :alertId")
    suspend fun markRead(alertId: String)

    @Query("DELETE FROM farm_alerts WHERE expiresAt IS NOT NULL AND expiresAt < :now")
    suspend fun deleteExpired(now: Long)

    @Query("SELECT * FROM farm_alerts WHERE farmerId = :farmerId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getByFarmer(farmerId: String, limit: Int = 50): List<FarmAlertEntity>

    @Query("SELECT * FROM farm_alerts WHERE dirty = 1")
    suspend fun getDirty(): List<FarmAlertEntity>

    @Query("UPDATE farm_alerts SET dirty = 0, syncedAt = :syncedAt WHERE alertId IN (:alertIds)")
    suspend fun clearDirty(alertIds: List<String>, syncedAt: Long)
}

@Dao
interface ListingDraftDao {
    @Upsert
    suspend fun upsert(draft: ListingDraftEntity)

    @Query("SELECT * FROM listing_drafts WHERE farmerId = :farmerId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getByFarmer(farmerId: String): ListingDraftEntity?

    @Query("DELETE FROM listing_drafts WHERE draftId = :draftId")
    suspend fun delete(draftId: String)

    @Query("DELETE FROM listing_drafts WHERE expiresAt IS NOT NULL AND expiresAt < :now")
    suspend fun deleteExpired(now: Long)
}

@Dao
interface FarmerDashboardSnapshotDao {
    @Upsert
    suspend fun upsert(snapshot: FarmerDashboardSnapshotEntity)

    @Query("SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = :farmerId ORDER BY weekStartAt DESC LIMIT 1")
    suspend fun getLatest(farmerId: String): FarmerDashboardSnapshotEntity?

    @Query("SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = :farmerId ORDER BY weekStartAt DESC LIMIT 1")
    fun observeLatest(farmerId: String): Flow<FarmerDashboardSnapshotEntity?>

    @Query("SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = :farmerId AND weekStartAt = :weekStartAt")
    suspend fun getByWeek(farmerId: String, weekStartAt: Long): FarmerDashboardSnapshotEntity?

    @Query("SELECT * FROM farmer_dashboard_snapshots WHERE dirty = 1")
    suspend fun getDirty(): List<FarmerDashboardSnapshotEntity>

    @Query("UPDATE farmer_dashboard_snapshots SET dirty = 0, syncedAt = :syncedAt WHERE snapshotId IN (:snapshotIds)")
    suspend fun clearDirty(snapshotIds: List<String>, syncedAt: Long)
}
