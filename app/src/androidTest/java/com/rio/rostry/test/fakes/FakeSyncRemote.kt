package com.rio.rostry.test.fakes

import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.sync.SyncRemote
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeSyncRemote @Inject constructor(): SyncRemote {
    // Record of pushes by type for assertions
    val pushedProducts = mutableListOf<ProductEntity>()
    val pushedOrders = mutableListOf<OrderEntity>()
    val pushedTransfers = mutableListOf<TransferEntity>()
    val pushedTrackings = mutableListOf<ProductTrackingEntity>()
    val pushedChats = mutableListOf<ChatMessageEntity>()
    val pushedDailyLogs = mutableListOf<DailyLogEntity>()
    val pushedTasks = mutableListOf<TaskEntity>()
    val pushedBreedingPairs = mutableListOf<BreedingPairEntity>()
    val pushedAlerts = mutableListOf<FarmAlertEntity>()
    val pushedFarmerSnapshots = mutableListOf<FarmerDashboardSnapshotEntity>()
    val pushedVaccinations = mutableListOf<VaccinationRecordEntity>()
    val pushedGrowthRecords = mutableListOf<GrowthRecordEntity>()
    val pushedQuarantineRecords = mutableListOf<QuarantineRecordEntity>()
    val pushedMortalityRecords = mutableListOf<MortalityRecordEntity>()
    val pushedHatchingBatches = mutableListOf<HatchingBatchEntity>()
    val pushedHatchingLogs = mutableListOf<HatchingLogEntity>()
    val pushedMatingLogs = mutableListOf<MatingLogEntity>()
    val pushedEggCollections = mutableListOf<EggCollectionEntity>()
    val pushedEnthusiastSnapshots = mutableListOf<EnthusiastDashboardSnapshotEntity>()

    // Pre-seeded fetch responses for pull syncs
    var productsToFetch: List<ProductEntity> = emptyList()
    var ordersToFetch: List<OrderEntity> = emptyList()
    var transfersToFetch: List<TransferEntity> = emptyList()
    var trackingsToFetch: List<ProductTrackingEntity> = emptyList()
    var chatsToFetch: List<ChatMessageEntity> = emptyList()
    var dailyLogsToFetch: List<DailyLogEntity> = emptyList()
    var tasksToFetch: List<TaskEntity> = emptyList()
    var breedingPairsToFetch: List<BreedingPairEntity> = emptyList()
    var alertsToFetch: List<FarmAlertEntity> = emptyList()
    var farmerSnapshotsToFetch: List<FarmerDashboardSnapshotEntity> = emptyList()
    var vaccinationsToFetch: List<VaccinationRecordEntity> = emptyList()
    var growthRecordsToFetch: List<GrowthRecordEntity> = emptyList()
    var quarantineRecordsToFetch: List<QuarantineRecordEntity> = emptyList()
    var mortalityRecordsToFetch: List<MortalityRecordEntity> = emptyList()
    var hatchingBatchesToFetch: List<HatchingBatchEntity> = emptyList()
    var hatchingLogsToFetch: List<HatchingLogEntity> = emptyList()
    var matingLogsToFetch: List<MatingLogEntity> = emptyList()
    var eggCollectionsToFetch: List<EggCollectionEntity> = emptyList()
    var enthusiastSnapshotsToFetch: List<EnthusiastDashboardSnapshotEntity> = emptyList()

    override suspend fun fetchUpdatedProducts(since: Long, limit: Int): List<ProductEntity> = productsToFetch
    override suspend fun fetchUpdatedOrders(since: Long, limit: Int): List<OrderEntity> = ordersToFetch
    override suspend fun fetchUpdatedTransfers(since: Long, limit: Int): List<TransferEntity> = transfersToFetch
    override suspend fun fetchUpdatedTrackings(since: Long, limit: Int): List<ProductTrackingEntity> = trackingsToFetch
    override suspend fun fetchUpdatedChats(since: Long, limit: Int): List<ChatMessageEntity> = chatsToFetch
    override suspend fun pushProducts(entities: List<ProductEntity>): Int { pushedProducts += entities; return entities.size }
    override suspend fun fetchUpdatedDailyLogs(farmerId: String, since: Long, limit: Int): List<DailyLogEntity> = dailyLogsToFetch
    override suspend fun pushDailyLogs(farmerId: String, entities: List<DailyLogEntity>): Int { pushedDailyLogs += entities; return entities.size }
    override suspend fun fetchUpdatedTasks(farmerId: String, since: Long, limit: Int): List<TaskEntity> = tasksToFetch
    override suspend fun pushTasks(farmerId: String, entities: List<TaskEntity>): Int { pushedTasks += entities; return entities.size }
    override suspend fun pushOrders(entities: List<OrderEntity>): Int { pushedOrders += entities; return entities.size }
    override suspend fun pushTransfers(entities: List<TransferEntity>): Int { pushedTransfers += entities; return entities.size }
    override suspend fun pushTrackings(entities: List<ProductTrackingEntity>): Int { pushedTrackings += entities; return entities.size }
    override suspend fun pushChats(entities: List<ChatMessageEntity>): Int { pushedChats += entities; return entities.size }
    override suspend fun fetchUpdatedBreedingPairs(farmerId: String, since: Long, limit: Int): List<BreedingPairEntity> = breedingPairsToFetch
    override suspend fun pushBreedingPairs(farmerId: String, entities: List<BreedingPairEntity>): Int { pushedBreedingPairs += entities; return entities.size }
    override suspend fun fetchUpdatedAlerts(farmerId: String, since: Long, limit: Int): List<FarmAlertEntity> = alertsToFetch
    override suspend fun pushAlerts(farmerId: String, entities: List<FarmAlertEntity>): Int { pushedAlerts += entities; return entities.size }
    override suspend fun fetchUpdatedDashboardSnapshots(farmerId: String, since: Long, limit: Int): List<FarmerDashboardSnapshotEntity> = farmerSnapshotsToFetch
    override suspend fun pushDashboardSnapshots(farmerId: String, entities: List<FarmerDashboardSnapshotEntity>): Int { pushedFarmerSnapshots += entities; return entities.size }
    override suspend fun fetchUpdatedVaccinations(farmerId: String, since: Long, limit: Int): List<VaccinationRecordEntity> = vaccinationsToFetch
    override suspend fun pushVaccinations(farmerId: String, entities: List<VaccinationRecordEntity>): Int { pushedVaccinations += entities; return entities.size }
    override suspend fun fetchUpdatedGrowthRecords(farmerId: String, since: Long, limit: Int): List<GrowthRecordEntity> = growthRecordsToFetch
    override suspend fun pushGrowthRecords(farmerId: String, entities: List<GrowthRecordEntity>): Int { pushedGrowthRecords += entities; return entities.size }
    override suspend fun fetchUpdatedQuarantineRecords(farmerId: String, since: Long, limit: Int): List<QuarantineRecordEntity> = quarantineRecordsToFetch
    override suspend fun pushQuarantineRecords(farmerId: String, entities: List<QuarantineRecordEntity>): Int { pushedQuarantineRecords += entities; return entities.size }
    override suspend fun fetchUpdatedMortalityRecords(farmerId: String, since: Long, limit: Int): List<MortalityRecordEntity> = mortalityRecordsToFetch
    override suspend fun pushMortalityRecords(farmerId: String, entities: List<MortalityRecordEntity>): Int { pushedMortalityRecords += entities; return entities.size }
    override suspend fun fetchUpdatedHatchingBatches(farmerId: String, since: Long, limit: Int): List<HatchingBatchEntity> = hatchingBatchesToFetch
    override suspend fun pushHatchingBatches(farmerId: String, entities: List<HatchingBatchEntity>): Int { pushedHatchingBatches += entities; return entities.size }
    override suspend fun fetchUpdatedHatchingLogs(farmerId: String, since: Long, limit: Int): List<HatchingLogEntity> = hatchingLogsToFetch
    override suspend fun pushHatchingLogs(farmerId: String, entities: List<HatchingLogEntity>): Int { pushedHatchingLogs += entities; return entities.size }
    override suspend fun fetchUpdatedMatingLogs(userId: String, since: Long, limit: Int): List<MatingLogEntity> = matingLogsToFetch
    override suspend fun pushMatingLogs(userId: String, entities: List<MatingLogEntity>): Int { pushedMatingLogs += entities; return entities.size }
    override suspend fun fetchUpdatedEggCollections(userId: String, since: Long, limit: Int): List<EggCollectionEntity> = eggCollectionsToFetch
    override suspend fun pushEggCollections(userId: String, entities: List<EggCollectionEntity>): Int { pushedEggCollections += entities; return entities.size }
    override suspend fun fetchUpdatedEnthusiastSnapshots(userId: String, since: Long, limit: Int): List<EnthusiastDashboardSnapshotEntity> = enthusiastSnapshotsToFetch
    override suspend fun pushEnthusiastSnapshots(userId: String, entities: List<EnthusiastDashboardSnapshotEntity>): Int { pushedEnthusiastSnapshots += entities; return entities.size }
}
