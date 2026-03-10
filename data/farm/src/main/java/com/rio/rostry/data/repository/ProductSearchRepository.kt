package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductSearchRepository @Inject constructor(
    private val productDao: ProductDao
) {

    /**
     * Searches for products using FTS.
     * Appends '*' to the query to enable prefix matching (e.g. "app" matches "apple").
     */
    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        if (query.isBlank()) {
            return productDao.getAllProducts()
        }
        // FTS syntax: "query*" for prefix match. 
        // We sanitize to avoid syntax errors if user types special chars.
        val sanitizedQuery = query.replace("\"", "").trim()
        val ftsQuery = if (sanitizedQuery.isNotEmpty()) "$sanitizedQuery*" else ""
        
        return productDao.searchProducts(ftsQuery)
    }
}
