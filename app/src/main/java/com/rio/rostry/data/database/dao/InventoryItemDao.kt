package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.InventoryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: InventoryItemEntity)

    @Update
    suspend fun updateInventory(item: InventoryItemEntity)

    @Query("SELECT * FROM farm_inventory WHERE inventoryId = :id")
    fun getInventoryById(id: String): Flow<InventoryItemEntity?>

    @Query("SELECT * FROM farm_inventory WHERE farmerId = :farmerId ORDER BY name ASC")
    fun getInventoryByFarmer(farmerId: String): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM farm_inventory WHERE sourceAssetId = :assetId")
    suspend fun getInventoryBySourceAsset(assetId: String): List<InventoryItemEntity>

    @Query("UPDATE farm_inventory SET quantityAvailable = :qty, updatedAt = :updatedAt, dirty = 1 WHERE inventoryId = :id")
    suspend fun updateAvailableQuantity(id: String, qty: Double, updatedAt: Long)
    
    // Sync Support
    @Query("SELECT * FROM farm_inventory WHERE updatedAt > :sinceTime LIMIT :limit")
    suspend fun getUpdatedSince(sinceTime: Long, limit: Int = 100): List<InventoryItemEntity>
    
    /**
     * Returns all inventory items. Used for market listing conversion where we need
     * to map inventory data (quantity, unit) to product entities.
     */
    @Query("SELECT * FROM farm_inventory")
    fun getAllInventory(): Flow<List<InventoryItemEntity>>
}
