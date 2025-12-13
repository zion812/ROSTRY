package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rio.rostry.data.database.entity.ProductTrackingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductTrackingDao {
    @Upsert
    suspend fun upsertAll(items: List<ProductTrackingEntity>)

    @Upsert
    suspend fun upsert(item: ProductTrackingEntity)

    @Query("SELECT * FROM product_tracking WHERE productId = :productId AND isDeleted = 0 ORDER BY timestamp DESC")
    fun observeByProduct(productId: String): Flow<List<ProductTrackingEntity>>

    @Query("SELECT * FROM product_tracking WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int = 500): List<ProductTrackingEntity>

    @Query("DELETE FROM product_tracking WHERE isDeleted = 1")
    suspend fun purgeDeleted()

    @Query("SELECT * FROM product_tracking WHERE productId = :productId AND isDeleted = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getByProductId(productId: String): ProductTrackingEntity?

    @Query("UPDATE product_tracking SET dirty = 0, updatedAt = :updatedAt WHERE trackingId = :trackingId")
    suspend fun clearDirtyCustom(trackingId: String, updatedAt: Long)
}
