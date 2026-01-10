package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import com.rio.rostry.marketplace.validation.ProductValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productValidator: ProductValidator
) : ProductRepository {

    override fun validateProductReferences(product: ProductEntity): Boolean {
        return product.sellerId.isNotBlank()
    }

    override fun validateProductReferences(products: List<ProductEntity>): ProductRepository.ValidationResult {
        val invalid = products.filter { !validateProductReferences(it) }
        return ProductRepository.ValidationResult(invalid.isEmpty(), invalid)
    }

    override fun getAllProducts(): Flow<Resource<List<ProductEntity>>> {
        return productDao.getAllProducts().map { Resource.Success(it) }
    }

    override fun getProductById(productId: String): Flow<Resource<ProductEntity?>> {
        return productDao.getProductById(productId).map { Resource.Success(it) }
    }

    override fun getProductsBySeller(sellerId: String): Flow<Resource<List<ProductEntity>>> {
        return productDao.getProductsBySeller(sellerId).map { Resource.Success(it) }
    }

    override fun getProductsByCategory(category: String): Flow<Resource<List<ProductEntity>>> {
        return productDao.getProductsByCategory(category).map { Resource.Success(it) }
    }

    override suspend fun addProduct(product: ProductEntity, validateReferences: Boolean): Resource<String> {
        if (validateReferences && !validateProductReferences(product)) {
            return Resource.Error("Invalid product reference: seller does not exist")
        }
        val validation = productValidator.validateWithTraceability(product)
        if (!validation.valid) {
             return Resource.Error(validation.reasons.joinToString(", "))
        }
        productDao.insertProduct(product)
        return Resource.Success(product.productId)
    }

    override suspend fun updateProduct(product: ProductEntity): Resource<Unit> {
        productDao.updateProduct(product)
        return Resource.Success(Unit)
    }

    override suspend fun deleteProduct(productId: String): Resource<Unit> {
        productDao.deleteProduct(productId)
        return Resource.Success(Unit)
    }

    override suspend fun syncProductsFromRemote(): Resource<Unit> {
        return Resource.Success(Unit)
    }

    override fun searchProducts(query: String): Flow<Resource<List<ProductEntity>>> {
        return productDao.searchProducts("%$query%").map { Resource.Success(it) }
    }

    override suspend fun autocompleteProducts(prefix: String, limit: Int): List<ProductEntity> {
        return productDao.autocomplete(prefix, limit)
    }

    override suspend fun filterVerified(limit: Int, offset: Int): Resource<List<ProductEntity>> {
        return Resource.Success(productDao.filterVerified(limit, offset))
    }

    override suspend fun filterNearby(
        centerLat: Double,
        centerLng: Double,
        radiusKm: Double,
        limit: Int,
        offset: Int
    ): Resource<List<ProductEntity>> {
        val latChange = radiusKm / 111.0
        val lngChange = radiusKm / (111.0 * kotlin.math.cos(Math.toRadians(centerLat)))
        
        val minLat = centerLat - latChange
        val maxLat = centerLat + latChange
        val minLng = centerLng - kotlin.math.abs(lngChange)
        val maxLng = centerLng + kotlin.math.abs(lngChange)

        return Resource.Success(productDao.filterByBoundingBox(minLat, maxLat, minLng, maxLng, limit, offset))
    }

    override suspend fun filterByBreed(
        breed: String?,
        minPrice: Double,
        maxPrice: Double,
        limit: Int,
        offset: Int
    ): Resource<List<ProductEntity>> {
        return Resource.Success(productDao.filterByPriceBreed(minPrice, maxPrice, breed, limit, offset))
    }

    override suspend fun filterByAgeDays(
        minAgeDays: Int?,
        maxAgeDays: Int?,
        nowMillis: Long,
        limit: Int,
        offset: Int
    ): Resource<List<ProductEntity>> {
        val dayMillis = 24L * 60 * 60 * 1000
        val minBirth = maxAgeDays?.let { nowMillis - (it * dayMillis) }
        val maxBirth = minAgeDays?.let { nowMillis - (it * dayMillis) }
        return Resource.Success(productDao.filterByAge(minBirth, maxBirth, limit, offset))
    }

    override suspend fun filterTraceable(onlyTraceable: Boolean, base: List<ProductEntity>?): Resource<List<ProductEntity>> {
        if (base != null) {
             return Resource.Success(if (onlyTraceable) base.filter { it.familyTreeId != null || !it.parentIdsJson.isNullOrBlank() } else base)
        }
        return Resource.Success(emptyList())
    }

    override suspend fun backfillBirdCodes(): Resource<Int> {
        val products = productDao.getProductsWithMissingBirdCodes()
        return Resource.Success(products.size)
    }

    override suspend fun updateQrCodeUrl(productId: String, url: String?, updatedAt: Long): Resource<Unit> {
        productDao.updateQrCodeUrl(productId, url, updatedAt)
        return Resource.Success(Unit)
    }

    override suspend fun findById(productId: String): ProductEntity? {
        return productDao.findById(productId)
    }

    override suspend fun upsert(product: ProductEntity): Resource<Unit> {
        productDao.upsert(product)
        return Resource.Success(Unit)
    }

    override fun observeActiveWithBirth(): Flow<List<ProductEntity>> {
        return productDao.observeActiveWithBirth()
    }

    override fun observeRecentlyAddedForFarmer(farmerId: String, since: Long): Flow<List<ProductEntity>> {
        return productDao.observeRecentlyAddedForFarmer(farmerId, since)
    }

    override fun observeEligibleForTransferCountForFarmer(farmerId: String): Flow<Int> {
        return productDao.observeEligibleForTransferCountForFarmer(farmerId)
    }

    override suspend fun countActiveByOwnerId(ownerId: String): Int {
        return productDao.countActiveByOwnerId(ownerId)
    }

    override suspend fun seedStarterKits() {
        // Implementation for seeding starter kits
    }

    override suspend fun updateStage(productId: String, stage: com.rio.rostry.domain.model.LifecycleStage, transitionAt: Long) {
        productDao.updateStage(productId, stage, transitionAt, System.currentTimeMillis())
    }

    override suspend fun lockRecords(productId: String, lockedAt: Long): Resource<Unit> {
        return try {
            productDao.lockRecords(productId, lockedAt, System.currentTimeMillis())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to lock records: ${e.message}")
        }
    }
}
