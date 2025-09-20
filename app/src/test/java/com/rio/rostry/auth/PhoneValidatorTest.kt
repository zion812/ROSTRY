package com.rio.rostry.auth

import com.rio.rostry.util.PhoneValidator
import org.junit.Assert.*
import org.junit.Test

class PhoneValidatorTest {

    @Test
    fun `valid Indian phone numbers should return true`() {
        assertTrue(PhoneValidator.isValidIndianPhoneNumber("9876543210"))
        assertTrue(PhoneValidator.isValidIndianPhoneNumber("8876543210"))
        assertTrue(PhoneValidator.isValidIndianPhoneNumber("7876543210"))
        assertTrue(PhoneValidator.isValidIndianPhoneNumber("6876543210"))
    }

    @Test
    fun `invalid Indian phone numbers should return false`() {
        assertFalse(PhoneValidator.isValidIndianPhoneNumber("5876543210")) // Starts with 5
        assertFalse(PhoneValidator.isValidIndianPhoneNumber("987654321"))  // Too short
        assertFalse(PhoneValidator.isValidIndianPhoneNumber("98765432101")) // Too long
        assertFalse(PhoneValidator.isValidIndianPhoneNumber("abcd543210")) // Contains letters
    }

    @Test
    fun `cleanPhoneNumber should remove non-digit characters`() {
        assertEquals("9876543210", PhoneValidator.cleanPhoneNumber("+91-9876-543-210"))
        assertEquals("9876543210", PhoneValidator.cleanPhoneNumber("(987) 654-3210"))
        assertEquals("9876543210", PhoneValidator.cleanPhoneNumber("987 654 3210"))
    }

    @Test
    fun `formatPhoneNumber should format correctly`() {
        assertEquals("987-654-3210", PhoneValidator.formatPhoneNumber("9876543210"))
    }
}