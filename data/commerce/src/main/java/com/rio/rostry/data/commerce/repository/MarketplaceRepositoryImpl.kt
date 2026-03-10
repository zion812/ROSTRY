package com.rio.rostry.data.commerce.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.MarketListing
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.commerce.repository.MarketplaceRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MarketplaceRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class MarketplaceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MarketplaceRepository {

    private val listingsCollection = firestore.collection("market_listings")

    override fun getListings(): Flow<List<MarketListing>> = callbackFlow {
        val listener = listingsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val listings = snapshot?.documents?.mapNotNull {
                    it.toObject(MarketListing::class.java)
                } ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getListingById(listingId: String): Result<MarketListing> {
        return try {
            val document = listingsCollection.document(listingId).get().await()
            if (document.exists()) {
                val listing = document.toObject(MarketListing::class.java)
                if (listing != null) {
                    Result.Success(listing)
                } else {
                    Result.Error(Exception("Failed to parse listing data"))
                }
            } else {
                Result.Error(Exception("Listing not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createListing(listing: MarketListing): Result<MarketListing> {
        return try {
            listingsCollection.document(listing.id).set(listing).await()
            Result.Success(listing)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateListing(listing: MarketListing): Result<Unit> {
        return try {
            listingsCollection.document(listing.id).set(listing).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteListing(listingId: String): Result<Unit> {
        return try {
            listingsCollection.document(listingId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun searchListings(query: String): Flow<List<MarketListing>> = callbackFlow {
        val listener = listingsCollection
            .whereGreaterThanOrEqualTo("description", query)
            .whereLessThanOrEqualTo("description", query + "\uf8ff")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val listings = snapshot?.documents?.mapNotNull {
                    it.toObject(MarketListing::class.java)
                } ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }

    override fun getListingsBySeller(sellerId: String): Flow<List<MarketListing>> = callbackFlow {
        val listener = listingsCollection
            .whereEqualTo("sellerId", sellerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val listings = snapshot?.documents?.mapNotNull {
                    it.toObject(MarketListing::class.java)
                } ?: emptyList()
                trySend(listings)
            }
        awaitClose { listener.remove() }
    }
}
