package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.BreedingPair
import kotlinx.coroutines.flow.Flow

/**
 * Repository for breeding pairs and activities.
 */
interface BreedingRepository {
    fun observeActive(farmerId: String): Flow<List<BreedingPair>>
    fun observeActiveCount(farmerId: String): Flow<Int>
    suspend fun upsert(pair: BreedingPair)
    suspend fun countActive(farmerId: String): Int
    suspend fun getById(pairId: String): BreedingPair?
}
