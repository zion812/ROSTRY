package com.rio.rostry.utils.validation

import com.rio.rostry.domain.model.UserType

/**
 * Central validation engine for marketplace listings and inputs.
 * Add rules for age, traceability, location, pricing, media.
 */
object InputValidationEngine {
    data class ValidationResult(val valid: Boolean, val errors: List<String> = emptyList())

    fun validateAgeDays(ageDays: Int, allowZero: Boolean = false): ValidationResult {
        if (ageDays < 0) return ValidationResult(false, listOf("Age cannot be negative"))
        if (!allowZero && ageDays == 0) return ValidationResult(false, listOf("Age must be greater than 0"))
        return ValidationResult(true)
    }

    fun validateTraceabilityRequired(traceable: Boolean, hasEvidence: Boolean): ValidationResult {
        return if (traceable && !hasEvidence) ValidationResult(false, listOf("Traceability evidence required")) else ValidationResult(true)
    }

    fun validateLocation(lat: Double?, lng: Double?): ValidationResult {
        return if (lat == null || lng == null) ValidationResult(false, listOf("Location required")) else ValidationResult(true)
    }

    fun validatePriceCents(priceCents: Long, minCents: Long = 100L, maxCents: Long = 10_000_00L): ValidationResult {
        if (priceCents < minCents) return ValidationResult(false, listOf("Price too low"))
        if (priceCents > maxCents) return ValidationResult(false, listOf("Price too high"))
        return ValidationResult(true)
    }

    fun validateMedia(count: Int, maxCount: Int = 6): ValidationResult {
        if (count <= 0) return ValidationResult(false, listOf("At least one photo is required"))
        if (count > maxCount) return ValidationResult(false, listOf("Too many photos: max $maxCount"))
        return ValidationResult(true)
    }

    fun validateByUserType(userType: UserType, requiresVerification: Boolean, isVerified: Boolean): ValidationResult {
        if (requiresVerification && !isVerified) return ValidationResult(false, listOf("Verification required for this action"))
        return ValidationResult(true)
    }
}
