package com.rio.rostry.domain.farm.repository

import com.rio.rostry.data.database.entity.WatchedLineageEntity
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for watched lineages operations.
 * Migrated from app module as part of Phase 1 repository migration.
 */
interface WatchedLineagesRepository {
    fun getWatchedLineages(): Flow<Result<List<WatchedLineageEntity>>>
    suspend fun watchLineage(assetId: String, hash: String, name: String?, breed: String?): Result<Unit>
    suspend fun unwatchLineage(watchId: String): Result<Unit>
    suspend fun toggleDiscoveryFeed(watchId: String, enabled: Boolean): Result<Unit>
}
