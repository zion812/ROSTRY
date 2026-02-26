package com.rio.rostry.domain.config

/**
 * Result of configuration validation.
 */
sealed class ValidationResult {
    /**
     * Configuration is valid.
     */
    object Valid : ValidationResult()

    /**
     * Configuration is invalid with specific errors.
     */
    data class Invalid(val errors: List<ValidationError>) : ValidationResult()
}

/**
 * Represents a single validation error.
 */
data class ValidationError(
    val field: String,
    val message: String,
    val code: String
)
