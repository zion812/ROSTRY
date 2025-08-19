package com.rio.rostry.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.rio.rostry.data.models.MarketListing
import com.rio.rostry.data.models.ListingStatus
import com.rio.rostry.utils.AppLogger
import kotlinx.coroutines.tasks.await

class MarketplaceRepository(db: FirebaseFirestore? = null) {
    private val dbInstance = db ?: FirebaseFirestore.getInstance()
    private val listingsCollection = dbInstance.collection("marketListings")
    private val logger = AppLogger.getInstance()

    suspend fun createListing(listing: MarketListing): Result<String> {
        val traceName = "createListing"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Creating new listing for fowl: ${listing.fowlId}")
            
            // Validate listing data
            val validationError = validateListingData(listing)
            if (validationError != null) {
                logger.logWarning("Listing validation failed: $validationError")
                return Result.failure(IllegalArgumentException(validationError))
            }
            
            val ref = listingsCollection.document()
            val listingWithId = listing.copy(
                listingId = ref.id,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            ref.set(listingWithId).await()
            logger.logInfo("Successfully created listing with ID: ${ref.id}")
            
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("createListing", duration, true)
            logger.stopPerformanceTrace(traceName)
            
            Result.success(ref.id)
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("createListing", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to create listing in database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to create listing in database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("createListing", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to create listing: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to create listing: ${e.message}"))
        }
    }

    suspend fun updateListing(listing: MarketListing): Result<Unit> {
        val traceName = "updateListing"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Updating listing: ${listing.listingId}")
            
            // Validate listing data
            val validationError = validateListingData(listing)
            if (validationError != null) {
                logger.logWarning("Listing validation failed: $validationError")
                return Result.failure(IllegalArgumentException(validationError))
            }
            
            // Check if listing exists
            if (listing.listingId.isBlank()) {
                logger.logWarning("Listing ID is required for update")
                return Result.failure(IllegalArgumentException("Listing ID is required for update"))
            }
            
            listingsCollection.document(listing.listingId)
                .set(listing.copy(updatedAt = System.currentTimeMillis()))
                .await()
            logger.logInfo("Successfully updated listing with ID: ${listing.listingId}")
            
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("updateListing", duration, true)
            logger.stopPerformanceTrace(traceName)
            
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("updateListing", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to update listing in database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to update listing in database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("updateListing", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to update listing: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to update listing: ${e.message}"))
        }
    }

    suspend fun getActiveListings(): Result<List<MarketListing>> {
        val traceName = "getActiveListings"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Fetching active listings")
            
            val snapshot = listingsCollection
                .whereEqualTo("status", ListingStatus.ACTIVE)
                .get()
                .await()
            val listings = snapshot.mapNotNull { document ->
                document.toObject(MarketListing::class.java)
            }
            logger.logInfo("Successfully fetched ${listings.size} active listings")
            
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getActiveListings", duration, true)
            logger.stopPerformanceTrace(traceName)
            
            Result.success(listings)
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getActiveListings", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch listings from database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to fetch listings from database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getActiveListings", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch listings: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to fetch listings: ${e.message}"))
        }
    }

    suspend fun getUserListings(userId: String): Result<List<MarketListing>> {
        val traceName = "getUserListings"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Fetching listings for user: $userId")
            
            if (userId.isBlank()) {
                logger.logWarning("User ID is required")
                return Result.failure(IllegalArgumentException("User ID is required"))
            }
            
            val snapshot = listingsCollection
                .whereEqualTo("sellerId", userId)
                .get()
                .await()
            val listings = snapshot.mapNotNull { document ->
                document.toObject(MarketListing::class.java)
            }
            logger.logInfo("Successfully fetched ${listings.size} listings for user: $userId")
            
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getUserListings", duration, true)
            logger.stopPerformanceTrace(traceName)
            
            Result.success(listings)
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getUserListings", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch user listings from database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to fetch user listings from database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getUserListings", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch user listings: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to fetch user listings: ${e.message}"))
        }
    }

    suspend fun getListing(listingId: String): Result<MarketListing> {
        val traceName = "getListing"
        logger.startPerformanceTrace(traceName)
        val startTime = System.currentTimeMillis()
        
        return try {
            logger.logInfo("Fetching listing: $listingId")
            
            if (listingId.isBlank()) {
                logger.logWarning("Listing ID is required")
                return Result.failure(IllegalArgumentException("Listing ID is required"))
            }
            
            val document = listingsCollection.document(listingId).get().await()
            if (document.exists()) {
                val listing = document.toObject(MarketListing::class.java)
                if (listing != null) {
                    logger.logInfo("Successfully fetched listing: $listingId")
                    val duration = System.currentTimeMillis() - startTime
                    logger.recordNetworkPerformance("getListing", duration, true)
                    logger.stopPerformanceTrace(traceName)
                    Result.success(listing)
                } else {
                    logger.logWarning("Listing data is corrupted for ID: $listingId")
                    val duration = System.currentTimeMillis() - startTime
                    logger.recordNetworkPerformance("getListing", duration, false)
                    logger.stopPerformanceTrace(traceName)
                    Result.failure(DataException(DataErrorType.DATA_CORRUPTION, "Listing data is corrupted"))
                }
            } else {
                logger.logWarning("Listing not found: $listingId")
                val duration = System.currentTimeMillis() - startTime
                logger.recordNetworkPerformance("getListing", duration, false)
                logger.stopPerformanceTrace(traceName)
                Result.failure(DataException(DataErrorType.NOT_FOUND, "Listing not found"))
            }
        } catch (e: FirebaseFirestoreException) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getListing", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch listing from database: ${e.message}", e)
            Result.failure(DataException(DataErrorType.DATABASE_ERROR, "Failed to fetch listing from database: ${e.message}"))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.recordNetworkPerformance("getListing", duration, false)
            logger.stopPerformanceTrace(traceName)
            logger.logError("Failed to fetch listing: ${e.message}", e)
            Result.failure(DataException(DataErrorType.UNKNOWN_ERROR, "Failed to fetch listing: ${e.message}"))
        }
    }

    /**
     * Validate listing data before saving
     */
    private fun validateListingData(listing: MarketListing): String? {
        if (listing.fowlId.isBlank()) {
            return "Fowl ID is required"
        }
        
        if (listing.sellerId.isBlank()) {
            return "Seller ID is required"
        }
        
        if (listing.price <= 0) {
            return "Valid price is required"
        }
        
        if (listing.currency.isBlank()) {
            return "Currency is required"
        }
        
        if (listing.description.isBlank()) {
            return "Description is required"
        }
        
        return null // No validation errors
    }
}