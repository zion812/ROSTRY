package com.rio.rostry.data.repo

import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObjects
import com.rio.rostry.data.local.MarketplaceDao
import com.rio.rostry.data.models.market.*
import com.rio.rostry.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MarketplaceRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val marketplaceDao: MarketplaceDao
) : MarketplaceRepository {

    override fun getListings(
        userLocation: Location?,
        radiusInKm: Double?
    ): Flow<Result<List<MarketplaceListing>>> = flow {
        emit(Result.Loading)
        try {
            // Note: This fetches all listings and filters client-side. 
            // For larger datasets, a server-side geo-query solution (e.g., GeoFirestore)
            // would be more efficient.
            val snapshot = firestore.collection("marketplace_listings")
                .whereEqualTo("isSold", false)
                .get()
                .await()
            
            var listings = snapshot.toObjects<MarketplaceListing>()

            if (userLocation != null && radiusInKm != null) {
                val radiusInMeters = radiusInKm * 1000
                listings = listings.filter { listing ->
                    listing.location?.let {
                        val distance = calculateDistance(userLocation, it)
                        distance <= radiusInMeters
                    } ?: false
                }
            }

            marketplaceDao.insertListings(listings)
            emit(Result.Success(listings))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getListingById(listingId: String): Flow<Result<MarketplaceListing?>> = flow {
        emit(Result.Loading)
        try {
            val snapshot = firestore.collection("marketplace_listings").document(listingId).get().await()
            val listing = snapshot.toObject(MarketplaceListing::class.java)
            emit(Result.Success(listing))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun createListing(listing: MarketplaceListing): Result<Unit> {
        return try {
            val document = firestore.collection("marketplace_listings").document()
            val newListing = listing.copy(id = document.id, sellerId = auth.currentUser?.uid ?: "")
            document.set(newListing).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Wishlist, Cart, and Messaging functions will be implemented later
    override fun getWishlistItems(userId: String): Flow<Result<List<WishlistItem>>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToWishlist(userId: String, listingId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromWishlist(userId: String, listingId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getCartItems(userId: String): Flow<Result<List<CartItem>>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToCart(userId: String, listingId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromCart(userId: String, listingId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCartItemQuantity(userId: String, listingId: String, quantity: Int): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun clearCart(userId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getConversations(userId: String): Flow<Result<List<Conversation>>> {
        TODO("Not yet implemented")
    }

    override fun getMessages(conversationId: String): Flow<Result<List<Message>>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(conversationId: String, message: Message): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createConversation(conversation: Conversation): Result<String> {
        TODO("Not yet implemented")
    }

    private fun calculateDistance(location1: Location, geoPoint2: GeoPoint): Float {
        val location2 = Location("").apply {
            latitude = geoPoint2.latitude
            longitude = geoPoint2.longitude
        }
        return location1.distanceTo(location2) // Distance in meters
    }
}
