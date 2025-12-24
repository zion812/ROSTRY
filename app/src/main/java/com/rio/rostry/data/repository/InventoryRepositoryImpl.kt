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
            firestore.collection("farm_inventory").document(item.inventoryId)
                .set(toSave.copy(dirty = false), SetOptions.merge()).await()
            dao.upsert(toSave.copy(dirty = false))
            Resource.Success(item.inventoryId)
        } catch (e: Exception) {
            Resource.Success(item.inventoryId)
        }
    }

    override suspend fun updateInventory(item: InventoryItemEntity): Resource<Unit> {
        return try {
            val toSave = item.copy(dirty = true, updatedAt = System.currentTimeMillis())
            dao.updateInventory(toSave)
            firestore.collection("farm_inventory").document(item.inventoryId)
                .set(toSave.copy(dirty = false), SetOptions.merge()).await()
            dao.updateInventory(toSave.copy(dirty = false))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Local update only")
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
}
