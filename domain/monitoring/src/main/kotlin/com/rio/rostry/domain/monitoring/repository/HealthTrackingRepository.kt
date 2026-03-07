package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.HealthRecord
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for health tracking operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface HealthTrackingRepository {
    /**
     * Get health records for a farm asset.
     * @param assetId The asset ID
     * @return Flow of health records
     */
    fun getHealthRecords(assetId: String): Flow<List<HealthRecord>>

    /**
     * Create a new health record.
     * @param record The health record to create
     * @return Result containing the created record or error
     */
    suspend fun createHealthRecord(record: HealthRecord): Result<HealthRecord>

    /**
     * Update a health record.
     * @param record The updated record
     * @return Result indicating success or error
     */
    suspend fun updateHealthRecord(record: HealthRecord): Result<Unit>

    /**
     * Delete a health record.
     * @param recordId The record ID to delete
     * @return Result indicating success or error
     */
    suspend fun deleteHealthRecord(recordId: String): Result<Unit>

    /**
     * Get health records by type.
     * @param assetId The asset ID
     * @param type The health record type
     * @return Flow of matching records
     */
    fun getHealthRecordsByType(assetId: String, type: String): Flow<List<HealthRecord>>
}
