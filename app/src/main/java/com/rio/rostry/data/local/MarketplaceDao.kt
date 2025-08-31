package com.rio.rostry.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.models.market.* 
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {

    // MarketplaceListings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<MarketplaceListing>)

    @Query("SELECT * FROM marketplace_listings WHERE NOT isSold ORDER BY createdTimestamp DESC")
    fun getListings(): Flow<List<MarketplaceListing>>

    @Query("SELECT * FROM marketplace_listings WHERE id = :listingId")
    fun getListingById(listingId: String): Flow<MarketplaceListing?>

    @Query("DELETE FROM marketplace_listings")
    suspend fun clearListings()

    // Conversations and Messages
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)

    @Query("SELECT * FROM conversations WHERE listingId = :listingId AND buyerId = :buyerId")
    suspend fun getConversationForListing(listingId: String, buyerId: String): Conversation?

    @Query("SELECT * FROM conversations WHERE :userId IN (participantIds)")
    fun getConversations(userId: String): Flow<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>>

    // Wishlist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItem)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId AND listingId = :listingId")
    suspend fun removeFromWishlist(userId: String, listingId: String)

    @Query("SELECT * FROM wishlist_items WHERE userId = :userId")
    fun getWishlistItems(userId: String): Flow<List<WishlistItem>>

    // Cart
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(item: CartItem)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND listingId = :listingId")
    suspend fun removeFromCart(userId: String, listingId: String)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE userId = :userId AND listingId = :listingId")
    suspend fun updateCartItemQuantity(userId: String, listingId: String, quantity: Int)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: String): Flow<List<CartItem>>

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)
}
