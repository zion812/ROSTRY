package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.DeliveryHubEntity

@Dao
interface DeliveryHubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(hub: DeliveryHubEntity)

    @Update
    suspend fun update(hub: DeliveryHubEntity)

    @Query("SELECT * FROM delivery_hubs")
    suspend fun listAll(): List<DeliveryHubEntity>

    // Simple bounding-box based proximity (approx). Caller narrows box.
    @Query("SELECT * FROM delivery_hubs WHERE (:minLat IS NULL OR latitude >= :minLat) AND (:maxLat IS NULL OR latitude <= :maxLat) AND (:minLng IS NULL OR longitude >= :minLng) AND (:maxLng IS NULL OR longitude <= :maxLng)")
    suspend fun findInBox(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?): List<DeliveryHubEntity>
}
