package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.DashboardSnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Repository for farmer dashboard snapshots and statistics.
 */
interface FarmerDashboardRepository {
    fun observeLatest(farmerId: String): Flow<DashboardSnapshot?>
    suspend fun upsert(snapshot: DashboardSnapshot)
    suspend fun getByWeek(farmerId: String, weekStartAt: Long): DashboardSnapshot?
}
