package com.rio.rostry.performance

import app.cash.turbine.test
import com.rio.rostry.ui.enthusiast.EnthusiastHomeViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertTrue
import org.junit.Test
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.EnthusiastDashboard
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.data.database.dao.EnthusiastDashboardSnapshotDao
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.database.dao.EventsDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.data.database.entity.TransferEntity

@OptIn(ExperimentalCoroutinesApi::class)
class HomeLoadPerformanceTest {

    @Test
    fun enthusiastHome_firstEmission_under3s() = runTest {
        val uid = "U123"
        val analyticsRepo = mockk<AnalyticsRepository>()
        val breedingRepo = mockk<EnthusiastBreedingRepository>()
        val snapshotDao = mockk<EnthusiastDashboardSnapshotDao>()
        val farmAlertDao = mockk<FarmAlertDao>()
        val eventsDao = mockk<EventsDao>()
        val transferDao = mockk<TransferDao>()
        val growthDao = mockk<GrowthRecordDao>()
        val pairDao = mockk<BreedingPairDao>()
        val hatchingBatchDao = mockk<HatchingBatchDao>()
        val currentUser = mockk<CurrentUserProvider>()
        val syncManager = mockk<SyncManager>(relaxed = true)
        val tracker = mockk<EnthusiastAnalyticsTracker>(relaxed = true)

        every { currentUser.userIdOrNull() } returns uid
        every { analyticsRepo.enthusiastDashboard(uid) } returns MutableStateFlow(EnthusiastDashboard(0.0, 0, 0.0, emptyList()))
        every { transferDao.getTransfersFromUser(uid) } returns flowOf(emptyList<TransferEntity>())
        every { transferDao.getTransfersToUser(uid) } returns flowOf(emptyList<TransferEntity>())
        every { breedingRepo.observePairsToMate(uid) } returns flowOf(emptyList())
        every { breedingRepo.observeEggsCollectedToday(uid) } returns flowOf(0)
        every { breedingRepo.observeIncubationTimers(uid) } returns flowOf(emptyList())
        every { breedingRepo.observeHatchingDue(uid, any()) } returns flowOf(emptyList())
        every { growthDao.observeCountForFarmerBetween(uid, any(), any()) } returns flowOf(0)
        every { pairDao.observeActive(uid) } returns flowOf(emptyList())
        every { hatchingBatchDao.observeHatchingDue(uid, any(), any()) } returns flowOf(emptyList())
        every { eventsDao.streamUpcoming(any()) } returns flowOf(emptyList<EventEntity>())
        every { farmAlertDao.observeUnread(uid) } returns flowOf(emptyList<FarmAlertEntity>())
        every { snapshotDao.observeLatest(uid) } returns flowOf(null)

        val start = System.currentTimeMillis()
        val vm = EnthusiastHomeViewModel(
            analyticsRepo,
            breedingRepo,
            snapshotDao,
            farmAlertDao,
            eventsDao,
            transferDao,
            growthDao,
            pairDao,
            hatchingBatchDao,
            currentUser,
            syncManager,
            tracker
        )

        var firstEmissionMs: Long = Long.MAX_VALUE
        withTimeout(3_000) {
            vm.ui.test {
                val state = awaitItem()
                if (!state.isLoading) {
                    firstEmissionMs = System.currentTimeMillis() - start
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
        assertTrue("First emission took $firstEmissionMs ms which exceeds 3000 ms budget", firstEmissionMs <= 3000)
    }

    @Test
    fun dao_queries_1k_entities_under_500ms_and_combine_under_1s() = runTest {
        // In-memory Room with Robolectric Application context
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.rio.rostry.data.database.AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val productDao = db.productDao()
            val now = System.currentTimeMillis()
            // Seed 1000 products
            val items = (1..1000).map {
                com.rio.rostry.data.database.entity.ProductEntity(
                    productId = "P$it",
                    sellerId = "S1",
                    name = "Bird $it",
                    description = "Desc",
                    category = "BIRD",
                    price = 100.0,
                    quantity = 1.0,
                    unit = "unit",
                    location = "",
                    status = "active",
                    createdAt = now - it,
                    updatedAt = now - it,
                    lastModifiedAt = now - it,
                    isDeleted = false,
                    dirty = false
                )
            }
            val t1 = kotlin.system.measureTimeMillis { productDao.insertProducts(items) }

            // Query performance (<500ms)
            val tStart = System.currentTimeMillis()
            productDao.filterByDateRange(now - 10000, now, limit = 500, offset = 0)
            val qTime = System.currentTimeMillis() - tStart
            assertTrue("DAO query exceeded 500ms: ${'$'}qTime ms", qTime <= 500)

            // Combine timing (<1s) using two flows
            val f1 = kotlinx.coroutines.flow.MutableStateFlow(1)
            val f2 = kotlinx.coroutines.flow.MutableStateFlow(2)
            val combined = kotlinx.coroutines.flow.combine(f1, f2) { a, b -> a + b }
            var first = 0
            val combineTime = kotlin.system.measureTimeMillis {
                combined.test {
                    first = awaitItem()
                    cancelAndIgnoreRemainingEvents()
                }
            }
            assertTrue("Combine first emission exceeded 1000ms: ${'$'}combineTime ms", combineTime <= 1000)
            assertTrue(first == 3)
        } finally {
            db.close()
        }
    }
}
