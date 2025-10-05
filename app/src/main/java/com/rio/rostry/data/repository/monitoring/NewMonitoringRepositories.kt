package com.rio.rostry.data.repository.monitoring

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// Breeding Repository
interface BreedingRepository {
    fun observeActive(farmerId: String): Flow<List<BreedingPairEntity>>
    fun observeActiveCount(farmerId: String): Flow<Int>
    suspend fun upsert(pair: BreedingPairEntity)
    suspend fun countActive(farmerId: String): Int
    suspend fun getById(pairId: String): BreedingPairEntity?
}

@Singleton
class BreedingRepositoryImpl @Inject constructor(
    private val breedingPairDao: BreedingPairDao
) : BreedingRepository {
    override fun observeActive(farmerId: String): Flow<List<BreedingPairEntity>> =
        breedingPairDao.observeActive(farmerId)

    override fun observeActiveCount(farmerId: String): Flow<Int> =
        breedingPairDao.observeActiveCount(farmerId)

    override suspend fun upsert(pair: BreedingPairEntity) =
        breedingPairDao.upsert(pair)

    override suspend fun countActive(farmerId: String): Int =
        breedingPairDao.countActive(farmerId)

    override suspend fun getById(pairId: String): BreedingPairEntity? =
        breedingPairDao.getById(pairId)
}

// Farm Alert Repository
interface FarmAlertRepository {
    fun observeUnread(farmerId: String): Flow<List<FarmAlertEntity>>
    suspend fun countUnread(farmerId: String): Int
    suspend fun insert(alert: FarmAlertEntity)
    suspend fun markRead(alertId: String)
    suspend fun cleanupExpired()
}

@Singleton
class FarmAlertRepositoryImpl @Inject constructor(
    private val farmAlertDao: FarmAlertDao
) : FarmAlertRepository {
    override fun observeUnread(farmerId: String): Flow<List<FarmAlertEntity>> =
        farmAlertDao.observeUnread(farmerId)

    override suspend fun countUnread(farmerId: String): Int =
        farmAlertDao.countUnread(farmerId)

    override suspend fun insert(alert: FarmAlertEntity) =
        farmAlertDao.insert(alert)

    override suspend fun markRead(alertId: String) =
        farmAlertDao.markRead(alertId)

    override suspend fun cleanupExpired() =
        farmAlertDao.deleteExpired(System.currentTimeMillis())
}

// Listing Draft Repository
interface ListingDraftRepository {
    suspend fun getDraft(farmerId: String): ListingDraftEntity?
    suspend fun saveDraft(draft: ListingDraftEntity)
    suspend fun deleteDraft(draftId: String)
    suspend fun cleanupExpired()
}

@Singleton
class ListingDraftRepositoryImpl @Inject constructor(
    private val listingDraftDao: ListingDraftDao
) : ListingDraftRepository {
    override suspend fun getDraft(farmerId: String): ListingDraftEntity? =
        listingDraftDao.getByFarmer(farmerId)

    override suspend fun saveDraft(draft: ListingDraftEntity) =
        listingDraftDao.upsert(draft)

    override suspend fun deleteDraft(draftId: String) =
        listingDraftDao.delete(draftId)

    override suspend fun cleanupExpired() =
        listingDraftDao.deleteExpired(System.currentTimeMillis())
}

// Farmer Dashboard Repository
interface FarmerDashboardRepository {
    fun observeLatest(farmerId: String): Flow<FarmerDashboardSnapshotEntity?>
    suspend fun upsert(snapshot: FarmerDashboardSnapshotEntity)
    suspend fun getByWeek(farmerId: String, weekStartAt: Long): FarmerDashboardSnapshotEntity?
}

@Singleton
class FarmerDashboardRepositoryImpl @Inject constructor(
    private val farmerDashboardSnapshotDao: FarmerDashboardSnapshotDao
) : FarmerDashboardRepository {
    override fun observeLatest(farmerId: String): Flow<FarmerDashboardSnapshotEntity?> =
        farmerDashboardSnapshotDao.observeLatest(farmerId)

    override suspend fun upsert(snapshot: FarmerDashboardSnapshotEntity) =
        farmerDashboardSnapshotDao.upsert(snapshot)

    override suspend fun getByWeek(farmerId: String, weekStartAt: Long): FarmerDashboardSnapshotEntity? =
        farmerDashboardSnapshotDao.getByWeek(farmerId, weekStartAt)
}
