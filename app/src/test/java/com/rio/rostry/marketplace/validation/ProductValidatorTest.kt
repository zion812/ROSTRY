package com.rio.rostry.marketplace.validation

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.marketplace.model.ProductCategory
import org.junit.Assert.*
import org.junit.Test

class ProductValidatorTest {

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
        val p = baseProduct()
        val r = ProductValidator.validate(p)
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Growth monitoring") })
    }

    @Test
    fun `adding weight passes growth requirement`() {
        val p = baseProduct().copy(weightGrams = 900.0)
        val r = ProductValidator.validate(p)
        // Other rules should pass for MEAT category
        assertTrue(r.reasons.joinToString() , r.valid)
    }

    @Test
    fun `traceable adoption requires family tree`() {
        val p = baseProduct().copy(
            category = ProductCategory.toString(ProductCategory.AdoptionTraceable)!!,
            weightGrams = 900.0
        )
        val r = ProductValidator.validate(p)
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Family tree documentation is required") })
    }

    @Test
    fun `traceable adoption with tree passes`() {
        val p = baseProduct().copy(
            category = ProductCategory.toString(ProductCategory.AdoptionTraceable)!!,
            familyTreeId = "tree-123",
            weightGrams = 900.0
        )
        val r = ProductValidator.validate(p)
        assertTrue(r.reasons.joinToString(), r.valid)
    }

    @Test
    fun `chick requires vaccination`() {
        val now = System.currentTimeMillis()
        val p = baseProduct(now).copy(
            birthDate = now - 5L * 24 * 60 * 60 * 1000, // 5 days -> CHICK_0_5_WEEKS
            category = ProductCategory.toString(ProductCategory.AdoptionNonTraceable)!!
        )
        val r = ProductValidator.validate(p)
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Vaccination records are required") })
    }

    @Test
    fun `adult requires gender`() {
        val now = System.currentTimeMillis()
        val p = baseProduct(now).copy(
            birthDate = now - 30L * 7 * 24 * 60 * 60 * 1000, // ~210 days -> ADULT_20_52_WEEKS
            weightGrams = 1500.0
        )
        val r = ProductValidator.validate(p)
        assertFalse(r.valid)
        assertTrue(r.reasons.any { it.contains("Gender identification required") })
    }
}
