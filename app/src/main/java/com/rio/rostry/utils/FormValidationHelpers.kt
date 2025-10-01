package com.rio.rostry.utils

data class ValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String> = emptyMap(),
    val warnings: Map<String, String> = emptyMap()
)

object FieldValidator {
    fun required(value: String?, fieldName: String): String? {
        return if (value.isNullOrBlank()) "$fieldName is required" else null
    }

    fun minLength(value: String?, min: Int, fieldName: String): String? {
        return if (value != null && value.length < min) {
            "$fieldName must be at least $min characters"
        } else null
    }

    fun maxLength(value: String?, max: Int, fieldName: String): String? {
        return if (value != null && value.length > max) {
            "$fieldName must be at most $max characters"
        } else null
    }

    fun numeric(value: String?, fieldName: String): String? {
        return if (value != null && value.toDoubleOrNull() == null) {
            "$fieldName must be a number"
        } else null
    }

    fun positiveNumber(value: String?, fieldName: String): String? {
        val num = value?.toDoubleOrNull()
        return if (num != null && num <= 0) {
            "$fieldName must be positive"
        } else null
    }

    fun email(value: String?, fieldName: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return if (value != null && !emailRegex.matches(value)) {
            "Invalid email format"
        } else null
    }

    fun phone(value: String?, fieldName: String): String? {
        val phoneRegex = "^[0-9]{10}$".toRegex()
        return if (value != null && !phoneRegex.matches(value)) {
            "Phone number must be 10 digits"
        } else null
    }

    fun dateFormat(value: String?, format: String, fieldName: String): String? {
        // Simple YYYY-MM-DD validation
        if (format == "yyyy-MM-dd" && value != null) {
            val parts = value.split("-")
            if (parts.size != 3) return "Date must be in YYYY-MM-DD format"
            val year = parts[0].toIntOrNull()
            val month = parts[1].toIntOrNull()
            val day = parts[2].toIntOrNull()
            if (year == null || month == null || day == null) {
                return "Invalid date format"
            }
            if (month !in 1..12 || day !in 1..31) {
                return "Invalid date values"
            }
        }
        return null
    }

    fun inRange(value: Double?, min: Double, max: Double, fieldName: String): String? {
        return if (value != null && (value < min || value > max)) {
            "$fieldName must be between $min and $max"
        } else null
    }

    fun oneOf(value: String?, options: List<String>, fieldName: String): String? {
        return if (value != null && value !in options) {
            "$fieldName must be one of: ${options.joinToString()}"
        } else null
    }
}

class FormValidator {
    private val errors = mutableMapOf<String, String>()
    private var currentField: String? = null
    private var currentValue: String? = null

    fun field(name: String, value: String?): FormValidator {
        currentField = name
        currentValue = value
        return this
    }

    fun required(): FormValidator {
        currentField?.let { field ->
            FieldValidator.required(currentValue, field)?.let { errors[field] = it }
        }
        return this
    }

    fun minLength(min: Int): FormValidator {
        currentField?.let { field ->
            FieldValidator.minLength(currentValue, min, field)?.let { errors[field] = it }
        }
        return this
    }

    fun maxLength(max: Int): FormValidator {
        currentField?.let { field ->
            FieldValidator.maxLength(currentValue, max, field)?.let { errors[field] = it }
        }
        return this
    }

    fun numeric(): FormValidator {
        currentField?.let { field ->
            FieldValidator.numeric(currentValue, field)?.let { errors[field] = it }
        }
        return this
    }

    fun positiveNumber(): FormValidator {
        currentField?.let { field ->
            FieldValidator.positiveNumber(currentValue, field)?.let { errors[field] = it }
        }
        return this
    }

    fun email(): FormValidator {
        currentField?.let { field ->
            FieldValidator.email(currentValue, field)?.let { errors[field] = it }
        }
        return this
    }

    fun validate(): ValidationResult {
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors.toMap()
        )
    }
}

class ConditionalValidator {
    private val validators = mutableListOf<Pair<() -> Boolean, () -> Map<String, String>>>()

    fun `when`(condition: () -> Boolean): RequirementBuilder {
        return RequirementBuilder(condition, validators)
    }

    fun validate(fields: Map<String, String?>): ValidationResult {
        val allErrors = mutableMapOf<String, String>()
        for ((condition, getErrors) in validators) {
            if (condition()) {
                allErrors.putAll(getErrors())
            }
        }
        return ValidationResult(
            isValid = allErrors.isEmpty(),
            errors = allErrors
        )
    }

    class RequirementBuilder(
        private val condition: () -> Boolean,
        private val validators: MutableList<Pair<() -> Boolean, () -> Map<String, String>>>
    ) {
        private val requirements = mutableMapOf<String, String>()

        fun require(field: String): RequirementBuilder {
            requirements[field] = "$field is required"
            return this
        }

        init {
            validators.add(condition to { requirements })
        }
    }
}
