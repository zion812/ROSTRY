package com.rio.rostry.data.farm.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.InventoryItem
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.farm.repository.InventoryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of InventoryRepository using Firebase Firestore and Room.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class InventoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : InventoryRepository {

    private val inventoryCollection = firestore.collection("inventory_items")

    override fun getInventoryByFarmer(farmerId: String): Flow<List<InventoryItem>> = callbackFlow {
        val listener = inventoryCollection
            .whereEqualTo("farmerId", farmerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull {
                    it.toObject(InventoryItem::class.java)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getItemById(itemId: String): Result<InventoryItem> {
        return try {
            val document = inventoryCollection.document(itemId).get().await()
            if (document.exists()) {
                val item = document.toObject(InventoryItem::class.java)
                if (item != null) {
                    Result.Success(item)
                } else {
                    Result.Error(Exception("Failed to parse inventory item data"))
                }
            } else {
                Result.Error(Exception("Inventory item not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createItem(item: InventoryItem): Result<InventoryItem> {
        return try {
            inventoryCollection.document(item.id).set(item).await()
            Result.Success(item)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateItem(item: InventoryItem): Result<Unit> {
        return try {
            inventoryCollection.document(item.id).set(item).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> {
        return try {
            inventoryCollection.document(itemId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getAllInventory(): Flow<List<InventoryItem>> = callbackFlow {
        val listener = inventoryCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull {
                    it.toObject(InventoryItem::class.java)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    override fun getInventoryByFarmAsset(farmAssetId: String): Flow<List<InventoryItem>> = callbackFlow {
        val listener = inventoryCollection
            .whereEqualTo("farmAssetId", farmAssetId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull {
                    it.toObject(InventoryItem::class.java)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateQuantity(itemId: String, quantity: Double): Result<Unit> {
        return try {
            inventoryCollection.document(itemId)
                .update("availableQuantity", quantity)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
