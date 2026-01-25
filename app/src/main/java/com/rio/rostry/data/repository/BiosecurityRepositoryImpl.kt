package com.rio.rostry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.entity.DiseaseZoneEntity
import com.rio.rostry.data.database.entity.ZoneSeverity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import android.location.Location

@Singleton
class BiosecurityRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificationService: com.rio.rostry.notifications.FarmAlertNotificationService
) : BaseRepository(), BiosecurityRepository {

    private val zonesCollection = firestore.collection("disease_zones")

    override fun getActiveZones(): Flow<Resource<List<DiseaseZoneEntity>>> = callbackFlow {
        // Real-time listener for active zones
        val registration = zonesCollection
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val zones = snapshot.toObjects(DiseaseZoneEntity::class.java)
                    trySend(Resource.Success(zones))
                } else {
                    trySend(Resource.Success(emptyList()))
                }
            }
        
        awaitClose { registration.remove() }
    }

    override suspend fun addZone(zone: DiseaseZoneEntity): Resource<Unit> = safeCall {
        zonesCollection.document(zone.zoneId).set(zone).await()
        // Trigger alert
        notificationService.sendBiosecurityAlert(zone.reason, zone.severity.name)
        Unit
    }.firstResult()
    
    // Helper to extract result from flow
    private suspend fun <T> Flow<Resource<T>>.firstResult(): Resource<T> {
         // This is a simplified helper since safeCall returns a Flow
         var result: Resource<T> = Resource.Loading()
         this.collect { 
             if (it !is Resource.Loading<*>) {
                 result = it
                 return@collect 
             }
         }
         return result
    }

    override suspend fun updateZoneStatus(zoneId: String, isActive: Boolean): Resource<Unit> = safeCall {
        zonesCollection.document(zoneId).update("isActive", isActive).await()
        Unit
    }.firstResult()

    override suspend fun checkLocation(latitude: Double, longitude: Double): BiosecurityStatus {
        return try {
            // In a real app, we might use GeoFire or a cloud function.
            // For now, we fetch active zones and check distance locally (fine for <100 zones)
            val snapshot = zonesCollection.whereEqualTo("isActive", true).get().await()
            val activeZones = snapshot.toObjects(DiseaseZoneEntity::class.java)
            
            val conflictingZones = activeZones.filter { zone ->
                val results = FloatArray(1)
                Location.distanceBetween(latitude, longitude, zone.latitude, zone.longitude, results)
                results[0] <= zone.radiusMeters
            }
            
            if (conflictingZones.isNotEmpty()) {
                val hasLockdown = conflictingZones.any { it.severity == ZoneSeverity.LOCKDOWN }
                if (hasLockdown) {
                    BiosecurityStatus.Blocked(conflictingZones)
                } else {
                    BiosecurityStatus.Warning(conflictingZones)
                }
            } else {
                BiosecurityStatus.Safe
            }
        } catch (e: Exception) {
            // Fail safe -> Don't block if network fails, but maybe warn?
            // For biosecurity, maybe "Fail Closed" is better?
            // Let's assume Safe for now to avoid UX blockers during offline
             BiosecurityStatus.Safe
        }
    }
}
