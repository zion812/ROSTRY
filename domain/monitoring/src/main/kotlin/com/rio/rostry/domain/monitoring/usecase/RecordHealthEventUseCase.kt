package com.rio.rostry.domain.monitoring.usecase

import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.monitoring.model.HealthRecord

/**
 * Use case for recording health events.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface RecordHealthEventUseCase {
    /**
     * Record a health event for a farm asset.
     * @param request The health event recording request
     * @return Result containing the created health record or error
     */
    suspend operator fun invoke(request: RecordHealthEventRequest): Result<HealthRecord>
}

/**
 * Request for recording a health event.
 */
data class RecordHealthEventRequest(
    val farmAssetId: String,
    val farmerId: String,
    val recordType: String,
    val status: String,
    val symptoms: List<String>? = null,
    val diagnosis: String? = null,
    val treatment: String? = null,
    val medications: List<String>? = null,
    val notes: String? = null,
    val recordedBy: String
)
