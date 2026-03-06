package com.rio.rostry.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.rio.rostry.data.repository.StorageRepository
import timber.log.Timber
import androidx.room.withTransaction
import com.rio.rostry.data.database.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FarmAssetRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val dao: FarmAssetDao,
    private val productDao: com.rio.rostry.data.database.dao.ProductDao,
    private val outboxDao: com.rio.rostry.data.database.dao.OutboxDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRepository: StorageRepository
) : FarmAssetRepository {

    override fun getAssetsByFarmer(farmerId: String): Flow<Resource<List<FarmAssetEntity>>> {
        return dao.getAssetsByFarmer(farmerId).map { Resource.Success(it) }
    }

    override fun getAssetById(assetId: String): Flow<Resource<FarmAssetEntity?>> {
        return dao.getAssetById(assetId).map { Resource.Success(it) }
    }

    override fun getAssetsByType(farmerId: String, type: String): Flow<Resource<List<FarmAssetEntity>>> {
        return dao.getAssetsByType(farmerId, type).map { Resource.Success(it) }
    }

    override fun getShowcaseAssets(farmerId: String): Flow<Resource<List<FarmAssetEntity>>> {
        return dao.getShowcaseAssets(farmerId).map { Resource.Success(it) }
    }

    override suspend fun addAsset(asset: FarmAssetEntity): Resource<String> {
        return try {
            val assetToSave = asset.copy(dirty = true, updatedAt = System.currentTimeMillis())
            dao.insertAsset(assetToSave)
            
            // Try Sync
            firestore.collection("farm_assets")
                .document(asset.assetId)
                .set(assetToSave.copy(dirty = false), SetOptions.merge())
                .await()
                
            dao.updateAsset(assetToSave.copy(dirty = false))
            Resource.Success(asset.assetId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync asset ${asset.assetId}")
            Resource.Success(asset.assetId) // Return success, sync later
        }
    }

    override suspend fun updateAsset(asset: FarmAssetEntity): Resource<Unit> {
        return try {
            val assetToSave = asset.copy(dirty = true, updatedAt = System.currentTimeMillis())
            dao.updateAsset(assetToSave)
            
            firestore.collection("farm_assets")
                .document(asset.assetId)
                .set(assetToSave.copy(dirty = false), SetOptions.merge())
                .await()
                
            dao.updateAsset(assetToSave.copy(dirty = false))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync asset update ${asset.assetId}")
            Resource.Error("Saved locally, sync failed")
        }
    }

    override suspend fun deleteAsset(assetId: String): Resource<Unit> {
        return try {
            val existing = dao.findById(assetId) ?: return Resource.Error("Not found")
            val tombstone = existing.copy(isDeleted = true, deletedAt = System.currentTimeMillis(), dirty = true)
            dao.updateAsset(tombstone)
            
            // Delete associated images from cloud storage
            existing.imageUrls.forEach { url ->
                if (url.contains("firebasestorage.googleapis.com")) {
                    // Extract remote path from URL if possible, or assume it's stored in a way we can resolve
                    // For now, we'll try to delete if we can derive the path.
                    // This is a simplified approach.
                    try {
                         // Simple extraction: .../o/remote%2Fpath?alt=...
                         val decoded = java.net.URLDecoder.decode(url.substringAfter("/o/").substringBefore("?"), "UTF-8")
                         storageRepository.deleteFile(decoded)
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to delete storage file: $url")
                    }
                }
            }

            firestore.collection("farm_assets").document(assetId).delete().await()
            dao.purgeDeleted() 
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Deleted locally, sync pending")
        }
    }

    override suspend fun updateQuantity(assetId: String, quantity: Double): Resource<Unit> {
        val now = System.currentTimeMillis()
        dao.updateQuantity(assetId, quantity, now)
        // Trigger generic sync or use WorkManager if immediate consistency needed
        return Resource.Success(Unit)
    }

    override suspend fun applyQuantityDelta(assetId: String, delta: Double): Resource<Unit> {
        return try {
            val asset = dao.findById(assetId) ?: return Resource.Error("Asset not found")
            database.withTransaction {
                dao.updateQuantity(assetId, asset.quantity + delta, System.currentTimeMillis())
                val outbox = com.rio.rostry.data.database.entity.OutboxEntity(
                    outboxId = java.util.UUID.randomUUID().toString(),
                    userId = asset.farmerId,
                    entityType = "FARM_ASSET_DELTA",
                    entityId = assetId,
                    operation = if (delta >= 0) com.rio.rostry.data.database.entity.OutboxEntity.OP_DELTA_ADD else com.rio.rostry.data.database.entity.OutboxEntity.OP_DELTA_SUB,
                    payloadJson = com.google.gson.Gson().toJson(mapOf("field" to "quantity", "delta" to delta)),
                    createdAt = System.currentTimeMillis()
                )
                outboxDao.insert(outbox)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to apply quantity delta")
            Resource.Error(e.message ?: "Update failed")
        }
    }

    override suspend fun updateHealthStatus(assetId: String, status: String): Resource<Unit> {
        val now = System.currentTimeMillis()
        dao.updateHealthStatus(assetId, status, now)
        return Resource.Success(Unit)
    }

    override suspend fun syncAssets(): Resource<Unit> {
        val userId = auth.currentUser?.uid ?: return Resource.Error("No user")
        return try {
            Timber.d("Starting asset sync for user: $userId")
            val snapshot = firestore.collection("farm_assets")
                .whereEqualTo("farmerId", userId)
                .get().await()
            
            val remoteAssets = snapshot.toObjects(FarmAssetEntity::class.java)
            Timber.d("Fetched ${remoteAssets.size} assets from Firestore")
            
            // Naive sync: insert specific logic to avoid overwriting dirty
            // omitted for brevity, similar to ProductRepositoryImpl
            var updatedCount = 0
            if (remoteAssets.isNotEmpty()) {
                remoteAssets.forEach { remote ->
                    val local = dao.findById(remote.assetId)
                    if (local == null || !local.dirty) {
                        dao.upsert(remote)
                        updatedCount++
                    } else {
                        Timber.d("Skipping overwrite of dirty local asset: ${remote.assetId}")
                    }
                }
            }
            Timber.d("Sync complete. Upserted $updatedCount assets.")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Sync failed: ${e.message}")
            Resource.Error(e.message ?: "Sync failed")
        }
    }
    
    // ========================================
    // Marketplace Lifecycle
    // ========================================
    
    override suspend fun markAsListed(assetId: String, listingId: String, listedAt: Long): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            dao.markAsListed(assetId, listingId, listedAt, now)
            Timber.d("Asset $assetId marked as LISTED (listingId=$listingId)")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to mark asset as listed")
            Resource.Error(e.message ?: "Failed to mark as listed")
        }
    }
    
    override suspend fun markAsDeListed(assetId: String): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            dao.markAsDeListed(assetId, now)
            Timber.d("Asset $assetId delisted, status reverted to ACTIVE")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delist asset")
            Resource.Error(e.message ?: "Failed to delist")
        }
    }
    
    override suspend fun markAsSold(assetId: String, buyerId: String, price: Double): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            dao.markAsSold(assetId, buyerId, price, now, now)
            Timber.d("Asset $assetId marked as SOLD to $buyerId for $$price")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to mark asset as sold")
            Resource.Error(e.message ?: "Failed to mark as sold")
        }
    }

    override suspend fun graduateBatch(batchId: String, newAssets: List<FarmAssetEntity>): Resource<Unit> {
        return try {
            // Ideally should be a Room Transaction
            // 1. Mark batch as CONVERTED
            val batch = dao.findById(batchId) ?: return Resource.Error("Batch not found")
            val updatedBatch = batch.copy(
                status = "CONVERTED",
                description = batch.description + "\n\n[System] Graduated on ${java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())}",
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            dao.updateAsset(updatedBatch)
            
            // 2. Insert all new assets
            newAssets.forEach { asset ->
                dao.insertAsset(asset.copy(dirty = true))
            }
            
            // 3. Sync Attempt (Best effort)
            // We can do this in background or rely on sync worker, but let's try pushing the batch update at least
            try {
                firestore.collection("farm_assets").document(batchId)
                    .set(updatedBatch.copy(dirty = false), SetOptions.merge())
            } catch (e: Exception) {
                Timber.w("Failed to sync graduated batch status immediately")
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to graduate batch $batchId")
            Resource.Error(e.message ?: "Graduation failed")
        }
    }

    // ========================================
    // Bird Studio - Appearance Customization
    // ========================================

    override suspend fun updateMetadataJson(assetId: String, metadataJson: String): Resource<Unit> {
        return try {
            dao.updateMetadataJson(assetId, metadataJson)
            Timber.d("Updated metadataJson for asset $assetId")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update metadataJson for $assetId")
            Resource.Error(e.message ?: "Failed to save appearance")
        }
    }

    override suspend fun createSnapshotListing(
        assetId: String,
        price: Double,
        listingTitle: String?,
        listingDescription: String?
    ): Resource<com.rio.rostry.data.database.entity.ProductEntity> {
        return try {
            val asset = dao.findById(assetId) ?: return Resource.Error("Asset not found")
            if (asset.activeListingProductId != null) {
                return Resource.Error("Asset is already listed")
            }

            val now = System.currentTimeMillis()
            val productId = "prod_snap_${java.util.UUID.randomUUID()}"
            
            // Create the snapshot listing
            val snapshot = com.rio.rostry.data.database.entity.ProductEntity(
                productId = productId,
                sellerId = asset.farmerId,
                name = listingTitle ?: asset.name,
                description = listingDescription ?: asset.description,
                category = asset.category,
                sourceAssetId = assetId,
                birthDate = asset.birthDate,
                gender = asset.gender,
                breed = asset.breed,
                color = asset.color,
                weightGrams = asset.weightGrams,
                price = price,
                healthStatus = asset.healthStatus,
                parentIdsJson = asset.parentIdsJson,
                birdCode = asset.birdCode,
                raisingPurpose = asset.raisingPurpose,
                status = "available",
                createdAt = now,
                updatedAt = now,
                dirty = true
            )

            // Atomic update inside a transaction
            database.withTransaction {
                productDao.upsert(snapshot)
                dao.linkActiveListing(assetId, productId, now)
            }

            // Queue for remote sync
            val outbox = com.rio.rostry.data.database.entity.OutboxEntity(
                outboxId = java.util.UUID.randomUUID().toString(),
                userId = asset.farmerId,
                entityType = com.rio.rostry.data.database.entity.OutboxEntity.TYPE_LISTING,
                entityId = productId,
                operation = "CREATE",
                payloadJson = com.google.gson.Gson().toJson(snapshot),
                createdAt = now,
                priority = "HIGH"
            )
            outboxDao.insert(outbox)

            Resource.Success(snapshot)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create snapshot listing for $assetId")
            Resource.Error(e.message ?: "Failed to create listing")
        }
    }

    override suspend fun delistAsset(assetId: String): Resource<Unit> {
        return try {
            val asset = dao.findById(assetId) ?: return Resource.Error("Asset not found")
            val now = System.currentTimeMillis()
            database.withTransaction {
                asset.activeListingProductId?.let { productId ->
                    val listing = productDao.findById(productId)
                    if (listing != null) {
                        productDao.upsert(listing.copy(isDeleted = true, deletedAt = now, dirty = true))
                    }
                }
                dao.unlinkActiveListing(assetId, now)
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delist asset $assetId")
            Resource.Error(e.message ?: "Failed to delist asset")
        }
    }
}
