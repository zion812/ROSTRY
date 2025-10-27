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
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductById(productId: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE productId = :productId LIMIT 1")
    suspend fun findById(productId: String): ProductEntity?

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY createdAt DESC")
    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = :status ORDER BY createdAt DESC")
    fun getProductsByStatus(status: String): Flow<List<ProductEntity>>

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProductById(productId: String)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Transaction
    suspend fun replaceAllProducts(products: List<ProductEntity>) {
        deleteAllProducts()
        insertProducts(products)
    }

    @Query("SELECT * FROM products WHERE name LIKE :query OR description LIKE :query ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE productId LIKE 'demo_prod_%'")
    suspend fun countDemoProducts(): Int

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
}