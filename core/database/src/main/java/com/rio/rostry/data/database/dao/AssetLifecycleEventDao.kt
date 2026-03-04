package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.AssetLifecycleEventEntity

@Dao
interface AssetLifecycleEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: AssetLifecycleEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<AssetLifecycleEventEntity>)

    @Query("SELECT * FROM asset_lifecycle_events WHERE assetId = :assetId ORDER BY occurredAt DESC")
    suspend fun getEventsForAsset(assetId: String): List<AssetLifecycleEventEntity>

    @Query("SELECT * FROM asset_lifecycle_events WHERE eventId = :eventId")
    suspend fun getEventById(eventId: String): AssetLifecycleEventEntity?
}
