package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.CompressionUtils
import com.rio.rostry.utils.ValidationUtils
import com.rio.rostry.utils.ValidationUtils.AgeGroup
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
}

@Singleton
class ProductMarketplaceRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductMarketplaceRepository {

    override suspend fun createProduct(product: ProductEntity, imageBytes: List<ByteArray>): Resource<String> = try {
        validateProduct(product)
        // Image optimization for rural networks
        val compressed = imageBytes.map { CompressionUtils.compressImage(it) }
        // For now, convert to inline data URLs so UI can render without external storage
        val dataUrls = compressed.map { bytes ->
            val b64 = java.util.Base64.getEncoder().encodeToString(bytes)
            "data:image/jpeg;base64,$b64"
        }
        val mergedUrls = (product.imageUrls + dataUrls).distinct()
        val withImages = product.copy(imageUrls = mergedUrls)
        val now = System.currentTimeMillis()
        val id = product.productId.ifBlank { java.util.UUID.randomUUID().toString() }
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

    override suspend fun updateProduct(product: ProductEntity): Resource<Unit> = try {
        validateProduct(product)
        val now = System.currentTimeMillis()
        productDao.upsert(product.copy(updatedAt = now, lastModifiedAt = now, dirty = true))
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to update product")
    }

    override suspend fun deleteProduct(productId: String): Resource<Unit> = try {
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

    override suspend fun autocomplete(prefix: String, limit: Int): Resource<List<ProductEntity>> = try {
        Resource.Success(productDao.autocomplete(prefix, limit))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Autocomplete failed")
    }

    override suspend fun filterByPriceBreed(minPrice: Double, maxPrice: Double, breed: String?, limit: Int, offset: Int): Resource<List<ProductEntity>> = try {
        Resource.Success(productDao.filterByPriceBreed(minPrice, maxPrice, breed, limit, offset))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Filter failed")
    }

    override suspend fun filterByAgeGroup(group: AgeGroup, limit: Int, offset: Int, now: Long): Resource<List<ProductEntity>> = try {
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

    override suspend fun filterByBoundingBox(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?, limit: Int, offset: Int): Resource<List<ProductEntity>> = try {
        Resource.Success(productDao.filterByBoundingBox(minLat, maxLat, minLng, maxLng, limit, offset))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Location filter failed")
    }

    override suspend fun filterVerified(limit: Int, offset: Int): Resource<List<ProductEntity>> = try {
        Resource.Success(productDao.filterVerified(limit, offset))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Verified filter failed")
    }

    private fun validateProduct(product: ProductEntity) {
        // Products under 5 weeks require vaccination proof
        if (ValidationUtils.requiresVaccinationProof(product.birthDate)) {
            require(!product.vaccinationRecordsJson.isNullOrBlank()) { "Vaccination proof required for products under 5 weeks" }
        }
        // Breeding products require family tree documentation
        if (ValidationUtils.requiresFamilyTree(product.breedingStatus)) {
            require(!product.familyTreeId.isNullOrBlank()) { "Family tree documentation required for breeding products" }
        }
    }
}
