package com.rio.rostry.breeding

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.repository.EnthusiastBreedingRepositoryImpl
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.google.firebase.analytics.FirebaseAnalytics
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AutoChickCreationTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)

    private lateinit var context: Context
    private lateinit var db: AppDatabase

    // Fakes to capture side effects
    private class FakeTaskRepository : com.rio.rostry.data.repository.monitoring.TaskRepository {
        val vaccinations = mutableListOf<Triple<String,String,Long>>()
        val growth = mutableListOf<Triple<String,String,Long>>()
        override fun observeDue(farmerId: String, now: Long) = flowOf(emptyList<com.rio.rostry.data.database.entity.TaskEntity>())
        override fun observeOverdue(farmerId: String, now: Long) = flowOf(emptyList<com.rio.rostry.data.database.entity.TaskEntity>())
        override fun observeDueWindow(farmerId: String, now: Long, endOfDay: Long) = flowOf(emptyList<com.rio.rostry.data.database.entity.TaskEntity>())
        override fun observeOverdueCount(farmerId: String, now: Long) = flowOf(0)
        override fun observeRecentCompleted(farmerId: String, limit: Int) = flowOf(emptyList<com.rio.rostry.data.database.entity.TaskEntity>())
        override fun observeByBatch(batchId: String) = flowOf(emptyList<com.rio.rostry.data.database.entity.TaskEntity>())
        override suspend fun upsert(task: com.rio.rostry.data.database.entity.TaskEntity) {}
        override suspend fun generateVaccinationTask(productId: String, farmerId: String, vaccineType: String, dueAt: Long) {
            vaccinations += Triple(productId, vaccineType, dueAt)
        }
        override suspend fun generateGrowthTask(productId: String, farmerId: String, week: Int, dueAt: Long) {
            growth += Triple(productId, "W$week", dueAt)
        }
        override suspend fun generateIncubationCheckTask(batchId: String, farmerId: String, dueAt: Long) {}
        override suspend fun findPendingByTypeProduct(farmerId: String, productId: String, taskType: String): List<com.rio.rostry.data.database.entity.TaskEntity> = emptyList()
        override suspend fun markComplete(taskId: String, completedBy: String) {}
        override suspend fun generateQuarantineCheckTask(productId: String, farmerId: String, dueAt: Long) {}
        override suspend fun snooze(taskId: String, snoozeUntil: Long) {}
        override suspend fun delete(taskId: String) {}
        override suspend fun generateStageTransitionTask(productId: String, farmerId: String, stage: String, dueAt: Long) {}
        override suspend fun generateBatchSplitTask(batchId: String, farmerId: String, dueAt: Long) {}
    }

    private class FakeVaccinationRepository : com.rio.rostry.data.repository.monitoring.VaccinationRepository {
        val upserts = mutableListOf<VaccinationRecordEntity>()
        override suspend fun upsert(record: VaccinationRecordEntity) { upserts += record }
        override fun observe(productId: String) = flowOf(emptyList<VaccinationRecordEntity>())
        override suspend fun dueReminders(byTime: Long): List<VaccinationRecordEntity> = emptyList()
    }

    private class FakeTraceabilityRepo : TraceabilityRepository {
        override suspend fun addBreedingRecord(record: BreedingRecordEntity) = Resource.Success(Unit)
        override suspend fun ancestors(productId: String, maxDepth: Int) = Resource.Success(emptyMap<Int,List<String>>())
        override suspend fun descendants(productId: String, maxDepth: Int) = Resource.Success(emptyMap<Int,List<String>>())
        override suspend fun breedingSuccess(parentId: String, partnerId: String) = Resource.Success(0 to 0)
        override suspend fun addLifecycleEvent(event: com.rio.rostry.data.database.entity.LifecycleEventEntity) = Resource.Success(Unit)
        override suspend fun verifyPath(productId: String, ancestorId: String, maxDepth: Int) = Resource.Success(true)
        override suspend fun verifyParentage(childId: String, parentId: String, partnerId: String) = Resource.Success(true)
        override suspend fun getTransferChain(productId: String) = Resource.Success<List<Any>>(emptyList())
        override fun createFamilyTree(maleId: String?, femaleId: String?, pairId: String?): String? =
            when {
                !maleId.isNullOrBlank() && !femaleId.isNullOrBlank() -> "FT_${'$'}maleId_${'$'}femaleId"
                !pairId.isNullOrBlank() -> "FT_PAIR_${'$'}pairId"
                else -> null
            }
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun completeHatch_createsChicksWithLineageAndInitialRecords() = runBlocking {
        val productDao = db.productDao()
        val eggDao = db.eggCollectionDao()
        val pairDao = db.breedingPairDao()
        val batchDao = db.hatchingBatchDao()
        val logDao = db.hatchingLogDao()

        val farmerId = "farmer1"
        val maleId = "male_${UUID.randomUUID()}"
        val femaleId = "female_${UUID.randomUUID()}"
        val now = System.currentTimeMillis()

        // Seed parent products
        productDao.upsert(ProductEntity(productId = maleId, sellerId = farmerId, name = "Male", description = "", category = "BIRD", price = 0.0, quantity = 1.0, unit = "unit", location = "", createdAt = now, updatedAt = now, lastModifiedAt = now, isDeleted = false, dirty = true))
        productDao.upsert(ProductEntity(productId = femaleId, sellerId = farmerId, name = "Female", description = "", category = "BIRD", price = 0.0, quantity = 1.0, unit = "unit", location = "", createdAt = now, updatedAt = now, lastModifiedAt = now, isDeleted = false, dirty = true))

        // Seed breeding pair
        val pairId = "pair_${UUID.randomUUID()}"
        pairDao.upsert(BreedingPairEntity(pairId = pairId, farmerId = farmerId, maleProductId = maleId, femaleProductId = femaleId, pairedAt = now, status = "ACTIVE"))

        // Seed egg collection linked to pair
        val collectionId = "col_${UUID.randomUUID()}"
        eggDao.upsert(EggCollectionEntity(collectionId = collectionId, pairId = pairId, farmerId = farmerId, eggsCollected = 6, collectedAt = now, qualityGrade = "A", weight = null, notes = null, createdAt = now, updatedAt = now, dirty = true, syncedAt = null))

        // Seed batch linked to egg collection
        val batchId = "batch_${UUID.randomUUID()}"
        batchDao.upsert(HatchingBatchEntity(batchId = batchId, name = "Batch-1", farmerId = farmerId, startedAt = now, expectedHatchAt = now + 86400000L, temperatureC = null, humidityPct = null, eggsCount = 6, sourceCollectionId = collectionId, notes = null, updatedAt = now, dirty = true, syncedAt = null))

        val taskFake = FakeTaskRepository()
        val vaxFake = FakeVaccinationRepository()

        val repo = EnthusiastBreedingRepositoryImpl(
            matingLogDao = db.matingLogDao(),
            eggCollectionDao = eggDao,
            breedingPairDao = pairDao,
            hatchingBatchDao = batchDao,
            hatchingLogDao = logDao,
            productDao = productDao,
            db = db,
            analytics = com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker(FirebaseAnalytics.getInstance(context)),
            taskRepository = taskFake,
            vaccinationRepository = vaxFake,
            traceabilityRepository = FakeTraceabilityRepo()
        )

        val res = repo.completeHatch(batchId = batchId, successCount = 2, failureCount = 0, culledCount = 0)
        require(res is Resource.Success)

        // Verify products created
        val allProducts = db.productDao().getAllProducts().first()
        val chicks = allProducts.filter { it.name.startsWith("Chick ") }
        assertEquals(2, chicks.size)
        // Each chick should have lineage stamped and CHICK stage
        chicks.forEach { c ->
            assertNotNull(c.familyTreeId)
            assertEquals(com.rio.rostry.domain.model.LifecycleStage.CHICK, c.stage)
            assertEquals(maleId, c.parentMaleId)
            assertEquals(femaleId, c.parentFemaleId)
        }

        // Verify DailyLog and GrowthRecord exist
        chicks.forEach { c ->
            val dailyLogs = db.dailyLogDao().observeForProduct(c.productId).first()
            val growths = db.growthRecordDao().observeForProduct(c.productId).first()
            assert(dailyLogs.isNotEmpty())
            assert(growths.isNotEmpty())
        }

        // Verify hatchSuccessRate uses exact successCount/eggsCount (no double counting)
        val updatedPair = db.breedingPairDao().getById(pairId)!!
        val expectedRate = 2.0 / 6.0
        assert(kotlin.math.abs((updatedPair.hatchSuccessRate ?: 0.0) - expectedRate) < 1e-6)

        // Verify vaccination tasks scheduled via fake and vaccination upserts done
        assert(vaxFake.upserts.size >= 2)
        assert(taskFake.vaccinations.size >= 2)
        assert(taskFake.growth.size >= 1)
    }
}
