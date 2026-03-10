package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.CartItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for shopping cart operations.
 */
interface CartRepository {
    
    /**
     * Observes cart items for a user.
     */
    fun observeCart(userId: String): Flow<List<CartItem>>
    
    /**
     * Adds or updates an item in the cart.
     */
    suspend fun addOrUpdateItem(
        userId: String,
        productId: String,
        quantity: Double,
        buyerLat: Double?,
        buyerLon: Double?
    ): Result<Unit>
    
    /**
     * Removes an item from the cart.
     */
    suspend fun removeItem(userId: String, productId: String): Result<Unit>
    
    /**
     * Updates the quantity of an item in the cart.
     */
    suspend fun updateQuantity(userId: String, productId: String, quantity: Double): Result<Unit>
}

