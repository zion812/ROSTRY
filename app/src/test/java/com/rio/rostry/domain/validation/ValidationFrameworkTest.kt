package com.rio.rostry.domain.validation

import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidationFrameworkTest {

    private lateinit var validationFramework: ValidationFramework
    private lateinit var entityValidator: EntityValidator

    @Before
    fun setup() {
        entityValidator = mockk(relaxed = true)
        validationFramework = ValidationFrameworkImpl(entityValidator)
    }

    @Test
    fun sanitizeText_escapesSqlInjectionPatterns() {
        val input = "user'; DROP TABLE users;-- comment"
        val sanitized = validationFramework.sanitizeText(input)
        // The sanitizer escapes single quotes to &#x27; and removes patterns like '; and '-- '
        assertFalse("Should not contain raw single quote", sanitized.contains("'"))
        assertFalse("Should not contain SQL comment pattern", sanitized.contains("-- "))
    }

    @Test
    fun sanitizeText_escapesHtmlTags() {
        val input = "<script>alert('xss')</script>"
        val sanitized = validationFramework.sanitizeText(input)
        assertFalse("Should not contain raw < character", sanitized.contains("<"))
        assertFalse("Should not contain raw > character", sanitized.contains(">"))
        assertTrue("Should contain escaped form", sanitized.contains("&lt;"))
    }

    @Test
    fun sanitizeText_trimInput() {
        val input = "  hello world  "
        val sanitized = validationFramework.sanitizeText(input)
        assertEquals("hello world", sanitized)
    }

    @Test
    fun validateEmail_acceptsValidEmail() {
        val email = "test@example.com"
        val result = validationFramework.validateEmail(email)
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun validateEmail_rejectsInvalidEmail() {
        val email = "test@example"
        val result = validationFramework.validateEmail(email)
        assertTrue(result is InputValidationResult.Invalid)
        val error = (result as InputValidationResult.Invalid).errors.first()
        assertEquals("Invalid email format", error.message)
    }

    @Test
    fun validateEmail_rejectsBlankEmail() {
        val email = ""
        val result = validationFramework.validateEmail(email)
        assertTrue(result is InputValidationResult.Invalid)
        val error = (result as InputValidationResult.Invalid).errors.first()
        assertEquals("Email is required", error.message)
    }

    @Test
    fun validatePhone_acceptsInternationalFormat() {
        val phone = "+1234567890"
        val result = validationFramework.validatePhone(phone)
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun validatePhone_rejectsInvalidCharacters() {
        val phone = "123-abc-4567"
        val result = validationFramework.validatePhone(phone)
        assertTrue(result is InputValidationResult.Invalid)
        val error = (result as InputValidationResult.Invalid).errors.first()
        assertTrue("Error message should mention format", error.message.contains("Invalid phone number format", ignoreCase = true))
    }

    @Test
    fun validateCoordinates_acceptsValidLatLon() {
        val result = validationFramework.validateCoordinates(45.0, 90.0)
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun validateCoordinates_rejectsOutofBoundsLatLon() {
        val result = validationFramework.validateCoordinates(100.0, 200.0)
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.message.contains("Latitude", ignoreCase = true) })
        assertTrue(errors.any { it.message.contains("Longitude", ignoreCase = true) })
    }
}
