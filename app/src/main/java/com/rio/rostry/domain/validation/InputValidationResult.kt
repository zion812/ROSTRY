package com.rio.rostry.domain.validation

/**
 * Result of validating an input value or batch.
 */
sealed class InputValidationResult {
    object Valid : InputValidationResult()
    data class Invalid(val errors: List<InputValidationError>) : InputValidationResult()
}

/**
 * Describes a single validation failure.
 */
data class InputValidationError(
    val field: String,
    val message: String,
    val code: String
)

/**
 * Result of validating a batch of items.
 */
data class BatchValidationResult(
    /** Indices of valid items. */
    val valid: List<Int>,
    /** Map of index → list of validation errors. */
    val invalid: Map<Int, List<InputValidationError>>
) {
    val isAllValid: Boolean get() = invalid.isEmpty()
}

/**
 * Type alias for backward compatibility.
 * Some verification system code references ValidationResult instead of InputValidationResult.
 */
typealias ValidationResult = InputValidationResult
