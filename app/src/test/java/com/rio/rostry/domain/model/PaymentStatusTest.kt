package com.rio.rostry.domain.model

import org.junit.Assert.*
import org.junit.Test

class PaymentStatusTest {

    @Test
    fun fromString_paid_returnsSuccess() {
        val status = PaymentStatus.fromString("Paid")
        assertEquals(PaymentStatus.SUCCESS, status)
        assertTrue(status.isPaid)
    }

    @Test
    fun fromString_caseInsensitivity() {
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.fromString("PAID"))
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.fromString("paid"))
    }

    @Test
    fun fromString_paidSuccess_returnsSuccess() {
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.fromString("PAID_SUCCESS"))
    }

    @Test
    fun roundTrip_canonicalValues() {
        val values = listOf("PENDING", "SUCCESS", "FAILED", "REFUNDED")
        for (value in values) {
            val status = PaymentStatus.fromString(value)
            assertEquals(value, status.toStoredString())
        }
    }

    @Test
    fun fromString_unknown_defaultsToPending() {
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString("UNKNOWN"))
    }
}