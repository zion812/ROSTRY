package com.rio.rostry.data.monitoring.repository

import android.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.BiosecurityStatus
import com.rio.rostry.core.model.DiseaseZone
import com.rio.rostry.data.database.entity.DiseaseZoneEntity
import com.rio.rostry.data.monitoring.mapper.toDiseaseZone
import com.rio.rostry.data.monitoring.mapper.toEntity
import com.rio.rostry.domain.monitoring.repository.BiosecurityRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of biosecurity operations.
 * Handles disease zone management and location checking.
 */
@Singleton
class BiosecurityRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BiosecurityRepository {

    private val zonesCollection = firestore.collection("disease_zones")

    override fun getActiveZones(): Flow<Result<List<DiseaseZone>>> = callbackFlow {
        // Real-time listener for active zones
        val registration = zonesCollection
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(Exception(error.message ?: "Unknown error")))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val entities = snapshot.toObjects(DiseaseZoneEntity::class.java)
                    val zones = entities.map { it.toDiseaseZone() }
                    trySend(Result.Success(zones))
                } else {
                    trySend(Result.Success(emptyList()))
                }
            }
        
        awaitClose { registration.remove() }
    }

    override suspend fun addZone(zone: DiseaseZone): Result<Unit> {
        return try {
            zonesCollection.document(zone.zoneId).set(zone.toEntity()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateZoneStatus(zoneId: String, isActive: Boolean): Result<Unit> {
        return try {
            zonesCollection.document(zoneId).update("isActive", isActive).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun checkLocation(latitude: Double, longitude: Double): BiosecurityStatus {
        return try {
            // In a real app, we might use GeoFire or a cloud function.
            // For now, we fetch active zones and check distance locally (fine for <100 zones)
            val snapshot = zonesCollection.whereEqualTo("isActive", true).get().await()
            val activeEntities = snapshot.toObjects(DiseaseZoneEntity::class.java)
            val activeZones = activeEntities.map { it.toDiseaseZone() }
            
            val conflictingZones = activeZones.filter { zone ->
                val results = FloatArray(1)
                Location.distanceBetween(latitude, longitude, zone.latitude, zone.longitude, results)
                results[0] <= zone.radiusMeters
            }
            
            if (conflictingZones.isNotEmpty()) {
                val hasLockdown = conflictingZones.any { 
                    it.severity == com.rio.rostry.core.model.ZoneSeverity.LOCKDOWN 
                }
                if (hasLockdown) {
                    BiosecurityStatus.Blocked(conflictingZones)
                } else {
                    BiosecurityStatus.Warning(conflictingZones)
                }
            } else {
                BiosecurityStatus.Safe
            }
        } catch (e: Exception) {
            // Fail safe -> Don't block if network fails
            // For biosecurity, we assume Safe to avoid UX blockers during offline
            BiosecurityStatus.Safe
        }
    }
}


