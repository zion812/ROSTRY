package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.InventoryItemEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    
    fun getInventoryByFarmer(farmerId: String): Flow<Resource<List<InventoryItemEntity>>>
    
    fun getInventoryById(inventoryId: String): Flow<Resource<InventoryItemEntity?>>
    
    suspend fun addInventory(item: InventoryItemEntity): Resource<String>
    
    suspend fun updateInventory(item: InventoryItemEntity): Resource<Unit>
    
    suspend fun updateAvailableQuantity(inventoryId: String, quantity: Double): Resource<Unit>
    
    /**
     * Converts a Farm Asset into Inventory items (partial or full).
     */
    suspend fun createInventoryFromAsset(
        assetId: String, 
        quantityAllocated: Double,
        unit: String,
        sku: String? = null
    ): Resource<String>
    
    suspend fun syncInventory(): Resource<Unit>
    
    /**
     * Returns all inventory items as a Flow. Used for market listing conversion
     * where we need to map inventory data (quantity, unit) to product entities.
     */
    fun getAllInventory(): Flow<Resource<List<InventoryItemEntity>>>
    
    /**
     * Allocate inventory for an order - reserves stock to prevent over-selling.
     */
    suspend fun allocateInventory(inventoryId: String, quantity: Double): Resource<Unit>
    
    /**
     * Release inventory back to available stock (e.g., cancelled order).
     */
    suspend fun releaseInventory(inventoryId: String, quantity: Double): Resource<Unit>
    
    /**
     * Confirm sold inventory - removes from reserved (after successful delivery).
     */
    suspend fun confirmSold(inventoryId: String, quantity: Double): Resource<Unit>
}
