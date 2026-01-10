package com.rio.rostry.data.sync

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.ConnectivityManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rio.rostry.domain.model.UserType

/**
 * Unit tests for SyncManager testing conflict resolution,
 * retry logic, and offline-first synchronization patterns.
 */
class SyncManagerTest {

    @MockK private lateinit var userDao: UserDao
    @MockK private lateinit var productDao: ProductDao
    @MockK private lateinit var orderDao: OrderDao
    @MockK private lateinit var productTrackingDao: ProductTrackingDao
    @MockK private lateinit var chatMessageDao: ChatMessageDao
    @MockK private lateinit var transferDao: TransferDao
    @MockK private lateinit var syncStateDao: SyncStateDao
    @MockK private lateinit var outboxDao: OutboxDao
    @MockK private lateinit var firestoreService: SyncRemote
    @MockK private lateinit var connectivityManager: ConnectivityManager
    @MockK private lateinit var breedingPairDao: BreedingPairDao
    @MockK private lateinit var farmAlertDao: FarmAlertDao
    @MockK private lateinit var farmerDashboardSnapshotDao: FarmerDashboardSnapshotDao
    @MockK private lateinit var vaccinationRecordDao: VaccinationRecordDao
    @MockK private lateinit var growthRecordDao: GrowthRecordDao
    @MockK private lateinit var quarantineRecordDao: QuarantineRecordDao
    @MockK private lateinit var mortalityRecordDao: MortalityRecordDao
    @MockK private lateinit var hatchingBatchDao: HatchingBatchDao
    @MockK private lateinit var hatchingLogDao: HatchingLogDao
    @MockK private lateinit var dailyLogDao: DailyLogDao
    @MockK private lateinit var taskDao: TaskDao
    @MockK private lateinit var matingLogDao: MatingLogDao
    @MockK private lateinit var eggCollectionDao: EggCollectionDao
    @MockK private lateinit var enthusiastDashboardSnapshotDao: EnthusiastDashboardSnapshotDao
    @MockK private lateinit var roleMigrationDao: RoleMigrationDao
    @MockK private lateinit var firebaseAuth: FirebaseAuth
    @MockK private lateinit var firebaseUser: FirebaseUser
    @MockK private lateinit var traceabilityRepository: TraceabilityRepository
    @MockK private lateinit var sessionManager: SessionManager
    @MockK private lateinit var batchSummaryDao: BatchSummaryDao

    private lateinit var syncManager: SyncManager
    private val gson = Gson()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns TEST_USER_ID
        every { connectivityManager.isOnline() } returns true
        coEvery { sessionManager.sessionRole() } returns flowOf(UserType.FARMER)
        coEvery { syncStateDao.get() } returns SyncStateEntity()

        syncManager = SyncManager(
            userDao = userDao,
            productDao = productDao,
            orderDao = orderDao,
            productTrackingDao = productTrackingDao,
            chatMessageDao = chatMessageDao,
            transferDao = transferDao,
            syncStateDao = syncStateDao,
            outboxDao = outboxDao,
            firestoreService = firestoreService,
            connectivityManager = connectivityManager,
            gson = gson,
            breedingPairDao = breedingPairDao,
            farmAlertDao = farmAlertDao,
            farmerDashboardSnapshotDao = farmerDashboardSnapshotDao,
            vaccinationRecordDao = vaccinationRecordDao,
            growthRecordDao = growthRecordDao,
            quarantineRecordDao = quarantineRecordDao,
            mortalityRecordDao = mortalityRecordDao,
            hatchingBatchDao = hatchingBatchDao,
            hatchingLogDao = hatchingLogDao,
            dailyLogDao = dailyLogDao,
            taskDao = taskDao,
            matingLogDao = matingLogDao,
            eggCollectionDao = eggCollectionDao,
            enthusiastDashboardSnapshotDao = enthusiastDashboardSnapshotDao,
            roleMigrationDao = roleMigrationDao,
            firebaseAuth = firebaseAuth,
            traceabilityRepository = traceabilityRepository,
            sessionManager = sessionManager,
            batchSummaryDao = batchSummaryDao
        )
    }

    @Test
    fun `syncAll returns success when all domains sync successfully`() = runTest {
        // Given - all remote fetches return empty
        setupEmptySync()

        // When
        val result = syncManager.syncAll()

        // Then
        assertThat(result).isInstanceOf(Resource.Success::class.java)
        val stats = (result as Resource.Success).data
        assertThat(stats).isNotNull()
    }

    @Test
    fun `syncAll handles network offline gracefully`() = runTest {
        // Given
        every { connectivityManager.isOnline() } returns false
        coEvery { firestoreService.fetchUpdatedTrackings(any(), any()) } throws Exception("Network error")

        // When
        val result = syncManager.syncAll()

        // Then - should still succeed but with errors logged
        assertThat(result).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun `syncAll respects batch size limits`() = runTest {
        // Given
        setupEmptySync()
        val largeList = (1..100).map { createTestProduct(it.toString()) }
        coEvery { productDao.getUpdatedSince(any(), limit = 50) } returns largeList.take(50)

        // When
        syncManager.syncAll()

        // Then - verify only 50 items were fetched (batch limit)
        coVerify { productDao.getUpdatedSince(any(), limit = 50) }
    }

    @Test
    fun `dirty flag is cleared after successful push`() = runTest {
        // Given
        setupEmptySync()
        val dirtyProduct = createTestProduct("dirty_1").copy(dirty = true)
        coEvery { productDao.getUpdatedSince(any(), limit = 50) } returns listOf(dirtyProduct)
        coEvery { firestoreService.pushProducts(any()) } just Runs

        val capturedProducts = slot<List<ProductEntity>>()
        coEvery { productDao.insertProducts(capture(capturedProducts)) } just Runs

        // When
        syncManager.syncAll()

        // Then
        coVerify { firestoreService.pushProducts(any()) }
        assertThat(capturedProducts.captured.first().dirty).isFalse()
    }

    @Test
    fun `conflict events are emitted when remote overwrites local`() = runTest {
        // Given
        setupEmptySync()
        val now = System.currentTimeMillis()
        val localProduct = createTestProduct("conflict_1").copy(
            name = "Local Name",
            updatedAt = now - 1000
        )
        val remoteProduct = createTestProduct("conflict_1").copy(
            name = "Remote Name",
            updatedAt = now // Remote is newer
        )

        coEvery { firestoreService.fetchUpdatedProducts(any()) } returns listOf(remoteProduct)
        coEvery { productDao.findById("conflict_1") } returns localProduct

        // Collect conflict events
        val emittedEvents = mutableListOf<SyncManager.ConflictEvent>()
        val job = kotlinx.coroutines.launch {
            syncManager.conflictEvents.collect { emittedEvents.add(it) }
        }

        // When
        syncManager.syncAll()

        // Give time for emission
        kotlinx.coroutines.delay(100)
        job.cancel()

        // Then - conflict event should be emitted for 'name' field
        assertThat(emittedEvents).isNotEmpty()
        assertThat(emittedEvents.first().conflictFields).contains("name")
    }

    @Test
    fun `outbox entries are processed in priority order`() = runTest {
        // Given
        setupEmptySync()
        coEvery { outboxDao.getPendingPrioritized(limit = 50) } returns emptyList()

        // When
        syncManager.syncAll()

        // Then - verify prioritized query was used
        coVerify { outboxDao.getPendingPrioritized(limit = 50) }
    }

    // Helper functions
    private fun setupEmptySync() {
        coEvery { firestoreService.fetchUpdatedTrackings(any(), any()) } returns emptyList()
        coEvery { firestoreService.fetchUpdatedUsers(any()) } returns emptyList()
        coEvery { firestoreService.fetchUpdatedProducts(any()) } returns emptyList()
        coEvery { firestoreService.fetchUpdatedOrders(any(), any()) } returns emptyList()
        coEvery { firestoreService.fetchUpdatedTransfers(any(), any()) } returns emptyList()
        coEvery { firestoreService.fetchUpdatedChats(any(), any()) } returns emptyList()
        coEvery { productTrackingDao.getUpdatedSince(any(), limit = 50) } returns emptyList()
        coEvery { productDao.getUpdatedSince(any(), limit = 50) } returns emptyList()
        coEvery { orderDao.getUpdatedSince(any(), limit = 50) } returns emptyList()
        coEvery { transferDao.getUpdatedSince(any(), limit = 50) } returns emptyList()
        coEvery { outboxDao.getPendingPrioritized(limit = 50) } returns emptyList()
    }

    private fun createTestProduct(id: String) = ProductEntity(
        productId = id,
        sellerId = TEST_USER_ID,
        name = "Test Product $id",
        description = "Description",
        price = 100.0,
        quantity = 10,
        unit = "BIRDS",
        category = "POULTRY",
        status = "ACTIVE",
        dirty = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    companion object {
        private const val TEST_USER_ID = "test_user_123"
    }
}
