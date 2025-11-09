package com.rio.rostry.utils

import org.junit.Assert.*
import org.junit.Test

class PhoneValidationTest {

    @Test
    fun validate_e164_basic() {
        assertTrue(isValidE164("+911234567890"))
        assertTrue(isValidE164("+12025550123")) // US
        assertTrue(isValidE164("+447911123456")) // UK mobile
        assertTrue(isValidE164("+61491570156")) // AU test
        assertFalse(isValidE164("911234567890"))
        assertFalse(isValidE164("+00"))
        assertFalse(isValidE164("+1abc"))
    }

    @Test
    fun format_to_e164_with_country_code() {
        assertEquals("+911234567890", formatToE164("+91", "1234567890"))
        assertEquals("+12025550123", formatToE164("1", "202-555-0123"))
        assertEquals("+447911123456", formatToE164("+44", "07911 123456"))
        assertNull(formatToE164("+00", "123"))
        assertNull(formatToE164("+1", "abc"))
    }
}
