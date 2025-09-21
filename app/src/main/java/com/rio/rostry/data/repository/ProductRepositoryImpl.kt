package com.rio.rostry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) : BaseRepository(), ProductRepository {

    private val productsCollection = firestore.collection("products")

    // Always serve from local database (source of truth for UI)
    override fun getAllProducts(): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        try {
            productDao.getAllProducts().collect { products ->
                emit(Resource.Success(products))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error fetching products from local DB: ${e.message}"))
        }
    }

    override fun getProductById(productId: String): Flow<Resource<ProductEntity?>> = flow {
        emit(Resource.Loading())
        try {
            productDao.getProductById(productId).collect { product ->
                if (product != null) {
                    emit(Resource.Success(product))
                } else {
                    // Optionally try to fetch from remote if not found locally, then cache
                    val remoteProduct = productsCollection.document(productId).get().await().toObject(ProductEntity::class.java)
                    if (remoteProduct != null) {
                        productDao.insertProduct(remoteProduct)
                        emit(Resource.Success(remoteProduct))
                    } else {
                        emit(Resource.Error("Product not found locally or remotely"))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error fetching product: ${e.message}"))
        }
    }

    override fun getProductsBySeller(sellerId: String): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        try {
            productDao.getProductsBySeller(sellerId).collect { products ->
                emit(Resource.Success(products))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error fetching products by seller: ${e.message}"))
        }
    }

    override fun getProductsByCategory(category: String): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        try {
            productDao.getProductsByCategory(category).collect { products ->
                emit(Resource.Success(products))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error fetching products by category: ${e.message}"))
        }
    }

    override suspend fun addProduct(product: ProductEntity): Resource<String> = safeCall<String> {
        // Add to Firestore
        val documentRef = productsCollection.document()
        val productWithId = product.copy(productId = documentRef.id)
        documentRef.set(productWithId).await()
        
        // Add to local DB
        productDao.insertProduct(productWithId)
        
        // Return the ID of the new product
        productWithId.productId
    }.firstOrNull() ?: Resource.Error("Failed to add product")

    override suspend fun updateProduct(product: ProductEntity): Resource<Unit> = safeCall<Unit> {
        // Update in Firestore
        productsCollection.document(product.productId).set(product).await()
        
        // Update in local DB
        productDao.updateProduct(product)
        
        Resource.Success(Unit)
    }.firstOrNull() ?: Resource.Error("Failed to update product")

    override suspend fun deleteProduct(productId: String): Resource<Unit> = safeCall<Unit> {
        // Delete from Firestore
        productsCollection.document(productId).delete().await()
        
        // Delete from local DB
        productDao.deleteProduct(productId)
        
        Resource.Success(Unit)
    }.firstOrNull() ?: Resource.Error("Failed to delete product")

    override suspend fun syncProductsFromRemote(): Resource<Unit> = safeCall<Unit> {
        val snapshot = productsCollection.get().await()
        val products = snapshot.map { it.toObject<ProductEntity>() }
        productDao.replaceAllProducts(products)
        Resource.Success(Unit)
    }.firstOrNull() ?: Resource.Error("Failed to sync products")
    
    override fun searchProducts(query: String): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        try {
            productDao.searchProducts(query).collect { products ->
                emit(Resource.Success(products))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error searching products: ${e.message}"))
        }
    }
}