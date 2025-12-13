package com.rio.rostry.domain.model

import org.junit.Assert.*
import org.junit.Test

class OrderStatusTest {

    @Test
    fun roundTripTest() {
        OrderStatus.values().forEach { status ->
            assertEquals(status, OrderStatus.fromString(status.toStoredString()))
        }
    }

    @Test
    fun caseInsensitivityTest() {
        assertEquals(OrderStatus.PENDING_PAYMENT, OrderStatus.fromString("pending_payment"))
        assertEquals(OrderStatus.PENDING_PAYMENT, OrderStatus.fromString("PENDING_PAYMENT"))
        assertEquals(OrderStatus.PENDING_PAYMENT, OrderStatus.fromString("Pending_Payment"))
    }

    @Test
    fun unknownValueTest() {
        assertEquals(OrderStatus.PENDING_PAYMENT, OrderStatus.fromString("UNKNOWN_STATUS"))
    }

    @Test
    fun storedStringFormatTest() {
        assertEquals("PENDING_PAYMENT", OrderStatus.PENDING_PAYMENT.toStoredString())
        assertEquals("OUT_FOR_DELIVERY", OrderStatus.OUT_FOR_DELIVERY.toStoredString())
        assertEquals("DELIVERED", OrderStatus.DELIVERED.toStoredString())
    }
}