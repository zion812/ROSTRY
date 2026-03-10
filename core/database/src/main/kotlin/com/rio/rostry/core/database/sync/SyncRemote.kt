package com.rio.rostry.core.database.sync

import com.rio.rostry.data.database.entity.*
import com.rio.rostry.domain.model.UserType

/**
 * SyncRemote abstracts the remote API used by SyncManager.
 * Implemented by FirestoreService in prod and by fakes in androidTest.
 */
interface SyncRemote {
    suspend fun fetchUpdatedProducts(since: Long, limit: Int = 500): List<ProductEntity>
    suspend fun fetchUpdatedOrders(userId: String, since: Long, limit: Int = 500): List<OrderEntity>
    suspend fun fetchUpdatedTransfers(userId: String?, since: Long, limit: Int = 500): List<TransferEntity>
    suspend fun fetchUpdatedTrackings(userId: String?, since: Long, limit: Int = 500): List<ProductTrackingEntity>
    suspend fun fetchUpdatedChats(userId: String?, since: Long, limit: Int = 1000): List<ChatMessageEntity>
    suspend fun pushProducts(entities: List<ProductEntity>): Int
    suspend fun fetchUpdatedDailyLogs(userId: String, role: UserType, since: Long, limit: Int = 1000): List<DailyLogEntity>
    suspend fun pushDailyLogs(userId: String, role: UserType, entities: List<DailyLogEntity>): Int
    suspend fun fetchUpdatedTasks(userId: String, role: UserType, since: Long, limit: Int = 1000): List<TaskEntity>
    suspend fun pushTasks(userId: String, role: UserType, entities: List<TaskEntity>): Int
    suspend fun pushOrders(entities: List<OrderEntity>): Int
    suspend fun pushTransfers(entities: List<TransferEntity>): Int
    suspend fun pushTrackings(entities: List<ProductTrackingEntity>): Int
    suspend fun pushChats(entities: List<ChatMessageEntity>): Int
    suspend fun fetchUpdatedBreedingPairs(userId: String, role: UserType, since: Long, limit: Int = 500): List<BreedingPairEntity>
    suspend fun pushBreedingPairs(userId: String, role: UserType, entities: List<BreedingPairEntity>): Int
    suspend fun fetchUpdatedAlerts(userId: String, role: UserType, since: Long, limit: Int = 500): List<FarmAlertEntity>
    suspend fun pushAlerts(userId: String, role: UserType, entities: List<FarmAlertEntity>): Int
    suspend fun fetchUpdatedDashboardSnapshots(userId: String, role: UserType, since: Long, limit: Int = 100): List<FarmerDashboardSnapshotEntity>
    suspend fun pushDashboardSnapshots(userId: String, role: UserType, entities: List<FarmerDashboardSnapshotEntity>): Int
    suspend fun fetchUpdatedVaccinations(userId: String, role: UserType, since: Long, limit: Int = 500): List<VaccinationRecordEntity>
    suspend fun pushVaccinations(userId: String, role: UserType, entities: List<VaccinationRecordEntity>): Int
    suspend fun fetchUpdatedGrowthRecords(userId: String, role: UserType, since: Long, limit: Int = 500): List<GrowthRecordEntity>
    suspend fun pushGrowthRecords(userId: String, role: UserType, entities: List<GrowthRecordEntity>): Int
    suspend fun fetchUpdatedQuarantineRecords(userId: String, role: UserType, since: Long, limit: Int = 500): List<QuarantineRecordEntity>
    suspend fun pushQuarantineRecords(userId: String, role: UserType, entities: List<QuarantineRecordEntity>): Int
    suspend fun fetchUpdatedMortalityRecords(userId: String, role: UserType, since: Long, limit: Int = 500): List<MortalityRecordEntity>
    suspend fun pushMortalityRecords(userId: String, role: UserType, entities: List<MortalityRecordEntity>): Int
    suspend fun fetchUpdatedHatchingBatches(userId: String, role: UserType, since: Long, limit: Int = 500): List<HatchingBatchEntity>
    suspend fun pushHatchingBatches(userId: String, role: UserType, entities: List<HatchingBatchEntity>): Int
    suspend fun fetchUpdatedHatchingLogs(userId: String, role: UserType, since: Long, limit: Int = 500): List<HatchingLogEntity>
    suspend fun pushHatchingLogs(userId: String, role: UserType, entities: List<HatchingLogEntity>): Int
    suspend fun fetchUpdatedMatingLogs(userId: String, since: Long, limit: Int = 500): List<MatingLogEntity>
    suspend fun pushMatingLogs(userId: String, entities: List<MatingLogEntity>): Int
    suspend fun fetchUpdatedEggCollections(userId: String, since: Long, limit: Int = 500): List<EggCollectionEntity>
    suspend fun pushEggCollections(userId: String, entities: List<EggCollectionEntity>): Int
    suspend fun fetchUpdatedEnthusiastSnapshots(userId: String, since: Long, limit: Int = 100): List<EnthusiastDashboardSnapshotEntity>
    suspend fun pushEnthusiastSnapshots(userId: String, entities: List<EnthusiastDashboardSnapshotEntity>): Int
    suspend fun fetchUpdatedUsers(since: Long, limit: Int = 500): List<UserEntity>
    suspend fun fetchUsersByIds(ids: List<String>): List<UserEntity>
    suspend fun fetchUpdatedBatchSummaries(userId: String, since: Long, limit: Int = 500): List<BatchSummaryEntity>
    suspend fun pushBatchSummaries(userId: String, entities: List<BatchSummaryEntity>): Int
    suspend fun fetchUpdatedMediaItems(userId: String, since: Long, limit: Int = 500): List<MediaItemEntity>
    suspend fun pushMediaItems(userId: String, entities: List<MediaItemEntity>): Int
    suspend fun incrementFarmAssetField(assetId: String, field: String, delta: Double): com.rio.rostry.utils.Resource<Unit>
}
