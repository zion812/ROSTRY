package com.rio.rostry.data.repository.monitoring

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.database.mapper.*
import com.rio.rostry.core.model.*
import com.rio.rostry.domain.monitoring.repository.*
import com.rio.rostry.domain.commerce.repository.ListingDraftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Breeding Repository
@Singleton
class BreedingRepositoryImpl @Inject constructor(
    private val breedingPairDao: BreedingPairDao
) : BreedingRepository {
    override fun observeActive(farmerId: String): Flow<List<BreedingPair>> =
        breedingPairDao.observeActive(farmerId).map { list -> list.map { it.toDomain() } }

    override fun observeActiveCount(farmerId: String): Flow<Int> =
        breedingPairDao.observeActive(farmerId).map { it.size }

    override suspend fun upsert(pair: BreedingPair) =
        breedingPairDao.upsert(pair.toEntity())

    override suspend fun countActive(farmerId: String): Int =
        breedingPairDao.countActive(farmerId)

    override suspend fun getById(pairId: String): BreedingPair? =
        breedingPairDao.getById(pairId)?.toDomain()
}

// Farm Alert Repository moved to data:monitoring

// Listing Draft Repository
@Singleton
class ListingDraftRepositoryImpl @Inject constructor(
    private val listingDraftDao: ListingDraftDao
) : ListingDraftRepository {
    override suspend fun getDraft(farmerId: String): ListingDraft? =
        listingDraftDao.getByFarmer(farmerId)?.toDomain()

    override suspend fun saveDraft(draft: ListingDraft) =
        listingDraftDao.upsert(draft.toEntity())

    override suspend fun deleteDraft(draftId: String) =
        listingDraftDao.delete(draftId)

    override suspend fun cleanupExpired() =
        listingDraftDao.deleteExpired(System.currentTimeMillis())
}

// Farmer Dashboard Repository
@Singleton
class FarmerDashboardRepositoryImpl @Inject constructor(
    private val farmerDashboardSnapshotDao: FarmerDashboardSnapshotDao
) : FarmerDashboardRepository {
    override fun observeLatest(farmerId: String): Flow<DashboardSnapshot?> =
        farmerDashboardSnapshotDao.observeLatest(farmerId).map { it?.toDomain() }

    override suspend fun upsert(snapshot: DashboardSnapshot) =
        farmerDashboardSnapshotDao.upsert(snapshot.toEntity())

    override suspend fun getByWeek(farmerId: String, weekStartAt: Long): DashboardSnapshot? =
        farmerDashboardSnapshotDao.getByWeek(farmerId, weekStartAt)?.toDomain()
}
