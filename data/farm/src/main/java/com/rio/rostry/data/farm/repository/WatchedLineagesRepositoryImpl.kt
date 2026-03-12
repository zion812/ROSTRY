package com.rio.rostry.data.farm.repository

import com.rio.rostry.data.database.dao.WatchedLineageDao
import com.rio.rostry.data.database.entity.WatchedLineageEntity
import com.rio.rostry.domain.farm.repository.WatchedLineagesRepository
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchedLineagesRepositoryImpl @Inject constructor(
    private val dao: WatchedLineageDao
) : WatchedLineagesRepository {

    override fun getWatchedLineages(): Flow<Result<List<WatchedLineageEntity>>> =
        dao.getAllWatched().map { Result.Success(it) }

    override suspend fun watchLineage(assetId: String, hash: String, name: String?, breed: String?): Result<Unit> {
        return try {
            val existing = dao.findByAssetId(assetId)
            if (existing != null) return Result.Success(Unit)
            val watch = WatchedLineageEntity(
                watchId = java.util.UUID.randomUUID().toString(),
                assetId = assetId, lineageHash = hash,
                birdName = name, breed = breed, dirty = true
            )
            dao.insert(watch)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun unwatchLineage(watchId: String): Result<Unit> {
        return try { dao.deleteById(watchId); Result.Success(Unit) }
        catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun toggleDiscoveryFeed(watchId: String, enabled: Boolean): Result<Unit> {
        return try { dao.setFeedEnabled(watchId, enabled); Result.Success(Unit) }
        catch (e: Exception) { Result.Error(e) }
    }
}
