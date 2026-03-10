package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.HatchingBatch
import com.rio.rostry.core.model.HatchingLog
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for hatching batch and log management.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface HatchingRepository {
    /**
     * Observe all hatching batches.
     */
    fun observeBatches(): Flow<List<HatchingBatch>>
    
    /**
     * Observe hatching logs for a specific batch.
     */
    fun observeLogs(batchId: String): Flow<List<HatchingLog>>
    
    /**
     * Insert or update a hatching batch.
     */
    suspend fun upsert(batch: HatchingBatch)
    
    /**
     * Insert a new hatching log entry.
     */
    suspend fun insert(log: HatchingLog)
}
