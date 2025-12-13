package com.rio.rostry.marketplace.validation

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductValidator @Inject constructor(
    private val quarantineDao: QuarantineRecordDao
) {

    data class ValidationResult(
        val valid: Boolean,
        val reasons: List<String> = emptyList()
    )

    suspend fun validateWithTraceability(product: ProductEntity, sourceProductId: String? = null): ValidationResult {
        val reasons = mutableListOf<String>()

        if (product.name.isBlank()) {
            reasons.add("Product name cannot be empty")
        }
        if (product.price < 0) {
            reasons.add("Price cannot be negative")
        }
        if (product.quantity < 0) {
            reasons.add("Quantity cannot be negative")
        }
        
        // TODO: Add more specific traceability validation logic here if needed
        // For example, checking if familyTreeId is present if traceability is claimed

        return ValidationResult(reasons.isEmpty(), reasons)
    }

    suspend fun checkQuarantineStatus(productId: String): Boolean {
        return try {
            quarantineDao.observeForProduct(productId).first()
                .any { it.status == "ACTIVE" }
        } catch (e: Exception) {
            false
        }
    }
}
