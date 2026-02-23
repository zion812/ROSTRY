package com.rio.rostry.domain.manager

/**
 * Validates configuration values against schema rules.
 */
object ConfigurationValidator {

    fun validate(config: AppConfiguration): ConfigValidationResult {
        val errors = mutableListOf<String>()

        // Validate admin identifiers
        config.security.adminIdentifiers.forEachIndexed { index, id ->
            if (!isValidAdminIdentifier(id)) {
                errors.add("adminIdentifiers[$index]: '$id' is not a valid email or phone number")
            }
        }

        // Validate thresholds are positive
        with(config.thresholds) {
            if (storageQuotaMB <= 0) errors.add("storageQuotaMB must be positive, got $storageQuotaMB")
            if (maxBatchSize <= 0) errors.add("maxBatchSize must be positive, got $maxBatchSize")
            if (circuitBreakerFailureRate <= 0.0 || circuitBreakerFailureRate > 1.0)
                errors.add("circuitBreakerFailureRate must be (0,1], got $circuitBreakerFailureRate")
            if (hubCapacity <= 0) errors.add("hubCapacity must be positive, got $hubCapacity")
            if (deliveryRadiusKm <= 0.0) errors.add("deliveryRadiusKm must be positive, got $deliveryRadiusKm")
        }

        // Validate timeouts in range 1-300
        with(config.timeouts) {
            if (networkRequestSeconds !in 1..300)
                errors.add("networkRequestSeconds must be 1-300, got $networkRequestSeconds")
            if (circuitBreakerOpenSeconds !in 1..300)
                errors.add("circuitBreakerOpenSeconds must be 1-300, got $circuitBreakerOpenSeconds")
            retryDelaysSeconds.forEachIndexed { i, delay ->
                if (delay !in 1..300) errors.add("retryDelaysSeconds[$i] must be 1-300, got $delay")
            }
        }

        return if (errors.isEmpty()) {
            ConfigValidationResult.Valid
        } else {
            ConfigValidationResult.Invalid(errors)
        }
    }

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val PHONE_REGEX = Regex("^\\+?[1-9]\\d{6,14}$")

    private fun isValidAdminIdentifier(identifier: String): Boolean {
        return EMAIL_REGEX.matches(identifier) || PHONE_REGEX.matches(identifier)
    }
}
