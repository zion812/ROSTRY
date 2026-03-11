package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.EggCollection
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.monitoring.mapper.toEggCollection
import com.rio.rostry.data.monitoring.mapper.toEntity
import com.rio.rostry.domain.monitoring.repository.EnthusiastBreedingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of EnthusiastBreedingRepository using Room database.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class EnthusiastBreedingRepositoryImpl @Inject constructor(
    private val eggCollectionDao: EggCollectionDao
) : EnthusiastBreedingRepository {

    override fun observeEggCollectionsByPair(pairId: String): Flow<List<EggCollection>> {
        return eggCollectionDao.observeByPair(pairId)
            .map { entities -> entities.map { it.toEggCollection() } }
    }

    override suspend fun getTotalEggsByPair(pairId: String): Int {
        return eggCollectionDao.getTotalEggsByPair(pairId)
    }

    override suspend fun getEggCollectionsByPair(pairId: String): List<EggCollection> {
        return eggCollectionDao.getCollectionsByPair(pairId)
            .map { it.toEggCollection() }
    }

    override suspend fun getEggCollectionsDueBetween(start: Long, end: Long): List<EggCollection> {
        return eggCollectionDao.getCollectionsDueBetween(start, end)
            .map { it.toEggCollection() }
    }

    override suspend fun countEggsForFarmerBetween(farmerId: String, start: Long, end: Long): Int {
        return eggCollectionDao.countEggsForFarmerBetween(farmerId, start, end)
    }

    override fun observeRecentByFarmer(farmerId: String, limit: Int): Flow<List<EggCollection>> {
        return eggCollectionDao.observeRecentByFarmer(farmerId, limit)
            .map { entities -> entities.map { it.toEggCollection() } }
    }

    override suspend fun getById(collectionId: String): EggCollection? {
        return eggCollectionDao.getById(collectionId)?.toEggCollection()
    }

    override suspend fun upsert(collection: EggCollection) {
        eggCollectionDao.upsert(collection.toEntity())
    }

    override suspend fun getDirty(): List<EggCollection> {
        return eggCollectionDao.getDirty()
            .map { it.toEggCollection() }
    }

    override suspend fun clearDirty(collectionIds: List<String>, syncedAt: Long) {
        eggCollectionDao.clearDirty(collectionIds, syncedAt)
    }
}
