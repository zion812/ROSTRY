package com.rio.rostry.marketplace.validation

import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.marketplace.model.ProductCategory
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ProductValidatorTest {

    private val fakeTraceability = object : TraceabilityRepository {
        override suspend fun addBreedingRecord(record: com.rio.rostry.data.database.entity.BreedingRecordEntity): Resource<Unit> =
            Resource.Success(Unit)

        override suspend fun ancestors(productId: String, maxDepth: Int): Resource<Map<Int, List<String>>> =
            Resource.Success(emptyMap())

        override suspend fun descendants(productId: String, maxDepth: Int): Resource<Map<Int, List<String>>> =
            Resource.Success(emptyMap())

        override suspend fun breedingSuccess(parentId: String, partnerId: String): Resource<Pair<Int, Int>> =
            Resource.Success(0 to 0)

        override suspend fun addLifecycleEvent(event: com.rio.rostry.data.database.entity.LifecycleEventEntity): Resource<Unit> =
            Resource.Success(Unit)

        override suspend fun verifyPath(productId: String, ancestorId: String, maxDepth: Int): Resource<Boolean> =
            Resource.Success(true)

        override suspend fun verifyParentage(childId: String, parentId: String, partnerId: String): Resource<Boolean> =
            Resource.Success(true)

        override suspend fun getTransferChain(productId: String): Resource<List<Any>> =
            Resource.Success(emptyList())

        override suspend fun validateProductLineage(productId: String, expectedParentMaleId: String?, expectedParentFemaleId: String?) =
            Resource.Success(true)

        override suspend fun getProductHealthScore(productId: String) = Resource.Success(100)

        override suspend fun getTransferEligibilityReport(productId: String): Resource<Map<String, Any>> =
            Resource.Success(mapOf("eligible" to true, "reasons" to emptyList<String>()))

        override suspend fun getNodeMetadata(productId: String): Resource<com.rio.rostry.data.repository.NodeMetadata> =
            Resource.Success(
                com.rio.rostry.data.repository.NodeMetadata(
                    productId = productId,
                    name = "Test",
                    breed = null,
                    stage = null,
                    ageWeeks = null,
                    healthScore = 100,
                    lifecycleStatus = null
                )
            )

        override suspend fun getNodeMetadataBatch(productIds: List<String>): Resource<Map<String, com.rio.rostry.data.repository.NodeMetadata>> =
            Resource.Success(
                productIds.associateWith { id ->
                    com.rio.rostry.data.repository.NodeMetadata(
                        productId = id,
                        name = "Test",
                        breed = null,
                        stage = null,
                        ageWeeks = null,
                        healthScore = 100,
                        lifecycleStatus = null
                    )
                }
            )

        override fun createFamilyTree(maleId: String?, femaleId: String?, pairId: String?): String? = null
    }

    private val vaccinationDao: VaccinationRecordDao = mockk()
    private val growthDao: GrowthRecordDao = mockk()
    private val dailyLogDao: DailyLogDao = mockk()
    private val quarantineDao: QuarantineRecordDao = mockk()

    private val pv = ProductValidator(fakeTraceability, vaccinationDao, growthDao, dailyLogDao, quarantineDao)

    private fun baseProduct(now: Long = System.currentTimeMillis()): ProductEntity = ProductEntity(
        productId = "p1",
        sellerId = "u1",
        name = "Rhode Island Red",
        description = "Healthy bird",
        category = "MEAT",
        price = 1200.0,
        quantity = 1.0,
        unit = "piece",
        location = "Farm",
        latitude = 12.9,
        longitude = 77.6,
        imageUrls = listOf("url1", "url2"),
        status = "available",
        condition = null,
        harvestDate = null,
        expiryDate = null,
        birthDate = now - 10L * 24 * 60 * 60 * 1000, // ~10 days -> YOUNG_5_20_WEEKS requires growth data
        vaccinationRecordsJson = null,
        weightGrams = null,
        heightCm = null,
        gender = null,
        color = null,
        breed = "RIR",
        familyTreeId = null,
        parentIdsJson = null,
        breedingStatus = null,
        transferHistoryJson = null
    )

    @Test
    fun `young group requires growth monitoring`() {
        // default empty flows
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct()
        val r = pv.validate(p)
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Growth monitoring") })
    }

    @Test
    fun `adding weight passes growth requirement`() {
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(weightGrams = 900.0)
        val r = pv.validate(p)
        // Other rules should pass for MEAT category
        assertTrue(r.reasons.joinToString() , r.valid)
    }

    @Test
    fun `traceable adoption requires family tree`() {
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(
            category = ProductCategory.toString(ProductCategory.AdoptionTraceable)!!,
            weightGrams = 900.0
        )
        val r = pv.validate(p)
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Family tree documentation is required") })
    }

    @Test
    fun `traceable adoption with tree passes`() {
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(
            category = ProductCategory.toString(ProductCategory.AdoptionTraceable)!!,
            familyTreeId = "tree-123",
            weightGrams = 900.0
        )
        val r = pv.validate(p)
        assertTrue(r.reasons.joinToString(), r.valid)
    }

    @Test
    fun `chick requires vaccination`() {
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val now = System.currentTimeMillis()
        val p = baseProduct(now).copy(
            birthDate = now - 5L * 24 * 60 * 60 * 1000, // 5 days -> CHICK_0_5_WEEKS
            category = ProductCategory.toString(ProductCategory.AdoptionNonTraceable)!!
        )
        val r = pv.validate(p)
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Vaccination records are required") })
    }

    @Test
    fun `adult requires gender`() {
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val now = System.currentTimeMillis()
        val p = baseProduct(now).copy(
            birthDate = now - 30L * 7 * 24 * 60 * 60 * 1000, // ~210 days -> ADULT_20_52_WEEKS
            weightGrams = 1500.0
        )
        val r = pv.validate(p)
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Gender identification required") })
    }

    // validateWithTraceability variants used by publish flow
    @Test
    fun `traceable adoption requires tree - with traceability validator`() {
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(
            category = ProductCategory.toString(ProductCategory.AdoptionTraceable)!!,
            weightGrams = 900.0
        )
        val r = runBlocking { pv.validateWithTraceability(p) }
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Family tree") })
    }

    @Test
    fun `traceable adoption with tree passes - with traceability validator`() {
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(
            category = ProductCategory.toString(ProductCategory.AdoptionTraceable)!!,
            familyTreeId = "tree-123",
            weightGrams = 900.0
        )
        val r = runBlocking { pv.validateWithTraceability(p) }
        assertTrue(r.reasons.joinToString(), r.valid)
    }

    @Test
    fun `meat category minimal valid passes - with traceability validator`() {
        coEvery { vaccinationDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { dailyLogDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { growthDao.observeForProduct(any()) } returns flowOf(emptyList())
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(
            birthDate = System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000, // ~60 days
            weightGrams = 1200.0
        )
        val r = runBlocking { pv.validateWithTraceability(p) }
        assertTrue(r.reasons.joinToString(), r.valid)
    }
}
