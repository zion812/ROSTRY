package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isDeleted = 0")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id AND isDeleted = 0")
    suspend fun getProductById(id: String): Product?

    @Query("SELECT * FROM products WHERE farmerId = :farmerId AND isDeleted = 0")
    fun getProductsByFarmerId(farmerId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category AND isDeleted = 0")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE products SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun deleteProduct(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM products WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    suspend fun purgeDeletedProducts(beforeTimestamp: Long)
}