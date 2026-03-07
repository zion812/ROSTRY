package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.InventoryItem
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for inventory operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface InventoryRepository {
    /**
     * Get all inventory items for a farmer.
     * @param farmerId The farmer ID
     * @return Flow of inventory items
     */
    fun getInventoryByFarmer(farmerId: String): Flow<List<InventoryItem>>

    /**
     * Get inventory item by ID.
     * @param itemId The item ID
     * @return Result containing the item or error
     */
    suspend fun getItemById(itemId: String): Result<InventoryItem>

    /**
     * Create a new inventory item.
     * @param item The item to create
     * @return Result containing the created item or error
     */
    suspend fun createItem(item: InventoryItem): Result<InventoryItem>

    /**
     * Update an inventory item.
     * @param item The updated item
     * @return Result indicating success or error
     */
    suspend fun updateItem(item: InventoryItem): Result<Unit>

    /**
     * Delete an inventory item.
     * @param itemId The item ID to delete
     * @return Result indicating success or error
     */
    suspend fun deleteItem(itemId: String): Result<Unit>

    /**
     * Get inventory by farm asset ID.
     * @param farmAssetId The farm asset ID
     * @return Flow of inventory items from the asset
     */
    fun getInventoryByFarmAsset(farmAssetId: String): Flow<List<InventoryItem>>

    /**
     * Update available quantity.
     * @param itemId The item ID
     * @param quantity The new quantity
     * @return Result indicating success or error
     */
    suspend fun updateQuantity(itemId: String, quantity: Int): Result<Unit>
}
