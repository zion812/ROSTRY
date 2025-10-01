package com.rio.rostry.utils.validation

/**
 * Centralized validation engine to consolidate input, business rules, and data integrity checks.
 * This module is self-contained and does not depend on app-specific classes.
 */
object ValidationEngine {
    /** Run a chain of validators in order. Stops on first failure if [shortCircuit] is true. */
    fun <T> validate(
        value: T,
        validators: List<Validator<T>>,
        shortCircuit: Boolean = true
    ): ValidationReport {
        val errors = mutableListOf<ValidationError>()
        for (validator in validators) {
            val res = validator.validate(value)
            if (!res.isValid) {
                errors += res.errors
                if (shortCircuit) break
            }
        }
        return if (errors.isEmpty()) ValidationReport.Valid else ValidationReport.Invalid(errors)
    }

    /** Create a builder for chaining validators fluently. */
    fun <T> chain(): ValidationChain<T> = ValidationChain()
}

/** Fluent chain for building composite validators. */
class ValidationChain<T> internal constructor() {
    private val validators = mutableListOf<Validator<T>>()

    fun add(validator: Validator<T>): ValidationChain<T> {
        validators += validator
        return this
    }

    fun addAll(vararg validator: Validator<T>): ValidationChain<T> {
        validators += validator
        return this
    }

    fun build(): Validator<T> = CompositeValidator(validators)
}

/** Base validator interface. */
fun interface Validator<T> {
    fun validate(value: T): ValidationResult
}

/** Combine multiple validators into one. */
class CompositeValidator<T>(
    private val validators: List<Validator<T>>
) : Validator<T> {
    override fun validate(value: T): ValidationResult {
        val allErrors = validators.flatMap { it.validate(value).errors }
        return if (allErrors.isEmpty()) ValidationResult.valid() else ValidationResult.invalid(allErrors)
    }
}

/** Validation result with optional multiple errors. */
class ValidationResult private constructor(val isValid: Boolean, val errors: List<ValidationError>) {
    companion object {
        fun valid(): ValidationResult = ValidationResult(true, emptyList())
        fun invalid(vararg error: ValidationError): ValidationResult = ValidationResult(false, error.toList())
        fun invalid(errors: List<ValidationError>): ValidationResult = ValidationResult(false, errors)
    }
}

/** Specific validation error with code and optional i18n message key. */
data class ValidationError(
    val code: String,
    val message: String? = null,
    val messageKey: String? = null,
    val metadata: Map<String, Any?> = emptyMap()
)

/** Final report that indicates Valid or provides all errors. */
sealed interface ValidationReport {
    data object Valid : ValidationReport
    data class Invalid(val errors: List<ValidationError>) : ValidationReport
}

/** Utility factory helpers. */
object Validators {
    /** Ensures string is not blank. */
    fun nonBlank(code: String = "required", messageKey: String? = null): Validator<String> = Validator { s ->
        if (s.isBlank()) ValidationResult.invalid(ValidationError(code, message = null, messageKey = messageKey))
        else ValidationResult.valid()
    }

    /** Ensures length is within [min, max]. */
    fun lengthIn(min: Int = 0, max: Int = Int.MAX_VALUE, code: String = "length_range"): Validator<String> = Validator { s ->
        if (s.length in min..max) ValidationResult.valid()
        else ValidationResult.invalid(ValidationError(code, metadata = mapOf("min" to min, "max" to max)))
    }

    /** Regex match validator. */
    fun regex(pattern: Regex, code: String = "pattern"): Validator<String> = Validator { s ->
        if (pattern.matches(s)) ValidationResult.valid()
        else ValidationResult.invalid(ValidationError(code))
    }

    /** Generic predicate validator. */
    fun <T> predicate(code: String, messageKey: String? = null, predicate: (T) -> Boolean): Validator<T> = Validator { v ->
        if (predicate(v)) ValidationResult.valid() else ValidationResult.invalid(ValidationError(code, messageKey = messageKey))
    }
}
