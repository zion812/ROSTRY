package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.Product
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for product search operations.
 */
interface ProductSearchRepository {
    /**
     * Search products by query.
     */
    suspend fun searchProducts(query: String, limit: Int = 20): Result<List<Product>>
    
    /**
     * Search products by filters.
     */
    suspend fun searchByFilters(
        minPrice: Double?,
        maxPrice: Double?,
        breed: String?,
        location: String?,
        limit: Int = 20
    ): Result<List<Product>>
    
    /**
     * Get autocomplete suggestions.
     */
    suspend fun getAutocompleteSuggestions(prefix: String, limit: Int = 10): Result<List<String>>
}
