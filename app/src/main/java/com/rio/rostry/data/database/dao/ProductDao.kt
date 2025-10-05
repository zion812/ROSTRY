package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.rio.rostry.data.database.entity.ProductEntity
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

    // Example of a more complex query, e.g., searching products
    @Query("SELECT * FROM products WHERE name LIKE :query OR description LIKE :query ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    // Autocomplete suggestions by name or breed (prefix match)
    @Query("SELECT * FROM products WHERE name LIKE :prefix || '%' OR breed LIKE :prefix || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun autocomplete(prefix: String, limit: Int = 10): List<ProductEntity>

    // Filter by price range and breed (nullable breed ignored via COALESCE)
    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice AND (:breed IS NULL OR breed = :breed) AND isDeleted = 0 ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun filterByPriceBreed(minPrice: Double, maxPrice: Double, breed: String?, limit: Int = 50, offset: Int = 0): List<ProductEntity>

    // Filter by age window computed externally via birthDate bounds
    @Query("SELECT * FROM products WHERE (:minBirth IS NULL OR birthDate >= :minBirth) AND (:maxBirth IS NULL OR birthDate <= :maxBirth) AND isDeleted = 0 ORDER BY birthDate DESC LIMIT :limit OFFSET :offset")
    suspend fun filterByAge(minBirth: Long?, maxBirth: Long?, limit: Int = 50, offset: Int = 0): List<ProductEntity>

    // Location bounding box filter (simple and offline-friendly). Pass nulls to skip.
    @Query("SELECT * FROM products WHERE (:minLat IS NULL OR latitude >= :minLat) AND (:maxLat IS NULL OR latitude <= :maxLat) AND (:minLng IS NULL OR longitude >= :minLng) AND (:maxLng IS NULL OR longitude <= :maxLng) AND isDeleted = 0 ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun filterByBoundingBox(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?, limit: Int = 50, offset: Int = 0): List<ProductEntity>

    // Verified sellers filter (JOIN with users where verificationStatus = 'VERIFIED')
    @Query("SELECT p.* FROM products p INNER JOIN users u ON p.sellerId = u.userId WHERE u.verificationStatus = 'VERIFIED' AND p.isDeleted = 0 ORDER BY p.updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun filterVerified(limit: Int = 50, offset: Int = 0): List<ProductEntity>

    // Date range filter by createdAt
    @Query("SELECT * FROM products WHERE createdAt >= :startDate AND createdAt <= :endDate AND isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun filterByDateRange(startDate: Long, endDate: Long, limit: Int = 50, offset: Int = 0): List<ProductEntity>

    // Incremental sync helpers
    @Query("SELECT * FROM products WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int = 500): List<ProductEntity>

    @Query("DELETE FROM products WHERE isDeleted = 1")
    suspend fun purgeDeleted()

    // Evict products older than threshold that are not referenced by active tracking entries
    @Query(
        "DELETE FROM products " +
        "WHERE updatedAt < :thresholdMillis AND isDeleted = 0 AND productId NOT IN (" +
        "  SELECT DISTINCT productId FROM product_tracking WHERE isDeleted = 0" +
        ")"
    )
    suspend fun purgeStaleMarketplace(thresholdMillis: Long)

    // Count active products (birds) owned by a farmer for mortality rate calculation
    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :ownerId AND isDeleted = 0 AND status = 'ACTIVE'")
    suspend fun countActiveByOwnerId(ownerId: String): Int
}
