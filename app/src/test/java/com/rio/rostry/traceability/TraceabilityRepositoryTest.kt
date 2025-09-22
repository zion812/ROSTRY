package com.rio.rostry.traceability

import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ProductTraitDao
import com.rio.rostry.data.database.dao.ProductTrackingDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.BreedingRecordEntity
import com.rio.rostry.data.database.entity.ProductTrackingEntity
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.TraceabilityRepositoryImpl
import com.rio.rostry.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TraceabilityRepositoryTest {

    private lateinit var breedingDao: BreedingRecordDao
    private lateinit var productDao: ProductDao
    private lateinit var lifecycleDao: LifecycleEventDao
    private lateinit var productTraitDao: ProductTraitDao
    private lateinit var transferDao: TransferDao
    private lateinit var productTrackingDao: ProductTrackingDao

    private lateinit var repo: TraceabilityRepository

    @Before
    fun setup() {
        breedingDao = mockk()
        productDao = mockk()
        lifecycleDao = mockk()
        productTraitDao = mockk()
        transferDao = mockk()
        productTrackingDao = mockk()
        repo = TraceabilityRepositoryImpl(
            breedingDao,
            productDao,
            lifecycleDao,
            productTraitDao,
            transferDao,
            productTrackingDao
        )
    }

    @Test
    fun ancestors_descendants_and_verification_work() = runBlocking {
        // Graph: A + B -> C, C + D -> E
        val rec1 = BreedingRecordEntity(
            recordId = "r1", childId = "C", parentId = "A", partnerId = "B", success = true, timestamp = 1L
        )
        val rec2 = BreedingRecordEntity(
            recordId = "r2", childId = "E", parentId = "C", partnerId = "D", success = true, timestamp = 2L
        )
        coEvery { breedingDao.recordsByChild("E") } returns listOf(rec2)
        coEvery { breedingDao.recordsByChild("C") } returns listOf(rec1)
        coEvery { breedingDao.recordsByChild("A") } returns emptyList()
        coEvery { breedingDao.recordsByChild("B") } returns emptyList()
        coEvery { breedingDao.recordsByChild("D") } returns emptyList()

        coEvery { breedingDao.recordsByParent("A") } returns listOf(rec1)
        coEvery { breedingDao.recordsByParent("B") } returns listOf(rec1)
        coEvery { breedingDao.recordsByParent("C") } returns listOf(rec2)
        coEvery { breedingDao.recordsByParent("D") } returns listOf(rec2)
        coEvery { breedingDao.recordsByParent("E") } returns emptyList()

        // ancestors of E up to 3 levels should include C at 1, and A,B,D at 2
        val anc = repo.ancestors("E", maxDepth = 3)
        assertTrue(anc is Resource.Success)
        val aData = requireNotNull((anc as Resource.Success).data)
        assertEquals(setOf("C"), aData[1]?.toSet())
        assertEquals(setOf("A","B","D"), aData[2]?.toSet())

        // descendants of A should include C at 1, E at 2
        val desc = repo.descendants("A", maxDepth = 3)
        assertTrue(desc is Resource.Success)
        val dData = requireNotNull((desc as Resource.Success).data)
        assertEquals(setOf("C"), dData[1]?.toSet())
        assertEquals(setOf("E"), dData[2]?.toSet())

        // verifyPath(E, A) true; verifyPath(E, D) true; verifyPath(C, D) false
        val vp1 = repo.verifyPath("E", "A")
        assertTrue(requireNotNull((vp1 as Resource.Success).data))
        val vp2 = repo.verifyPath("E", "D")
        assertTrue(requireNotNull((vp2 as Resource.Success).data))
        val vp3 = repo.verifyPath("C", "D")
        assertTrue(requireNotNull((vp3 as Resource.Success).data) == false)

        // verifyParentage(C, A, B) true and symmetric
        coEvery { breedingDao.recordsByChild("C") } returns listOf(rec1)
        val vpa = repo.verifyParentage("C", "A", "B")
        assertTrue(requireNotNull((vpa as Resource.Success).data))
        val vpb = repo.verifyParentage("C", "B", "A")
        assertTrue(requireNotNull((vpb as Resource.Success).data))
    }

    @Test
    fun transfer_chain_uses_tracking_events() = runBlocking {
        val t1 = ProductTrackingEntity(
            trackingId = "t1", productId = "P", ownerId = "owner", status = "CREATED", metadataJson = null,
            timestamp = 100L, createdAt = 100L, updatedAt = 100L, isDeleted = false, deletedAt = null, dirty = false
        )
        val t2 = ProductTrackingEntity(
            trackingId = "t2", productId = "P", ownerId = "owner", status = "MOVED", metadataJson = null,
            timestamp = 200L, createdAt = 200L, updatedAt = 200L, isDeleted = false, deletedAt = null, dirty = false
        )
        coEvery { productTrackingDao.getByProduct("P") } returns flowOf(listOf(t2, t1))
        val chainRes = repo.getTransferChain("P")
        assertTrue(chainRes is Resource.Success)
        val chain = requireNotNull((chainRes as Resource.Success).data)
        // Only tracking present; sorted ascending by timestamp
        val times = chain.filterIsInstance<ProductTrackingEntity>().map { it.timestamp }
        assertEquals(listOf(100L, 200L), times)
    }
}
