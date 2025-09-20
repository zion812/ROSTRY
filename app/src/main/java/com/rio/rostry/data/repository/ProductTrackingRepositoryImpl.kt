package com.rio.rostry.data.repository

import com.rio.rostry.data.local.ProductTrackingDao
import com.rio.rostry.domain.model.ProductTracking as DomainProductTracking
import com.rio.rostry.data.model.ProductTracking as DataProductTracking
import com.rio.rostry.domain.repository.ProductTrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ProductTrackingRepositoryImpl @Inject constructor(
    private val productTrackingDao: ProductTrackingDao
) : BaseRepository(), ProductTrackingRepository {

    override fun getAllProductTrackings(): Flow<List<DomainProductTracking>> {
        return productTrackingDao.getAllProductTrackings().map { trackings ->
            trackings.map { it.toDomainModel() }
        }
    }

    override fun getProductTrackingsByProductId(productId: String): Flow<List<DomainProductTracking>> {
        return productTrackingDao.getProductTrackingsByProductId(productId).map { trackings ->
            trackings.map { it.toDomainModel() }
        }
    }

    override suspend fun getProductTrackingById(id: String): DomainProductTracking? {
        return productTrackingDao.getProductTrackingById(id)?.toDomainModel()
    }

    override suspend fun insertProductTracking(productTracking: DomainProductTracking) {
        productTrackingDao.insertProductTracking(productTracking.toDataModel())
    }

    override suspend fun updateProductTracking(productTracking: DomainProductTracking) {
        val updatedTracking = productTracking.copy(updatedAt = Date())
        productTrackingDao.updateProductTracking(updatedTracking.toDataModel())
    }

    override suspend fun deleteProductTracking(id: String) {
        // We need to get the object first to delete it
        productTrackingDao.getProductTrackingById(id)?.let { tracking ->
            productTrackingDao.deleteProductTracking(tracking)
        }
    }

    private fun DataProductTracking.toDomainModel(): DomainProductTracking {
        return DomainProductTracking(
            id = id,
            productId = productId,
            userId = userId,
            latitude = latitude,
            longitude = longitude,
            notes = notes,
            imageUrl = imageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainProductTracking.toDataModel(): DataProductTracking {
        return DataProductTracking(
            id = id,
            productId = productId,
            userId = userId,
            latitude = latitude,
            longitude = longitude,
            notes = notes,
            imageUrl = imageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}