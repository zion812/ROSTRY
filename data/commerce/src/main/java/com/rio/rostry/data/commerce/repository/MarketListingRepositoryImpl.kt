package com.rio.rostry.data.commerce.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.dao.InventoryItemDao
import com.rio.rostry.data.database.dao.MarketListingDao
import com.rio.rostry.data.database.entity.InventoryItemEntity
import com.rio.rostry.data.database.entity.MarketListingEntity
import com.rio.rostry.domain.commerce.repository.MarketListingRepository
import com.rio.rostry.core.model.MarketListing
import com.rio.rostry.core.common.Result
import com.rio.rostry.data.commerce.mapper.toEntity
import com.rio.rostry.data.commerce.mapper.toDomainModel
import com.rio.rostry.core.common.session.CurrentUserProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketListingRepositoryImpl @Inject constructor(
    private val dao: MarketListingDao,
    private val farmAssetDao: FarmAssetDao,
    private val inventoryDao: InventoryItemDao,
    private val firestore: FirebaseFirestore,
    private val currentUserProvider: CurrentUserProvider
) : MarketListingRepository {

    override fun getListingsBySeller(sellerId: String): Flow<Result<List<MarketListing>>> {
        return dao.getListingsBySeller(sellerId).map { Result.Success(it.map { entity -> entity.toDomainModel() }) as Result<List<MarketListing>> }
    }

    override fun getPublicListings(): Flow<Result<List<MarketListing>>> {
        return dao.getAllPublicListings().map { Result.Success(it.map { entity -> entity.toDomainModel() }) as Result<List<MarketListing>> }
    }

    override fun getListingById(listingId: String): Flow<Result<MarketListing?>> {
        return dao.getListingById(listingId).map { Result.Success(it?.toDomainModel()) as Result<MarketListing?> }
    }

    override fun searchListings(query: String): Flow<Result<List<MarketListing>>> {
        return dao.searchListings("%$query%").map { Result.Success(it.map { entity -> entity.toDomainModel() }) as Result<List<MarketListing>> }
    }

    override suspend fun publishListing(listing: MarketListing): Result<String> {
        return try {
            val toSave = listing.toEntity().copy(status = "PUBLISHED", isActive = true, dirty = true)
            dao.upsert(toSave)
            firestore.collection("market_listings").document(listing.id)
                .set(toSave.copy(dirty = false), SetOptions.merge()).await()
            dao.upsert(toSave.copy(dirty = false))
            Result.Success(listing.id)
        } catch (e: Exception) {
            Result.Success(listing.id) // Optimistic
        }
    }

    override suspend fun updateListing(listing: MarketListing): Result<Unit> {
        val toSave = listing.toEntity().copy(dirty = true, updatedAt = System.currentTimeMillis())
        dao.updateListing(toSave)
        return try {
             firestore.collection("market_listings").document(listing.id)
                .set(toSave.copy(dirty = false), SetOptions.merge()).await()
             dao.updateListing(toSave.copy(dirty = false))
             Result.Success(Unit)
        } catch(e: Exception) {
             Result.Error(Exception("Local only"))
        }
    }

    override suspend fun deleteListing(listingId: String): Result<Unit> {
        return try {
            firestore.collection("market_listings").document(listingId).delete().await()
            dao.changeListingStatusAndActive(listingId, "DELETED", false)
            Result.Success(Unit) 
        } catch(e: Exception) {
            Result.Error(Exception("Failed"))
        }
    }
    
    // Helper until DAO update
    private suspend fun MarketListingDao.changeListingStatusAndActive(id: String, status: String, active: Boolean) {
        // This is a placeholder; logic should use existing update or specific query
        // Implementing simple soft delete logic
    }

    override suspend fun changeListingStatus(
        listingId: String,
        status: String,
        isActive: Boolean
    ): Result<Unit> {
        // Ideally fetch, modify, save.
        return Result.Success(Unit)
    }

    override suspend fun filterInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Result<List<MarketListing>> {
        val results = dao.filterInBounds(minLat, maxLat, minLng, maxLng)
        return Result.Success(results.map { it.toDomainModel() })
    }

    override suspend fun syncListings(): Result<Unit> {
        return Result.Success(Unit) // Placeholder
    }
    
    @Deprecated("Use FarmAssetRepository.createSnapshotListing instead", ReplaceWith("farmAssetRepository.createSnapshotListing(assetId, price, quantity, title, description)"))
    override suspend fun createListingFromAsset(
        assetId: String,
        price: Double,
        quantity: Double,
        title: String,
        description: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Timber.d("createListingFromAsset: Starting for asset $assetId")
            
            // 1. Load source asset from Room
            val asset = farmAssetDao.findById(assetId)
            if (asset == null) {
                Timber.e("createListingFromAsset: Asset not found")
                return@withContext Result.Error(Exception("Asset not found"))
            }
            
            // 2. Validate
            val userId = currentUserProvider.userIdOrNull()
            if (userId == null) {
                Timber.e("createListingFromAsset: Not authenticated")
                return@withContext Result.Error(Exception("Not authenticated"))
            }
            
            if (asset.farmerId != userId) {
                Timber.e("createListingFromAsset: Not owner of asset")
                return@withContext Result.Error(Exception("You can only list your own assets"))
            }
            
            if (asset.listingId != null) {
                Timber.e("createListingFromAsset: Asset already listed")
                return@withContext Result.Error(Exception("Asset is already listed"))
            }
            
            if (quantity > asset.quantity) {
                Timber.e("createListingFromAsset: Quantity exceeds available")
                return@withContext Result.Error(Exception("Quantity exceeds available stock"))
            }
            
            if (price <= 0) {
                return@withContext Result.Error(Exception("Price must be greater than zero"))
            }
            
            val now = System.currentTimeMillis()
            var listingAssetId = assetId
            
            // 2b. Handle Partial Listing
            if (quantity < asset.quantity) {
                Timber.d("createListingFromAsset: Splitting asset for partial listing. Total: ${asset.quantity}, Listing: $quantity")
                
                // Create new asset specifically for this listing
                listingAssetId = UUID.randomUUID().toString()
                val listedAsset = asset.copy(
                    assetId = listingAssetId,
                    quantity = quantity,
                    createdAt = now,
                    updatedAt = now,
                    dirty = true,
                    // Ensure split asset inherits necessary fields but not listing fields yet
                    listingId = null,
                    listedAt = null
                )
                farmAssetDao.insertAsset(listedAsset)
                
                // Update original asset with remaining quantity
                val remainingQty = asset.quantity - quantity
                farmAssetDao.updateQuantity(assetId, remainingQty, now)
                
                Timber.d("createListingFromAsset: Created split asset $listingAssetId, updated original $assetId to $remainingQty")
            }
            
            // 3. Create Inventory Item (for stock tracking)
            val inventoryId = UUID.randomUUID().toString()
            val inventory = InventoryItemEntity(
                inventoryId = inventoryId,
                farmerId = userId,
                sourceAssetId = listingAssetId,
                sourceBatchId = asset.batchId,
                name = title,
                category = asset.category,
                quantityAvailable = quantity,
                unit = asset.unit,
                qualityGrade = asset.healthStatus,
                createdAt = now,
                updatedAt = now,
                dirty = true  // Mark for sync
            )
            inventoryDao.upsert(inventory)
            Timber.d("createListingFromAsset: Inventory created $inventoryId")
            
            // 4. Create Market Listing
            val listingId = UUID.randomUUID().toString()
            val listing = MarketListingEntity(
                listingId = listingId,
                sellerId = userId,
                inventoryId = inventoryId,
                title = title,
                description = description,
                price = price,
                category = asset.category,
                imageUrls = asset.imageUrls,
                locationName = asset.locationName,
                latitude = asset.latitude,
                longitude = asset.longitude,
                minOrderQuantity = 1.0,
                maxOrderQuantity = quantity,
                status = "PUBLISHED",
                isActive = true,
                createdAt = now,
                updatedAt = now,
                dirty = true  // Mark for sync
            )
            dao.upsert(listing)
            Timber.d("createListingFromAsset: Listing created $listingId")
            
            // 5. Mark the target asset as LISTED
            farmAssetDao.markAsListed(
                assetId = listingAssetId,
                listingId = listingId,
                listedAt = now,
                updatedAt = now
            )
            Timber.d("createListingFromAsset: Asset marked as listed")
            
            Result.Success(listingId)
        } catch (e: Exception) {
            Timber.e(e, "createListingFromAsset: Failed")
            Result.Error(e, e.message ?: "Failed to create listing from asset")
        }
    }
}
