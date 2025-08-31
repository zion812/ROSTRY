package com.rio.rostry.data.repo

import android.location.Location
import com.rio.rostry.data.models.market.* 
import com.rio.rostry.utils.Result
import kotlinx.coroutines.flow.Flow

interface MarketplaceRepository {

    // Listings
        fun getListings(
        userLocation: Location? = null,
        radiusInKm: Double? = null
    ): Flow<Result<List<MarketplaceListing>>>
    fun getListingById(listingId: String): Flow<Result<MarketplaceListing?>>
    suspend fun createListing(listing: MarketplaceListing): Result<Unit>

    // Wishlist
    fun getWishlistItems(userId: String): Flow<Result<List<WishlistItem>>>
    suspend fun addToWishlist(userId: String, listingId: String): Result<Unit>
    suspend fun removeFromWishlist(userId: String, listingId: String): Result<Unit>

    // Cart
    fun getCartItems(userId: String): Flow<Result<List<CartItem>>>
    suspend fun addToCart(userId: String, listingId: String): Result<Unit>
    suspend fun removeFromCart(userId: String, listingId: String): Result<Unit>
    suspend fun updateCartItemQuantity(userId: String, listingId: String, quantity: Int): Result<Unit>
    suspend fun clearCart(userId: String): Result<Unit>

    // Messaging
    fun getConversations(userId: String): Flow<Result<List<Conversation>>>
    fun getMessages(conversationId: String): Flow<Result<List<Message>>>
    suspend fun sendMessage(conversationId: String, message: Message): Result<Unit>
    suspend fun createConversation(conversation: Conversation): Result<String>
}
