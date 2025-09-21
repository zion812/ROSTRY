package com.rio.rostry.utils

import org.junit.Assert.*
import org.junit.Test

class PhoneValidationTest {

    @Test
    fun normalize_validIndianNumbers() {
        assertEquals("+911234567890", normalizeToE164India("1234567890"))
        assertEquals("+911234567890", normalizeToE164India("+911234567890"))
        assertEquals("+911234567890", normalizeToE164India("91 1234567890"))
    }

    @Test
    fun normalize_invalidNumbers() {
        assertNull(normalizeToE164India("12345"))
        assertNull(normalizeToE164India("+921234567890"))
        assertNull(normalizeToE164India("abcd"))
    }

    @Test
    fun isValidIndianPhone_checks() {
        assertTrue(isValidIndianPhone("+911234567890"))
        assertTrue(isValidIndianPhone("1234567890"))
        assertFalse(isValidIndianPhone("+921234567890"))
    }
}
