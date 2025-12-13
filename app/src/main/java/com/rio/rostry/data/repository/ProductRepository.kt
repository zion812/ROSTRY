package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    data class ValidationResult(val isValid: Boolean, val invalidProducts: List<ProductEntity>)

    fun validateProductReferences(product: ProductEntity): Boolean

    fun validateProductReferences(products: List<ProductEntity>): ValidationResult

    fun getAllProducts(): Flow<Resource<List<ProductEntity>>>

    fun getProductById(productId: String): Flow<Resource<ProductEntity?>>

    fun getProductsBySeller(sellerId: String): Flow<Resource<List<ProductEntity>>>

    fun getProductsByCategory(category: String): Flow<Resource<List<ProductEntity>>>

    suspend fun addProduct(product: ProductEntity, validateReferences: Boolean = true): Resource<String> {
        if (validateReferences && !validateProductReferences(product)) {
            return Resource.Error("Invalid product reference: seller does not exist")
        }
        throw NotImplementedError("addProduct must be implemented")
    }

    suspend fun updateProduct(product: ProductEntity): Resource<Unit>

    suspend fun deleteProduct(productId: String): Resource<Unit>

    suspend fun syncProductsFromRemote(): Resource<Unit> // For offline-first

    // Example: Search might combine local and remote results or prioritize local then fetch
    fun searchProducts(query: String): Flow<Resource<List<ProductEntity>>>

    suspend fun autocompleteProducts(prefix: String, limit: Int = 10): List<ProductEntity>

    // Advanced marketplace filters
    suspend fun filterVerified(limit: Int = 50, offset: Int = 0): Resource<List<ProductEntity>>

    suspend fun filterNearby(
        centerLat: Double,
        centerLng: Double,
        radiusKm: Double,
        limit: Int = 100,
        offset: Int = 0
    ): Resource<List<ProductEntity>>

    suspend fun filterByBreed(
        breed: String?,
        minPrice: Double = 0.0,
        maxPrice: Double = Double.MAX_VALUE,
        limit: Int = 100,
        offset: Int = 0
    ): Resource<List<ProductEntity>>

    suspend fun filterByAgeDays(
        minAgeDays: Int? = null,
        maxAgeDays: Int? = null,
        nowMillis: Long = System.currentTimeMillis(),
        limit: Int = 100,
        offset: Int = 0
    ): Resource<List<ProductEntity>>

    suspend fun filterTraceable(onlyTraceable: Boolean, base: List<ProductEntity>? = null): Resource<List<ProductEntity>>

    /**
     * Backfills birdCode and colorTag fields for existing products that have null values.
     *
     * This method provides a one-time migration to populate missing birdCode and colorTag fields
     * for legacy products. It queries all products where birdCode or colorTag is null, computes
     * the values using BirdIdGenerator, and updates the products in a transaction.
     *
     * **Invocation Strategy**: This method is available for use but is not automatically executed
     * by the framework. Implementers should decide where to invoke it based on their app lifecycle:
     * - During app startup (e.g., Application.onCreate or a StartupWorker)
     * - As part of a Room database migration callback
     * - Manually via an admin/debug screen
     *
     * Guard the invocation with a flag (e.g., SharedPreferences or a metadata table) to prevent
     * repeated execution on every startup. Lazy initialization on read is available as a fallback
     * for exceptional cases but is not the primary mechanism.
     *
     * @return Resource.Success with the count of updated products, or Resource.Error on failure.
     */
    suspend fun backfillBirdCodes(): Resource<Int>

    /**
     * Updates the QR code URL for a product.
     *
     * @param productId The ID of the product to update
     * @param url The URL of the QR code, or null to clear it
     * @param updatedAt The timestamp of the update
     * @return Resource.Success if the update succeeds, Resource.Error otherwise
     */
    suspend fun updateQrCodeUrl(productId: String, url: String?, updatedAt: Long): Resource<Unit>

    /**
     * Finds a product by its ID.
     *
     * @param productId The ID of the product to find
     * @return The ProductEntity if found, null otherwise
     */
    suspend fun findById(productId: String): ProductEntity?

    /**
     * Upserts a product (inserts if not exists, updates if exists).
     *
     * @param product The product to upsert
     * @return Resource.Success if the operation succeeds, Resource.Error otherwise
     */
    suspend fun upsert(product: ProductEntity): Resource<Unit>

    /**
     * Observes products that are active and have a birth date.
     * Used for calculating growth stages and age tracking.
     */
    fun observeActiveWithBirth(): Flow<List<ProductEntity>>

    /**
     * Observes products recently added by a farmer within a specified time range.
     *
     * @param farmerId The ID of the farmer
     * @param since The start timestamp to filter from
     * @return Flow of products added since the specified time
     */
    fun observeRecentlyAddedForFarmer(farmerId: String, since: Long): Flow<List<ProductEntity>>

    /**
     * Observes the count of products eligible for transfer for a farmer.
     *
     * @param farmerId The ID of the farmer
     * @return Flow of count of products eligible for transfer
     */
    fun observeEligibleForTransferCountForFarmer(farmerId: String): Flow<Int>

    /**
     * Counts the number of active products owned by a specific user.
     *
     * @param ownerId The ID of the owner
     * @return The count of active products
     */
    suspend fun countActiveByOwnerId(ownerId: String): Int

    suspend fun seedStarterKits()

    suspend fun updateStage(productId: String, stage: com.rio.rostry.domain.model.LifecycleStage, transitionAt: Long)
}