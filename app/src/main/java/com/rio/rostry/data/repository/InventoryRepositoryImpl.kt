package com.rio.rostry.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rio.rostry.data.database.dao.InventoryItemDao
import com.rio.rostry.data.database.entity.InventoryItemEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepositoryImpl @Inject constructor(
    private val dao: InventoryItemDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : InventoryRepository {

    override fun getInventoryByFarmer(farmerId: String): Flow<Resource<List<InventoryItemEntity>>> {
        return dao.getInventoryByFarmer(farmerId).map { Resource.Success(it) }
    }

    override fun getInventoryById(inventoryId: String): Flow<Resource<InventoryItemEntity?>> {
        return dao.getInventoryById(inventoryId).map { Resource.Success(it) }
    }

    override suspend fun addInventory(item: InventoryItemEntity): Resource<String> {
        return try {
            val toSave = item.copy(dirty = true)
            dao.upsert(toSave)
            try {
                firestore.collection("farm_inventory").document(item.inventoryId)
                    .set(toSave.copy(dirty = false), SetOptions.merge()).await()
                dao.upsert(toSave.copy(dirty = false))
            } catch (e: Exception) {
                Timber.e(e, "Failed to sync to Firestore, saved locally")
            }
            Resource.Success(item.inventoryId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to add inventory")
            Resource.Error(e.message ?: "Failed to save inventory locally")
        }
    }

    override suspend fun updateInventory(item: InventoryItemEntity): Resource<Unit> {
        return try {
            val toSave = item.copy(dirty = true, updatedAt = System.currentTimeMillis())
            dao.updateInventory(toSave)
            try {
                firestore.collection("farm_inventory").document(item.inventoryId)
                   .set(toSave.copy(dirty = false), SetOptions.merge()).await()
                dao.updateInventory(toSave.copy(dirty = false))
            } catch (e: Exception) {
                Timber.e(e, "Failed to sync to Firestore, saved locally")
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update inventory")
            Resource.Error(e.message ?: "Failed to update inventory locally")
        }
    }

    override suspend fun updateAvailableQuantity(inventoryId: String, quantity: Double): Resource<Unit> {
        dao.updateAvailableQuantity(inventoryId, quantity, System.currentTimeMillis())
        return Resource.Success(Unit)
    }

    override suspend fun createInventoryFromAsset(
        assetId: String,
        quantityAllocated: Double,
        unit: String,
        sku: String?
    ): Resource<String> {
        val newItem = InventoryItemEntity(
            inventoryId = UUID.randomUUID().toString(),
            farmerId = auth.currentUser?.uid ?: return Resource.Error("No user"),
            sourceAssetId = assetId,
            name = "Inventory form Asset", // Should be passed or looked up
            category = "General", // Should look up asset category
            quantityAvailable = quantityAllocated,
            unit = unit,
            sku = sku,
            dirty = true
        )
        // Note: Real implementation should also decrease quantity of Asset if applicable
        return addInventory(newItem)
    }

    override suspend fun syncInventory(): Resource<Unit> {
         val userId = auth.currentUser?.uid ?: return Resource.Error("No user")
         return try {
             val snapshot = firestore.collection("farm_inventory").whereEqualTo("farmerId", userId).get().await()
             val remote = snapshot.toObjects(InventoryItemEntity::class.java)
             remote.forEach { dao.upsert(it) } // Simplified overwrite
             Resource.Success(Unit)
         } catch (e: Exception) {
             Resource.Error(e.message ?: "Failed")
         }
    }
    
    override fun getAllInventory(): Flow<Resource<List<InventoryItemEntity>>> {
        return dao.getAllInventory().map { Resource.Success(it) }
    }
    
    /**
     * Allocate inventory for an order - reserves stock to prevent over-selling.
     */
    override suspend fun allocateInventory(inventoryId: String, quantity: Double): Resource<Unit> {
        return try {
            val item = dao.getInventoryByIdSync(inventoryId)
                ?: return Resource.Error("Inventory not found")
            
            if (item.quantityAvailable < quantity) {
                return Resource.Error("Insufficient stock: ${item.quantityAvailable} available, $quantity requested")
            }
            
            val updated = item.copy(
                quantityAvailable = item.quantityAvailable - quantity,
                quantityReserved = (item.quantityReserved ?: 0.0) + quantity,
                dirty = true,
                updatedAt = System.currentTimeMillis()
            )
            
            dao.updateInventory(updated)
            Timber.d("Allocated $quantity units from inventory $inventoryId")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to allocate inventory")
            Resource.Error(e.message ?: "Failed to allocate inventory")
        }
    }
    
    /**
     * Release inventory back to available stock (e.g., cancelled order).
     */
    override suspend fun releaseInventory(inventoryId: String, quantity: Double): Resource<Unit> {
        return try {
            val item = dao.getInventoryByIdSync(inventoryId)
                ?: return Resource.Error("Inventory not found")
            
            val reserved = item.quantityReserved ?: 0.0
            val releaseAmount = quantity.coerceAtMost(reserved)
            
            val updated = item.copy(
                quantityAvailable = item.quantityAvailable + releaseAmount,
                quantityReserved = reserved - releaseAmount,
                dirty = true,
                updatedAt = System.currentTimeMillis()
            )
            
            dao.updateInventory(updated)
            Timber.d("Released $releaseAmount units back to inventory $inventoryId")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to release inventory")
            Resource.Error(e.message ?: "Failed to release inventory")
        }
    }
    
    /**
     * Confirm sold inventory - removes from reserved (after successful delivery).
     * Since InventoryItemEntity doesn't track sold quantity, we just reduce reserved.
     */
    override suspend fun confirmSold(inventoryId: String, quantity: Double): Resource<Unit> {
        return try {
            val item = dao.getInventoryByIdSync(inventoryId)
                ?: return Resource.Error("Inventory not found")
            
            val reserved = item.quantityReserved ?: 0.0
            val confirmAmount = quantity.coerceAtMost(reserved)
            
            val updated = item.copy(
                quantityReserved = reserved - confirmAmount,
                dirty = true,
                updatedAt = System.currentTimeMillis()
            )
            
            dao.updateInventory(updated)
            Timber.d("Confirmed sale of $confirmAmount units from inventory $inventoryId")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to confirm sold inventory")
            Resource.Error(e.message ?: "Failed to confirm sold")
        }
    }
}
