package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Product
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.LifecycleStage
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductMapperTest {

    @Test
    fun `toDomain maps all fields correctly`() {
        val entity = ProductEntity(
            productId = "p1",
            sellerId = "s1",
            name = "Bird 1",
            description = "Nice bird",
            category = "Broiler",
            price = 100.0,
            quantity = 10.0,
            unit = "piece",
            location = "Farm A",
            latitude = 1.0,
            longitude = 2.0,
            imageUrls = listOf("url1"),
            birthDate = 500L,
            gender = "Male",
            breed = "Rhode Island Red",
            birdCode = "RIR-001",
            colorTag = "RED",
            familyTreeId = "ft1",
            stage = LifecycleStage.BREEDER,
            lifecycleStatus = "active",
            createdAt = 400L,
            updatedAt = 600L,
            qrCodeUrl = "qr1",
            recordsLockedAt = null,
            autoLockAfterDays = 30
        )

        val domain = entity.toProduct()

        assertEquals(entity.productId, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.sellerId, domain.sellerId)
        assertEquals(entity.price, domain.price, 0.001)
        assertEquals(entity.breed, domain.breed)
        assertEquals("BREEDER", domain.stage)
        assertEquals("active", domain.lifecycleStatus)
        assertEquals(entity.birdCode, domain.birdCode)
    }

    @Test
    fun `toEntity maps all fields correctly`() {
        val domain = Product(
            id = "p1",
            name = "Bird 1",
            sellerId = "s1",
            category = "Broiler",
            price = 100.0,
            quantity = 10.0,
            unit = "piece",
            currency = "USD",
            description = "Nice bird",
            imageUrls = listOf("url1"),
            breed = "Rhode Island Red",
            gender = "Male",
            birthDate = 500L,
            ageWeeks = 4,
            colorTag = "RED",
            birdCode = "RIR-001",
            stage = "BREEDER",
            lifecycleStatus = "active",
            lastStageTransitionAt = 550L,
            latitude = 1.0,
            longitude = 2.0,
            location = "Farm A",
            familyTreeId = "ft1",
            parentMaleId = "pm1",
            parentFemaleId = "pf1",
            isTraceable = true,
            isVerified = true,
            verificationLevel = "LEVEL_1",
            qrCodeUrl = "qr1",
            metadata = null,
            recordsLockedAt = null,
            autoLockAfterDays = 30,
            createdAt = 400L,
            updatedAt = 600L
        )

        val entity = domain.toEntity()

        assertEquals(domain.id, entity.productId)
        assertEquals(domain.name, entity.name)
        assertEquals(domain.sellerId, entity.sellerId)
        assertEquals(domain.price, entity.price, 0.001)
        assertEquals(domain.breed, entity.breed)
        assertEquals(domain.lifecycleStatus, entity.lifecycleStatus)
        assertEquals(domain.birdCode, entity.birdCode)
    }
}
