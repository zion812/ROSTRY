package com.rio.rostry.domain.monitoring.usecase

import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.monitoring.model.HealthRecord
import java.time.Instant

/**
 * Use case for tracking vaccination records.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface TrackVaccinationUseCase {
    /**
     * Track a vaccination event for a farm asset.
     * @param request The vaccination tracking request
     * @return Result containing the created health record or error
     */
    suspend operator fun invoke(request: TrackVaccinationRequest): Result<HealthRecord>
}

/**
 * Request for tracking a vaccination.
 */
data class TrackVaccinationRequest(
    val farmAssetId: String,
    val farmerId: String,
    val vaccineName: String,
    val dosage: String? = null,
    val administeredBy: String,
    val notes: String? = null,
    val nextDueDate: Instant? = null
)
