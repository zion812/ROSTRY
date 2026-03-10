package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.BiosecurityStatus
import com.rio.rostry.core.model.DiseaseZone
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for biosecurity operations.
 */
interface BiosecurityRepository {
    /**
     * Get all active disease zones.
     */
    fun getActiveZones(): Flow<Result<List<DiseaseZone>>>
    
    /**
     * Add a new disease zone.
     */
    suspend fun addZone(zone: DiseaseZone): Result<Unit>
    
    /**
     * Update the active status of a zone.
     */
    suspend fun updateZoneStatus(zoneId: String, isActive: Boolean): Result<Unit>
    
    /**
     * Check biosecurity status for a location.
     */
    suspend fun checkLocation(latitude: Double, longitude: Double): BiosecurityStatus
}

