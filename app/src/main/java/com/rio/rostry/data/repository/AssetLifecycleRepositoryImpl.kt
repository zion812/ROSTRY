package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.AssetLifecycleEventDao
import com.rio.rostry.data.database.entity.AssetLifecycleEventEntity
import com.rio.rostry.domain.repository.AssetLifecycleRepository
import javax.inject.Inject

class AssetLifecycleRepositoryImpl @Inject constructor(
    private val dao: AssetLifecycleEventDao
) : AssetLifecycleRepository {

    override suspend fun recordEvent(event: AssetLifecycleEventEntity) {
        dao.insert(event)
    }

    override suspend fun getEventsForAsset(assetId: String): List<AssetLifecycleEventEntity> {
        return dao.getEventsForAsset(assetId)
    }

    override suspend fun getEventById(eventId: String): AssetLifecycleEventEntity? {
        return dao.getEventById(eventId)
    }

    override suspend fun syncPendingEvents() {
        // Implementation for syncing offline events will be added here
    }
}
