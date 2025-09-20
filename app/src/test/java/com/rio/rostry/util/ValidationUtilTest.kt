package com.rio.rostry.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilTest {

    @Test
    fun `isValidEmail should return true for valid emails`() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"))
    }

    @Test
    fun `isValidEmail should return false for invalid emails`() {
        assertFalse(ValidationUtil.isValidEmail("invalid.email"))
    }

    @Test
    fun `isValidPhone should return true for valid phone numbers`() {
        assertTrue(ValidationUtil.isValidPhone("1234567890"))
    }

    @Test
    fun `isValidPhone should return false for invalid phone numbers`() {
        assertFalse(ValidationUtil.isValidPhone("123"))
    }

    @Test
    fun `isValidPassword should return true for valid passwords`() {
        assertTrue(ValidationUtil.isValidPassword("password")) // At least 6 characters
    }

    @Test
    fun `isValidPassword should return false for invalid passwords`() {
        assertFalse(ValidationUtil.isValidPassword("12345")) // Less than 6 characters
    }

    @Test
    fun `isValidName should return true for valid names`() {
        assertTrue(ValidationUtil.isValidName("John"))
    }

    @Test
    fun `isValidName should return false for invalid names`() {
        assertFalse(ValidationUtil.isValidName(""))
    }

    @Test
    fun `isValidPrice should return true for valid prices`() {
        assertTrue(ValidationUtil.isValidPrice(10.0))
    }

    @Test
    fun `isValidPrice should return false for invalid prices`() {
        assertFalse(ValidationUtil.isValidPrice(0.0))
    }

    @Test
    fun `isValidQuantity should return true for valid quantities`() {
        assertTrue(ValidationUtil.isValidQuantity(1))
    }

    @Test
    fun `isValidQuantity should return false for invalid quantities`() {
        assertFalse(ValidationUtil.isValidQuantity(0))
    }
}