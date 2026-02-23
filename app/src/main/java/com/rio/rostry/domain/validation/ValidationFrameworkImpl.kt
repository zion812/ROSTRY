package com.rio.rostry.domain.validation

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central validation framework providing text sanitization, file validation,
 * foreign key checks, and batch validation.
 */
@Singleton
class ValidationFrameworkImpl @Inject constructor() : ValidationFramework {

    private val textInputValidator = TextInputValidator()

    override fun <T> validate(value: T, validator: InputValidator<T>): InputValidationResult {
        return validator.validate(value)
    }

    override fun sanitizeText(input: String): String {
        return textInputValidator.sanitize(input)
    }

    override fun validateEmail(email: String): InputValidationResult {
        return EmailValidator.validate(email)
    }

    override fun validatePhone(phone: String): InputValidationResult {
        return PhoneValidator.validate(phone)
    }

    override fun validateCoordinates(latitude: Double, longitude: Double): InputValidationResult {
        return CoordinateValidator.validate(latitude, longitude)
    }

    override fun <T> validateBatch(
        items: List<T>,
        validator: InputValidator<T>
    ): BatchValidationResult {
        val validIndices = mutableListOf<Int>()
        val invalidMap = mutableMapOf<Int, List<InputValidationError>>()

        items.forEachIndexed { index, item ->
            val result = validator.validate(item)
            when (result) {
                is InputValidationResult.Valid -> validIndices.add(index)
                is InputValidationResult.Invalid -> invalidMap[index] = result.errors
            }
        }

        return BatchValidationResult(valid = validIndices, invalid = invalidMap)
    }
}

/**
 * Validation framework interface.
 */
interface ValidationFramework {
    fun <T> validate(value: T, validator: InputValidator<T>): InputValidationResult
    fun sanitizeText(input: String): String
    fun validateEmail(email: String): InputValidationResult
    fun validatePhone(phone: String): InputValidationResult
    fun validateCoordinates(latitude: Double, longitude: Double): InputValidationResult
    fun <T> validateBatch(items: List<T>, validator: InputValidator<T>): BatchValidationResult
}
