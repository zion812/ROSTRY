package com.rio.rostry.domain.transfer

import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.TransferAnalyticsDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.domain.error.ErrorHandler
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.validation.EntityValidator
import com.rio.rostry.domain.validation.InputValidationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TransferSystem.
 */
class TransferSystemTest {
    
    private lateinit var transferSystem: TransferSystem
    private lateinit var productDao: ProductDao
    private lateinit var userDao: UserDao
    private lateinit var transferDao: TransferDao
    private lateinit var transferAnalyticsDao: TransferAnalyticsDao
    private lateinit var auditLogDao: AuditLogDao
    private lateinit var entityValidator: EntityValidator
    private lateinit var errorHandler: ErrorHandler
    
    @Before
    fun setup() {
        productDao = mockk(relaxed = true)
        userDao = mockk(relaxed = true)
        transferDao = mockk(relaxed = true)
        transferAnalyticsDao = mockk(relaxed = true)
        auditLogDao = mockk(relaxed = true)
        entityValidator = mockk(relaxed = true)
        errorHandler = mockk(relaxed = true)
        
        transferSystem = TransferSystemImpl(
            productDao = productDao,
            userDao = userDao,
            transferDao = transferDao,
            transferAnalyticsDao = transferAnalyticsDao,
            auditLogDao = auditLogDao,
            entityValidator = entityValidator,
            errorHandler = errorHandler
        )
    }
    
    @Test
    fun `searchProducts returns products owned by user`() = runTest {
        // Given
        val userId = "user123"
        val product1 = createTestProduct("prod1", userId, "Bird 1")
        val product2 = createTestProduct("prod2", userId, "Bird 2")
        val product3 = createTestProduct("prod3", "otherUser", "Bird 3")
        
        coEvery { productDao.getProductsBySellerSuspend(userId) } returns listOf(product1, product2)
        
        // When
        val request = TransferSearchRequest(
            userId = userId,
            query = "",
            filters = TransferFilters()
        )
        val result = transferSystem.searchProducts(request)
        
        // Then
        assertTrue(result.isSuccess)
        val products = result.getOrNull()
        assertNotNull(products)
        assertEquals(2, products?.size)
        assertTrue(products?.all { it.ownerId == userId } == true)
    }
    
    @Test
    fun `searchProducts filters by query`() = runTest {
        // Given
        val userId = "user123"
        val product1 = createTestProduct("prod1", userId, "Aseel Bird")
        val product2 = createTestProduct("prod2", userId, "Shamo Bird")
        
        coEvery { productDao.getProductsBySellerSuspend(userId) } returns listOf(product1, product2)
        
        // When
        val request = TransferSearchRequest(
            userId = userId,
            query = "Aseel",
            filters = TransferFilters()
        )
        val result = transferSystem.searchProducts(request)
        
        // Then
        assertTrue(result.isSuccess)
        val products = result.getOrNull()
        assertEquals(1, products?.size)
        assertEquals("Aseel Bird", products?.first()?.name)
    }
    
    @Test
    fun `searchProducts filters by category`() = runTest {
        // Given
        val userId = "user123"
        val product1 = createTestProduct("prod1", userId, "Bird 1", category = "ROOSTER")
        val product2 = createTestProduct("prod2", userId, "Bird 2", category = "HEN")
        
        coEvery { productDao.getProductsBySellerSuspend(userId) } returns listOf(product1, product2)
        
        // When
        val request = TransferSearchRequest(
            userId = userId,
            query = "",
            filters = TransferFilters(category = "ROOSTER")
        )
        val result = transferSystem.searchProducts(request)
        
        // Then
        assertTrue(result.isSuccess)
        val products = result.getOrNull()
        assertEquals(1, products?.size)
        assertEquals("ROOSTER", products?.first()?.category)
    }
    
    @Test
    fun `searchRecipients excludes current user`() = runTest {
        // Given
        val currentUserId = "user123"
        val user1 = createTestUser("user123", "Current User")
        val user2 = createTestUser("user456", "Other User")
        val user3 = createTestUser("user789", "Another User")
        
        coEvery { userDao.getAllUsersSnapshot() } returns listOf(user1, user2, user3)
        
        // When
        val request = RecipientSearchRequest(
            query = "",
            excludeUserId = currentUserId
        )
        val result = transferSystem.searchRecipients(request)
        
        // Then
        assertTrue(result.isSuccess)
        val recipients = result.getOrNull()
        assertEquals(2, recipients?.size)
        assertFalse(recipients?.any { it.userId == currentUserId } == true)
    }
    
    @Test
    fun `searchRecipients filters by query`() = runTest {
        // Given
        val currentUserId = "user123"
        val user1 = createTestUser("user456", "John Doe", email = "john@example.com")
        val user2 = createTestUser("user789", "Jane Smith", email = "jane@example.com")
        
        coEvery { userDao.getAllUsersSnapshot() } returns listOf(user1, user2)
        
        // When
        val request = RecipientSearchRequest(
            query = "John",
            excludeUserId = currentUserId
        )
        val result = transferSystem.searchRecipients(request)
        
        // Then
        assertTrue(result.isSuccess)
        val recipients = result.getOrNull()
        assertEquals(1, recipients?.size)
        assertEquals("John Doe", recipients?.first()?.name)
    }
    
    @Test
    fun `initiateTransfer creates transfer record`() = runTest {
        // Given
        val productId = "prod123"
        val recipientId = "user456"
        val product = createTestProduct(productId, "user123", "Test Bird")
        val recipient = createTestUser(recipientId, "Recipient")
        
        coEvery { productDao.findById(productId) } returns product
        coEvery { userDao.findById(recipientId) } returns recipient
        coEvery { entityValidator.validateProductEligibility(product) } returns InputValidationResult.Valid
        coEvery { transferDao.findActiveTransferForProduct(productId) } returns null
        
        // When
        val result = transferSystem.initiateTransfer(productId, recipientId)
        
        // Then
        assertTrue(result is TransferOperationResult.Success)
        coVerify { transferDao.upsert(any()) }
        coVerify { transferAnalyticsDao.insert(any()) }
        coVerify { auditLogDao.insert(any()) }
    }
    
    @Test
    fun `initiateTransfer fails when product not found`() = runTest {
        // Given
        val productId = "prod123"
        val recipientId = "user456"
        
        coEvery { productDao.findById(productId) } returns null
        
        // When
        val result = transferSystem.initiateTransfer(productId, recipientId)
        
        // Then
        assertTrue(result is TransferOperationResult.Failure)
        assertEquals("Product not found", (result as TransferOperationResult.Failure).error)
    }
    
    @Test
    fun `detectConflicts identifies ownership mismatch`() = runTest {
        // Given
        val transferId = "transfer123"
        val transfer = mockk<com.rio.rostry.data.database.entity.TransferEntity>(relaxed = true) {
            coEvery { this@mockk.transferId } returns transferId
            coEvery { this@mockk.productId } returns "prod123"
            coEvery { this@mockk.fromUserId } returns "user123"
        }
        val product = createTestProduct("prod123", "differentUser", "Test Bird")
        
        coEvery { transferDao.findById(transferId) } returns transfer
        coEvery { productDao.findById("prod123") } returns product
        
        // When
        val result = transferSystem.detectConflicts(transferId)
        
        // Then
        assertTrue(result.isSuccess)
        val conflicts = result.getOrNull()
        assertNotNull(conflicts)
        assertTrue(conflicts?.any { it.conflictType == ConflictType.OWNERSHIP_MISMATCH } == true)
    }
    
    // Helper functions
    
    private fun createTestProduct(
        id: String,
        sellerId: String,
        name: String,
        category: String? = null
    ): ProductEntity {
        return ProductEntity(
            productId = id,
            sellerId = sellerId,
            name = name,
            category = category ?: "",
            status = "available",
            price = 100.0,
            imageUrls = listOf("https://example.com/image.jpg"),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    private fun createTestUser(
        id: String,
        name: String,
        email: String? = null
    ): UserEntity {
        val now = java.util.Date()
        return UserEntity(
            userId = id,
            fullName = name,
            email = email,
            createdAt = now,
            updatedAt = now
        )
    }
}
