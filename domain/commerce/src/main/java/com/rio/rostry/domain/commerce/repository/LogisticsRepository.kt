package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.data.database.entity.DeliveryHubEntity
import com.rio.rostry.core.common.Result

/**
 * Domain interface for logistics and delivery operations.
 * Migrated from app module as part of Phase 1 repository migration.
 */
interface LogisticsRepository {
    suspend fun assignNearestHub(productLat: Double?, productLon: Double?, userLat: Double?, userLon: Double?): Result<DeliveryHubEntity?>
    suspend fun startDelivery(orderId: String, hubId: String?): Result<Unit>
    suspend fun markDelivered(orderId: String, note: String? = null): Result<Unit>
}
