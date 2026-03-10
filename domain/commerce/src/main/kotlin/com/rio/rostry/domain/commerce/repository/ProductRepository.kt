package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Product
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.model.LifecycleStage
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for product/bird management operations.
 * 
 * This interface defines the domain-level operations for managing products (birds)
 * in the ROSTRY system. It is framework-independent and uses domain models.
 */
interface ProductRepository {

    /**
     * Validation result for batch product operations.
     */
    data class ValidationResult(
        val isValid: Boolean,
        val invalidProducts: List<Product>
    )

    /**
     * Validates that a product's references (seller, etc.) are valid.
     */
    fun validateProductReferences(product: Product): Boolean

    /**
     * Validates references for multiple products.
     */
    fun validateProductReferences(products: List<Product>): ValidationResult

    /**
     * Observes all products in the system.
     */
    fun getAllProducts(): Flow<Result<List<Product>>>

    /**
     * Observes a specific product by ID.
     */
    fun getProductById(productId: String): Flow<Result<Product?>>

    /**
     * Gets a product by ID (suspend function for one-time fetch).
     */
    suspend fun getById(productId: String): Product?

    /**
     * Transfers ownership of a product to a new user.
     */
    suspend fun transferOwnership(productId: String, newOwnerId: String): Result<Unit>

    /**
     * Observes all products for a specific seller.
     */
    fun getProductsBySeller(sellerId: String): Flow<Result<List<Product>>>

    /**
     * Observes products in a specific category.
     */
    fun getProductsByCategory(category: String): Flow<Result<List<Product>>>

    /**
     * Adds a new product to the system.
     * 
     * @param product The product to add
     * @param validateReferences Whether to validate seller and other references
     * @return Result containing the product ID if successful
     */
    suspend fun addProduct(product: Product, validateReferences: Boolean = true): Result<String>

    /**
     * Updates an existing product.
     */
    suspend fun updateProduct(product: Product): Result<Unit>

    /**
     * Updates product metadata (custom JSON fields).
     */
    suspend fun updateProductMetadata(productId: String, metadataJson: String): Result<Unit>

    /**
     * Deletes a product from the system.
     */
    suspend fun deleteProduct(productId: String): Result<Unit>

    /**
     * Syncs products from remote source (for offline-first architecture).
     */
    suspend fun syncProductsFromRemote(): Result<Unit>

    /**
     * Searches products by query string.
     */
    fun searchProducts(query: String): Flow<Result<List<Product>>>

    /**
     * Autocompletes product search with a prefix.
     */
    suspend fun autocompleteProducts(prefix: String, limit: Int = 10): List<Product>

    // ==================== Marketplace Filters ====================

    /**
     * Filters verified products.
     */
    suspend fun filterVerified(limit: Int = 50, offset: Int = 0): Result<List<Product>>

    /**
     * Filters products near a geographic location.
     */
    suspend fun filterNearby(
        centerLat: Double,
        centerLng: Double,
        radiusKm: Double,
        limit: Int = 100,
        offset: Int = 0
    ): Result<List<Product>>

    /**
     * Filters products by breed and price range.
     */
    suspend fun filterByBreed(
        breed: String?,
        minPrice: Double = 0.0,
        maxPrice: Double = Double.MAX_VALUE,
        limit: Int = 100,
        offset: Int = 0
    ): Result<List<Product>>

    /**
     * Filters products by age in days.
     */
    suspend fun filterByAgeDays(
        minAgeDays: Int? = null,
        maxAgeDays: Int? = null,
        nowMillis: Long = System.currentTimeMillis(),
        limit: Int = 100,
        offset: Int = 0
    ): Result<List<Product>>

    /**
     * Filters products by traceability status.
     */
    suspend fun filterTraceable(onlyTraceable: Boolean, base: List<Product>? = null): Result<List<Product>>

    // ==================== Lifecycle Management ====================

    /**
     * Updates the lifecycle stage of a product.
     */
    suspend fun updateStage(productId: String, stage: LifecycleStage, transitionAt: Long): Result<Unit>

    /**
     * Gets all birds with birth dates for lifecycle processing.
     */
    suspend fun getAllBirdsWithBirthDate(): List<Product>

    /**
     * Batch updates lifecycle fields for multiple birds.
     */
    suspend fun updateBirdsLifecycle(birds: List<Product>): Result<Unit>

    /**
     * Observes products that are active and have a birth date.
     */
    fun observeActiveWithBirth(): Flow<List<Product>>

    // ==================== Farmer-Specific Operations ====================

    /**
     * Observes products recently added by a farmer.
     */
    fun observeRecentlyAddedForFarmer(farmerId: String, since: Long): Flow<List<Product>>

    /**
     * Observes the count of products eligible for transfer for a farmer.
     */
    fun observeEligibleForTransferCountForFarmer(farmerId: String): Flow<Int>

    /**
     * Counts active products owned by a specific user.
     */
    suspend fun countActiveByOwnerId(ownerId: String): Int

    // ==================== Pedigree & Lineage ====================

    /**
     * Gets all direct offspring for a bird.
     */
    suspend fun getOffspring(parentId: String): List<Product>

    // ==================== QR Code & Identification ====================

    /**
     * Updates the QR code URL for a product.
     */
    suspend fun updateQrCodeUrl(productId: String, url: String?, updatedAt: Long): Result<Unit>

    /**
     * Backfills bird codes for products missing them.
     * 
     * This is a one-time migration operation.
     * 
     * @return Result containing the count of updated products
     */
    suspend fun backfillBirdCodes(): Result<Int>

    // ==================== Data Integrity ====================

    /**
     * Checks if a product's records are locked for editing.
     */
    fun isRecordsLocked(product: Product, nowMillis: Long = System.currentTimeMillis()): Boolean

    /**
     * Checks if a specific record can be edited.
     */
    fun canEditRecord(
        product: Product,
        recordCreatedAt: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean

    /**
     * Locks all records for a product (typically during transfer).
     */
    suspend fun lockRecords(productId: String, lockedAt: Long = System.currentTimeMillis()): Result<Unit>

    // ==================== Utility Operations ====================

    /**
     * Upserts a product (insert or update).
     */
    suspend fun upsert(product: Product): Result<Unit>

    /**
     * Seeds starter kits for new users.
     */
    suspend fun seedStarterKits(): Result<Unit>
}

