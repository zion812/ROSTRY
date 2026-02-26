package com.rio.rostry.marketplace.intelligence

import android.util.Log
import com.rio.rostry.data.database.dao.DeliveryHubDao
import com.rio.rostry.data.database.dao.HubAssignmentDao
import com.rio.rostry.data.database.entity.DeliveryHubEntity
import com.rio.rostry.data.database.entity.HubAssignmentEntity
import com.rio.rostry.domain.manager.AppConfiguration
import com.rio.rostry.domain.manager.ConfigurationManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * Hub assignment result
 */
data class HubAssignment(
    val hubId: String,
    val hubName: String,
    val distanceKm: Double,
    val capacity: Int,
    val currentLoad: Int,
    val requiresManualReview: Boolean = false
)

/**
 * Location-based Hub Assignment Service.
 * 
 * Assigns products to the nearest available delivery hub based on:
 * - Seller location (latitude/longitude)
 * - Hub capacity (from configuration)  
 * - Distance calculation (haversine formula)
 * - Maximum delivery radius (100km default)
 *
 * Requirements: 7.1-7.7
 */
@Singleton
class HubAssignmentService @Inject constructor(
    private val hubDao: DeliveryHubDao,
    private val hubAssignmentDao: HubAssignmentDao,
    private val configurationManager: ConfigurationManager
) {
    companion object {
        private const val TAG = "HubAssignmentService"
        private const val EARTH_RADIUS_KM = 6371.0
        private const val DEFAULT_MAX_RADIUS_KM = 100.0
        private const val DEFAULT_HUB_CAPACITY = 1000
    }

    /**
     * Assign a product to the nearest hub based on seller location.
     * Considers hub capacity and distance limits.
     *
     * @param productId The product to assign
     * @param sellerLat Seller's latitude
     * @param sellerLon Seller's longitude
     * @return HubAssignment result
     */
    suspend fun assignHub(
        productId: String,
        sellerLat: Double,
        sellerLon: Double
    ): HubAssignment {
        val config = try { configurationManager.get() } catch (e: Exception) { null }
        val maxRadius = config?.thresholds?.deliveryRadiusKm ?: DEFAULT_MAX_RADIUS_KM
        val hubCapacity = config?.thresholds?.hubCapacity ?: DEFAULT_HUB_CAPACITY

        // Get all hubs
        val allHubs = hubDao.listAll()
        if (allHubs.isEmpty()) {
            Log.w(TAG, "No delivery hubs available. Flagging product $productId for manual review.")
            return HubAssignment(
                hubId = "",
                hubName = "Manual Review Required",
                distanceKm = 0.0,
                capacity = 0,
                currentLoad = 0,
                requiresManualReview = true
            )
        }

        // Calculate distances and filter within radius
        val hubsWithDistance = allHubs
            .filter { it.latitude != null && it.longitude != null }
            .map { hub ->
                val distance = haversineDistance(sellerLat, sellerLon, hub.latitude!!, hub.longitude!!)
                hub to distance
            }
            .filter { it.second <= maxRadius }
            .sortedBy { it.second }

        if (hubsWithDistance.isEmpty()) {
            Log.w(TAG, "No hub within ${maxRadius}km for product $productId at ($sellerLat, $sellerLon). Flagging for manual review.")
            return HubAssignment(
                hubId = "",
                hubName = "Manual Review Required",
                distanceKm = 0.0,
                capacity = hubCapacity,
                currentLoad = 0,
                requiresManualReview = true
            )
        }

        // Find nearest hub with available capacity
        for ((hub, distance) in hubsWithDistance) {
            val currentLoad = hubAssignmentDao.getHubLoadCount(hub.hubId)
            if (currentLoad < hubCapacity) {
                // Assign to this hub
                val entity = HubAssignmentEntity(
                    productId = productId,
                    hubId = hub.hubId,
                    distanceKm = distance,
                    assignedAt = System.currentTimeMillis(),
                    sellerLocationLat = sellerLat,
                    sellerLocationLon = sellerLon
                )
                hubAssignmentDao.insert(entity)
                
                Log.i(TAG, "Assigned product $productId to hub ${hub.hubId} (${hub.name}) at ${String.format("%.1f", distance)}km")
                return HubAssignment(
                    hubId = hub.hubId,
                    hubName = hub.name,
                    distanceKm = distance,
                    capacity = hubCapacity,
                    currentLoad = currentLoad + 1
                )
            }
        }

        // All hubs at capacity - assign to nearest anyway but flag
        val (nearestHub, nearestDistance) = hubsWithDistance.first()
        val currentLoad = hubAssignmentDao.getHubLoadCount(nearestHub.hubId)
        Log.w(TAG, "All hubs at capacity. Assigning product $productId to nearest hub ${nearestHub.hubId} with overflow.")

        val entity = HubAssignmentEntity(
            productId = productId,
            hubId = nearestHub.hubId,
            distanceKm = nearestDistance,
            assignedAt = System.currentTimeMillis(),
            sellerLocationLat = sellerLat,
            sellerLocationLon = sellerLon
        )
        hubAssignmentDao.insert(entity)

        return HubAssignment(
            hubId = nearestHub.hubId,
            hubName = nearestHub.name,
            distanceKm = nearestDistance,
            capacity = hubCapacity,
            currentLoad = currentLoad + 1,
            requiresManualReview = true
        )
    }

    /**
     * Update hub assignment when seller location changes.
     */
    suspend fun updateAssignment(productId: String, newLat: Double, newLon: Double): HubAssignment {
        // Delete existing assignment
        hubAssignmentDao.deleteByProductId(productId)
        // Reassign
        return assignHub(productId, newLat, newLon)
    }

    /**
     * Validate hub assignment before order confirmation.
     */
    suspend fun validateAssignment(productId: String): Boolean {
        val assignment = hubAssignmentDao.getByProductId(productId)
        if (assignment == null) {
            Log.w(TAG, "No hub assignment found for product $productId")
            return false
        }

        // Check the hub still exists
        val hubs = hubDao.listAll()
        val hubExists = hubs.any { it.hubId == assignment.hubId }
        if (!hubExists) {
            Log.w(TAG, "Assigned hub ${assignment.hubId} no longer exists for product $productId")
            return false
        }

        return true
    }

    /**
     * Calculate the distance between two geographic coordinates using the Haversine formula.
     * 
     * @return Distance in kilometers
     */
    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + 
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * 
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }
}
