package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.MortalityRecord
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for mortality record management.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 * 
 * Note: Implementation handles automatic inventory quantity adjustment
 * when mortality records are inserted.
 */
interface MortalityRepository {
    /**
     * Observe all mortality records.
     */
    fun observeAll(): Flow<List<MortalityRecord>>
    
    /**
     * Insert a new mortality record.
     * Implementation will automatically decrement the linked FarmAsset's quantity.
     */
    suspend fun insert(record: MortalityRecord)
}
