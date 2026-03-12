package com.rio.rostry.domain.commerce.validation

import com.rio.rostry.core.common.Result

/**
 * Domain interface for product validation.
 *
 * Validates product listings including field completeness,
 * traceability requirements, and quarantine status checks.
 */
interface ProductValidator {

    /** Detailed validation result. */
    data class ValidationResult(
        val valid: Boolean,
        val reasons: List<String> = emptyList()
    )

    /** Validate a product with optional traceability check. */
    suspend fun validateWithTraceability(
        productName: String,
        price: Double,
        quantity: Int,
        sourceProductId: String? = null
    ): ValidationResult

    /** Check if a product is currently under quarantine. */
    suspend fun checkQuarantineStatus(productId: String): Boolean
}
