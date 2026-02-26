package com.rio.rostry.domain.validation

/**
 * Email validator (RFC 5322 simplified).
 */
object EmailValidator {
    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?(\\.[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?)*\\.[A-Za-z]{2,}$"
    )

    fun validate(email: String): InputValidationResult {
        return if (email.isBlank()) {
            InputValidationResult.Invalid(
                listOf(InputValidationError("email", "Email is required", "EMAIL_REQUIRED"))
            )
        } else if (!EMAIL_REGEX.matches(email)) {
            InputValidationResult.Invalid(
                listOf(InputValidationError("email", "Invalid email format", "EMAIL_INVALID"))
            )
        } else {
            InputValidationResult.Valid
        }
    }
}

/**
 * International phone number validator.
 */
object PhoneValidator {
    // Accepts optional + prefix, then 7-15 digits
    private val PHONE_REGEX = Regex("^\\+?[1-9]\\d{6,14}$")

    fun validate(phone: String): InputValidationResult {
        val cleanPhone = phone.replace(Regex("[\\s()-]"), "")
        return if (cleanPhone.isBlank()) {
            InputValidationResult.Invalid(
                listOf(InputValidationError("phone", "Phone number is required", "PHONE_REQUIRED"))
            )
        } else if (!PHONE_REGEX.matches(cleanPhone)) {
            InputValidationResult.Invalid(
                listOf(InputValidationError("phone", "Invalid phone number format. Use international format (e.g., +1234567890)", "PHONE_INVALID"))
            )
        } else {
            InputValidationResult.Valid
        }
    }
}

/**
 * Latitude/longitude coordinate validator.
 */
object CoordinateValidator {

    fun validate(latitude: Double, longitude: Double): InputValidationResult {
        val errors = mutableListOf<InputValidationError>()
        if (latitude < -90.0 || latitude > 90.0) {
            errors.add(InputValidationError("latitude", "Latitude must be between -90 and 90", "LAT_OUT_OF_RANGE"))
        }
        if (longitude < -180.0 || longitude > 180.0) {
            errors.add(InputValidationError("longitude", "Longitude must be between -180 and 180", "LON_OUT_OF_RANGE"))
        }
        return if (errors.isEmpty()) InputValidationResult.Valid else InputValidationResult.Invalid(errors)
    }
}

/**
 * Date range validator.
 */
object DateRangeValidator {
    
    fun validate(startDate: Long, endDate: Long): InputValidationResult {
        val errors = mutableListOf<InputValidationError>()
        
        if (startDate < 0) {
            errors.add(InputValidationError("startDate", "Start date cannot be negative", "START_DATE_INVALID"))
        }
        
        if (endDate < 0) {
            errors.add(InputValidationError("endDate", "End date cannot be negative", "END_DATE_INVALID"))
        }
        
        if (startDate > endDate) {
            errors.add(InputValidationError("dateRange", "Start date must be before or equal to end date", "DATE_RANGE_INVALID"))
        }
        
        return if (errors.isEmpty()) InputValidationResult.Valid else InputValidationResult.Invalid(errors)
    }
}

/**
 * Enum validator for validating string values against allowed enum values.
 */
class EnumValidator<T : Enum<T>>(
    private val enumClass: Class<T>,
    private val fieldName: String = "value"
) : InputValidator<String> {
    
    private val allowedValues = enumClass.enumConstants?.map { it.name } ?: emptyList()
    
    override fun validate(value: String): InputValidationResult {
        return if (value.isBlank()) {
            InputValidationResult.Invalid(
                listOf(InputValidationError(fieldName, "$fieldName is required", "${fieldName.uppercase()}_REQUIRED"))
            )
        } else if (value !in allowedValues) {
            InputValidationResult.Invalid(
                listOf(
                    InputValidationError(
                        fieldName,
                        "Invalid $fieldName. Allowed values: ${allowedValues.joinToString(", ")}",
                        "${fieldName.uppercase()}_INVALID"
                    )
                )
            )
        } else {
            InputValidationResult.Valid
        }
    }
}
