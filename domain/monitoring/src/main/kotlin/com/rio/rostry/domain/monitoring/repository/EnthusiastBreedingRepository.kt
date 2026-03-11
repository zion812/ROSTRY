package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.EggCollection
import kotlinx.coroutines.flow.Flow

/**
 * Repository for enthusiast breeding operations including egg collections.
 */
interface EnthusiastBreedingRepository {
    /**
     * Observe all egg collections for a pair.
     */
    fun observeEggCollectionsByPair(pairId: String): Flow<List<EggCollection>>
    
    /**
     * Get total eggs collected for a pair.
     */
    suspend fun getTotalEggsByPair(pairId: String): Int
    
    /**
     * Get egg collections for a pair.
     */
    suspend fun getEggCollectionsByPair(pairId: String): List<EggCollection>
    
    /**
     * Get egg collections due between two timestamps.
     */
    suspend fun getEggCollectionsDueBetween(start: Long, end: Long): List<EggCollection>
    
    /**
     * Count eggs collected for a farmer between two timestamps.
     */
    suspend fun countEggsForFarmerBetween(farmerId: String, start: Long, end: Long): Int
    
    /**
     * Observe recent egg collections for a farmer.
     */
    fun observeRecentByFarmer(farmerId: String, limit: Int = 50): Flow<List<EggCollection>>
    
    /**
     * Get egg collection by ID.
     */
    suspend fun getById(collectionId: String): EggCollection?
    
    /**
     * Upsert an egg collection.
     */
    suspend fun upsert(collection: EggCollection)
    
    /**
     * Get dirty (unsynced) egg collections.
     */
    suspend fun getDirty(): List<EggCollection>
    
    /**
     * Clear dirty flag for collections.
     */
    suspend fun clearDirty(collectionIds: List<String>, syncedAt: Long)
}
