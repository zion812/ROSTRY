package com.rio.rostry.domain.integrity

import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for OrphanedProductHandler.
 * 
 * Tests:
 * - Detection of orphaned products (missing owner)
 * - Assignment of orphaned products to system account
 * - System account creation
 * - Logging of orphaned product assignments
 */
class OrphanedProductHandlerTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var productDao: ProductDao
    private lateinit var handler: OrphanedProductHandler

    @Before
    fun setup() {
        database = mockk(relaxed = true)
        userDao = mockk(relaxed = true)
        productDao = mockk(relaxed = true)
        
        coEvery { database.userDao() } returns userDao
        coEvery { database.productDao() } returns productDao
        
        handler = OrphanedProductHandler(database)
    }

    @Test
    fun `detectOrphanedProducts returns products with missing sellers`() = runTest {
        // Given
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "missing_user",
            name = "Test Product"
        )
        val validProduct = ProductEntity(
            productId = "prod2",
            sellerId = "valid_user",
            name = "Valid Product"
        )
        
        coEvery { productDao.getAllProducts() } returns listOf(orphanedProduct, validProduct)
        coEvery { userDao.getUserById("missing_user") } returns null
        coEvery { userDao.getUserById("valid_user") } returns UserEntity(
            userId = "valid_user",
            name = "Valid User",
            email = "valid@test.com"
        )
        
        // When
        val orphanedProducts = handler.detectOrphanedProducts()
        
        // Then
        assertEquals(1, orphanedProducts.size)
        assertEquals("prod1", orphanedProducts[0].productId)
    }

    @Test
    fun `detectOrphanedProducts returns products with empty sellerId`() = runTest {
        // Given
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "",
            name = "Test Product"
        )
        
        coEvery { productDao.getAllProducts() } returns listOf(orphanedProduct)
        
        // When
        val orphanedProducts = handler.detectOrphanedProducts()
        
        // Then
        assertEquals(1, orphanedProducts.size)
        assertEquals("prod1", orphanedProducts[0].productId)
    }

    @Test
    fun `assignOrphanedProductsToSystem creates system account if not exists`() = runTest {
        // Given
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "missing_user",
            name = "Test Product"
        )
        
        coEvery { userDao.getUserById("system_orphaned_products") } returns null andThen UserEntity(
            userId = "system_orphaned_products",
            name = "System Account",
            email = "system@rostry.internal"
        )
        coEvery { userDao.insert(any()) } returns Unit
        coEvery { productDao.update(any()) } returns Unit
        
        // When
        val assignedCount = handler.assignOrphanedProductsToSystem(listOf(orphanedProduct))
        
        // Then
        assertEquals(1, assignedCount)
        coVerify { userDao.insert(any()) }
        coVerify { productDao.update(any()) }
    }

    @Test
    fun `assignOrphanedProductsToSystem assigns products to system account`() = runTest {
        // Given
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "missing_user",
            name = "Test Product"
        )
        val systemAccount = UserEntity(
            userId = "system_orphaned_products",
            name = "System Account",
            email = "system@rostry.internal"
        )
        
        coEvery { userDao.getUserById("system_orphaned_products") } returns systemAccount
        coEvery { productDao.update(any()) } returns Unit
        
        // When
        val assignedCount = handler.assignOrphanedProductsToSystem(listOf(orphanedProduct))
        
        // Then
        assertEquals(1, assignedCount)
        coVerify { 
            productDao.update(match { 
                it.productId == "prod1" && it.sellerId == "system_orphaned_products"
            })
        }
    }

    @Test
    fun `detectAndAssignOrphanedProducts handles complete workflow`() = runTest {
        // Given
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "missing_user",
            name = "Test Product"
        )
        val systemAccount = UserEntity(
            userId = "system_orphaned_products",
            name = "System Account",
            email = "system@rostry.internal"
        )
        
        coEvery { productDao.getAllProducts() } returns listOf(orphanedProduct)
        coEvery { userDao.getUserById("missing_user") } returns null
        coEvery { userDao.getUserById("system_orphaned_products") } returns systemAccount
        coEvery { productDao.update(any()) } returns Unit
        
        // When
        val assignedCount = handler.detectAndAssignOrphanedProducts()
        
        // Then
        assertEquals(1, assignedCount)
        coVerify { productDao.update(any()) }
    }

    @Test
    fun `isProductOrphaned returns true for product with missing seller`() = runTest {
        // Given
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "missing_user",
            name = "Test Product"
        )
        
        coEvery { productDao.getProductById("prod1") } returns orphanedProduct
        coEvery { userDao.getUserById("missing_user") } returns null
        
        // When
        val isOrphaned = handler.isProductOrphaned("prod1")
        
        // Then
        assertTrue(isOrphaned)
    }

    @Test
    fun `isProductOrphaned returns false for product with valid seller`() = runTest {
        // Given
        val validProduct = ProductEntity(
            productId = "prod1",
            sellerId = "valid_user",
            name = "Test Product"
        )
        val validUser = UserEntity(
            userId = "valid_user",
            name = "Valid User",
            email = "valid@test.com"
        )
        
        coEvery { productDao.getProductById("prod1") } returns validProduct
        coEvery { userDao.getUserById("valid_user") } returns validUser
        
        // When
        val isOrphaned = handler.isProductOrphaned("prod1")
        
        // Then
        assertFalse(isOrphaned)
    }

    @Test
    fun `isProductOrphaned returns true for product with empty sellerId`() = runTest {
        // Given
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "",
            name = "Test Product"
        )
        
        coEvery { productDao.getProductById("prod1") } returns orphanedProduct
        
        // When
        val isOrphaned = handler.isProductOrphaned("prod1")
        
        // Then
        assertTrue(isOrphaned)
    }
}
