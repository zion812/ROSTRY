package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.DeliveryHubDao
import com.rio.rostry.data.database.dao.OrderTrackingEventDao
import com.rio.rostry.data.database.entity.DeliveryHubEntity
import com.rio.rostry.data.database.entity.OrderTrackingEventEntity
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.ValidationUtils
import javax.inject.Inject
import javax.inject.Singleton

interface LogisticsRepository {
    suspend fun assignNearestHub(productLat: Double?, productLon: Double?, userLat: Double?, userLon: Double?): Resource<DeliveryHubEntity?>
    suspend fun startDelivery(orderId: String, hubId: String?): Resource<Unit>
    suspend fun markDelivered(orderId: String, note: String? = null): Resource<Unit>
}

@Singleton
class LogisticsRepositoryImpl @Inject constructor(
    private val hubDao: DeliveryHubDao,
    private val trackingDao: OrderTrackingEventDao
) : LogisticsRepository {

    override suspend fun assignNearestHub(productLat: Double?, productLon: Double?, userLat: Double?, userLon: Double?): Resource<DeliveryHubEntity?> {
        return try {
            // If coords missing, fallback to first hub if any
            val hubs = hubDao.listAll()
            if (hubs.isEmpty()) Resource.Success(null) else if (productLat == null || productLon == null || userLat == null || userLon == null) {
                Resource.Success(hubs.firstOrNull())
            } else {
                // Choose hub by minimizing (product->hub + hub->user)
                var best: DeliveryHubEntity? = null
                var bestDist = Double.MAX_VALUE
                for (hub in hubs) {
                    val hlat = hub.latitude ?: continue
                    val hlon = hub.longitude ?: continue
                    val d1 = ValidationUtils.distanceKm(productLat, productLon, hlat, hlon)
                    val d2 = ValidationUtils.distanceKm(hlat, hlon, userLat, userLon)
                    val total = d1 + d2
                    if (total < bestDist) {
                        bestDist = total
                        best = hub
                    }
                }
                Resource.Success(best)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to assign hub")
        }
    }

    override suspend fun startDelivery(orderId: String, hubId: String?): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            trackingDao.insert(
                OrderTrackingEventEntity(
                    eventId = java.util.UUID.randomUUID().toString(),
                    orderId = orderId,
                    status = "OUT_FOR_DELIVERY",
                    hubId = hubId,
                    note = "Routed via hub ${hubId ?: "N/A"}",
                    timestamp = now
                )
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to start delivery")
        }
    }

    override suspend fun markDelivered(orderId: String, note: String?): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()
            trackingDao.insert(
                OrderTrackingEventEntity(
                    eventId = java.util.UUID.randomUUID().toString(),
                    orderId = orderId,
                    status = "DELIVERED",
                    hubId = null,
                    note = note ?: "Delivered",
                    timestamp = now
                )
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark delivered")
        }
    }
}
