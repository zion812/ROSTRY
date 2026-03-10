package com.rio.rostry.data.commerce.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Product
import com.rio.rostry.data.commerce.mapper.toEntity
import com.rio.rostry.data.commerce.mapper.toProduct
import com.rio.rostry.data.commerce.mapper.toProducts
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.domain.commerce.repository.ProductRepository
import com.rio.rostry.domain.model.LifecycleStage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProductRepository for the commerce domain.
 * 
 * This repository handles all product/bird management operations, including:
 * - CRUD operations
 * - Marketplace filtering and search
 * - Lifecycle management
 * - Traceability and pedigree
 * - Data integrity and record locking
 * 
 * It uses ProductMapper to convert between database entities and domain models.
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
) : ProductRepository {

    // ==================== Validation ====================

    override fun validateProductReferences(product: Product): Boolean {
        return product.sellerId.isNotBlank()
    }

    override fun validateProductReferences(products: List<Product>): ProductRepository.ValidationResult {
        val invalid = products.filter { !validateProductReferences(it) }
        return ProductRepository.ValidationResult(invalid.isEmpty(), invalid)
    }

    // ==================== Basic CRUD Operations ====================

    override fun getAllProducts(): Flow<Result<List<Product>>> {
        return productDao.getAllProducts().map { entities ->
            Result.Success(entities.toProducts())
        }
    }

    override fun getProductById(productId: String): Flow<Result<Product?>> {
        return productDao.getProductById(productId).map { entity ->
            Result.Success(entity?.toProduct())
        }
    }

    override suspend fun getById(productId: String): Product? {
        return productDao.findById(productId)?.toProduct()
    }

    override suspend fun transferOwnership(productId: String, newOwnerId: String): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            productDao.updateSellerIdAndTouch(productId, newOwnerId, now)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getProductsBySeller(sellerId: String): Flow<Result<List<Product>>> {
        return productDao.getProductsBySeller(sellerId).map { entities ->
            Result.Success(entities.toProducts())
        }
    }

    override fun getProductsByCategory(category: String): Flow<Result<List<Product>>> {
        return productDao.getProductsByCategory(category).map { entities ->
            Result.Success(entities.toProducts())
        }
    }

    override suspend fun addProduct(product: Product, validateReferences: Boolean): Result<String> {
        if (validateReferences && !validateProductReferences(product)) {
            return Result.Error(Exception("Invalid product reference: seller does not exist"))
        }
        
        return try {
            val entity = product.toEntity()
            
            productDao.insertProduct(entity)
            Result.Success(product.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            productDao.updateProduct(product.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProductMetadata(productId: String, metadataJson: String): Result<Unit> {
        return try {
            productDao.updateMetadata(productId, metadataJson, System.currentTimeMillis())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productDao.deleteProduct(productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun syncProductsFromRemote(): Result<Unit> {
        return Result.Success(Unit)
    }

    // ==================== Search & Autocomplete ====================

    override fun searchProducts(query: String): Flow<Result<List<Product>>> {
        return productDao.searchProducts("%$query%").map { entities ->
            Result.Success(entities.toProducts())
        }
    }

    override suspend fun autocompleteProducts(prefix: String, limit: Int): List<Product> {
        return productDao.autocomplete(prefix, limit).toProducts()
    }

    // ==================== Marketplace Filters ====================

    override suspend fun filterVerified(limit: Int, offset: Int): Result<List<Product>> {
        return try {
            val entities = productDao.filterVerified(limit, offset)
            Result.Success(entities.toProducts())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterNearby(
        centerLat: Double,
        centerLng: Double,
        radiusKm: Double,
        limit: Int,
        offset: Int
    ): Result<List<Product>> {
        return try {
            val latChange = radiusKm / 111.0
            val lngChange = radiusKm / (111.0 * kotlin.math.cos(Math.toRadians(centerLat)))
            
            val minLat = centerLat - latChange
            val maxLat = centerLat + latChange
            val minLng = centerLng - kotlin.math.abs(lngChange)
            val maxLng = centerLng + kotlin.math.abs(lngChange)

            val entities = productDao.filterByBoundingBox(minLat, maxLat, minLng, maxLng, limit, offset)
            Result.Success(entities.toProducts())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterByBreed(
        breed: String?,
        minPrice: Double,
        maxPrice: Double,
        limit: Int,
        offset: Int
    ): Result<List<Product>> {
        return try {
            val entities = productDao.filterByPriceBreed(minPrice, maxPrice, breed, limit, offset)
            Result.Success(entities.toProducts())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterByAgeDays(
        minAgeDays: Int?,
        maxAgeDays: Int?,
        nowMillis: Long,
        limit: Int,
        offset: Int
    ): Result<List<Product>> {
        return try {
            val dayMillis = 24L * 60 * 60 * 1000
            val minBirth = maxAgeDays?.let { nowMillis - (it * dayMillis) }
            val maxBirth = minAgeDays?.let { nowMillis - (it * dayMillis) }
            val entities = productDao.filterByAge(minBirth, maxBirth, limit, offset)
            Result.Success(entities.toProducts())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun filterTraceable(onlyTraceable: Boolean, base: List<Product>?): Result<List<Product>> {
        return try {
            if (base != null) {
                val filtered = if (onlyTraceable) {
                    base.filter { it.isTraceable }
                } else {
                    base
                }
                Result.Success(filtered)
            } else {
                Result.Success(emptyList())
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ==================== Lifecycle Management ====================

    override suspend fun updateStage(productId: String, stage: LifecycleStage, transitionAt: Long): Result<Unit> {
        return try {
            productDao.updateStage(productId, stage, transitionAt, System.currentTimeMillis())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAllBirdsWithBirthDate(): List<Product> {
        return productDao.getAllBirdsWithBirthDate().toProducts()
    }

    override suspend fun updateBirdsLifecycle(birds: List<Product>): Result<Unit> {
        return try {
            birds.forEach { bird ->
                val entity = bird.toEntity()
                productDao.updateLifecycleFields(
                    productId = entity.productId,
                    stage = entity.stage,
                    lifecycleStatus = entity.lifecycleStatus,
                    ageWeeks = entity.ageWeeks,
                    lastStageTransitionAt = entity.lastStageTransitionAt,
                    updatedAt = System.currentTimeMillis()
                )
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeActiveWithBirth(): Flow<List<Product>> {
        return productDao.observeActiveWithBirth().map { entities ->
            entities.toProducts()
        }
    }

    // ==================== Farmer-Specific Operations ====================

    override fun observeRecentlyAddedForFarmer(farmerId: String, since: Long): Flow<List<Product>> {
        return productDao.observeRecentlyAddedForFarmer(farmerId, since).map { entities ->
            entities.toProducts()
        }
    }

    override fun observeEligibleForTransferCountForFarmer(farmerId: String): Flow<Int> {
        return productDao.observeEligibleForTransferCountForFarmer(farmerId)
    }

    override suspend fun countActiveByOwnerId(ownerId: String): Int {
        return productDao.countActiveByOwnerId(ownerId)
    }

    // ==================== Pedigree & Lineage ====================

    override suspend fun getOffspring(parentId: String): List<Product> {
        return productDao.getOffspring(parentId).toProducts()
    }

    // ==================== QR Code & Identification ====================

    override suspend fun updateQrCodeUrl(productId: String, url: String?, updatedAt: Long): Result<Unit> {
        return try {
            productDao.updateQrCodeUrl(productId, url, updatedAt)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun backfillBirdCodes(): Result<Int> {
        return try {
            val products = productDao.getProductsWithMissingBirdCodes()
            Result.Success(products.size)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ==================== Data Integrity ====================

    override fun isRecordsLocked(product: Product, nowMillis: Long): Boolean {
        val lockedAt = product.recordsLockedAt ?: return false
        return lockedAt > 0
    }

    override fun canEditRecord(
        product: Product,
        recordCreatedAt: Long,
        nowMillis: Long
    ): Boolean {
        // If records are locked, no editing allowed
        if (isRecordsLocked(product, nowMillis)) {
            return false
        }
        
        // If auto-lock is configured, check if record is within edit window
        val autoLockDays = product.autoLockAfterDays ?: return true
        if (autoLockDays <= 0) return true
        
        val dayMillis = 24L * 60 * 60 * 1000
        val lockThreshold = recordCreatedAt + (autoLockDays * dayMillis)
        return nowMillis < lockThreshold
    }

    override suspend fun lockRecords(productId: String, lockedAt: Long): Result<Unit> {
        return try {
            productDao.lockRecords(productId, lockedAt, System.currentTimeMillis())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ==================== Utility Operations ====================

    override suspend fun upsert(product: Product): Result<Unit> {
        return try {
            productDao.upsert(product.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun seedStarterKits(): Result<Unit> {
        // Implementation for seeding starter kits
        return Result.Success(Unit)
    }
}


