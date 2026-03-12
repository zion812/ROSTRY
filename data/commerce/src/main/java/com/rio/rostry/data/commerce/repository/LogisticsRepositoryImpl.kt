package com.rio.rostry.data.commerce.repository

import com.rio.rostry.data.database.dao.DeliveryHubDao
import com.rio.rostry.data.database.dao.OrderTrackingEventDao
import com.rio.rostry.data.database.entity.DeliveryHubEntity
import com.rio.rostry.data.database.entity.OrderTrackingEventEntity
import com.rio.rostry.domain.commerce.repository.LogisticsRepository
import com.rio.rostry.core.common.Result
import com.rio.rostry.utils.ValidationUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogisticsRepositoryImpl @Inject constructor(
    private val hubDao: DeliveryHubDao,
    private val trackingDao: OrderTrackingEventDao
) : LogisticsRepository {

    override suspend fun assignNearestHub(productLat: Double?, productLon: Double?, userLat: Double?, userLon: Double?): Result<DeliveryHubEntity?> {
        return try {
            val hubs = hubDao.listAll()
            if (hubs.isEmpty()) Result.Success(null) else if (productLat == null || productLon == null || userLat == null || userLon == null) {
                Result.Success(hubs.firstOrNull())
            } else {
                var best: DeliveryHubEntity? = null
                var bestDist = Double.MAX_VALUE
                for (hub in hubs) {
                    val hlat = hub.latitude ?: continue
                    val hlon = hub.longitude ?: continue
                    val d1 = ValidationUtils.distanceKm(productLat, productLon, hlat, hlon)
                    val d2 = ValidationUtils.distanceKm(hlat, hlon, userLat, userLon)
                    val total = d1 + d2
                    if (total < bestDist) { bestDist = total; best = hub }
                }
                Result.Success(best)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun startDelivery(orderId: String, hubId: String?): Result<Unit> {
        return try {
            trackingDao.insert(OrderTrackingEventEntity(
                eventId = java.util.UUID.randomUUID().toString(),
                orderId = orderId,
                status = "OUT_FOR_DELIVERY",
                hubId = hubId,
                note = "Routed via hub ${hubId ?: "N/A"}",
                timestamp = System.currentTimeMillis()
            ))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun markDelivered(orderId: String, note: String?): Result<Unit> {
        return try {
            trackingDao.insert(OrderTrackingEventEntity(
                eventId = java.util.UUID.randomUUID().toString(),
                orderId = orderId,
                status = "DELIVERED",
                hubId = null,
                note = note ?: "Delivered",
                timestamp = System.currentTimeMillis()
            ))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
