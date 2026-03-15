package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("SELECT * FROM products WHERE productId = :id")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE productId = :id")
    suspend fun findById(id: String): ProductEntity? = getById(id)

    @Query("SELECT * FROM products WHERE sellerId = :userId ORDER BY createdAt DESC")
    fun getProductsForUser(userId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = :status ORDER BY createdAt DESC")
    fun getProductsByStatus(status: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE sellerId = :userId")
    suspend fun getProductCount(userId: String): Int
}