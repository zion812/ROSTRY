package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.QuarantineRecord
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for quarantine record management.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface QuarantineRepository {
    /**
     * Observe quarantine records for a specific product.
     */
    fun observe(productId: String): Flow<List<QuarantineRecord>>
    
    /**
     * Observe quarantine records by status.
     */
    fun observeByStatus(status: String): Flow<List<QuarantineRecord>>
    
    /**
     * Insert a new quarantine record.
     */
    suspend fun insert(record: QuarantineRecord)
    
    /**
     * Update an existing quarantine record.
     */
    suspend fun update(record: QuarantineRecord)
}
