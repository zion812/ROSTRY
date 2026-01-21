package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.repository.PublicBirdRepository
import com.rio.rostry.domain.repository.PublicBirdView
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicBirdRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val farmAssetDao: com.rio.rostry.data.database.dao.FarmAssetDao
) : PublicBirdRepository {

    override suspend fun lookupBird(birdCode: String): Resource<PublicBirdView> = withContext(Dispatchers.IO) {
        try {
            // Priority 1: Check Market Products
            val product = productDao.findByBirdCode(birdCode)
            if (product != null) {
                val isPublic = !product.status.isNullOrBlank() && product.status != "private"
                if (isPublic) {
                    return@withContext Resource.Success(PublicBirdView.Full(product))
                } else {
                    return@withContext Resource.Success(PublicBirdView.Restricted(
                        birdCode = birdCode, 
                        message = "Bird exists (Market) but details are private."
                    ))
                }
            }
            
            // Priority 2: Check Farm Inventory (Showcase Assets)
            val asset = farmAssetDao.findByBirdCode(birdCode)
            if (asset != null) {
                // If it's Showcase enabled, show it. Otherwise private.
                if (asset.isShowcase) {
                    // Map FarmAssetEntity to ProductEntity for display
                    // NOTE: In a real app we might use a shared domain model or mapping function.
                    // For now, simple mapping for "View Only"
                    val mappedProduct = ProductEntity(
                        productId = asset.assetId,
                        sellerId = asset.farmerId,
                        name = asset.name,
                        description = asset.description,
                        category = asset.category,
                        imageUrls = asset.imageUrls,
                        status = "public_showcase",
                        birdCode = asset.birdCode,
                        breed = asset.breed,
                        birthDate = asset.birthDate,
                        gender = asset.gender,
                        weightGrams = asset.weightGrams,
                        healthStatus = asset.healthStatus,
                        isShowcased = true
                    )
                    return@withContext Resource.Success(PublicBirdView.Full(mappedProduct))
                } else {
                    return@withContext Resource.Success(PublicBirdView.Restricted(
                        birdCode = birdCode, 
                        message = "Bird exists (Farm Asset) but details are private."
                    ))
                }
            }

            return@withContext Resource.Error("Bird with code '$birdCode' not found")

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to lookup bird")
        }
    }
}
