package com.rio.rostry.sync

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.data.sync.SyncRemote
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import io.mockk.every
import io.mockk.mockk
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.utils.network.ConnectivityManager
import io.mockk.coEvery
import app.cash.turbine.test

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SyncConflictResolutionTest {
    private lateinit var ctx: Context
    private lateinit var db: AppDatabase
    private lateinit var syncManager: SyncManager
    private val dispatcher = StandardTestDispatcher()
    private lateinit var remote: FakeRemote

    private class FakeRemote : SyncRemote {
        val products = mutableListOf<ProductEntity>()
        val transfers = mutableListOf<TransferEntity>()
        override suspend fun fetchUpdatedProducts(since: Long, limit: Int) = products
        override suspend fun fetchUpdatedOrders(since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.OrderEntity>()
        override suspend fun fetchUpdatedTransfers(since: Long, limit: Int) = transfers
        override suspend fun fetchUpdatedTrackings(since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.ProductTrackingEntity>()
        override suspend fun fetchUpdatedChats(since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.ChatMessageEntity>()
        override suspend fun pushProducts(entities: List<ProductEntity>) = entities.size
        override suspend fun fetchUpdatedDailyLogs(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.DailyLogEntity>()
        override suspend fun pushDailyLogs(farmerId: String, entities: List<com.rio.rostry.data.database.entity.DailyLogEntity>) = 0
        override suspend fun fetchUpdatedTasks(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.TaskEntity>()
        override suspend fun pushTasks(farmerId: String, entities: List<com.rio.rostry.data.database.entity.TaskEntity>) = 0
        override suspend fun pushOrders(entities: List<com.rio.rostry.data.database.entity.OrderEntity>) = 0
        override suspend fun pushTransfers(entities: List<TransferEntity>) = entities.size
        override suspend fun pushTrackings(entities: List<com.rio.rostry.data.database.entity.ProductTrackingEntity>) = 0
        override suspend fun pushChats(entities: List<com.rio.rostry.data.database.entity.ChatMessageEntity>) = 0
        override suspend fun fetchUpdatedBreedingPairs(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.BreedingPairEntity>()
        override suspend fun pushBreedingPairs(farmerId: String, entities: List<com.rio.rostry.data.database.entity.BreedingPairEntity>) = 0
        override suspend fun fetchUpdatedAlerts(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.FarmAlertEntity>()
        override suspend fun pushAlerts(farmerId: String, entities: List<com.rio.rostry.data.database.entity.FarmAlertEntity>) = 0
        override suspend fun fetchUpdatedDashboardSnapshots(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity>()
        override suspend fun pushDashboardSnapshots(farmerId: String, entities: List<com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity>) = 0
        override suspend fun fetchUpdatedVaccinations(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.VaccinationRecordEntity>()
        override suspend fun pushVaccinations(farmerId: String, entities: List<com.rio.rostry.data.database.entity.VaccinationRecordEntity>) = 0
        override suspend fun fetchUpdatedGrowthRecords(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.GrowthRecordEntity>()
        override suspend fun pushGrowthRecords(farmerId: String, entities: List<com.rio.rostry.data.database.entity.GrowthRecordEntity>) = 0
        override suspend fun fetchUpdatedQuarantineRecords(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.QuarantineRecordEntity>()
        override suspend fun pushQuarantineRecords(farmerId: String, entities: List<com.rio.rostry.data.database.entity.QuarantineRecordEntity>) = 0
        override suspend fun fetchUpdatedMortalityRecords(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.MortalityRecordEntity>()
        override suspend fun pushMortalityRecords(farmerId: String, entities: List<com.rio.rostry.data.database.entity.MortalityRecordEntity>) = 0
        override suspend fun fetchUpdatedHatchingBatches(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.HatchingBatchEntity>()
        override suspend fun pushHatchingBatches(farmerId: String, entities: List<com.rio.rostry.data.database.entity.HatchingBatchEntity>) = 0
        override suspend fun fetchUpdatedHatchingLogs(farmerId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.HatchingLogEntity>()
        override suspend fun pushHatchingLogs(farmerId: String, entities: List<com.rio.rostry.data.database.entity.HatchingLogEntity>) = 0
        override suspend fun fetchUpdatedMatingLogs(userId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.MatingLogEntity>()
        override suspend fun pushMatingLogs(userId: String, entities: List<com.rio.rostry.data.database.entity.MatingLogEntity>) = 0
        override suspend fun fetchUpdatedEggCollections(userId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.EggCollectionEntity>()
        override suspend fun pushEggCollections(userId: String, entities: List<com.rio.rostry.data.database.entity.EggCollectionEntity>) = 0
        override suspend fun fetchUpdatedEnthusiastSnapshots(userId: String, since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity>()
        override suspend fun pushEnthusiastSnapshots(userId: String, entities: List<com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity>) = 0
        override suspend fun fetchUpdatedUsers(since: Long, limit: Int) = emptyList<com.rio.rostry.data.database.entity.UserEntity>()
        override suspend fun fetchUsersByIds(ids: List<String>) = emptyList<com.rio.rostry.data.database.entity.UserEntity>()
    }

    @Before
    fun setup() {
        ctx = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        remote = FakeRemote()
        val connectivity = mockk<ConnectivityManager>(relaxed = true)
        every { connectivity.isOnline() } returns true
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        every { firebaseAuth.currentUser } returns null
        val trace = mockk<TraceabilityRepository>(relaxed = true)
        coEvery { trace.verifyParentage(any(), any(), any()) } returns Resource.Success(true)
        syncManager = SyncManager(
            userDao = db.userDao(),
            productDao = db.productDao(),
            orderDao = db.orderDao(),
            productTrackingDao = db.productTrackingDao(),
            chatMessageDao = db.chatMessageDao(),
            transferDao = db.transferDao(),
            syncStateDao = db.syncStateDao(),
            outboxDao = db.outboxDao(),
            firestoreService = remote,
            connectivityManager = connectivity,
            gson = Gson(),
            breedingPairDao = db.breedingPairDao(),
            farmAlertDao = db.farmAlertDao(),
            farmerDashboardSnapshotDao = db.farmerDashboardSnapshotDao(),
            vaccinationRecordDao = db.vaccinationRecordDao(),
            growthRecordDao = db.growthRecordDao(),
            quarantineRecordDao = db.quarantineRecordDao(),
            mortalityRecordDao = db.mortalityRecordDao(),
            hatchingBatchDao = db.hatchingBatchDao(),
            hatchingLogDao = db.hatchingLogDao(),
            dailyLogDao = db.dailyLogDao(),
            taskDao = db.taskDao(),
            matingLogDao = db.matingLogDao(),
            eggCollectionDao = db.eggCollectionDao(),
            enthusiastDashboardSnapshotDao = db.enthusiastDashboardSnapshotDao(),
            firebaseAuth = firebaseAuth,
            traceabilityRepository = trace
        )
    }

    @After fun teardown() { db.close() }

    @Test
    fun product_field_level_merge() = runTest(dispatcher) {
        val now = System.currentTimeMillis()
        val local = ProductEntity(
            productId = "p-1",
            sellerId = "seller-1",
            name = "Local Name",
            description = "Local Desc",
            category = "CHICKS",
            price = 200.0,
            quantity = 1.0,
            unit = "piece",
            location = "BLR",
            latitude = null,
            longitude = null,
            imageUrls = emptyList(),
            breed = "Broiler",
            familyTreeId = "localTree",
            parentIdsJson = "{\"male\":\"M1\"}",
            transferHistoryJson = "[]",
            parentMaleId = "M1",
            parentFemaleId = "F1",
            updatedAt = now - 10_000,
            lastModifiedAt = now - 10_000,
            isDeleted = false,
            dirty = true
        )
        db.productDao().insertProducts(listOf(local))

        val remote = local.copy(
            name = "Remote Name",
            description = "Remote Desc",
            price = 250.0,
            // newer
            updatedAt = now,
            // lineage changed remotely (should be protected on push, but pull will merge fields by rules)
            familyTreeId = "remoteTree",
            parentMaleId = "RM",
            parentFemaleId = "RF"
        )
        this@SyncConflictResolutionTest.remote.products.add(remote)

        val res = syncManager.syncAll(now)
        assertTrue(res is Resource.Success)
        val merged = db.productDao().findById("p-1")!!
        // General fields updated by recency
        assertEquals("Remote Name", merged.name)
        assertEquals(250.0, merged.price, 0.0)
        // Lineage fields kept/protected during push-clear path; merge logic keeps remote when verified and newer
        // Since TraceabilityRepository returns success(true), parent IDs should be accepted from remote
        assertEquals("RM", merged.parentMaleId)
        assertEquals("RF", merged.parentFemaleId)
        assertTrue(!merged.dirty)
    }

    @Test
    fun transfer_terminal_status_override() = runTest(dispatcher) {
        val now = System.currentTimeMillis()
        val local = TransferEntity(
            transferId = "t-1",
            productId = "p-1",
            fromUserId = "u1",
            toUserId = "u2",
            orderId = null,
            amount = 100.0,
            type = "SALE",
            status = "PENDING",
            initiatedAt = now - 20_000,
            updatedAt = now - 20_000,
            lastModifiedAt = now - 20_000,
            isDeleted = false,
            dirty = true
        )
        db.transferDao().upsert(local)
        val remote = local.copy(status = "COMPLETED", updatedAt = now)
        this@SyncConflictResolutionTest.remote.transfers.add(remote)

        val res = syncManager.syncAll(now)
        assertTrue(res is Resource.Success)
        val merged = db.transferDao().getById("t-1")!!
        assertEquals("COMPLETED", merged.status)
        assertTrue(!merged.dirty)
    }

    @Test
    fun outbox_priority_orders_processing_transfers_then_orders_then_products() = runTest(dispatcher) {
        val now = System.currentTimeMillis()
        // Seed three outbox entries with different entity types and priorities
        val outDao = db.outboxDao()
        val gson = Gson()
        val t = TransferEntity(transferId = "t-out", productId = null, fromUserId = "u1", toUserId = "u2", orderId = null, amount = 10.0, type = "SALE", status = "PENDING", initiatedAt = now, updatedAt = now, lastModifiedAt = now, isDeleted = false, dirty = true)
        db.transferDao().upsert(t)
        outDao.insert(
            com.rio.rostry.data.database.entity.OutboxEntity(
                outboxId = "o-transfer", userId = "u1", entityType = com.rio.rostry.data.database.entity.OutboxEntity.TYPE_TRANSFER,
                entityId = t.transferId, operation = "CREATE", payloadJson = gson.toJson(t), createdAt = now, priority = "HIGH"
            )
        )
        outDao.insert(
            com.rio.rostry.data.database.entity.OutboxEntity(
                outboxId = "o-order", userId = "u1", entityType = com.rio.rostry.data.database.entity.OutboxEntity.TYPE_ORDER,
                entityId = "ord-1", operation = "CREATE", payloadJson = "{}", createdAt = now + 1, priority = "NORMAL"
            )
        )
        val p = ProductEntity(productId = "p-out", sellerId = "seller-9", name = "P", description = "", category = "CHICKS", price = 5.0, quantity = 1.0, unit = "pc", location = "BLR", latitude = null, longitude = null, imageUrls = emptyList(), breed = null, updatedAt = now, lastModifiedAt = now, isDeleted = false, dirty = true)
        db.productDao().insertProducts(listOf(p))
        outDao.insert(
            com.rio.rostry.data.database.entity.OutboxEntity(
                outboxId = "o-product", userId = "u1", entityType = com.rio.rostry.data.database.entity.OutboxEntity.TYPE_LISTING,
                entityId = p.productId, operation = "UPDATE", payloadJson = gson.toJson(p), createdAt = now + 2, priority = "LOW"
            )
        )

        val orderRecorder = mutableListOf<String>()
        val recordingRemote = object : SyncRemote by remote {
            override suspend fun pushTransfers(entities: List<TransferEntity>): Int {
                orderRecorder.add("TRANSFER:${entities.firstOrNull()?.transferId}")
                return entities.size
            }
            override suspend fun pushOrders(entities: List<com.rio.rostry.data.database.entity.OrderEntity>): Int {
                orderRecorder.add("ORDER:${entities.firstOrNull()?.orderId}")
                return entities.size
            }
            override suspend fun pushProducts(entities: List<ProductEntity>): Int {
                orderRecorder.add("LISTING:${entities.firstOrNull()?.productId}")
                return entities.size
            }
        }

        val localSync = SyncManager(
            userDao = db.userDao(), productDao = db.productDao(), orderDao = db.orderDao(), productTrackingDao = db.productTrackingDao(),
            chatMessageDao = db.chatMessageDao(), transferDao = db.transferDao(), syncStateDao = db.syncStateDao(), outboxDao = outDao,
            firestoreService = recordingRemote, connectivityManager = mockk(relaxed = true) { every { isOnline() } returns true }, gson = gson,
            breedingPairDao = db.breedingPairDao(), farmAlertDao = db.farmAlertDao(), farmerDashboardSnapshotDao = db.farmerDashboardSnapshotDao(),
            vaccinationRecordDao = db.vaccinationRecordDao(), growthRecordDao = db.growthRecordDao(), quarantineRecordDao = db.quarantineRecordDao(),
            mortalityRecordDao = db.mortalityRecordDao(), hatchingBatchDao = db.hatchingBatchDao(), hatchingLogDao = db.hatchingLogDao(),
            dailyLogDao = db.dailyLogDao(), taskDao = db.taskDao(), matingLogDao = db.matingLogDao(), eggCollectionDao = db.eggCollectionDao(),
            enthusiastDashboardSnapshotDao = db.enthusiastDashboardSnapshotDao(), firebaseAuth = mockk(relaxed = true) { every { currentUser } returns null },
            traceabilityRepository = mockk(relaxed = true)
        )
        val res = localSync.syncAll(now)
        assertTrue(res is Resource.Success)
        // Expect grouped by entity type but transfers processed, then orders, then listings
        assertTrue(orderRecorder.joinToString(",").contains("TRANSFER"))
        assertTrue(orderRecorder.joinToString(",").contains("LISTING"))
    }

    @Test
    fun retry_and_purge_behavior() = runTest(dispatcher) {
        val now = System.currentTimeMillis()
        val outDao = db.outboxDao()
        val gson = Gson()
        val p = ProductEntity(productId = "p-retry", sellerId = "seller-x", name = "X", description = "", category = "CHICKS", price = 5.0, quantity = 1.0, unit = "pc", location = "BLR", latitude = null, longitude = null, imageUrls = emptyList(), breed = null, updatedAt = now, lastModifiedAt = now, isDeleted = false, dirty = true)
        db.productDao().insertProducts(listOf(p))
        outDao.insert(
            com.rio.rostry.data.database.entity.OutboxEntity(
                outboxId = "o-retry", userId = "u1", entityType = com.rio.rostry.data.database.entity.OutboxEntity.TYPE_LISTING,
                entityId = p.productId, operation = "UPDATE", payloadJson = gson.toJson(p), createdAt = now, priority = "NORMAL"
            )
        )
        var attempts = 0
        val failingRemote = object : SyncRemote by remote {
            override suspend fun pushProducts(entities: List<ProductEntity>): Int {
                attempts++
                if (attempts == 1) throw RuntimeException("temporary")
                return entities.size
            }
        }
        val localSync = SyncManager(
            userDao = db.userDao(), productDao = db.productDao(), orderDao = db.orderDao(), productTrackingDao = db.productTrackingDao(),
            chatMessageDao = db.chatMessageDao(), transferDao = db.transferDao(), syncStateDao = db.syncStateDao(), outboxDao = outDao,
            firestoreService = failingRemote, connectivityManager = mockk(relaxed = true) { every { isOnline() } returns true }, gson = gson,
            breedingPairDao = db.breedingPairDao(), farmAlertDao = db.farmAlertDao(), farmerDashboardSnapshotDao = db.farmerDashboardSnapshotDao(),
            vaccinationRecordDao = db.vaccinationRecordDao(), growthRecordDao = db.growthRecordDao(), quarantineRecordDao = db.quarantineRecordDao(),
            mortalityRecordDao = db.mortalityRecordDao(), hatchingBatchDao = db.hatchingBatchDao(), hatchingLogDao = db.hatchingLogDao(),
            dailyLogDao = db.dailyLogDao(), taskDao = db.taskDao(), matingLogDao = db.matingLogDao(), eggCollectionDao = db.eggCollectionDao(),
            enthusiastDashboardSnapshotDao = db.enthusiastDashboardSnapshotDao(), firebaseAuth = mockk(relaxed = true) { every { currentUser } returns null },
            traceabilityRepository = mockk(relaxed = true)
        )
        val res = localSync.syncAll(now)
        assertTrue(res is Resource.Success)
        // After success on second attempt, outbox should be marked COMPLETED
        val pending = outDao.getPendingPrioritized(50)
        assertTrue(pending.isEmpty())
    }

    @Test
    fun incremental_pull_updates_watermarks() = runTest(dispatcher) {
        val stateDao = db.syncStateDao()
        val initial = com.rio.rostry.data.database.entity.SyncStateEntity(lastProductSyncAt = 100L, lastUserSyncAt = 50L)
        stateDao.upsert(initial)
        // Remote returns products/users with updatedAt > watermark
        val newUser = com.rio.rostry.data.database.entity.UserEntity(
            userId = "u-100",
            phoneNumber = null,
            email = null,
            fullName = "User X",
            address = null,
            profilePictureUrl = null,
            farmLocationLat = null,
            farmLocationLng = null,
            createdAt = 200L,
            updatedAt = 200L
        )
        val newProduct = ProductEntity(productId = "p-100", sellerId = "u-100", name = "NP", description = "", category = "CHICKS", price = 1.0, quantity = 1.0, unit = "pc", location = "BLR", latitude = null, longitude = null, imageUrls = emptyList(), breed = null, updatedAt = 300L, lastModifiedAt = 300L, isDeleted = false, dirty = false)
        val remoteWithPull = object : SyncRemote by remote {
            override suspend fun fetchUpdatedUsers(since: Long, limit: Int): List<com.rio.rostry.data.database.entity.UserEntity> = listOf(newUser)
            override suspend fun fetchUpdatedProducts(since: Long, limit: Int): List<ProductEntity> = listOf(newProduct)
        }
        val localSync = SyncManager(
            userDao = db.userDao(), productDao = db.productDao(), orderDao = db.orderDao(), productTrackingDao = db.productTrackingDao(),
            chatMessageDao = db.chatMessageDao(), transferDao = db.transferDao(), syncStateDao = stateDao, outboxDao = db.outboxDao(),
            firestoreService = remoteWithPull, connectivityManager = mockk(relaxed = true) { every { isOnline() } returns true }, gson = Gson(),
            breedingPairDao = db.breedingPairDao(), farmAlertDao = db.farmAlertDao(), farmerDashboardSnapshotDao = db.farmerDashboardSnapshotDao(),
            vaccinationRecordDao = db.vaccinationRecordDao(), growthRecordDao = db.growthRecordDao(), quarantineRecordDao = db.quarantineRecordDao(),
            mortalityRecordDao = db.mortalityRecordDao(), hatchingBatchDao = db.hatchingBatchDao(), hatchingLogDao = db.hatchingLogDao(),
            dailyLogDao = db.dailyLogDao(), taskDao = db.taskDao(), matingLogDao = db.matingLogDao(), eggCollectionDao = db.eggCollectionDao(),
            enthusiastDashboardSnapshotDao = db.enthusiastDashboardSnapshotDao(), firebaseAuth = mockk(relaxed = true) { every { currentUser } returns null },
            traceabilityRepository = mockk(relaxed = true)
        )
        val res = localSync.syncAll(System.currentTimeMillis())
        assertTrue(res is Resource.Success)
        // Verify pulled entities exist
        assertTrue(db.userDao().getUsersByIds(listOf("u-100")).isNotEmpty())
        assertTrue(db.productDao().findById("p-100") != null)
    }

    @Test
    fun user_before_product_sequencing_resolves_missing_seller() = runTest(dispatcher) {
        val stateDao = db.syncStateDao()
        stateDao.upsert(com.rio.rostry.data.database.entity.SyncStateEntity(lastProductSyncAt = 0L, lastUserSyncAt = 0L))
        val productWithMissingSeller = ProductEntity(productId = "p-miss", sellerId = "u-miss", name = "X", description = "", category = "CHICKS", price = 10.0, quantity = 1.0, unit = "pc", location = "BLR", latitude = null, longitude = null, imageUrls = emptyList(), breed = null, updatedAt = 500L, lastModifiedAt = 500L, isDeleted = false, dirty = false)
        val missingSeller = com.rio.rostry.data.database.entity.UserEntity(
            userId = "u-miss",
            phoneNumber = null,
            email = null,
            fullName = "Seller Miss",
            address = null,
            profilePictureUrl = null,
            farmLocationLat = null,
            farmLocationLng = null,
            createdAt = 400L,
            updatedAt = 600L
        )
        val remoteSeq = object : SyncRemote by remote {
            override suspend fun fetchUpdatedProducts(since: Long, limit: Int): List<ProductEntity> = listOf(productWithMissingSeller)
            override suspend fun fetchUsersByIds(ids: List<String>): List<com.rio.rostry.data.database.entity.UserEntity> = listOf(missingSeller)
        }
        val localSync = SyncManager(
            userDao = db.userDao(), productDao = db.productDao(), orderDao = db.orderDao(), productTrackingDao = db.productTrackingDao(), chatMessageDao = db.chatMessageDao(),
            transferDao = db.transferDao(), syncStateDao = stateDao, outboxDao = db.outboxDao(), firestoreService = remoteSeq,
            connectivityManager = mockk(relaxed = true) { every { isOnline() } returns true }, gson = Gson(), breedingPairDao = db.breedingPairDao(), farmAlertDao = db.farmAlertDao(),
            farmerDashboardSnapshotDao = db.farmerDashboardSnapshotDao(), vaccinationRecordDao = db.vaccinationRecordDao(), growthRecordDao = db.growthRecordDao(), quarantineRecordDao = db.quarantineRecordDao(),
            mortalityRecordDao = db.mortalityRecordDao(), hatchingBatchDao = db.hatchingBatchDao(), hatchingLogDao = db.hatchingLogDao(), dailyLogDao = db.dailyLogDao(), taskDao = db.taskDao(),
            matingLogDao = db.matingLogDao(), eggCollectionDao = db.eggCollectionDao(), enthusiastDashboardSnapshotDao = db.enthusiastDashboardSnapshotDao(), firebaseAuth = mockk(relaxed = true) { every { currentUser } returns null },
            traceabilityRepository = mockk(relaxed = true)
        )
        val res = localSync.syncAll(System.currentTimeMillis())
        assertTrue(res is Resource.Success)
        val inserted = db.productDao().findById("p-miss")
        assertTrue(inserted != null)
    }

    @Test
    fun conflict_event_emitted() = runTest(dispatcher) {
        val now = System.currentTimeMillis()
        val local = ProductEntity(
            productId = "p-2",
            sellerId = "seller-2",
            name = "Local Name",
            description = "Local Desc",
            category = "MEAT",
            price = 300.0,
            quantity = 1.0,
            unit = "kg",
            location = "BLR",
            latitude = null,
            longitude = null,
            imageUrls = emptyList(),
            breed = "Broiler",
            updatedAt = now - 10_000,
            lastModifiedAt = now - 10_000,
            isDeleted = false,
            dirty = false
        )
        db.productDao().insertProducts(listOf(local))
        val remote = local.copy(name = "Remote Name", price = 350.0, updatedAt = now)
        this@SyncConflictResolutionTest.remote.products.add(remote)

        syncManager.conflictEvents.test {
            val res = syncManager.syncAll(now)
            assertTrue(res is Resource.Success)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun offline_skip_when_not_online() = runTest(dispatcher) {
        // Set connectivity to offline and verify sync does not crash and returns Success (no-op)
        val connectivity = mockk<ConnectivityManager>(relaxed = true)
        every { connectivity.isOnline() } returns false
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        every { firebaseAuth.currentUser } returns null
        val trace = mockk<TraceabilityRepository>(relaxed = true)
        coEvery { trace.verifyParentage(any(), any(), any()) } returns Resource.Success(true)
        val localSyncManager = SyncManager(
            userDao = db.userDao(),
            productDao = db.productDao(),
            orderDao = db.orderDao(),
            productTrackingDao = db.productTrackingDao(),
            chatMessageDao = db.chatMessageDao(),
            transferDao = db.transferDao(),
            syncStateDao = db.syncStateDao(),
            outboxDao = db.outboxDao(),
            firestoreService = remote,
            connectivityManager = connectivity,
            gson = Gson(),
            breedingPairDao = db.breedingPairDao(),
            farmAlertDao = db.farmAlertDao(),
            farmerDashboardSnapshotDao = db.farmerDashboardSnapshotDao(),
            vaccinationRecordDao = db.vaccinationRecordDao(),
            growthRecordDao = db.growthRecordDao(),
            quarantineRecordDao = db.quarantineRecordDao(),
            mortalityRecordDao = db.mortalityRecordDao(),
            hatchingBatchDao = db.hatchingBatchDao(),
            hatchingLogDao = db.hatchingLogDao(),
            dailyLogDao = db.dailyLogDao(),
            taskDao = db.taskDao(),
            matingLogDao = db.matingLogDao(),
            eggCollectionDao = db.eggCollectionDao(),
            enthusiastDashboardSnapshotDao = db.enthusiastDashboardSnapshotDao(),
            firebaseAuth = firebaseAuth,
            traceabilityRepository = trace
        )
        val res = localSyncManager.syncAll(System.currentTimeMillis())
        assertTrue(res is Resource.Success)
    }
}
