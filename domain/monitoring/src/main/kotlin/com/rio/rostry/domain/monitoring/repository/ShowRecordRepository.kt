package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.ShowRecord
import com.rio.rostry.core.model.ShowRecordStats
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for show/competition record management.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface ShowRecordRepository {
    /**
     * Observe show records for a specific product.
     */
    fun getRecordsForProduct(productId: String): Flow<List<ShowRecord>>
    
    /**
     * Get show records for a product synchronously.
     */
    suspend fun getRecordsForProductSync(productId: String): Result<List<ShowRecord>>
    
    /**
     * Get a specific show record by ID.
     */
    suspend fun getRecord(recordId: String): Result<ShowRecord>
    
    /**
     * Add a new show record.
     */
    suspend fun addRecord(record: ShowRecord): Result<Unit>
    
    /**
     * Update an existing show record.
     */
    suspend fun updateRecord(record: ShowRecord): Result<Unit>
    
    /**
     * Delete a show record (soft delete).
     */
    suspend fun deleteRecord(recordId: String): Result<Unit>
    
    /**
     * Get statistics for show records of a product.
     */
    suspend fun getStats(productId: String): Result<List<ShowRecordStats>>
}

