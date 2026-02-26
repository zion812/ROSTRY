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
    fun validate_acceptsVerifiedProduct() {
        val product = ProductEntity(
            id = "prod1",
            name = "Test Product",
            ownerId = "user1",
            verificationStatus = "VERIFIED",
            isDeleted = false
        )
        
        val request = ProductEligibilityRequest(product, "user1")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Valid)
    }

    @Test
    fun validate_rejectsPendingVerification() {
        val product = ProductEntity(
            id = "prod1",
            name = "Test Product",
            ownerId = "user1",
            verificationStatus = "PENDING",
            isDeleted = false
        )
        
        val request = ProductEligibilityRequest(product, "user1")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "PRODUCT_NOT_VERIFIED" })
    }

    @Test
    fun validate_rejectsRejectedVerification() {
        val product = ProductEntity(
            id = "prod1",
            name = "Test Product",
            ownerId = "user1",
            verificationStatus = "REJECTED",
            isDeleted = false
        )
        
        val request = ProductEligibilityRequest(product, "user1")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "PRODUCT_NOT_VERIFIED" })
    }

    @Test
    fun validate_rejectsNullVerification() {
        val product = ProductEntity(
            id = "prod1",
            name = "Test Product",
            ownerId = "user1",
            verificationStatus = null,
            isDeleted = false
        )
        
        val request = ProductEligibilityRequest(product, "user1")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "PRODUCT_NOT_VERIFIED" })
    }

    @Test
    fun validate_rejectsOwnershipMismatch() {
        val product = ProductEntity(
            id = "prod1",
            name = "Test Product",
            ownerId = "user1",
            verificationStatus = "VERIFIED",
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
            id = "prod1",
            name = "Test Product",
            ownerId = "user1",
            verificationStatus = "VERIFIED",
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
            id = "prod1",
            name = "Test Product",
            ownerId = "user1",
            verificationStatus = "PENDING",
            isDeleted = true
        )
        
        val request = ProductEligibilityRequest(product, "user2")
        val result = validator.validate(request)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        
        // Should have multiple errors
        assertTrue(errors.size >= 2)
        assertTrue(errors.any { it.code == "PRODUCT_NOT_VERIFIED" })
        assertTrue(errors.any { it.code == "OWNERSHIP_MISMATCH" })
        assertTrue(errors.any { it.code == "PRODUCT_DELETED" })
    }
}
