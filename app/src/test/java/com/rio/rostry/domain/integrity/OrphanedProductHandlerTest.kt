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
import java.util.Date

/**
 * Unit tests for OrphanedProductHandler.
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
        val now = Date()
        
        coEvery { productDao.getAllProductsSnapshot() } returns listOf(orphanedProduct, validProduct)
        coEvery { userDao.findById("missing_user") } returns null
        coEvery { userDao.findById("valid_user") } returns UserEntity(
            userId = "valid_user",
            fullName = "Valid User",
            email = "valid@test.com",
            createdAt = now,
            updatedAt = now
        )
        
        val orphanedProducts = handler.detectOrphanedProducts()
        
        assertEquals(1, orphanedProducts.size)
        assertEquals("prod1", orphanedProducts[0].productId)
    }

    @Test
    fun `detectOrphanedProducts returns products with empty sellerId`() = runTest {
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "",
            name = "Test Product"
        )
        
        coEvery { productDao.getAllProductsSnapshot() } returns listOf(orphanedProduct)
        
        val orphanedProducts = handler.detectOrphanedProducts()
        
        assertEquals(1, orphanedProducts.size)
        assertEquals("prod1", orphanedProducts[0].productId)
    }

    @Test
    fun `assignOrphanedProductsToSystem assigns products to system account`() = runTest {
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "missing_user",
            name = "Test Product"
        )
        val now = Date()
        val systemAccount = UserEntity(
            userId = "system_orphaned_products",
            fullName = "System Account",
            email = "system@rostry.internal",
            createdAt = now,
            updatedAt = now
        )
        
        coEvery { userDao.findById("system_orphaned_products") } returns systemAccount
        
        val assignedCount = handler.assignOrphanedProductsToSystem(listOf(orphanedProduct))
        
        assertEquals(1, assignedCount)
        coVerify { productDao.upsert(match { it.productId == "prod1" && it.sellerId == "system_orphaned_products" }) }
    }

    @Test
    fun `detectAndAssignOrphanedProducts handles complete workflow`() = runTest {
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "missing_user",
            name = "Test Product"
        )
        val now = Date()
        val systemAccount = UserEntity(
            userId = "system_orphaned_products",
            fullName = "System Account",
            email = "system@rostry.internal",
            createdAt = now,
            updatedAt = now
        )
        
        coEvery { productDao.getAllProductsSnapshot() } returns listOf(orphanedProduct)
        coEvery { userDao.findById("missing_user") } returns null
        coEvery { userDao.findById("system_orphaned_products") } returns systemAccount
        
        val assignedCount = handler.detectAndAssignOrphanedProducts()
        
        assertEquals(1, assignedCount)
        coVerify { productDao.upsert(any()) }
    }

    @Test
    fun `isProductOrphaned returns true for product with missing seller`() = runTest {
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "missing_user",
            name = "Test Product"
        )
        
        coEvery { productDao.findById("prod1") } returns orphanedProduct
        coEvery { userDao.findById("missing_user") } returns null
        
        val isOrphaned = handler.isProductOrphaned("prod1")
        
        assertTrue(isOrphaned)
    }

    @Test
    fun `isProductOrphaned returns false for product with valid seller`() = runTest {
        val validProduct = ProductEntity(
            productId = "prod1",
            sellerId = "valid_user",
            name = "Test Product"
        )
        val now = Date()
        val validUser = UserEntity(
            userId = "valid_user",
            fullName = "Valid User",
            email = "valid@test.com",
            createdAt = now,
            updatedAt = now
        )
        
        coEvery { productDao.findById("prod1") } returns validProduct
        coEvery { userDao.findById("valid_user") } returns validUser
        
        val isOrphaned = handler.isProductOrphaned("prod1")
        
        assertFalse(isOrphaned)
    }

    @Test
    fun `isProductOrphaned returns true for product with empty sellerId`() = runTest {
        val orphanedProduct = ProductEntity(
            productId = "prod1",
            sellerId = "",
            name = "Test Product"
        )
        
        coEvery { productDao.findById("prod1") } returns orphanedProduct
        
        val isOrphaned = handler.isProductOrphaned("prod1")
        
        assertTrue(isOrphaned)
    }
}
