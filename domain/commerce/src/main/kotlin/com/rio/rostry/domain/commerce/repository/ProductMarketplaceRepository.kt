package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Product
import com.rio.rostry.core.model.Result
import com.rio.rostry.utils.ValidationUtils.AgeGroup

/**
 * Domain repository for marketplace product operations.
 */
interface ProductMarketplaceRepository {
    suspend fun createProduct(product: Product, imageBytes: List<ByteArray> = emptyList()): Result<String>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>

    // Filters & search
    suspend fun autocomplete(prefix: String, limit: Int = 10): Result<List<Product>>
    suspend fun filterByPriceBreed(minPrice: Double, maxPrice: Double, breed: String?, limit: Int = 50, offset: Int = 0): Result<List<Product>>
    suspend fun filterByAgeGroup(group: AgeGroup, limit: Int = 50, offset: Int = 0, now: Long = System.currentTimeMillis()): Result<List<Product>>
    suspend fun filterByBoundingBox(minLat: Double?, maxLat: Double?, minLng: Double?, maxLng: Double?, limit: Int = 50, offset: Int = 0): Result<List<Product>>
    suspend fun filterVerified(limit: Int = 50, offset: Int = 0): Result<List<Product>>
    suspend fun filterByDateRange(startDate: Long?, endDate: Long?, limit: Int = 50, offset: Int = 0): Result<List<Product>>
    suspend fun respectBird(birdId: String): Result<Unit>
}
