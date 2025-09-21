package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.CartDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.CartItemEntity
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.ValidationUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface CartRepository {
    fun observeCart(userId: String): Flow<List<CartItemEntity>>
    suspend fun addOrUpdateItem(userId: String, productId: String, quantity: Double, buyerLat: Double?, buyerLon: Double?): Resource<Unit>
    suspend fun removeItem(userId: String, productId: String): Resource<Unit>
}

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) : CartRepository {

    override fun observeCart(userId: String): Flow<List<CartItemEntity>> = cartDao.observeCart(userId)

    override suspend fun addOrUpdateItem(userId: String, productId: String, quantity: Double, buyerLat: Double?, buyerLon: Double?): Resource<Unit> = try {
        require(quantity > 0) { "Quantity must be > 0" }
        // Validate delivery feasibility (<= 50km)
        val product = productDao.findById(productId) ?: return Resource.Error("Product not found")
        val canDeliver = ValidationUtils.withinDeliveryRadius(product.latitude, product.longitude, buyerLat, buyerLon, 50.0)
        require(canDeliver) { "Delivery not available beyond 50 km" }

        val existing = cartDao.find(userId, productId)
        val item = existing?.copy(quantity = quantity, addedAt = System.currentTimeMillis())
            ?: CartItemEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                productId = productId,
                quantity = quantity,
                addedAt = System.currentTimeMillis()
            )
        cartDao.upsert(item)
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to add to cart")
    }

    override suspend fun removeItem(userId: String, productId: String): Resource<Unit> = try {
        cartDao.removeByUserAndProduct(userId, productId)
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to remove from cart")
    }
}
