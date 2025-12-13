package com.rio.rostry.traceability

import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ProductTraitDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.FamilyTreeDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.dao.ProductTrackingDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.BreedingRecordEntity
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.TraceabilityRepositoryImpl
import com.rio.rostry.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LineageVerificationTest {

    private lateinit var breedingDao: BreedingRecordDao
    private lateinit var productDao: ProductDao
    private lateinit var lifecycleDao: LifecycleEventDao
    private lateinit var productTraitDao: ProductTraitDao
    private lateinit var transferDao: TransferDao
    private lateinit var productTrackingDao: ProductTrackingDao
    private lateinit var transferVerificationDao: TransferVerificationDao
    private lateinit var disputeDao: DisputeDao
    private lateinit var vaccinationDao: VaccinationRecordDao
    private lateinit var dailyLogDao: DailyLogDao
    private lateinit var growthDao: GrowthRecordDao
    private lateinit var quarantineDao: QuarantineRecordDao
    private lateinit var familyTreeDao: FamilyTreeDao

    private lateinit var repo: TraceabilityRepository

    @Before
    fun setup() {
        breedingDao = mockk()
        productDao = mockk()
        lifecycleDao = mockk()
        productTraitDao = mockk()
        transferDao = mockk()
        productTrackingDao = mockk()
        transferVerificationDao = mockk()
        disputeDao = mockk()
        vaccinationDao = mockk()
        dailyLogDao = mockk()
        growthDao = mockk()
        quarantineDao = mockk()
        familyTreeDao = mockk()
        val userRepository = mockk<com.rio.rostry.data.repository.UserRepository>()
        repo = TraceabilityRepositoryImpl(
            breedingDao,
            productDao,
            lifecycleDao,
            productTraitDao,
            transferDao,
            transferVerificationDao,
            disputeDao,
            productTrackingDao,
            vaccinationDao,
            dailyLogDao,
            growthDao,
            quarantineDao,
            userRepository,
            familyTreeDao  // Add this line
        )
    }

    @Test
    fun verifyParentage_negativeCases() = runBlocking {
        // No records for child
        coEvery { breedingDao.recordsByChild("X") } returns emptyList()
        val res1 = repo.verifyParentage("X", "P1", "P2")
        assertTrue(res1 is Resource.Success)
        assertFalse(requireNotNull((res1 as Resource.Success).data))

        // Records exist but different parents
        val rec = BreedingRecordEntity(
            recordId = "r1", parentId = "A", partnerId = "B", childId = "C", success = true, timestamp = 1L
        )
        coEvery { breedingDao.recordsByChild("C") } returns listOf(rec)
        val res2 = repo.verifyParentage("C", "X", "Y")
        assertTrue(res2 is Resource.Success)
        assertFalse(requireNotNull((res2 as Resource.Success).data))
    }

    @Test
    fun verifyPath_negativeCase() = runBlocking {
        // Build simple graph A+B->C
        val rec = BreedingRecordEntity("r1", "A", "B", "C", true, null, 1L)
        coEvery { breedingDao.recordsByChild("C") } returns listOf(rec)
        coEvery { breedingDao.recordsByChild("A") } returns emptyList()
        coEvery { breedingDao.recordsByChild("B") } returns emptyList()

        // D is not an ancestor of C
        val res = repo.verifyPath("C", "D")
        assertTrue(res is Resource.Success)
        assertFalse(requireNotNull((res as Resource.Success).data))
    }
}
