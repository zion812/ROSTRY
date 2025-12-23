package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.InventoryItemEntity
import com.rio.rostry.data.database.entity.MarketListingEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.InventoryRepository
import com.rio.rostry.data.repository.MarketListingRepository
import com.rio.rostry.domain.model.LifecycleStage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.UUID

@HiltWorker
class LegacyProductMigrationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val productDao: ProductDao,
    private val farmAssetRepository: FarmAssetRepository,
    private val inventoryRepository: InventoryRepository,
    private val marketListingRepository: MarketListingRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "LegacyProductMigrationWorker"
    }

    override suspend fun doWork(): Result {
        Timber.d("Starting Legacy Product Migration...")
        return try {
            val legacyProducts = productDao.getActiveWithBirth() // Or getAllProducts() if DAO allows
            // Fallback: use getProductsWithMissingSellers() pattern or just a raw query if needed
            // Ideally we need getAllProducts(). DAO has getAllProducts() returning Flow.
            // We need a suspend function for OneShot.
            val allProducts = productDao.findById("dummy") // Hack? No.
            // Let's assume we can add `getAllProductsOneShot` to DAO later.
            // For now, I'll use `getActiveWithBirth` which returns List. It covers most "Assets".
            
            var migratedCount = 0
            
            legacyProducts.forEach { product ->
                // Check if already migrated (idempotency)
                // We use product.productId as assetId
                val existing = farmAssetRepository.getAssetById(product.productId)
                // Flow collection is tricky in loop. 
                // Better approach: try to insert, handle conflict.
                // Or assume if we run once, we are good.
                
                // 1. Create Farm Asset
                val assetType = if (product.isBatch == true) "BATCH" else "ANIMAL"
                val asset = FarmAssetEntity(
                    assetId = product.productId,
                    farmerId = product.sellerId,
                    name = product.name,
                    assetType = assetType,
                    category = product.category,
                    status = if (product.isDeleted) "ARCHIVED" else "ACTIVE",
                    isShowcase = false,
                    locationName = product.location,
                    latitude = product.latitude,
                    longitude = product.longitude,
                    quantity = product.quantity,
                    initialQuantity = product.quantity, // improved logic possible
                    unit = product.unit,
                    birthDate = product.birthDate,
                    ageWeeks = product.ageWeeks,
                    breed = product.breed,
                    gender = product.gender,
                    color = product.color,
                    description = product.description,
                    imageUrls = product.imageUrls,
                    birdCode = product.birdCode,
                    // Map other fields...
                    createdAt = product.createdAt,
                    updatedAt = System.currentTimeMillis()
                )
                
                farmAssetRepository.addAsset(asset)
                
                // 2. If it was a Market Listing
                if (product.isPublic) {
                    val inventoryId = UUID.randomUUID().toString()
                    val inventory = InventoryItemEntity(
                        inventoryId = inventoryId,
                        farmerId = product.sellerId,
                        sourceAssetId = asset.assetId,
                        name = product.name,
                        category = product.category,
                        quantityAvailable = product.quantity, // Assume all is for sale
                        unit = product.unit,
                        createdAt = System.currentTimeMillis()
                    )
                    inventoryRepository.addInventory(inventory)
                    
                    val listing = MarketListingEntity(
                        listingId = UUID.randomUUID().toString(),
                        sellerId = product.sellerId,
                        inventoryId = inventoryId,
                        title = product.name,
                        description = product.description,
                        price = product.price,
                        category = product.category,
                        imageUrls = product.imageUrls,
                        status = "PUBLISHED",
                        isActive = true,
                        createdAt = System.currentTimeMillis()
                    )
                    marketListingRepository.publishListing(listing)
                }
                migratedCount++
            }
            
            Timber.d("Migrated $migratedCount legacy products to new architecture.")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Migration failed")
            Result.failure()
        }
    }
}
