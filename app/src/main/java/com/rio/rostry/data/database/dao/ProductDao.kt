package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.LifecycleStage
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductFts(productFts: com.rio.rostry.data.database.entity.ProductFtsEntity)

    @Transaction
    suspend fun insertProduct(product: ProductEntity) {
        insertProductInternal(product)
        updateProductFts(product)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductInternal(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>) {
        insertProductsInternal(products)
        products.forEach { updateProductFts(it) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductsInternal(products: List<ProductEntity>)

    @Transaction
    suspend fun upsert(product: ProductEntity) {
        insertProductInternal(product)
        updateProductFts(product)
    }

    private suspend fun updateProductFts(product: ProductEntity) {
        insertProductFts(
            com.rio.rostry.data.database.entity.ProductFtsEntity(
                productId = product.productId,
                name = product.name,
                description = product.description,
                category = product.category,
                breed = product.breed,
                location = product.location,
                condition = product.condition
            )
        )
    }

    @Update
    suspend fun updateProduct(product: ProductEntity) {
        updateProductInternal(product)
        updateProductFts(product)
    }

    @Update
    suspend fun updateProductInternal(product: ProductEntity)

    @Transaction
    suspend fun deleteProduct(productId: String) {
        deleteProductInternal(productId)
        deleteProductFts(productId)
    }

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProductInternal(productId: String)

    @Query("DELETE FROM products_fts WHERE productId = :productId")
    suspend fun deleteProductFts(productId: String)

    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductById(productId: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE productId = :productId LIMIT 1")
    suspend fun findById(productId: String): ProductEntity?

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    suspend fun getAllProductsSnapshot(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    suspend fun getProductsBySellerSuspend(sellerId: String): List<ProductEntity>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = :status ORDER BY createdAt DESC")
    fun getProductsByStatus(status: String): Flow<List<ProductEntity>>

    @Transaction
    suspend fun deleteProductById(productId: String) {
        deleteProductInternal(productId)
        deleteProductFts(productId)
    }

    @Transaction
    suspend fun deleteAllProducts() {
        deleteAllProductsInternal()
        deleteAllProductsFts()
    }

    @Query("DELETE FROM products")
    suspend fun deleteAllProductsInternal()

    @Query("DELETE FROM products_fts")
    suspend fun deleteAllProductsFts()

    @Transaction
    suspend fun replaceAllProducts(products: List<ProductEntity>) {
        deleteAllProducts()
        insertProducts(products)
    }

    @Transaction
    @Query("""
        SELECT products.* FROM products 
        JOIN products_fts ON products.productId = products_fts.productId 
        WHERE products_fts MATCH :query
    """)
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE productId LIKE 'demo_prod_%'")
    suspend fun countDemoProducts(): Int

    @Query("SELECT * FROM products WHERE birdCode = :code LIMIT 1")
    suspend fun findByBirdCode(code: String): ProductEntity?

    @Query("SELECT * FROM products WHERE name LIKE :prefix || '%' OR breed LIKE :prefix || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun autocomplete(prefix: String, limit: Int = 10): List<ProductEntity>

    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice AND (:breed IS NULL OR breed = :breed) AND isDeleted = 0 ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun filterByPriceBreed(minPrice: Double, maxPrice: Double, breed: String?, limit: Int = 50, offset: Int = 0): List<ProductEntity>

    @Query("SELECT * FROM products WHERE (:minBirth IS NULL OR birthDate >= :minBirth) AND (:maxBirth IS NULL OR birthDate <= :maxBirth) AND isDeleted = 0 ORDER BY birthDate DESC LIMIT :limit OFFSET :offset")
    suspend fun filterByAge(minBirth: Long?, maxBirth: Long?, limit: Int = 50, offset: Int = 0): List<ProductEntity>

    @Query("SELECT * FROM products WHERE (:minLat IS NULL OR latitude >= :minLat) AND (:maxLat IS NULL OR latitude <= :maxLat) AND (:minLng IS NULL OR longitude >= :minLng) AND (:maxLng IS NULL OR longitude <= :maxLng) AND isDeleted = 0 ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun filterByBoundingBox(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?, limit: Int = 50, offset: Int = 0): List<ProductEntity>

    @Query("SELECT p.* FROM products p INNER JOIN users u ON p.sellerId = u.userId WHERE u.verificationStatus = 'VERIFIED' AND p.isDeleted = 0 ORDER BY p.updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun filterVerified(limit: Int = 50, offset: Int = 0): List<ProductEntity>

    @Query("SELECT * FROM products WHERE createdAt >= :startDate AND createdAt <= :endDate AND isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun filterByDateRange(startDate: Long, endDate: Long, limit: Int = 50, offset: Int = 0): List<ProductEntity>

    @Query("SELECT * FROM products WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int = 500): List<ProductEntity>

    @Query("DELETE FROM products WHERE isDeleted = 1")
    suspend fun purgeDeleted()

    @Query("DELETE FROM products WHERE updatedAt < :thresholdMillis AND isDeleted = 0 AND productId NOT IN (SELECT DISTINCT productId FROM product_tracking WHERE isDeleted = 0)")
    suspend fun purgeStaleMarketplace(thresholdMillis: Long)

    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :ownerId AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)")
    suspend fun countActiveByOwnerId(ownerId: String): Int

    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :ownerId AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)")
    fun observeActiveCountByOwnerId(ownerId: String): Flow<Int>

    @Query("SELECT * FROM products WHERE sellerId = :farmerId AND stage = :stage AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)")
    fun observeByStage(farmerId: String, stage: LifecycleStage): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE sellerId = :farmerId AND lifecycleStatus = :status")
    fun observeByLifecycleStatus(farmerId: String, status: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :farmerId AND stage = 'BREEDER' AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)")
    fun observeBreederCount(farmerId: String): Flow<Int>

    @Query("SELECT * FROM products WHERE sellerId = :farmerId AND breederEligibleAt IS NOT NULL AND breederEligibleAt <= :now AND (stage IS NULL OR stage != 'BREEDER')")
    suspend fun getBreederEligible(farmerId: String, now: Long): List<ProductEntity>

    @Query("UPDATE products SET stage = :stage, lastStageTransitionAt = :transitionAt, updatedAt = :updatedAt, dirty = 1 WHERE productId = :productId")
    suspend fun updateStage(productId: String, stage: LifecycleStage, transitionAt: Long, updatedAt: Long)

    @Query("UPDATE products SET lifecycleStatus = :status, updatedAt = :updatedAt, dirty = 1 WHERE productId = :productId")
    suspend fun updateLifecycleStatus(productId: String, status: String, updatedAt: Long)

    @Query("UPDATE products SET ageWeeks = :ageWeeks WHERE productId = :productId")
    suspend fun updateAgeWeeks(productId: String, ageWeeks: Int)

    @Query("UPDATE products SET breederEligibleAt = :eligibleAt, updatedAt = :updatedAt, dirty = 1 WHERE productId = :productId")
    suspend fun updateBreederEligibleAt(productId: String, eligibleAt: Long, updatedAt: Long)

    @Query("UPDATE products SET sellerId = :newSellerId, updatedAt = :updatedAt, dirty = 1 WHERE productId = :productId")
    suspend fun updateSellerIdAndTouch(productId: String, newSellerId: String, updatedAt: Long)

    @Query("SELECT * FROM products WHERE (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND birthDate IS NOT NULL")
    fun observeActiveWithBirth(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND birthDate IS NOT NULL")
    suspend fun getActiveWithBirth(): List<ProductEntity>

    /**
     * Alias for getActiveWithBirth() used by LifecycleUpdateWorker.
     */
    @Query("SELECT * FROM products WHERE birthDate IS NOT NULL AND isDeleted = 0")
    suspend fun getAllBirdsWithBirthDate(): List<ProductEntity>

    /**
     * Batch update lifecycle fields for a single bird.
     * Used by LifecycleUpdateWorker for daily stage recalculation.
     */
    @Query("""
        UPDATE products 
        SET stage = :stage, 
            lifecycleStatus = :lifecycleStatus, 
            ageWeeks = :ageWeeks, 
            lastStageTransitionAt = :lastStageTransitionAt, 
            updatedAt = :updatedAt, 
            dirty = 1 
        WHERE productId = :productId
    """)
    suspend fun updateLifecycleFields(
        productId: String,
        stage: LifecycleStage?,
        lifecycleStatus: String?,
        ageWeeks: Int?,
        lastStageTransitionAt: Long?,
        updatedAt: Long
    )

    @Query("UPDATE products SET qrCodeUrl = :url, updatedAt = :updatedAt, dirty = 1 WHERE productId = :productId")
    suspend fun updateQrCodeUrl(productId: String, url: String?, updatedAt: Long)

    @Query("SELECT * FROM products WHERE sellerId = :farmerId AND createdAt >= :sevenDaysAgo AND isDeleted = 0 ORDER BY createdAt DESC")
    fun observeRecentlyAddedForFarmer(farmerId: String, sevenDaysAgo: Long): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :farmerId AND createdAt >= :startTime AND createdAt <= :endTime AND isDeleted = 0")
    fun observeRecentlyAddedCountForFarmer(farmerId: String, startTime: Long, endTime: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :farmerId AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND ageWeeks >= 12 AND isDeleted = 0")
    fun observeEligibleForTransferCountForFarmer(farmerId: String): Flow<Int>

    @Query("SELECT p.* FROM products p LEFT JOIN users u ON p.sellerId = u.userId WHERE u.userId IS NULL AND p.isDeleted = 0")
    suspend fun getProductsWithMissingSellers(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE sellerId = :farmerId AND isBatch = 1 AND status = 'ACTIVE' AND isDeleted = 0 AND birthDate IS NOT NULL AND birthDate <= :minBirthDate")
    suspend fun getBatchesReadyToSplit(farmerId: String, minBirthDate: Long): List<ProductEntity>

    @Query("SELECT * FROM products WHERE birdCode IS NULL OR colorTag IS NULL")
    suspend fun getProductsWithMissingBirdCodes(): List<ProductEntity>

    @Query("UPDATE products SET quantity = quantity - :amount, updatedAt = :updatedAt, dirty = 1 WHERE productId = :productId AND quantity >= :amount")
    suspend fun decrementQuantity(productId: String, amount: Int, updatedAt: Long)

    @Query("SELECT stage, COUNT(*) as count FROM products WHERE sellerId = :farmerId AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND isDeleted = 0 GROUP BY stage")
    fun observeStageCounts(farmerId: String): Flow<List<StageCount>>

    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :farmerId AND stage = 'CHICK' AND ageWeeks >= 8 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND isDeleted = 0")
    fun observeReadyToGrowCount(farmerId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :farmerId AND stage = 'GROWER' AND ageWeeks >= 18 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND isDeleted = 0")
    fun observeReadyToLayCount(farmerId: String): Flow<Int>

    // =========================================================================
    // Split-Brain Data Architecture: Aggregate queries for DashboardCache
    // =========================================================================

    /** Get all distinct seller IDs for dashboard cache generation. */
    @Query("SELECT DISTINCT sellerId FROM products WHERE isDeleted = 0")
    suspend fun getDistinctSellerIds(): List<String>

    /** Count active birds for a farmer (for dashboard cache). */
    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :farmerId AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)")
    suspend fun countActiveByFarmer(farmerId: String): Int

    /** Count batches for a farmer (for dashboard cache). */
    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :farmerId AND isBatch = 1 AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL)")
    suspend fun countBatchesByFarmer(farmerId: String): Int

    /** Get active birds with weight data for proactive culling detection. */
    @Query("SELECT * FROM products WHERE sellerId = :farmerId AND isDeleted = 0 AND (lifecycleStatus = 'ACTIVE' OR lifecycleStatus IS NULL) AND weightGrams IS NOT NULL AND isBatch = 0")
    suspend fun getActiveWithWeightByFarmer(farmerId: String): List<ProductEntity>

    // =========================================================================
    // Product-Asset Bridge: Queries linking marketplace listings to farm assets
    // =========================================================================

    /**
     * Get the marketplace listing for a specific farm asset.
     * Returns null if the asset has not been listed yet.
     */
    @Query("SELECT * FROM products WHERE sourceAssetId = :assetId AND isDeleted = 0 LIMIT 1")
    fun getListingForAsset(assetId: String): Flow<ProductEntity?>

    /**
     * Sync version - get listing for asset (suspending).
     */
    @Query("SELECT * FROM products WHERE sourceAssetId = :assetId AND isDeleted = 0 LIMIT 1")
    suspend fun getListingForAssetSync(assetId: String): ProductEntity?

    /**
     * Check if an asset already has a marketplace listing.
     */
    @Query("SELECT COUNT(*) > 0 FROM products WHERE sourceAssetId = :assetId AND isDeleted = 0")
    suspend fun hasListingForAsset(assetId: String): Boolean

    // =========================================================================
    // Data Integrity: Record locking for trust & traceability
    // =========================================================================

    /**
     * Lock all records for a product (typically called during transfer).
     * Sets recordsLockedAt timestamp to prevent future edits.
     */
    @Query("UPDATE products SET recordsLockedAt = :lockedAt, updatedAt = :updatedAt, dirty = 1 WHERE productId = :productId")
    suspend fun lockRecords(productId: String, lockedAt: Long, updatedAt: Long)

    /**
     * Increment edit count and track last editor for a product.
     */
    @Query("UPDATE products SET editCount = editCount + 1, lastEditedBy = :editorId, updatedAt = :updatedAt, dirty = 1 WHERE productId = :productId")
    suspend fun incrementEditCount(productId: String, editorId: String, updatedAt: Long)

    // =========================================================================
    // Pedigree Optimization Queries
    // =========================================================================

    /**
     * Get all direct offspring for a bird (where it is either the sire or dam).
     */
    @Query("SELECT * FROM products WHERE (parentMaleId = :parentId OR parentFemaleId = :parentId) AND isDeleted = 0")
    suspend fun getOffspring(parentId: String): List<ProductEntity>

    /**
     * Get potential parents for selection.
     * Filters by owner, excludes self, avoids batches, deleted items, and optionally filters by gender.
     */
    @Query("""
        SELECT * FROM products 
        WHERE sellerId = :ownerId 
        AND productId != :excludeId 
        AND isBatch = 0 
        AND isDeleted = 0 
        AND (:gender IS NULL OR gender = :gender)
        ORDER BY name ASC
    """)
    suspend fun getPotentialParents(ownerId: String, excludeId: String, gender: String?): List<ProductEntity>

    /**
     * Batch fetch offspring for multiple parents.
     */
    @Query("SELECT * FROM products WHERE (parentMaleId IN (:parentIds) OR parentFemaleId IN (:parentIds)) AND isDeleted = 0")
    suspend fun getOffspringBatch(parentIds: List<String>): List<ProductEntity>

    @Query("SELECT * FROM products WHERE adminFlagged = 1 ORDER BY createdAt DESC")
    fun getFlaggedProducts(): Flow<List<ProductEntity>>

    // ============ Location-Based Discovery (Nearby Farmers) ============

    /**
     * Find products within approximate radius using bounding box.
     * Uses Haversine-approximate lat/lon filtering for "farmers near me" discovery.
     * 
     * @param minLat south bound
     * @param maxLat north bound
     * @param minLon west bound
     * @param maxLon east bound
     */
    @Query("""
        SELECT * FROM products 
        WHERE latitude IS NOT NULL AND longitude IS NOT NULL
        AND latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLon AND :maxLon
        AND status != 'private'
        AND isDeleted = 0
        ORDER BY updatedAt DESC
    """)
    fun getProductsInBoundingBox(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): Flow<List<ProductEntity>>

    /**
     * Find distinct sellers (farmers) with active listings in a geographic area.
     */
    @Query("""
        SELECT DISTINCT sellerId FROM products 
        WHERE latitude IS NOT NULL AND longitude IS NOT NULL
        AND latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLon AND :maxLon
        AND status != 'private'
        AND isDeleted = 0
    """)
    suspend fun getNearbySellers(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): List<String>

    // ============ Purpose-Based Filtering ============

    /**
     * Get products filtered by raising purpose (MEAT, ADOPTION, BREEDING, EGG_PRODUCTION).
     */
    @Query("""
        SELECT * FROM products 
        WHERE raisingPurpose = :purpose
        AND status != 'private'
        AND isDeleted = 0
        ORDER BY updatedAt DESC
    """)
    fun getProductsByPurpose(purpose: String): Flow<List<ProductEntity>>

    /**
     * Get nearby products filtered by purpose — the core discovery query.
     * "Show me adoption birds near me" or "Show me meat birds near me"
     */
    @Query("""
        SELECT * FROM products 
        WHERE raisingPurpose = :purpose
        AND latitude IS NOT NULL AND longitude IS NOT NULL
        AND latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLon AND :maxLon
        AND status != 'private'
        AND isDeleted = 0
        ORDER BY updatedAt DESC
    """)
    fun getNearbyProductsByPurpose(
        purpose: String,
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): Flow<List<ProductEntity>>
}

data class StageCount(
    val stage: LifecycleStage?,
    val count: Int
)
