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
        // Offline-first: create ID locally and mark dirty for sync
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val local = product.copy(
            productId = id,
            updatedAt = now,
            lastModifiedAt = now,
            isDeleted = false,
            deletedAt = null,
            dirty = true
        )
        productDao.upsert(local)
        id
    }.firstOrNull() ?: Resource.Error("Failed to add product")

    override suspend fun updateProduct(product: ProductEntity): Resource<Unit> = safeCall<Unit> {
        // Offline-first: mark dirty and bump times; sync manager will push later
        val now = System.currentTimeMillis()
        val local = product.copy(
            updatedAt = now,
            lastModifiedAt = now,
            dirty = true
        )
        productDao.upsert(local)
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to update product")

    override suspend fun deleteProduct(productId: String): Resource<Unit> = safeCall<Unit> {
        // Offline-first soft delete: mark isDeleted and dirty
        val now = System.currentTimeMillis()
        val current = productDao.findById(productId)
        val softDeleted = (current ?: ProductEntity(
            productId = productId,
            sellerId = "",
            name = "",
            description = "",
            category = "",
            price = 0.0,
            quantity = 0.0,
            unit = "",
            location = ""
        )).copy(
            isDeleted = true,
            deletedAt = now,
            updatedAt = now,
            lastModifiedAt = now,
            dirty = true
        )
        productDao.upsert(softDeleted)
    }.firstOrNull() ?: Resource.Error("Failed to delete product")

    override suspend fun syncProductsFromRemote(): Resource<Unit> = safeCall<Unit> {
        // Deprecated in favor of SyncManager; keeping as a no-op success to preserve interface
        Unit
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
    override suspend fun autocompleteProducts(prefix: String, limit: Int): List<ProductEntity> = try {
        productDao.autocomplete(prefix, limit)
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun filterVerified(limit: Int, offset: Int): Resource<List<ProductEntity>> = safeCall {
        productDao.filterVerified(limit = limit, offset = offset)
    }.firstOrNull() ?: Resource.Error("Failed to filter verified products")

    override suspend fun filterNearby(
        centerLat: Double,
        centerLng: Double,
        radiusKm: Double,
        limit: Int,
        offset: Int
    ): Resource<List<ProductEntity>> = safeCall {
        // Compute naive bounding box first to leverage SQL filtering, then refine by exact distance.
        val latDegreeKm = 110.574 // approx
        val lngDegreeKm = 111.320 * kotlin.math.cos(Math.toRadians(centerLat))
        val dLat = radiusKm / latDegreeKm
        val dLng = radiusKm / lngDegreeKm
        val minLat = centerLat - dLat
        val maxLat = centerLat + dLat
        val minLng = centerLng - dLng
        val maxLng = centerLng + dLng
        val pre = productDao.filterByBoundingBox(minLat, maxLat, minLng, maxLng, limit = limit, offset = offset)
        pre.filter { p ->
            val lat = p.latitude
            val lng = p.longitude
            lat != null && lng != null && distanceKm(lat, lng, centerLat, centerLng) <= radiusKm
        }
    }.firstOrNull() ?: Resource.Error("Failed to filter nearby products")

    override suspend fun filterByBreed(
        breed: String?,
        minPrice: Double,
        maxPrice: Double,
        limit: Int,
        offset: Int
    ): Resource<List<ProductEntity>> = safeCall {
        productDao.filterByPriceBreed(minPrice, maxPrice, breed, limit, offset)
    }.firstOrNull() ?: Resource.Error("Failed to filter by breed/price")

    override suspend fun filterByAgeDays(
        minAgeDays: Int?,
        maxAgeDays: Int?,
        nowMillis: Long,
        limit: Int,
        offset: Int
    ): Resource<List<ProductEntity>> = safeCall {
        fun ageDaysToBirthMillis(ageDays: Int?): Long? = ageDays?.let { days ->
            val ms = days * 24L * 60 * 60 * 1000
            nowMillis - ms
        }
        val maxBirth = ageDaysToBirthMillis(minAgeDays) // younger bound -> larger birthDate
        val minBirth = ageDaysToBirthMillis(maxAgeDays) // older bound -> smaller birthDate
        productDao.filterByAge(minBirth = minBirth, maxBirth = maxBirth, limit = limit, offset = offset)
    }.firstOrNull() ?: Resource.Error("Failed to filter by age")

    override suspend fun filterTraceable(onlyTraceable: Boolean, base: List<ProductEntity>?): Resource<List<ProductEntity>> = safeCall {
        if (!onlyTraceable) {
            base ?: productDao.getAllProducts().firstOrNull() ?: emptyList()
        } else {
            val source = base ?: (productDao.getAllProducts().firstOrNull() ?: emptyList())
            source.filter { it.familyTreeId != null || !it.parentIdsJson.isNullOrBlank() }
        }
    }.firstOrNull() ?: Resource.Error("Failed to filter traceable products")

    private fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
            kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
            kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return R * c
    }
}