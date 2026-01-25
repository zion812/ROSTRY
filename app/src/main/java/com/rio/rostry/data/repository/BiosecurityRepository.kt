package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.DiseaseZoneEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BiosecurityRepository {
    
    fun getActiveZones(): Flow<Resource<List<DiseaseZoneEntity>>>
    
    suspend fun addZone(zone: DiseaseZoneEntity): Resource<Unit>
    
    suspend fun updateZoneStatus(zoneId: String, isActive: Boolean): Resource<Unit>
    
    suspend fun checkLocation(latitude: Double, longitude: Double): BiosecurityStatus
}

sealed class BiosecurityStatus {
    object Safe : BiosecurityStatus()
    data class Warning(val zones: List<DiseaseZoneEntity>) : BiosecurityStatus()
    data class Blocked(val zones: List<DiseaseZoneEntity>) : BiosecurityStatus()
}
