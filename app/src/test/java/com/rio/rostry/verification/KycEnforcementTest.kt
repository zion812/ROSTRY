package com.rio.rostry.verification

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepositoryImpl
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.argThat
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertIs

class KycEnforcementTest {

    @Test
    fun `PENDING farmer can add private birds`() = runBlocking {
        // Setup mocks
        val userRepository = mock<UserRepository>()
        val rbacGuard = mock<RbacGuard>()
        val currentUserProvider = mock<CurrentUserProvider>()
        val productDao = mock<com.rio.rostry.data.database.dao.ProductDao>()
        val auditLogDao = mock<com.rio.rostry.data.database.dao.AuditLogDao>()

        // Mock current user ID
        whenever(currentUserProvider.userIdOrNull()).thenReturn("test-user")

        // Mock user entity with PENDING verification status
        val pendingUser = com.rio.rostry.data.database.entity.UserEntity(
            userId = "test-user",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test User",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        whenever(userRepository.getUserById("test-user")).thenReturn(flowOf(Resource.Success(pendingUser)))

        // Mock RbacGuard to deny public product listing for PENDING users (isVerified = false)
        whenever(rbacGuard.isVerified()).thenReturn(false)
        whenever(rbacGuard.canListProduct()).thenReturn(false) // PENDING users can't list public products
        whenever(rbacGuard.canAddPrivateProduct()).thenReturn(true) // But can add private products

        // Stub DAO operations to prevent MissingInteractionException
        whenever(productDao.upsert(any())).thenAnswer { }
        whenever(auditLogDao.insert(any())).thenAnswer { }

        val productRepository = ProductRepositoryImpl(
            productDao,
            mock() // productValidator
        )

        // Create a private product (for local farm management)
        val privateProduct = ProductEntity(
            productId = "test-id",
            sellerId = "test-user", // Match current user ID
            status = "private", // Private product for farm management
            name = "Test Bird",
            category = "BIRD",
            price = 0.0,
            quantity = 1.0,
            unit = "unit"
        )

        // Attempt to add private product
        val result = productRepository.addProduct(privateProduct, false)

        // Verify the result: PENDING farmers should be able to add private products
        assertTrue(result is Resource.Success<String>)

        // Verify DAO calls were made properly
        verify(productDao).upsert(argThat { this.status == "private" })
        verify(auditLogDao).insert(any())
    }

    @Test
    fun `PENDING farmer cannot list to market`() = runBlocking {
        // Setup mocks
        val userRepository = mock<UserRepository>()
        val rbacGuard = mock<RbacGuard>()
        val currentUserProvider = mock<CurrentUserProvider>()
        val productDao = mock<com.rio.rostry.data.database.dao.ProductDao>()
        val auditLogDao = mock<com.rio.rostry.data.database.dao.AuditLogDao>()

        // Mock current user ID
        whenever(currentUserProvider.userIdOrNull()).thenReturn("test-user")

        // Mock user entity with PENDING verification status
        val pendingUser = com.rio.rostry.data.database.entity.UserEntity(
            userId = "test-user",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test User",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        whenever(userRepository.getUserById("test-user")).thenReturn(flowOf(Resource.Success(pendingUser)))

        // Mock RbacGuard to deny public product listing for PENDING users (isVerified = false)
        whenever(rbacGuard.isVerified()).thenReturn(false)
        whenever(rbacGuard.canListProduct()).thenReturn(false) // PENDING users can't list public products
        whenever(rbacGuard.canAddPrivateProduct()).thenReturn(true) // But allow private

        // Stub DAO operations to prevent MissingInteractionException
        whenever(productDao.upsert(any())).thenAnswer { }
        whenever(auditLogDao.insert(any())).thenAnswer { }

        val productRepository = ProductRepositoryImpl(
            productDao,
            mock() // productValidator
        )

        // Create a public product (for market listing)
        val publicProduct = ProductEntity(
            productId = "test-id",
            sellerId = "test-user", // Match current user ID
            status = "active", // Public product for market
            name = "Test Bird",
            category = "BIRD",
            price = 100.0,
            quantity = 1.0,
            unit = "unit"
        )

        // Attempt to add public product
        val result = productRepository.addProduct(publicProduct, false)

        // Verify the result: PENDING farmers should NOT be able to list public products
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("KYC") == true || result.message?.contains("verification") == true || result.message?.contains("Complete KYC") == true)

        // Verify no DAO calls were made due to permission failure
        verify(productDao, never()).upsert(any())
        verify(auditLogDao, never()).insert(any())
    }

    @Test
    fun `PENDING farmer cannot update private to public`() = runBlocking {
        // Setup mocks
        val userRepository = mock<UserRepository>()
        val rbacGuard = mock<RbacGuard>()
        val currentUserProvider = mock<CurrentUserProvider>()
        val productDao = mock<com.rio.rostry.data.database.dao.ProductDao>()
        val auditLogDao = mock<com.rio.rostry.data.database.dao.AuditLogDao>()

        // Mock current user ID
        whenever(currentUserProvider.userIdOrNull()).thenReturn("test-user")

        // Mock user entity with PENDING verification status
        val pendingUser = com.rio.rostry.data.database.entity.UserEntity(
            userId = "test-user",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test User",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        whenever(userRepository.getUserById("test-user")).thenReturn(flowOf(Resource.Success(pendingUser)))

        // Mock existing private product
        val existingPrivateProduct = ProductEntity(
            productId = "test-id",
            sellerId = "test-user", // Match current user
            status = "private",
            name = "Test Bird",
            category = "BIRD",
            price = 0.0,
            quantity = 1.0,
            unit = "unit"
        )
        whenever(productDao.findById("test-id")).thenReturn(existingPrivateProduct)

        // Mock RbacGuard to deny public product listing for PENDING users (isVerified = false)
        whenever(rbacGuard.isVerified()).thenReturn(false)
        whenever(rbacGuard.canListProduct()).thenReturn(false) // PENDING users can't list public products

        // Stub DAO operations to prevent MissingInteractionException
        whenever(productDao.upsert(any())).thenAnswer { }
        whenever(auditLogDao.insert(any())).thenAnswer { }

        val productRepository = ProductRepositoryImpl(
            productDao,
            mock() // productValidator
        )

        // Create updated product with public status (active)
        val updatedProduct = existingPrivateProduct.copy(
            status = "active" // Trying to change from private to public
        )

        // Attempt to update product (private -> public)
        val result = productRepository.updateProduct(updatedProduct)

        // Verify the result: PENDING farmers should NOT be able to update private to public
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("KYC") == true || result.message?.contains("verification") == true || result.message?.contains("Complete KYC") == true)

        // Verify no DAO upsert was made due to permission failure
        verify(productDao, never()).upsert(any())
        verify(auditLogDao, never()).insert(any())
    }

    @Test
    fun `VERIFIED farmer can update private to public`() = runBlocking {
        // Setup mocks
        val userRepository = mock<UserRepository>()
        val rbacGuard = mock<RbacGuard>()
        val currentUserProvider = mock<CurrentUserProvider>()
        val productDao = mock<com.rio.rostry.data.database.dao.ProductDao>()
        val auditLogDao = mock<com.rio.rostry.data.database.dao.AuditLogDao>()

        // Mock current user ID
        whenever(currentUserProvider.userIdOrNull()).thenReturn("test-user")

        // Mock user entity with VERIFIED verification status
        val verifiedUser = com.rio.rostry.data.database.entity.UserEntity(
            userId = "test-user",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test User",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.VERIFIED,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        whenever(userRepository.getUserById("test-user")).thenReturn(flowOf(Resource.Success(verifiedUser)))

        // Mock existing private product
        val existingPrivateProduct = ProductEntity(
            productId = "test-id",
            sellerId = "test-user", // Match current user
            status = "private",
            name = "Test Bird",
            category = "BIRD",
            price = 0.0,
            quantity = 1.0,
            unit = "unit"
        )
        whenever(productDao.findById("test-id")).thenReturn(existingPrivateProduct)

        // Mock RbacGuard to allow public product listing for VERIFIED users (isVerified = true)
        whenever(rbacGuard.isVerified()).thenReturn(true)
        whenever(rbacGuard.canListProduct()).thenReturn(true)

        // Stub DAO operations to prevent MissingInteractionException
        whenever(productDao.upsert(any())).thenAnswer { }
        whenever(auditLogDao.insert(any())).thenAnswer { }

        val productRepository = ProductRepositoryImpl(
            productDao,
            mock() // productValidator
        )

        // Create updated product with public status (active)
        val updatedProduct = existingPrivateProduct.copy(
            status = "active" // Changing from private to public
        )

        // Attempt to update product (private -> public)
        val result = productRepository.updateProduct(updatedProduct)

        // Verify the result: VERIFIED farmers should be able to update private to public
        assertTrue(result is Resource.Success<Unit>)

        // Verify DAO upsert was made
        verify(productDao).upsert(any())
        verify(auditLogDao).insert(any())
    }

    @Test
    fun `PENDING farmer can update non-status fields of existing public product`() = runBlocking {
        // This test verifies that PENDING farmers can update non-status fields (name, price, etc.)
        // of their existing public products. The verification check only applies to status transitions
        // from private to public, not to updates of already-public products.

        // Setup mocks
        val userRepository = mock<UserRepository>()
        val rbacGuard = mock<RbacGuard>()
        val currentUserProvider = mock<CurrentUserProvider>()
        val productDao = mock<com.rio.rostry.data.database.dao.ProductDao>()
        val auditLogDao = mock<com.rio.rostry.data.database.dao.AuditLogDao>()

        // Mock current user ID
        whenever(currentUserProvider.userIdOrNull()).thenReturn("test-user")

        // Mock user entity with PENDING verification status
        val pendingUser = com.rio.rostry.data.database.entity.UserEntity(
            userId = "test-user",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test User",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        whenever(userRepository.getUserById("test-user")).thenReturn(flowOf(Resource.Success(pendingUser)))

        // Mock existing public product
        val existingPublicProduct = ProductEntity(
            productId = "test-id",
            sellerId = "test-user", // Match current user
            status = "active",
            name = "Test Bird",
            category = "BIRD",
            price = 100.0,
            quantity = 1.0,
            unit = "unit"
        )
        whenever(productDao.findById("test-id")).thenReturn(existingPublicProduct)

        // Mock RbacGuard - for this test, we're updating public to public (same status)
        // The restriction is only for private->public transitions, so this should be allowed
        whenever(rbacGuard.isVerified()).thenReturn(false)
        whenever(rbacGuard.canListProduct()).thenReturn(false) // Still PENDING

        // Stub DAO operations to prevent MissingInteractionException
        whenever(productDao.upsert(any())).thenAnswer { }
        whenever(auditLogDao.insert(any())).thenAnswer { }

        val productRepository = ProductRepositoryImpl(
            productDao,
            mock() // productValidator
        )

        // Create updated product with same public status (active)
        val updatedProduct = existingPublicProduct.copy(
            name = "Updated Test Bird" // Only changing name, not status
        )

        // Attempt to update product (public -> public, same status)
        val result = productRepository.updateProduct(updatedProduct)

        // PENDING farmers should be able to update non-status fields of existing public products
        // The verification check only applies to private -> public status transitions
        assertTrue(result is Resource.Success<Unit>)

        // Verify DAO upsert was made since status didn't change from private to public
        verify(productDao).upsert(any())
        verify(auditLogDao).insert(any())
    }

    @Test
    fun `PENDING farmer cannot create new public product directly`() = runBlocking {
        // Setup mocks
        val userRepository = mock<UserRepository>()
        val rbacGuard = mock<RbacGuard>()
        val currentUserProvider = mock<CurrentUserProvider>()
        val productDao = mock<com.rio.rostry.data.database.dao.ProductDao>()
        val auditLogDao = mock<com.rio.rostry.data.database.dao.AuditLogDao>()

        // Mock current user ID
        whenever(currentUserProvider.userIdOrNull()).thenReturn("test-user")

        // Mock user entity with PENDING verification status
        val pendingUser = com.rio.rostry.data.database.entity.UserEntity(
            userId = "test-user",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test User",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        whenever(userRepository.getUserById("test-user")).thenReturn(flowOf(Resource.Success(pendingUser)))

        // Mock RbacGuard to deny public product listing for PENDING users (isVerified = false)
        whenever(rbacGuard.isVerified()).thenReturn(false)
        whenever(rbacGuard.canListProduct()).thenReturn(false) // PENDING users can't list public products
        whenever(rbacGuard.canAddPrivateProduct()).thenReturn(true) // But allow private

        // Stub DAO operations to prevent MissingInteractionException
        whenever(productDao.upsert(any())).thenAnswer { }
        whenever(auditLogDao.insert(any())).thenAnswer { }

        val productRepository = ProductRepositoryImpl(
            productDao,
            mock() // productValidator
        )

        // Create a public product (for direct creation with public status)
        val publicProduct = ProductEntity(
            productId = "test-id",
            sellerId = "test-user", // Match current user ID
            status = "active", // Public product for market - trying to create directly with public status
            name = "Test Bird",
            category = "BIRD",
            price = 100.0,
            quantity = 1.0,
            unit = "unit"
        )

        // Attempt to add public product directly
        val result = productRepository.addProduct(publicProduct, false)

        // Verify the result: PENDING farmers should NOT be able to create public products directly
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("KYC") == true || result.message?.contains("verification") == true || result.message?.contains("Complete KYC") == true)

        // Verify no DAO calls were made due to permission failure
        verify(productDao, never()).upsert(any())
        verify(auditLogDao, never()).insert(any())
    }

    @Test
    fun `VERIFIED farmer can update public to public status`() = runBlocking {
        // Setup mocks
        val userRepository = mock<UserRepository>()
        val rbacGuard = mock<RbacGuard>()
        val currentUserProvider = mock<CurrentUserProvider>()
        val productDao = mock<com.rio.rostry.data.database.dao.ProductDao>()
        val auditLogDao = mock<com.rio.rostry.data.database.dao.AuditLogDao>()

        // Mock current user ID
        whenever(currentUserProvider.userIdOrNull()).thenReturn("test-user")

        // Mock user entity with VERIFIED verification status
        val verifiedUser = com.rio.rostry.data.database.entity.UserEntity(
            userId = "test-user",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test User",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.VERIFIED,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        whenever(userRepository.getUserById("test-user")).thenReturn(flowOf(Resource.Success(verifiedUser)))

        // Mock existing public product
        val existingPublicProduct = ProductEntity(
            productId = "test-id",
            sellerId = "test-user", // Match current user
            status = "active",
            name = "Test Bird",
            category = "BIRD",
            price = 100.0,
            quantity = 1.0,
            unit = "unit"
        )
        whenever(productDao.findById("test-id")).thenReturn(existingPublicProduct)

        // Mock RbacGuard - user is VERIFIED so isVerified = true
        whenever(rbacGuard.isVerified()).thenReturn(true)
        whenever(rbacGuard.canListProduct()).thenReturn(true)

        // Stub DAO operations to prevent MissingInteractionException
        whenever(productDao.upsert(any())).thenAnswer { }
        whenever(auditLogDao.insert(any())).thenAnswer { }

        val productRepository = ProductRepositoryImpl(
            productDao,
            mock() // productValidator
        )

        // Create updated product with same public status (active) but different data
        val updatedProduct = existingPublicProduct.copy(
            name = "Updated Test Bird",
            price = 150.0
        )

        // Attempt to update product (public -> public)
        val result = productRepository.updateProduct(updatedProduct)

        // This should succeed because the user is verified
        assertTrue(result is Resource.Success<Unit>)

        // Verify DAO upsert was made
        verify(productDao).upsert(any())
        verify(auditLogDao).insert(any())
    }
}
