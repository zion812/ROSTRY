package com.rio.rostry.domain.validation

/**
 * Generic validator interface for type-safe input validation.
 */
interface InputValidator<T> {
    fun validate(value: T): InputValidationResult
}

/**
 * Combines multiple validators — input must pass all to be valid.
 */
class CompositeValidator<T>(
    private val validators: List<InputValidator<T>>
) : InputValidator<T> {

    override fun validate(value: T): InputValidationResult {
        val allErrors = mutableListOf<InputValidationError>()
        for (validator in validators) {
            val result = validator.validate(value)
            if (result is InputValidationResult.Invalid) {
                allErrors.addAll(result.errors)
            }
        }
        return if (allErrors.isEmpty()) {
            InputValidationResult.Valid
        } else {
            InputValidationResult.Invalid(allErrors)
        }
    }
}
