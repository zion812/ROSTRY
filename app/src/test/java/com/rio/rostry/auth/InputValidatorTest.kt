package com.rio.rostry.auth

import com.rio.rostry.util.InputValidator
import org.junit.Assert.*
import org.junit.Test

class InputValidatorTest {

    @Test
    fun `valid email addresses should return true`() {
        assertTrue(InputValidator.isValidEmail("test@example.com"))
        assertTrue(InputValidator.isValidEmail("user.name@domain.co.in"))
        assertTrue(InputValidator.isValidEmail("test123@test-domain.org"))
    }

    @Test
    fun `invalid email addresses should return false`() {
        assertFalse(InputValidator.isValidEmail("invalid.email"))
        assertFalse(InputValidator.isValidEmail("@example.com"))
        assertFalse(InputValidator.isValidEmail("test@"))
        assertFalse(InputValidator.isValidEmail(""))
    }

    @Test
    fun `valid names should return true`() {
        assertTrue(InputValidator.isValidName("John Doe"))
        assertTrue(InputValidator.isValidName("Mary Jane Watson"))
        assertTrue(InputValidator.isValidName("John"))
    }

    @Test
    fun `invalid names should return false`() {
        assertFalse(InputValidator.isValidName(""))
        assertFalse(InputValidator.isValidName("John123"))
        assertFalse(InputValidator.isValidName("A".repeat(101))) // Too long
    }

    @Test
    fun `valid addresses should return true`() {
        assertTrue(InputValidator.isValidAddress("123 Main Street, City"))
        assertTrue(InputValidator.isValidAddress("A-101, Some Apartment, Some City, State"))
    }

    @Test
    fun `invalid addresses should return false`() {
        assertFalse(InputValidator.isValidAddress(""))
        assertFalse(InputValidator.isValidAddress("A".repeat(201))) // Too long
    }

    @Test
    fun `valid locations should return true`() {
        assertTrue(InputValidator.isValidLocation("Mumbai, Maharashtra"))
        assertTrue(InputValidator.isValidLocation("Bangalore"))
    }

    @Test
    fun `invalid locations should return false`() {
        assertFalse(InputValidator.isValidLocation(""))
        assertFalse(InputValidator.isValidLocation("A".repeat(101))) // Too long
    }

    @Test
    fun `valid ID numbers should return true`() {
        assertTrue(InputValidator.isValidIdNumber("123456789012")) // Aadhaar
        assertTrue(InputValidator.isValidIdNumber("ABCDE1234F")) // PAN
    }

    @Test
    fun `invalid ID numbers should return false`() {
        assertFalse(InputValidator.isValidIdNumber("12345")) // Too short
        assertFalse(InputValidator.isValidIdNumber("1234567890123")) // Too long
        assertFalse(InputValidator.isValidIdNumber("ABCDE12345")) // Invalid PAN format
    }

    @Test
    fun `validateProfile should return errors for invalid inputs`() {
        val errors = InputValidator.validateProfile(
            name = "",
            email = "invalid",
            address = "",
            location = "",
            idNumber = "123",
            userType = com.rio.rostry.domain.model.UserType.ENTHUSIAST
        )

        assertTrue(errors.isNotEmpty())
        assertTrue(errors.contains("Please enter a valid name"))
        assertTrue(errors.contains("Please enter a valid email address"))
        assertTrue(errors.contains("Please enter a valid address"))
        assertTrue(errors.contains("Please enter a valid location"))
        assertTrue(errors.contains("Please enter a valid ID number (Aadhaar or PAN)"))
    }

    @Test
    fun `validateProfile should return empty list for valid inputs`() {
        val errors = InputValidator.validateProfile(
            name = "John Doe",
            email = "john@example.com",
            address = "123 Main Street",
            location = "Mumbai",
            idNumber = "123456789012",
            userType = com.rio.rostry.domain.model.UserType.ENTHUSIAST
        )

        assertTrue(errors.isEmpty())
    }
}