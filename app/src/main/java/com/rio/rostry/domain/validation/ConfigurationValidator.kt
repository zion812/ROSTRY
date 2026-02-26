package com.rio.rostry.domain.validation

import android.util.Patterns
import com.rio.rostry.domain.config.AppConfiguration
import com.rio.rostry.domain.config.ValidationError
import com.rio.rostry.domain.config.ValidationResult

/**
 * Validates application configuration against defined schemas and rules.
 */
object ConfigurationValidator {

    /**
     * Validate complete application configuration.
     */
    fun validate(config: AppConfiguration): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate security config
        errors.addAll(validateSecurityConfig(config.security))

        // Validate thresholds
        errors.addAll(validateThresholdConfig(config.thresholds))

        // Validate timeouts
        errors.addAll(validateTimeoutConfig(config.timeouts))

        // Validate features
        errors.addAll(validateFeatureConfig(config.features))

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    private fun validateSecurityConfig(config: com.rio.rostry.domain.config.SecurityConfig): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        // Validate admin identifiers (email or phone format)
        config.adminIdentifiers.forEachIndexed { index, identifier ->
            if (!isValidEmailOrPhone(identifier)) {
                errors.add(
                    ValidationError(
                        field = "security.adminIdentifiers[$index]",
                        message = "Invalid email or phone format: $identifier",
                        code = "INVALID_ADMIN_IDENTIFIER"
                    )
                )
            }
        }

        // Validate moderation blocklist entries (non-empty strings)
        config.moderationBlocklist.forEachIndexed { index, entry ->
            if (entry.isBlank()) {
                errors.add(
                    ValidationError(
                        field = "security.moderationBlocklist[$index]",
                        message = "Blocklist entry cannot be empty",
                        code = "EMPTY_BLOCKLIST_ENTRY"
                    )
                )
            }
        }

        // Validate allowed file types (valid MIME types)
        config.allowedFileTypes.forEachIndexed { index, fileType ->
            if (!isValidMimeType(fileType)) {
                errors.add(
                    ValidationError(
                        field = "security.allowedFileTypes[$index]",
                        message = "Invalid MIME type: $fileType",
                        code = "INVALID_FILE_TYPE"
                    )
                )
            }
        }

        return errors
    }

    private fun validateThresholdConfig(config: com.rio.rostry.domain.config.ThresholdConfig): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        // Validate storage quota (positive integer)
        if (config.storageQuotaMB <= 0) {
            errors.add(
                ValidationError(
                    field = "thresholds.storageQuotaMB",
                    message = "Storage quota must be positive, got: ${config.storageQuotaMB}",
                    code = "INVALID_STORAGE_QUOTA"
                )
            )
        }

        // Validate max batch size (positive integer)
        if (config.maxBatchSize <= 0) {
            errors.add(
                ValidationError(
                    field = "thresholds.maxBatchSize",
                    message = "Max batch size must be positive, got: ${config.maxBatchSize}",
                    code = "INVALID_BATCH_SIZE"
                )
            )
        }

        // Validate circuit breaker failure rate (0.0 to 1.0)
        if (config.circuitBreakerFailureRate < 0.0 || config.circuitBreakerFailureRate > 1.0) {
            errors.add(
                ValidationError(
                    field = "thresholds.circuitBreakerFailureRate",
                    message = "Circuit breaker failure rate must be between 0.0 and 1.0, got: ${config.circuitBreakerFailureRate}",
                    code = "INVALID_FAILURE_RATE"
                )
            )
        }

        // Validate hub capacity (positive integer)
        if (config.hubCapacity <= 0) {
            errors.add(
                ValidationError(
                    field = "thresholds.hubCapacity",
                    message = "Hub capacity must be positive, got: ${config.hubCapacity}",
                    code = "INVALID_HUB_CAPACITY"
                )
            )
        }

        // Validate delivery radius (positive number)
        if (config.deliveryRadiusKm <= 0.0) {
            errors.add(
                ValidationError(
                    field = "thresholds.deliveryRadiusKm",
                    message = "Delivery radius must be positive, got: ${config.deliveryRadiusKm}",
                    code = "INVALID_DELIVERY_RADIUS"
                )
            )
        }

        return errors
    }

    private fun validateTimeoutConfig(config: com.rio.rostry.domain.config.TimeoutConfig): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        // Validate network request timeout (1-300 seconds)
        if (config.networkRequestSeconds < 1 || config.networkRequestSeconds > 300) {
            errors.add(
                ValidationError(
                    field = "timeouts.networkRequestSeconds",
                    message = "Network request timeout must be between 1 and 300 seconds, got: ${config.networkRequestSeconds}",
                    code = "INVALID_NETWORK_TIMEOUT"
                )
            )
        }

        // Validate circuit breaker open timeout (1-300 seconds)
        if (config.circuitBreakerOpenSeconds < 1 || config.circuitBreakerOpenSeconds > 300) {
            errors.add(
                ValidationError(
                    field = "timeouts.circuitBreakerOpenSeconds",
                    message = "Circuit breaker open timeout must be between 1 and 300 seconds, got: ${config.circuitBreakerOpenSeconds}",
                    code = "INVALID_CIRCUIT_BREAKER_TIMEOUT"
                )
            )
        }

        // Validate retry delays (all positive)
        config.retryDelaysSeconds.forEachIndexed { index, delay ->
            if (delay <= 0) {
                errors.add(
                    ValidationError(
                        field = "timeouts.retryDelaysSeconds[$index]",
                        message = "Retry delay must be positive, got: $delay",
                        code = "INVALID_RETRY_DELAY"
                    )
                )
            }
        }

        return errors
    }

    private fun validateFeatureConfig(config: com.rio.rostry.domain.config.FeatureConfig): List<ValidationError> {
        // Feature flags are boolean, no validation needed beyond type checking
        // which is handled by Kotlin's type system
        return emptyList()
    }

    /**
     * Check if a string is a valid email or phone number.
     */
    private fun isValidEmailOrPhone(identifier: String): Boolean {
        // Check email format
        if (Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
            return true
        }

        // Check phone format (basic validation for international format)
        // Accepts formats like: +1234567890, +12 345 678 90, etc.
        val phonePattern = Regex("^\\+?[1-9]\\d{1,14}$")
        val cleanedPhone = identifier.replace(Regex("[\\s-]"), "")
        return phonePattern.matches(cleanedPhone)
    }

    /**
     * Check if a string is a valid MIME type.
     */
    private fun isValidMimeType(mimeType: String): Boolean {
        // Basic MIME type validation: type/subtype
        val mimePattern = Regex("^[a-zA-Z0-9]+/[a-zA-Z0-9+.-]+$")
        return mimePattern.matches(mimeType)
    }
}
