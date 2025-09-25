package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getAllProducts(): Flow<Resource<List<ProductEntity>>>

    fun getProductById(productId: String): Flow<Resource<ProductEntity?>>

    fun getProductsBySeller(sellerId: String): Flow<Resource<List<ProductEntity>>>

    fun getProductsByCategory(category: String): Flow<Resource<List<ProductEntity>>>

    suspend fun addProduct(product: ProductEntity): Resource<String> // Returns ID of new product

    suspend fun updateProduct(product: ProductEntity): Resource<Unit>

    suspend fun deleteProduct(productId: String): Resource<Unit>

    suspend fun syncProductsFromRemote(): Resource<Unit> // For offline-first

    // Example: Search might combine local and remote results or prioritize local then fetch
    fun searchProducts(query: String): Flow<Resource<List<ProductEntity>>>

    suspend fun autocompleteProducts(prefix: String, limit: Int = 10): List<ProductEntity>
}
