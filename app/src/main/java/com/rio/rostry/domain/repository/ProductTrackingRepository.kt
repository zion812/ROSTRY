package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.ProductTracking
import kotlinx.coroutines.flow.Flow

interface ProductTrackingRepository {
    fun getAllProductTrackings(): Flow<List<ProductTracking>>
    fun getProductTrackingsByProductId(productId: String): Flow<List<ProductTracking>>
    suspend fun getProductTrackingById(id: String): ProductTracking?
    suspend fun insertProductTracking(productTracking: ProductTracking)
    suspend fun updateProductTracking(productTracking: ProductTracking)
    suspend fun deleteProductTracking(id: String)
}