package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.WatchedLineageDao
import com.rio.rostry.data.database.entity.WatchedLineageEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface WatchedLineagesRepository {
    fun getWatchedLineages(): Flow<Resource<List<WatchedLineageEntity>>>
    /**
     * Watches a lineage.
     * @param assetId The ID of the asset.
     * @param hash Stable hash representing the biological identity of the lineage (typically derived from assetId).
     */
    suspend fun watchLineage(assetId: String, hash: String, name: String?, breed: String?): Resource<Unit>
    suspend fun unwatchLineage(watchId: String): Resource<Unit>
    suspend fun toggleDiscoveryFeed(watchId: String, enabled: Boolean): Resource<Unit>
}

@Singleton
class WatchedLineagesRepositoryImpl @Inject constructor(
    private val dao: WatchedLineageDao
) : WatchedLineagesRepository {

    override fun getWatchedLineages(): Flow<Resource<List<WatchedLineageEntity>>> {
        return dao.getAllWatched().map { Resource.Success(it) }
    }

    override suspend fun watchLineage(assetId: String, hash: String, name: String?, breed: String?): Resource<Unit> {
        return try {
            val existing = dao.findByAssetId(assetId)
            if (existing != null) return Resource.Success(Unit)

            val watch = WatchedLineageEntity(
                watchId = java.util.UUID.randomUUID().toString(),
                assetId = assetId,
                lineageHash = hash,
                birdName = name,
                breed = breed,
                dirty = true
            )
            dao.insert(watch)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to watch lineage")
        }
    }

    override suspend fun unwatchLineage(watchId: String): Resource<Unit> {
        return try {
            dao.deleteById(watchId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unwatch")
        }
    }

    override suspend fun toggleDiscoveryFeed(watchId: String, enabled: Boolean): Resource<Unit> {
        return try {
            dao.setFeedEnabled(watchId, enabled)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to toggle feed")
        }
    }
}
