package com.rio.rostry.data.repository

import android.content.Context
import android.net.Uri
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.utils.CompressionUtils
import com.rio.rostry.utils.ValidationUtils
import com.rio.rostry.utils.ValidationUtils.AgeGroup
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Product
import com.rio.rostry.data.database.mapper.toDomain
import com.rio.rostry.data.database.mapper.toEntity
import com.rio.rostry.marketplace.validation.ProductValidator
import com.rio.rostry.domain.commerce.repository.ProductMarketplaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductMarketplaceRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productValidator: ProductValidator,
    private val storageRepository: StorageRepository,
    private val appContext: Context,
    private val rbacGuard: RbacGuard
) : ProductMarketplaceRepository {

    override suspend fun createProduct(product: Product, imageBytes: List<ByteArray>): Result<String> {
        return try {
            val entity = product.toEntity()
            // Check if this is a public listing (requires verification) or private product
            if (entity.isPublic) {
                if (!rbacGuard.canListProduct()) {
                    return Result.Error(Exception("Complete KYC verification to list products publicly. Go to Profile → Verification."))
                }
            } else {
                if (!rbacGuard.canAddPrivateProduct()) {
                    return Result.Error(Exception("Upgrade to Farmer/Enthusiast for farm management. Profile → Upgrade Role."))
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
                        val result = storageRepository.uploadFile(
                            localUriString = localUriString,
                            remotePath = remotePath,
                            compress = false,
                            sizeLimitBytes = 1_500_000L
                        )
                        when (result) {
                            is Result.Success -> remoteUrls.add(result.data ?: "")
                            is Result.Error -> return Result.Error(result.exception ?: Exception("Upload failed"))
                            else -> {}
                        }
                    } finally {
                        tempFile.delete()
                    }
                }
            } catch (e: Exception) {
                return Result.Error(Exception("Failed to upload images: ${e.message}"))
            }
            val mergedUrls = (product.imageUrls + remoteUrls).distinct()
            // UI layers consuming ProductEntity.imageUrls must handle HTTP/HTTPS URLs instead of data: URLs
            val entityWithImages = entity.copy(imageUrls = mergedUrls)
            val now = System.currentTimeMillis()
            productDao.upsert(
                entityWithImages.copy(
                    productId = id,
                    updatedAt = now,
                    lastModifiedAt = now,
                    dirty = true,
                    isDeleted = false,
                    deletedAt = null
                )
            )
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            validateProduct(product)
            val now = System.currentTimeMillis()
            productDao.upsert(product.toEntity().copy(updatedAt = now, lastModifiedAt = now, dirty = true))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val current = productDao.findById(productId)
            val softDeleted = (current ?: return Result.Error(Exception("Product not found"))).copy(
                isDeleted = true,
                deletedAt = now,
                updatedAt = now,
                lastModifiedAt = now,
                dirty = true
            )
            productDao.upsert(softDeleted)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun autocomplete(prefix: String, limit: Int): Result<List<Product>> {
        return try {
            Result.Success(productDao.autocomplete(prefix, limit).map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterByPriceBreed(minPrice: Double, maxPrice: Double, breed: String?, limit: Int, offset: Int): Result<List<Product>> {
        return try {
            Result.Success(productDao.filterByPriceBreed(minPrice, maxPrice, breed, limit, offset).map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterByAgeGroup(group: AgeGroup, limit: Int, offset: Int, now: Long): Result<List<Product>> {
        return try {
            // Compute birthDate window for the group
            val dayMillis = 24L * 60 * 60 * 1000
            val (minBirth, maxBirth) = when (group) {
                AgeGroup.DAY_OLD -> (now - 7 * dayMillis) to now
                AgeGroup.CHICK -> (now - 35 * dayMillis) to (now - 8 * dayMillis)
                AgeGroup.GROWER -> (now - 140 * dayMillis) to (now - 36 * dayMillis)
                AgeGroup.ADULT -> null to (now - 141 * dayMillis)
            }
            Result.Success(productDao.filterByAge(minBirth, maxBirth, limit, offset).map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterByBoundingBox(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?, limit: Int, offset: Int): Result<List<Product>> {
        return try {
            Result.Success(productDao.filterByBoundingBox(minLat, maxLat, minLng, maxLng, limit, offset).map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterVerified(limit: Int, offset: Int): Result<List<Product>> {
        return try {
            Result.Success(productDao.filterVerified(limit, offset).map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterByDateRange(startDate: Long?, endDate: Long?, limit: Int, offset: Int): Result<List<Product>> {
        return try {
            val start = startDate ?: 0L
            val end = endDate ?: System.currentTimeMillis()
            Result.Success(productDao.filterByDateRange(start, end, limit, offset).map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun validateProduct(product: Product) {
        // Enforce comprehensive marketplace rules using ProductValidator with lineage checks when applicable
        val result = productValidator.validateWithTraceability(product)
        require(result.valid) { result.reasons.joinToString(separator = "; ") }
    }

    override suspend fun respectBird(birdId: String): Result<Unit> {
        // TODO: Implement backend call. For now, just return success to support optimistic UI updates.
        return Result.Success(Unit)
    }
}
