package com.rio.rostry.data.farm.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Product
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.farm.repository.ProductSearchRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProductSearchRepository using Firebase Firestore.
 */
@Singleton
class ProductSearchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductSearchRepository {

    private val productsCollection = firestore.collection("products")

    override suspend fun searchProducts(query: String, limit: Int): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .limit(limit.toLong())
                .get()
                .await()
            
            val products = querySnapshot.documents.mapNotNull { it.toObject(Product::class.java) }
            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchByFilters(
        minPrice: Double?,
        maxPrice: Double?,
        breed: String?,
        location: String?,
        limit: Int
    ): Result<List<Product>> {
        return try {
            var query: com.google.firebase.firestore.Query = productsCollection
            
            if (minPrice != null) {
                query = query.whereGreaterThanOrEqualTo("price", minPrice)
            }
            if (maxPrice != null) {
                query = query.whereLessThanOrEqualTo("price", maxPrice)
            }
            if (breed != null) {
                query = query.whereEqualTo("breed", breed)
            }
            if (location != null) {
                query = query.whereEqualTo("location", location)
            }
            
            val querySnapshot = query.limit(limit.toLong()).get().await()
            val products = querySnapshot.documents.mapNotNull { it.toObject(Product::class.java) }
            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAutocompleteSuggestions(prefix: String, limit: Int): Result<List<String>> {
        return try {
            val querySnapshot = productsCollection
                .whereGreaterThanOrEqualTo("name", prefix)
                .whereLessThanOrEqualTo("name", prefix + "\uf8ff")
                .limit(limit.toLong())
                .get()
                .await()
            
            val suggestions = querySnapshot.documents.mapNotNull { 
                it.getString("name") 
            }.distinct()
            
            Result.Success(suggestions)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
