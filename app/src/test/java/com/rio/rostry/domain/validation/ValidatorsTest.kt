package com.rio.rostry.domain.validation

import org.junit.Assert.*
import org.junit.Test

class ValidatorsTest {

    @Test
    fun dateRangeValidator_acceptsValidRange() {
        val result = DateRangeValidator.validate(1000L, 2000L)
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun dateRangeValidator_rejectsNegativeStartDate() {
        val result = DateRangeValidator.validate(-1L, 2000L)
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "START_DATE_INVALID" })
    }

    @Test
    fun dateRangeValidator_rejectsNegativeEndDate() {
        val result = DateRangeValidator.validate(1000L, -1L)
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "END_DATE_INVALID" })
    }

    @Test
    fun dateRangeValidator_rejectsStartAfterEnd() {
        val result = DateRangeValidator.validate(2000L, 1000L)
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "DATE_RANGE_INVALID" })
    }

    @Test
    fun dateRangeValidator_acceptsEqualDates() {
        val result = DateRangeValidator.validate(1000L, 1000L)
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun enumValidator_acceptsValidValue() {
        enum class TestEnum { VALUE1, VALUE2, VALUE3 }
        val validator = EnumValidator(TestEnum::class.java, "testField")
        val result = validator.validate("VALUE1")
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun enumValidator_rejectsInvalidValue() {
        enum class TestEnum { VALUE1, VALUE2, VALUE3 }
        val validator = EnumValidator(TestEnum::class.java, "testField")
        val result = validator.validate("INVALID")
        assertTrue(result is InputValidationResult.Invalid)
        val error = (result as InputValidationResult.Invalid).errors.first()
        assertEquals("TESTFIELD_INVALID", error.code)
        assertTrue(error.message.contains("Allowed values"))
    }

    @Test
    fun enumValidator_rejectsBlankValue() {
        enum class TestEnum { VALUE1, VALUE2, VALUE3 }
        val validator = EnumValidator(TestEnum::class.java, "testField")
        val result = validator.validate("")
        assertTrue(result is InputValidationResult.Invalid)
        val error = (result as InputValidationResult.Invalid).errors.first()
        assertEquals("TESTFIELD_REQUIRED", error.code)
    }

    @Test
    fun emailValidator_acceptsValidEmails() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "test123@test-domain.com"
        )
        
        validEmails.forEach { email ->
            val result = EmailValidator.validate(email)
            assertTrue("$email should be valid", result is InputValidationResult.Valid)
        }
    }

    @Test
    fun emailValidator_rejectsInvalidEmails() {
        val invalidEmails = listOf(
            "test@",
            "@example.com",
            "test@example",
            "test example@test.com",
            "test@.com"
        )
        
        invalidEmails.forEach { email ->
            val result = EmailValidator.validate(email)
            assertTrue("$email should be invalid", result is InputValidationResult.Invalid)
        }
    }

    @Test
    fun phoneValidator_acceptsValidPhones() {
        val validPhones = listOf(
            "+1234567890",
            "+12345678901234",
            "1234567890"
        )
        
        validPhones.forEach { phone ->
            val result = PhoneValidator.validate(phone)
            assertTrue("$phone should be valid", result is InputValidationResult.Valid)
        }
    }

    @Test
    fun phoneValidator_rejectsInvalidPhones() {
        val invalidPhones = listOf(
            "123",  // Too short
            "+123456789012345678",  // Too long
            "abc1234567",  // Contains letters
            "+0123456789"  // Starts with 0 after +
        )
        
        invalidPhones.forEach { phone ->
            val result = PhoneValidator.validate(phone)
            assertTrue("$phone should be invalid", result is InputValidationResult.Invalid)
        }
    }

    @Test
    fun phoneValidator_handlesFormattedPhones() {
        val phone = "+1 (234) 567-8901"
        val result = PhoneValidator.validate(phone)
        // Should strip formatting and validate
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun coordinateValidator_acceptsValidCoordinates() {
        val validCoords = listOf(
            Pair(0.0, 0.0),
            Pair(45.0, 90.0),
            Pair(-45.0, -90.0),
            Pair(90.0, 180.0),
            Pair(-90.0, -180.0)
        )
        
        validCoords.forEach { (lat, lon) ->
            val result = CoordinateValidator.validate(lat, lon)
            assertTrue("($lat, $lon) should be valid", result is InputValidationResult.Valid)
        }
    }

    @Test
    fun coordinateValidator_rejectsInvalidCoordinates() {
        val invalidCoords = listOf(
            Pair(91.0, 0.0),  // Lat too high
            Pair(-91.0, 0.0),  // Lat too low
            Pair(0.0, 181.0),  // Lon too high
            Pair(0.0, -181.0),  // Lon too low
            Pair(100.0, 200.0)  // Both invalid
        )
        
        invalidCoords.forEach { (lat, lon) ->
            val result = CoordinateValidator.validate(lat, lon)
            assertTrue("($lat, $lon) should be invalid", result is InputValidationResult.Invalid)
        }
    }
}
