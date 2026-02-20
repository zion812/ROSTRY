package com.rio.rostry.domain.repository

import com.rio.rostry.data.database.entity.AssetLifecycleEventEntity
import kotlinx.coroutines.flow.Flow

interface AssetLifecycleRepository {
    suspend fun recordEvent(event: AssetLifecycleEventEntity)
    suspend fun getEventsForAsset(assetId: String): List<AssetLifecycleEventEntity>
    suspend fun getEventById(eventId: String): AssetLifecycleEventEntity?
    suspend fun syncPendingEvents()
}
