package com.rio.rostry.enthusiast

import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.data.database.dao.MatingLogDao
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.HatchingLogDao
import com.rio.rostry.data.database.entity.BreedingPairEntity
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.data.repository.EnthusiastBreedingRepositoryImpl
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.UUID

class EnthusiastBreedingRepositoryTest {

    private lateinit var repo: EnthusiastBreedingRepository
    private lateinit var pairDao: BreedingPairDao
    private lateinit var matingDao: MatingLogDao
    private lateinit var eggDao: EggCollectionDao
    private lateinit var batchDao: HatchingBatchDao
    private lateinit var hatchDao: HatchingLogDao
    private lateinit var productDao: ProductDao
    private lateinit var appDb: com.rio.rostry.data.database.AppDatabase
    private lateinit var analytics: com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker
    private lateinit var taskRepository: com.rio.rostry.data.repository.monitoring.TaskRepository
    private lateinit var vaccinationRepository: com.rio.rostry.data.repository.monitoring.VaccinationRepository
    private lateinit var traceabilityRepository: com.rio.rostry.data.repository.TraceabilityRepository

    @Before
    fun setUp() {
        pairDao = mock(BreedingPairDao::class.java)
        matingDao = mock(MatingLogDao::class.java)
        eggDao = mock(EggCollectionDao::class.java)
        batchDao = mock(HatchingBatchDao::class.java)
        hatchDao = mock(HatchingLogDao::class.java)
        productDao = mock(ProductDao::class.java)
        appDb = mock(com.rio.rostry.data.database.AppDatabase::class.java)
        analytics = mock(com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker::class.java)
        taskRepository = mock(com.rio.rostry.data.repository.monitoring.TaskRepository::class.java)
        vaccinationRepository = mock(com.rio.rostry.data.repository.monitoring.VaccinationRepository::class.java)
        traceabilityRepository = mock(com.rio.rostry.data.repository.TraceabilityRepository::class.java)
        repo = EnthusiastBreedingRepositoryImpl(
            matingLogDao = matingDao,
            eggCollectionDao = eggDao,
            breedingPairDao = pairDao,
            hatchingBatchDao = batchDao,
            hatchingLogDao = hatchDao,
            productDao = productDao,
            db = appDb,
            analytics = analytics,
            taskRepository = taskRepository,
            vaccinationRepository = vaccinationRepository,
            traceabilityRepository = traceabilityRepository
        )
    }

    @Test
    fun testCreatePairValidation() = runBlocking {
        // Test null validation
        val result = repo.createPair(
            farmerId = "user-1",
            maleProductId = "",
            femaleProductId = "",
            notes = null
        )
        assertTrue("Should fail for null IDs", result is Resource.Error)
    }

    @Test
    fun testLogMatingValidation() = runBlocking {
        // Stub pair exists
        `when`(pairDao.getById(anyString())).thenReturn(
            BreedingPairEntity(
                pairId = "pair-1",
                farmerId = "user-1",
                maleProductId = "m1",
                femaleProductId = "f1",
                pairedAt = System.currentTimeMillis(),
                status = "ACTIVE",
                createdAt = System.currentTimeMillis()
            )
        )
        val result = repo.logMating(
            pairId = "pair-1",
            observedBehavior = null,
            conditions = null
        )
        // Mock doesn't throw; validate call made
        verify(matingDao, atLeastOnce()).upsert(any())
    }

    @Test
    fun testObservePairsToMate() = runBlocking {
        val now = System.currentTimeMillis()
        val cutoff = now - (7L * 24 * 60 * 60 * 1000)
        val pairs = listOf(
            BreedingPairEntity(
                pairId = "p1",
                farmerId = "user-1",
                maleProductId = "m1",
                femaleProductId = "f1",
                pairedAt = now,
                status = "ACTIVE",
                createdAt = now
            )
        )
        `when`(pairDao.observeActive("user-1")).thenReturn(flowOf(pairs))
        `when`(matingDao.observeLastMatedByFarmer("user-1")).thenReturn(
            flowOf(emptyList()) // No matings, so all pairs to mate
        )
        val result = repo.observePairsToMate("user-1").first()
        assertTrue("Should have pairs to mate", result.isNotEmpty())
    }

    @Test
    fun testLogHatch_eggsSetFallbackFromCollection() = runBlocking {
        // Prepare: batch without eggsCount but with sourceCollectionId
        val batchId = "batch-xyz"
        val collectionId = "coll-abc"
        val pairId = "pair-1"
        val now = System.currentTimeMillis()
        `when`(batchDao.getById(batchId)).thenReturn(
            com.rio.rostry.data.database.entity.HatchingBatchEntity(
                batchId = batchId,
                name = "Batch",
                farmerId = "user-1",
                startedAt = now,
                expectedHatchAt = now + 1000,
                temperatureC = null,
                humidityPct = null,
                eggsCount = null, // force fallback
                sourceCollectionId = collectionId,
                notes = null,
                updatedAt = now,
                dirty = false,
                syncedAt = null
            )
        )
        // Collection with eggsCollected
        `when`(eggDao.getById(collectionId)).thenReturn(
            com.rio.rostry.data.database.entity.EggCollectionEntity(
                collectionId = collectionId,
                pairId = pairId,
                farmerId = "user-1",
                eggsCollected = 12,
                collectedAt = now,
                qualityGrade = "A",
                weight = null,
                notes = null,
                createdAt = now,
                updatedAt = now,
                dirty = false,
                syncedAt = null
            )
        )
        // Pair to update
        `when`(pairDao.getById(pairId)).thenReturn(
            com.rio.rostry.data.database.entity.BreedingPairEntity(
                pairId = pairId,
                farmerId = "user-1",
                maleProductId = "m1",
                femaleProductId = "f1",
                pairedAt = now,
                status = "ACTIVE",
                eggsCollected = 0,
                hatchSuccessRate = 0.0,
                notes = null,
                createdAt = now,
                updatedAt = now,
                dirty = false,
                syncedAt = null
            )
        )
        // Hatched count
        `when`(hatchDao.countByBatchAndType(batchId, "HATCHED")).thenReturn(9)

        val res = repo.logHatch(batchId = batchId, productId = null, eventType = "HATCHED", notes = null)
        assertTrue(res is com.rio.rostry.utils.Resource.Success)
        // Verify pair was updated with rate 9/12 = 0.75
        verify(pairDao, atLeastOnce()).upsert(org.mockito.kotlin.argThat<com.rio.rostry.data.database.entity.BreedingPairEntity> {
            kotlin.math.abs(hatchSuccessRate - 0.75) < 1e-6
        })
    }
    }
