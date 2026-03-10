package com.rio.rostry.data.commerce.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.CartItem
import com.rio.rostry.data.commerce.mapper.toCartItem
import com.rio.rostry.data.database.dao.CartDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.CartItemEntity
import com.rio.rostry.domain.commerce.repository.CartRepository
import com.rio.rostry.utils.ValidationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CartRepository for shopping cart operations.
 */
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) : CartRepository {

    override fun observeCart(userId: String): Flow<List<CartItem>> {
        return cartDao.observeCart(userId).map { list -> list.map { it.toCartItem() } }
    }

    override suspend fun addOrUpdateItem(
        userId: String,
        productId: String,
        quantity: Double,
        buyerLat: Double?,
        buyerLon: Double?
    ): Result<Unit> {
        return try {
            require(quantity > 0) { "Quantity must be > 0" }
            
            // Validate delivery feasibility (<= 50km)
            val product = productDao.findById(productId) 
                ?: return Result.Error(Exception("Product not found"))
            
            if (buyerLat == null || buyerLon == null) {
                return Result.Error(Exception("Delivery address is required to add this item to cart"))
            }
            
            val canDeliver = ValidationUtils.withinDeliveryRadius(
                product.latitude, 
                product.longitude, 
                buyerLat, 
                buyerLon, 
                50.0
            )
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
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeItem(userId: String, productId: String): Result<Unit> {
        return try {
            cartDao.removeByUserAndProduct(userId, productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateQuantity(userId: String, productId: String, quantity: Double): Result<Unit> {
        return try {
            require(quantity > 0) { "Quantity must be > 0" }
            val existing = cartDao.find(userId, productId) 
                ?: return Result.Error(Exception("Item not in cart"))
            val updated = existing.copy(quantity = quantity, addedAt = System.currentTimeMillis())
            cartDao.upsert(updated)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

