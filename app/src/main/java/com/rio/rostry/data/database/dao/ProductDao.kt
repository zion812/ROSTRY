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

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductById(productId: String): Flow<ProductEntity?>

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
}
