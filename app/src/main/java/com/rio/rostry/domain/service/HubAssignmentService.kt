package com.rio.rostry.domain.service

import com.rio.rostry.data.database.dao.DeliveryHubDao
import com.rio.rostry.data.database.entity.DeliveryHubEntity
import com.rio.rostry.domain.error.ErrorHandler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.roundToInt

/**
 * Assigns products and orders to delivery hubs based on proximity,
 * capacity, and service availability.
 */
@Singleton
class HubAssignmentService @Inject constructor(
    private val deliveryHubDao: DeliveryHubDao,
    private val errorHandler: ErrorHandler
) {

    companion object {
        const val MAX_HUB_RADIUS_KM = 100.0
        const val DEFAULT_CAPACITY = 1000
    }

    data class HubAssignment(
        val hubId: String,
        val hubName: String,
        val distanceKm: Double,
        val capacityAvailable: Boolean
    )

    /**
     * Find the nearest available hub for a given location.
     */
    suspend fun assignHub(latitude: Double, longitude: Double): Result<HubAssignment> {
        return try {
            val hubs = deliveryHubDao.listAll()

            if (hubs.isEmpty()) {
                return Result.failure(IllegalStateException("No delivery hubs available"))
            }

            // Calculate distances and filter by radius
            val candidates = hubs.mapNotNull { hub ->
                val distance = haversineDistanceKm(
                    latitude, longitude,
                    hub.latitude ?: 0.0, hub.longitude ?: 0.0
                )
                if (distance <= MAX_HUB_RADIUS_KM) {
                    HubAssignment(
                        hubId = hub.hubId,
                        hubName = hub.name,
                        distanceKm = distance,
                        capacityAvailable = true // Simplified capacity check
                    )
                } else null
            }.sortedBy { it.distanceKm }

            if (candidates.isEmpty()) {
                Result.failure(IllegalStateException("No hubs found within ${MAX_HUB_RADIUS_KM}km"))
            } else {
                // Pick nearest with capacity
                val assigned = candidates.first { it.capacityAvailable }
                Timber.d("Assigned hub: ${assigned.hubName} (${Math.round(assigned.distanceKm)}km)")
                Result.success(assigned)
            }
        } catch (e: Exception) {
            errorHandler.handle(e, "HubAssignmentService.assignHub")
            Result.failure(e)
        }
    }

    private fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
