package com.rio.rostry.data.farm.repository

import com.rio.rostry.domain.farm.repository.AssetLifecycleRepository
import com.rio.rostry.core.model.AssetLifecycleEvent
import com.rio.rostry.core.common.Result
import com.rio.rostry.data.farm.mapper.toAssetLifecycleEvent
import com.rio.rostry.data.farm.mapper.toEntity
import com.rio.rostry.data.database.dao.AssetLifecycleEventDao
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetLifecycleRepositoryImpl @Inject constructor(
    private val dao: AssetLifecycleEventDao
) : AssetLifecycleRepository {

    override suspend fun recordEvent(event: AssetLifecycleEvent): Result<Unit> {
        return try {
            dao.insert(event.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to record lifecycle event")
            Result.Error(e)
        }
    }

    override suspend fun getEventsForAsset(assetId: String): Result<List<AssetLifecycleEvent>> {
        return try {
            val entities = dao.getEventsForAsset(assetId)
            Result.Success(entities.map { it.toAssetLifecycleEvent() })
        } catch (e: Exception) {
            Timber.e(e, "Failed to get lifecycle events for asset: $assetId")
            Result.Error(e)
        }
    }

    override suspend fun getEventById(eventId: String): Result<AssetLifecycleEvent?> {
        return try {
            val entity = dao.getEventById(eventId)
            Result.Success(entity?.toAssetLifecycleEvent())
        } catch (e: Exception) {
            Timber.e(e, "Failed to get lifecycle event: $eventId")
            Result.Error(e)
        }
    }

    override suspend fun syncPendingEvents(): Result<Unit> {
        return try {
            // Implementation for syncing offline events will be added here
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync pending lifecycle events")
            Result.Error(e)
        }
    }
}
