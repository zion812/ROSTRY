package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ProductTrackingDao
import com.rio.rostry.data.database.entity.ProductTrackingEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TrackingRepository {
    fun getByProduct(productId: String): Flow<List<ProductTrackingEntity>>
    suspend fun addOrUpdate(entry: ProductTrackingEntity)
    suspend fun markDeleted(trackingId: String)
}

@Singleton
class TrackingRepositoryImpl @Inject constructor(
    private val dao: ProductTrackingDao
) : TrackingRepository {
    override fun getByProduct(productId: String): Flow<List<ProductTrackingEntity>> = dao.getByProduct(productId)

    override suspend fun addOrUpdate(entry: ProductTrackingEntity) {
        dao.upsert(entry.copy(dirty = true, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun markDeleted(trackingId: String) {
        // Fetch recent then soft delete; since we don't have a direct getter, rely on upsert contract from caller
        // In real impl, create a getById(). For now, noop placeholder.
    }
}
