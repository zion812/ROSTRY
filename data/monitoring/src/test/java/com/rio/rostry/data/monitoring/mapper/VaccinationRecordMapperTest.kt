package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.VaccinationRecord
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class VaccinationRecordMapperTest {

    @Test
    fun `toDomain maps all fields correctly`() {
        val entity = VaccinationRecordEntity(
            vaccinationId = "v1",
            productId = "p1",
            farmerId = "f1",
            vaccineType = "Newcastle",
            administeredAt = 1000L,
            scheduledAt = 900L,
            doseMl = 0.5,
            efficacyNotes = "Good",
            createdAt = 800L,
            updatedAt = 1100L
        )

        val domain = VaccinationRecordMapper.toDomain(entity)

        assertEquals(entity.vaccinationId, domain.id)
        assertEquals(entity.productId, domain.productId)
        assertEquals(entity.vaccineType, domain.vaccineName)
        assertEquals(entity.administeredAt, domain.date)
        assertEquals("0.5", domain.dosage)
        assertEquals(entity.efficacyNotes, domain.notes)
        assertEquals(entity.farmerId, domain.farmerId)
    }

    @Test
    fun `toEntity maps all fields correctly`() {
        val domain = VaccinationRecord(
            id = "v1",
            productId = "p1",
            vaccineName = "Newcastle",
            date = 1000L,
            dosage = "0.5",
            notes = "Good",
            farmerId = "f1",
            createdAt = 800L,
            updatedAt = 1100L
        )

        val entity = VaccinationRecordMapper.toEntity(domain)

        assertEquals(domain.id, entity.vaccinationId)
        assertEquals(domain.productId, entity.productId)
        assertEquals(domain.vaccineName, entity.vaccineType)
        assertEquals(domain.date, entity.administeredAt)
        assertEquals(0.5, entity.doseMl!!, 0.001)
        assertEquals(domain.notes, entity.efficacyNotes)
        assertEquals(domain.farmerId, entity.farmerId)
    }
}
