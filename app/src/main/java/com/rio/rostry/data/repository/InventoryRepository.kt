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
}
