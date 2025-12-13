package com.rio.rostry.data.repository

import android.content.Context
import android.net.Uri
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.utils.CompressionUtils
import com.rio.rostry.utils.ValidationUtils
import com.rio.rostry.utils.ValidationUtils.AgeGroup
import com.rio.rostry.utils.Resource
import com.rio.rostry.marketplace.validation.ProductValidator
import com.rio.rostry.utils.media.FirebaseStorageUploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface ProductMarketplaceRepository {
    suspend fun createProduct(product: ProductEntity, imageBytes: List<ByteArray> = emptyList()): Resource<String>
    suspend fun updateProduct(product: ProductEntity): Resource<Unit>
    suspend fun deleteProduct(productId: String): Resource<Unit>

    // Filters & search
    suspend fun autocomplete(prefix: String, limit: Int = 10): Resource<List<ProductEntity>>
    suspend fun filterByPriceBreed(minPrice: Double, maxPrice: Double, breed: String?, limit: Int = 50, offset: Int = 0): Resource<List<ProductEntity>>
    suspend fun filterByAgeGroup(group: AgeGroup, limit: Int = 50, offset: Int = 0, now: Long = System.currentTimeMillis()): Resource<List<ProductEntity>>
    suspend fun filterByBoundingBox(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?, limit: Int = 50, offset: Int = 0): Resource<List<ProductEntity>>
    suspend fun filterVerified(limit: Int = 50, offset: Int = 0): Resource<List<ProductEntity>>
    suspend fun filterByDateRange(startDate: Long?, endDate: Long?, limit: Int = 50, offset: Int = 0): Resource<List<ProductEntity>>
}

@Singleton
class ProductMarketplaceRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productValidator: ProductValidator,
    private val firebaseStorageUploader: FirebaseStorageUploader,
    private val appContext: Context,
    private val rbacGuard: RbacGuard
) : ProductMarketplaceRepository {

    override suspend fun createProduct(product: ProductEntity, imageBytes: List<ByteArray>): Resource<String> {
        return try {
            // Check if this is a public listing (requires verification) or private product
            if (product.isPublic) {
                if (!rbacGuard.canListProduct()) {
                    return Resource.Error("Complete KYC verification to list products publicly. Go to Profile → Verification.")
                }
            } else {
                if (!rbacGuard.canAddPrivateProduct()) {
                    return Resource.Error("Upgrade to Farmer/Enthusiast for farm management. Profile → Upgrade Role.")
                }
            }

            validateProduct(product)
            // Image optimization for rural networks
            val compressed = imageBytes.map { CompressionUtils.compressImage(it) }
            // Add validation before upload
            require(imageBytes.size <= 5) { "Maximum 5 images allowed per product" }
            compressed.forEach { require(it.size <= 1_500_000) { "Image exceeds 1.5MB limit after compression" } }
            // Replace base64 encoding with upload
            val id = product.productId.ifBlank { java.util.UUID.randomUUID().toString() }
            val remoteUrls = mutableListOf<String>()
            try {
                compressed.forEachIndexed { index, bytes ->
                    val tempFile = File(appContext.cacheDir, "temp_upload_${System.currentTimeMillis()}_${index}.jpg").apply { writeBytes(bytes) }
                    try {
                        val localUriString = Uri.fromFile(tempFile).toString()
                        val remotePath = "products/$id/image_${index}.jpg"
                        val result = firebaseStorageUploader.uploadFile(
                            localUriString = localUriString,
                            remotePath = remotePath,
                            compress = false,
                            sizeLimitBytes = 1_500_000L
                        )
                        remoteUrls.add(result.downloadUrl)
                    } finally {
                        tempFile.delete()
                    }
                }
            } catch (e: Exception) {
                return Resource.Error("Failed to upload images: ${e.message}")
            }
            val mergedUrls = (product.imageUrls + remoteUrls).distinct()
            // UI layers consuming ProductEntity.imageUrls must handle HTTP/HTTPS URLs instead of data: URLs
            val withImages = product.copy(imageUrls = mergedUrls)
            val now = System.currentTimeMillis()
            productDao.upsert(
                withImages.copy(
                    productId = id,
                    updatedAt = now,
                    lastModifiedAt = now,
                    dirty = true,
                    isDeleted = false,
                    deletedAt = null
                )
            )
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create product")
        }
    }

    override suspend fun updateProduct(product: ProductEntity): Resource<Unit> {
        return try {
            validateProduct(product)
            val now = System.currentTimeMillis()
            productDao.upsert(product.copy(updatedAt = now, lastModifiedAt = now, dirty = true))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update product")
        }
    }

    override suspend fun deleteProduct(productId: String): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val current = productDao.findById(productId)
            val softDeleted = (current ?: return Resource.Error("Product not found")).copy(
                isDeleted = true,
                deletedAt = now,
                updatedAt = now,
                lastModifiedAt = now,
                dirty = true
            )
            productDao.upsert(softDeleted)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete product")
        }
    }

    override suspend fun autocomplete(prefix: String, limit: Int): Resource<List<ProductEntity>> {
        return try {
            Resource.Success(productDao.autocomplete(prefix, limit))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Autocomplete failed")
        }
    }

    override suspend fun filterByPriceBreed(minPrice: Double, maxPrice: Double, breed: String?, limit: Int, offset: Int): Resource<List<ProductEntity>> {
        return try {
            Resource.Success(productDao.filterByPriceBreed(minPrice, maxPrice, breed, limit, offset))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Filter failed")
        }
    }

    override suspend fun filterByAgeGroup(group: AgeGroup, limit: Int, offset: Int, now: Long): Resource<List<ProductEntity>> {
        return try {
            // Compute birthDate window for the group
            val dayMillis = 24L * 60 * 60 * 1000
            val (minBirth, maxBirth) = when (group) {
                AgeGroup.DAY_OLD -> (now - 7 * dayMillis) to now
                AgeGroup.CHICK -> (now - 35 * dayMillis) to (now - 8 * dayMillis)
                AgeGroup.GROWER -> (now - 140 * dayMillis) to (now - 36 * dayMillis)
                AgeGroup.ADULT -> null to (now - 141 * dayMillis)
            }
            Resource.Success(productDao.filterByAge(minBirth, maxBirth, limit, offset))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Age filter failed")
        }
    }

    override suspend fun filterByBoundingBox(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?, limit: Int, offset: Int): Resource<List<ProductEntity>> {
        return try {
            Resource.Success(productDao.filterByBoundingBox(minLat, maxLat, minLng, maxLng, limit, offset))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Location filter failed")
        }
    }

    override suspend fun filterVerified(limit: Int, offset: Int): Resource<List<ProductEntity>> {
        return try {
            Resource.Success(productDao.filterVerified(limit, offset))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Verified filter failed")
        }
    }

    override suspend fun filterByDateRange(startDate: Long?, endDate: Long?, limit: Int, offset: Int): Resource<List<ProductEntity>> {
        return try {
            val start = startDate ?: 0L
            val end = endDate ?: System.currentTimeMillis()
            Resource.Success(productDao.filterByDateRange(start, end, limit, offset))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Date filter failed")
        }
    }

    private suspend fun validateProduct(product: ProductEntity) {
        // Enforce comprehensive marketplace rules using ProductValidator with lineage checks when applicable
        val result = productValidator.validateWithTraceability(product)
        require(result.valid) { result.reasons.joinToString(separator = "; ") }
    }
}
