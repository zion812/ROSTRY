package com.rio.rostry.data

import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.Product
import com.rio.rostry.domain.model.Order
import com.rio.rostry.domain.model.Transfer
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.model.KycStatus
import com.rio.rostry.util.DataIntegrityChecker
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DataIntegrityTest {
    
    @Test
    fun testValidUser() {
        val user = User(
            id = "user123",
            phone = "+1234567890",
            email = "test@example.com",
            userType = UserType.FARMER,
            verificationStatus = VerificationStatus.VERIFIED,
            name = "John Doe",
            address = "123 Main St",
            location = "Farm Location",
            kycStatus = KycStatus.VERIFIED,
            coins = 100,
            createdAt = Date(System.currentTimeMillis() - 3600000), // 1 hour ago
            updatedAt = Date()
        )
        
        val result = DataIntegrityChecker.validateUser(user)
        assertTrue("User validation should pass", result.isValid)
        assertEquals("No errors expected", 0, result.errors.size)
    }
    
    @Test
    fun testInvalidUserWithEmptyId() {
        val user = User(
            id = "",
            phone = "+1234567890",
            userType = UserType.FARMER,
            verificationStatus = VerificationStatus.VERIFIED,
            kycStatus = KycStatus.VERIFIED,
            coins = 100,
            createdAt = Date(System.currentTimeMillis() - 3600000),
            updatedAt = Date()
        )
        
        val result = DataIntegrityChecker.validateUser(user)
        assertFalse("User validation should fail", result.isValid)
        assertTrue("Should have errors", result.errors.isNotEmpty())
        assertTrue("Should contain ID error", result.errors.any { it.contains("ID cannot be empty") })
    }
    
    @Test
    fun testValidProduct() {
        val product = Product(
            id = "product123",
            name = "Organic Tomatoes",
            description = "Fresh organic tomatoes",
            price = 2.99,
            quantity = 50,
            unit = "kg",
            farmerId = "farmer123",
            imageUrl = "http://example.com/image.jpg",
            category = "vegetables",
            createdAt = Date(System.currentTimeMillis() - 3600000),
            updatedAt = Date()
        )
        
        val result = DataIntegrityChecker.validateProduct(product)
        assertTrue("Product validation should pass", result.isValid)
        assertEquals("No errors expected", 0, result.errors.size)
    }
    
    @Test
    fun testInvalidProductWithNegativePrice() {
        val product = Product(
            id = "product123",
            name = "Organic Tomatoes",
            description = "Fresh organic tomatoes",
            price = -2.99,
            quantity = 50,
            unit = "kg",
            farmerId = "farmer123",
            category = "vegetables",
            createdAt = Date(System.currentTimeMillis() - 3600000),
            updatedAt = Date()
        )
        
        val result = DataIntegrityChecker.validateProduct(product)
        assertFalse("Product validation should fail", result.isValid)
        assertTrue("Should have errors", result.errors.isNotEmpty())
        assertTrue("Should contain price error", result.errors.any { it.contains("price cannot be negative") })
    }
    
    @Test
    fun testValidOrder() {
        val order = Order(
            id = "order123",
            productId = "product123",
            buyerId = "buyer123",
            farmerId = "farmer123",
            quantity = 5,
            totalPrice = 14.95,
            status = "pending",
            deliveryAddress = "456 Oak St",
            createdAt = Date(System.currentTimeMillis() - 1800000),
            updatedAt = Date()
        )
        
        val result = DataIntegrityChecker.validateOrder(order)
        assertTrue("Order validation should pass", result.isValid)
        assertEquals("No errors expected", 0, result.errors.size)
    }
    
    @Test
    fun testInvalidOrderWithZeroQuantity() {
        val order = Order(
            id = "order123",
            productId = "product123",
            buyerId = "buyer123",
            farmerId = "farmer123",
            quantity = 0,
            totalPrice = 0.0,
            status = "pending",
            deliveryAddress = "456 Oak St",
            createdAt = Date(System.currentTimeMillis() - 1800000),
            updatedAt = Date()
        )
        
        val result = DataIntegrityChecker.validateOrder(order)
        assertFalse("Order validation should fail", result.isValid)
        assertTrue("Should have errors", result.errors.isNotEmpty())
        assertTrue("Should contain quantity error", result.errors.any { it.contains("quantity must be positive") })
    }
    
    @Test
    fun testValidTransfer() {
        val transfer = Transfer(
            id = "transfer123",
            fromUserId = "user123",
            toUserId = "user456",
            orderId = "order123",
            amount = 100.0,
            type = "payment",
            status = "completed",
            createdAt = Date(System.currentTimeMillis() - 900000),
            updatedAt = Date()
        )
        
        val result = DataIntegrityChecker.validateTransfer(transfer)
        assertTrue("Transfer validation should pass", result.isValid)
        assertEquals("No errors expected", 0, result.errors.size)
    }
    
    @Test
    fun testInvalidTransferWithSameUsers() {
        val transfer = Transfer(
            id = "transfer123",
            fromUserId = "user123",
            toUserId = "user123",
            amount = 100.0,
            type = "payment",
            status = "completed",
            createdAt = Date(System.currentTimeMillis() - 900000),
            updatedAt = Date()
        )
        
        val result = DataIntegrityChecker.validateTransfer(transfer)
        assertFalse("Transfer validation should fail", result.isValid)
        assertTrue("Should have errors", result.errors.isNotEmpty())
        assertTrue("Should contain same user error", result.errors.any { it.contains("cannot be the same") })
    }
}