package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow



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

    @Query("DELETE FROM farm_alerts WHERE farmerId = :farmerId AND isRead = 1")
    suspend fun deleteReadAlerts(farmerId: String)

    @Query("SELECT COUNT(*) FROM farm_alerts WHERE farmerId = :farmerId")
    suspend fun getAlertCountForFarmer(farmerId: String): Int
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

    @Query("SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = :farmerId ORDER BY weekStartAt DESC LIMIT :limit")
    fun observeLastN(farmerId: String, limit: Int): Flow<List<FarmerDashboardSnapshotEntity>>

    @Query("SELECT * FROM farmer_dashboard_snapshots WHERE farmerId = :farmerId ORDER BY weekStartAt DESC LIMIT :limit")
    suspend fun getLastN(farmerId: String, limit: Int): List<FarmerDashboardSnapshotEntity>

    @Query("SELECT * FROM farmer_dashboard_snapshots WHERE dirty = 1")
    suspend fun getDirty(): List<FarmerDashboardSnapshotEntity>

    @Query("UPDATE farmer_dashboard_snapshots SET dirty = 0, syncedAt = :syncedAt WHERE snapshotId IN (:snapshotIds)")
    suspend fun clearDirty(snapshotIds: List<String>, syncedAt: Long)
}
