package com.rio.rostry.domain.validation

import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.ProductEntity
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProductEligibilityValidatorTest {

    private lateinit var database: AppDatabase
    private lateinit var validator: ProductEligibilityValidator

    @Before
    fun setup() {
        database = mockk(relaxed = true)
        validator = ProductEligibilityValidator(database)
    }

    @Test
    fun validate_acceptsAvailableProduct() {
        val product = ProductEntity(
            productId = "prod1",
            name = "Test Product",
            sellerId = "user1",
            status = "available",
            isDeleted = false
        )
        
        val request = ProductEligibilityRequest(product, "user1")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun validate_rejectsPrivateProduct() {
        val product = ProductEntity(
            productId = "prod1",
            name = "Test Product",
            sellerId = "user1",
            status = "private",
            isDeleted = false
        )
        
        val request = ProductEligibilityRequest(product, "user1")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "PRODUCT_NOT_AVAILABLE" })
    }

    @Test
    fun validate_rejectsPendingApprovalProduct() {
        val product = ProductEntity(
            productId = "prod1",
            name = "Test Product",
            sellerId = "user1",
            status = "pending_approval",
            isDeleted = false
        )
        
        val request = ProductEligibilityRequest(product, "user1")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "PRODUCT_NOT_AVAILABLE" })
    }

    @Test
    fun validate_rejectsOwnershipMismatch() {
        val product = ProductEntity(
            productId = "prod1",
            name = "Test Product",
            sellerId = "user1",
            status = "available",
            isDeleted = false
        )
        
        val request = ProductEligibilityRequest(product, "user2")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "OWNERSHIP_MISMATCH" })
    }

    @Test
    fun validate_rejectsDeletedProduct() {
        val product = ProductEntity(
            productId = "prod1",
            name = "Test Product",
            sellerId = "user1",
            status = "available",
            isDeleted = true
        )
        
        val request = ProductEligibilityRequest(product, "user1")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "PRODUCT_DELETED" })
    }

    @Test
    fun validate_returnsMultipleErrors() {
        val product = ProductEntity(
            productId = "prod1",
            name = "Test Product",
            sellerId = "user1",
            status = "private",
            isDeleted = true
        )
        
        val request = ProductEligibilityRequest(product, "user2")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        
        // Should have multiple errors
        assertTrue(errors.size >= 2)
        assertTrue(errors.any { it.code == "PRODUCT_NOT_AVAILABLE" })
        assertTrue(errors.any { it.code == "OWNERSHIP_MISMATCH" })
        assertTrue(errors.any { it.code == "PRODUCT_DELETED" })
    }
}
