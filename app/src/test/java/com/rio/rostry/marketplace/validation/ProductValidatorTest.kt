package com.rio.rostry.marketplace.validation

import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.FamilyTreeEntity
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

    private val quarantineDao: QuarantineRecordDao = mockk()

    private val pv = ProductValidator(quarantineDao)

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
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct()
        val r = runBlocking { pv.validateWithTraceability(p) }
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Growth monitoring") })
    }

    @Test
    fun `adding weight passes growth requirement`() {
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(weightGrams = 900.0)
        val r = runBlocking { pv.validateWithTraceability(p) }
        // Other rules should pass for MEAT category
        assertTrue(r.reasons.joinToString() , r.valid)
    }

    @Test
    fun `traceable adoption requires family tree`() {
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(
            category = ProductCategory.toString(ProductCategory.AdoptionTraceable)!!,
            weightGrams = 900.0
        )
        val r = runBlocking { pv.validateWithTraceability(p) }
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Family tree documentation is required") })
    }

    @Test
    fun `traceable adoption with tree passes`() {
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
    fun `chick requires vaccination`() {
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val now = System.currentTimeMillis()
        val p = baseProduct(now).copy(
            birthDate = now - 5L * 24 * 60 * 60 * 1000, // 5 days -> CHICK_0_5_WEEKS
            category = ProductCategory.toString(ProductCategory.AdoptionNonTraceable)!!
        )
        val r = runBlocking { pv.validateWithTraceability(p) }
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Vaccination records are required") })
    }

    @Test
    fun `adult requires gender`() {
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val now = System.currentTimeMillis()
        val p = baseProduct(now).copy(
            birthDate = now - 30L * 7 * 24 * 60 * 60 * 1000, // ~210 days -> ADULT_20_52_WEEKS
            weightGrams = 1500.0
        )
        val r = runBlocking { pv.validateWithTraceability(p) }
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Gender identification required") })
    }

    // validateWithTraceability variants used by publish flow
    @Test
    fun `traceable adoption requires tree - with traceability validator`() {
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
        coEvery { quarantineDao.observeForProduct(any()) } returns flowOf(emptyList())
        val p = baseProduct().copy(
            birthDate = System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000, // ~60 days
            weightGrams = 1200.0
        )
        val r = runBlocking { pv.validateWithTraceability(p) }
        assertTrue(r.reasons.joinToString(), r.valid)
    }
}
