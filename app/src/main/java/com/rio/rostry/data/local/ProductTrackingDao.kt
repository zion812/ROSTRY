package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.ProductTracking
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductTrackingDao {
    @Query("SELECT * FROM product_tracking ORDER BY createdAt DESC")
    fun getAllProductTrackings(): Flow<List<ProductTracking>>

    @Query("SELECT * FROM product_tracking WHERE productId = :productId ORDER BY createdAt DESC")
    fun getProductTrackingsByProductId(productId: String): Flow<List<ProductTracking>>

    @Query("SELECT * FROM product_tracking WHERE id = :id")
    suspend fun getProductTrackingById(id: String): ProductTracking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductTracking(productTracking: ProductTracking)

    @Update
    suspend fun updateProductTracking(productTracking: ProductTracking)

    @Delete
    suspend fun deleteProductTracking(productTracking: ProductTracking)

    // Temporarily removing purge method
    // @Query("DELETE FROM product_tracking WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    // suspend fun purgeDeletedProductTrackings(beforeTimestamp: Long)
}