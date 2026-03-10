package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Wishlist
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for wishlist operations.
 */
interface WishlistRepository {
    
    /**
     * Observes wishlist items for a user.
     */
    fun observe(userId: String): Flow<List<Wishlist>>
    
    /**
     * Adds a product to the wishlist.
     */
    suspend fun add(userId: String, productId: String): Result<Unit>
    
    /**
     * Removes a product from the wishlist.
     */
    suspend fun remove(userId: String, productId: String): Result<Unit>
}

