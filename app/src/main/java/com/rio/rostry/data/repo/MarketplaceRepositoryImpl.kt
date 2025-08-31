package com.rio.rostry.data.repo

import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObjects
import com.rio.rostry.data.local.MarketplaceDao
import com.rio.rostry.data.models.market.*
import com.rio.rostry.data.models.messaging.Conversation
import com.rio.rostry.data.models.messaging.Message
import com.rio.rostry.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class MarketplaceRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val marketplaceDao: MarketplaceDao
) : MarketplaceRepository {

    object MarketplaceCollections {
        const val LISTINGS = "marketplace_listings"
        const val CONVERSATIONS = "conversations"
        const val MESSAGES = "messages"
        const val ORDERS = "orders"
    }

    override fun getListings(
        userLocation: Location?,
        radiusInKm: Double?
    ): Flow<Result<List<MarketplaceListing>>> = flow {
        emit(Result.Loading)
        try {
            // Note: This fetches all listings and filters client-side. 
            // For larger datasets, a server-side geo-query solution (e.g., GeoFirestore)
            // would be more efficient.
            val snapshot = firestore.collection(MarketplaceCollections.LISTINGS)
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
            val snapshot = firestore.collection(MarketplaceCollections.LISTINGS).document(listingId).get().await()
            val listing = snapshot.toObject(MarketplaceListing::class.java)
            emit(Result.Success(listing))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun createListing(
        fowlId: String,
        price: Double,
        description: String
    ): Result<Unit> {
        val newListing = MarketplaceListing(
            fowlId = fowlId,
            price = price,
            description = description
            // Other fields can be populated as needed, e.g., from the Fowl object
        )
        return createListing(newListing)
    }

    override suspend fun createListing(listing: MarketplaceListing): Result<Unit> {
        return try {
            val document = firestore.collection(MarketplaceCollections.LISTINGS).document()
            val newListing = listing.copy(id = document.id, sellerId = auth.currentUser?.uid ?: "")
            document.set(newListing).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getWishlistItems(userId: String): Flow<Result<List<WishlistItem>>> = flow {
        emit(Result.Loading)
        try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("wishlist").get().await()
            val items = snapshot.toObjects<WishlistItem>()
            emit(Result.Success(items))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun addToWishlist(userId: String, listingId: String): Result<Unit> = try {
        val wishlistItem = WishlistItem(id = listingId, userId = userId, listingId = listingId)
        firestore.collection("users").document(userId)
            .collection("wishlist").document(listingId).set(wishlistItem).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun removeFromWishlist(userId: String, listingId: String): Result<Unit> = try {
        firestore.collection("users").document(userId)
            .collection("wishlist").document(listingId).delete().await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun getCartItems(userId: String): Flow<Result<List<CartItem>>> = flow {
        emit(Result.Loading)
        try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("cart").get().await()
            val items = snapshot.toObjects<CartItem>()
            emit(Result.Success(items))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun addToCart(userId: String, listingId: String): Result<Unit> = try {
        val cartItemRef = firestore.collection("users").document(userId)
            .collection("cart").document(listingId)

        firestore.runTransaction {
            val snapshot = it.get(cartItemRef)
            if (snapshot.exists()) {
                val currentQuantity = snapshot.getLong("quantity") ?: 0
                it.update(cartItemRef, "quantity", currentQuantity + 1)
            } else {
                val newItem = CartItem(id = listingId, userId = userId, listingId = listingId, quantity = 1)
                it.set(cartItemRef, newItem)
            }
            null
        }.await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun removeFromCart(userId: String, listingId: String): Result<Unit> = try {
        firestore.collection("users").document(userId)
            .collection("cart").document(listingId).delete().await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateCartItemQuantity(userId: String, listingId: String, quantity: Int): Result<Unit> = try {
        val cartItemRef = firestore.collection("users").document(userId)
            .collection("cart").document(listingId)

        if (quantity > 0) {
            cartItemRef.update("quantity", quantity).await()
        } else {
            cartItemRef.delete().await()
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun clearCart(userId: String): Result<Unit> = try {
        val cartItems = firestore.collection("users").document(userId)
            .collection("cart").get().await()

        firestore.runBatch { batch ->
            for (document in cartItems.documents) {
                batch.delete(document.reference)
            }
        }.await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    // Messaging
    override fun getConversations(userId: String): Flow<Result<List<Conversation>>> = callbackFlow {
        val listener = firestore.collection(MarketplaceCollections.CONVERSATIONS)
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Result.Error(e))
                    return@addSnapshotListener
                }
                val conversations = snapshot?.toObjects(Conversation::class.java) ?: emptyList()
                trySend(Result.Success(conversations))
            }
        awaitClose { listener.remove() }
    }

    override fun getMessages(conversationId: String): Flow<Result<List<Message>>> = callbackFlow {
        val listener = firestore.collection(MarketplaceCollections.CONVERSATIONS).document(conversationId)
            .collection(MarketplaceCollections.MESSAGES)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Result.Error(e))
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(Result.Success(messages))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(message: Message): Result<Unit> = safeCall {
        firestore.runTransaction {
            val conversationRef = firestore.collection(MarketplaceCollections.CONVERSATIONS).document(message.conversationId)
            val messageRef = conversationRef.collection(MarketplaceCollections.MESSAGES).document()

            it.set(messageRef, message.copy(id = messageRef.id))
            it.update(conversationRef, "lastMessage", message.text, "lastUpdated", FieldValue.serverTimestamp())
        }.await()
        Result.Success(Unit)
    }

    override suspend fun createConversation(participants: List<String>): Result<String> = safeCall {
        val conversationsRef = firestore.collection(MarketplaceCollections.CONVERSATIONS)
        // Check if a conversation with these exact participants already exists
        val sortedParticipants = participants.sorted()
        val existing = conversationsRef
            .whereEqualTo("participants", sortedParticipants)
            .get()
            .await()

        if (existing.isEmpty) {
            val newConversationRef = conversationsRef.document()
            val newConversation = Conversation(
                id = newConversationRef.id,
                participants = sortedParticipants
            )
            newConversationRef.set(newConversation).await()
            Result.Success(newConversationRef.id)
        } else {
            Result.Success(existing.documents.first().id)
        }
    }

    private fun calculateDistance(location1: Location, geoPoint2: GeoPoint): Float {
        val location2 = Location("").apply {
            latitude = geoPoint2.latitude
            longitude = geoPoint2.longitude
        }
        return location1.distanceTo(location2) // Distance in meters
    }

    private suspend fun <T> safeCall(call: suspend () -> Result<T>): Result<T> {
        return try {
            call()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Orders
    override suspend fun createOrder(order: Order): Result<String> = safeCall {
        val newOrderRef = firestore.collection(MarketplaceCollections.ORDERS).document()
        newOrderRef.set(order.copy(id = newOrderRef.id)).await()
        Result.Success(newOrderRef.id)
    }

    override fun getOrderById(orderId: String): Flow<Result<Order?>> = flow {
        emit(Result.Loading)
        try {
            val snapshot = firestore.collection(MarketplaceCollections.ORDERS).document(orderId).get().await()
            val order = snapshot.toObject(Order::class.java)
            emit(Result.Success(order))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getOrders(userId: String): Flow<Result<List<Order>>> = callbackFlow {
        // Note: Firestore doesn't support OR queries on different fields in this manner.
        // This implementation fetches orders where the user is a buyer.
        // A more complete solution might involve two separate queries and merging the results,
        // or restructuring the data (e.g., having a 'participants' array like in conversations).
        val listener = firestore.collection(MarketplaceCollections.ORDERS)
            .whereEqualTo("buyerId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Result.Error(e))
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(Result.Success(orders))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = safeCall {
        firestore.collection(MarketplaceCollections.ORDERS).document(orderId)
            .update("status", status)
            .await()
        Result.Success(Unit)
    }
}
