package com.rio.rostry.data.farm.mapper

import com.rio.rostry.core.model.Transfer
import com.rio.rostry.data.database.entity.TransferEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class TransferMapperTest {

    @Test
    fun `toDomain maps all fields correctly`() {
        val entity = TransferEntity(
            transferId = "t1",
            productId = "p1",
            fromUserId = "u1",
            toUserId = "u2",
            orderId = "o1",
            amount = 500.0,
            currency = "USD",
            type = "PAYMENT",
            status = "PENDING",
            transferCode = "123456",
            transferType = "OWNERSHIP_HANDSHAKE",
            notes = "Test notes",
            initiatedAt = 1000L,
            updatedAt = 1100L
        )

        val domain = entity.toTransfer()

        assertEquals(entity.transferId, domain.transferId)
        assertEquals(entity.productId, domain.productId)
        assertEquals(entity.fromUserId, domain.fromUserId)
        assertEquals(entity.toUserId, domain.toUserId)
        assertEquals(entity.amount, domain.amount, 0.001)
        assertEquals(entity.status, domain.status)
        assertEquals(entity.transferCode, domain.transferCode)
    }

    @Test
    fun `toEntity maps all fields correctly`() {
        val domain = Transfer(
            transferId = "t1",
            productId = "p1",
            fromUserId = "u1",
            toUserId = "u2",
            orderId = "o1",
            amount = 500.0,
            currency = "USD",
            type = "PAYMENT",
            status = "PENDING",
            transferCode = "123456",
            transferCodeExpiresAt = 2000L,
            transferType = "OWNERSHIP_HANDSHAKE",
            lineageSnapshotJson = "{}",
            healthSnapshotJson = "{}",
            sellerPhotoUrl = "url1",
            buyerPhotoUrl = "url2",
            gpsLat = 1.0,
            gpsLng = 2.0,
            timeoutAt = 3000L,
            conditionsJson = "[]",
            transactionReference = "ref1",
            notes = "Test notes",
            initiatedAt = 1000L,
            completedAt = 1500L,
            updatedAt = 1100L,
            lastModifiedAt = 1100L
        )

        val entity = domain.toEntity()

        assertEquals(domain.transferId, entity.transferId)
        assertEquals(domain.productId, entity.productId)
        assertEquals(domain.fromUserId, entity.fromUserId)
        assertEquals(domain.toUserId, entity.toUserId)
        assertEquals(domain.amount, entity.amount, 0.001)
        assertEquals(domain.status, entity.status)
        assertEquals(domain.transferCode, entity.transferCode)
        assertEquals(domain.transferType, entity.transferType)
    }
}
