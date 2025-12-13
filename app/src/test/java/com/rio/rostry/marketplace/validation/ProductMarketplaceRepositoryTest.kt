package com.rio.rostry.marketplace.validation

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.marketplace.validation.ProductValidator.ValidationResult
import com.rio.rostry.utils.CompressionUtils
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.media.FirebaseStorageUploader
import com.rio.rostry.utils.media.FirebaseStorageUploader.UploadResult
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

class ProductMarketplaceRepositoryTest {

    private lateinit var productDao: ProductDao
    private lateinit var productValidator: ProductValidator
    private lateinit var firebaseStorageUploader: FirebaseStorageUploader
    private lateinit var context: android.content.Context
    private lateinit var mockCacheDir: File
    private lateinit var rbacGuard: RbacGuard
    private lateinit var repository: com.rio.rostry.data.repository.ProductMarketplaceRepositoryImpl

    @Before
    fun setup() {
        productDao = mockk(relaxed = true)
        productValidator = mockk(relaxed = true)
        firebaseStorageUploader = mockk(relaxed = true)
        context = mockk(relaxed = true)
        mockCacheDir = mockk(relaxed = true)
        rbacGuard = mockk(relaxed = true)
        coEvery { rbacGuard.canListProduct() } returns true
        coEvery { rbacGuard.canAddPrivateProduct() } returns true

        coEvery { productValidator.validateWithTraceability(any()) } returns ValidationResult(true, emptyList())
        every { context.cacheDir } returns mockCacheDir
        every { mockCacheDir.absolutePath } returns "/tmp/cache"

        repository = com.rio.rostry.data.repository.ProductMarketplaceRepositoryImpl(
            productDao = productDao,
            productValidator = productValidator,
            firebaseStorageUploader = firebaseStorageUploader,
            appContext = context,
            rbacGuard = rbacGuard
        )
    }

    @Test
    fun `createProduct with imageBytes uploads images and returns HTTP URLs`() = runBlocking {
        // Arrange
        val productId = "test-product-123"
        val imageBytes = listOf(ByteArray(100) { 1 }, ByteArray(200) { 2 })
        val product = ProductEntity(productId = productId, sellerId = "user-123")

        val uploadResults = listOf(
            mockk<UploadResult>().apply {
                every { downloadUrl } returns "https://firebasestorage.googleapis.com/v0/b/test.appspot.com/o/products%2Ftest-product-123%2Fimage_0.jpg?alt=media"
            },
            mockk<UploadResult>().apply {
                every { downloadUrl } returns "https://firebasestorage.googleapis.com/v0/b/test.appspot.com/o/products%2Ftest-product-123%2Fimage_1.jpg?alt=media"
            }
        )

        // Mock upload behavior
        coEvery {
            firebaseStorageUploader.uploadFile(any<String>(), any<String>(), any<Boolean>(), any<Long>())
        } returnsMany uploadResults

        // Mock file operations
        val tempFiles = mutableListOf<File>()
        every { mockCacheDir.listFiles() } returns emptyArray()
        
        // Mock file creation
        every { mockCacheDir.resolve(any<String>()) } answers {
            val file = mockk<File>(relaxed = true)
            every { file.exists() } returns true
            every { file.delete() } returns true
            tempFiles.add(file)
            file
        }
        
        // Mock create temporary file for each image
        every { mockCacheDir.resolve(match<String> { it.contains("temp_upload") }) } answers {
            val file = mockk<File>(relaxed = true)
            every { file.absolutePath } returns firstArg<String>()
            every { file.exists() } returns true
            every { file.delete() } returns true
            every { file.writeBytes(any<ByteArray>()) } just Runs
            tempFiles.add(file)
            file
        }

        // Act
        val result = repository.createProduct(product, imageBytes)

        // Assert
        assertTrue(result is Resource.Success)
        assertEquals(productId, result.data)

        // Verify uploads were called
        coVerify(exactly = 2) {
            firebaseStorageUploader.uploadFile(
                any(),
                match<String> { it.contains("image_") },
                false,
                1_500_000L
            )
        }

        // Verify that the product was upserted with HTTP URLs
        coVerify {
            productDao.upsert(
                match<ProductEntity> { entity ->
                    entity.imageUrls.size == 2 &&
                    entity.imageUrls.all { url ->
                        url.startsWith("https://") || url.startsWith("http://")
                    }
                }
            )
        }
    }

    @Test
    fun `createProduct enforces 5 image limit`() = runBlocking {
        // Arrange
        val product = ProductEntity(productId = "test-product", sellerId = "user-123")
        val tooManyImages = (1..6).map { ByteArray(100) { it.toByte() } } // 6 images, max is 5

        // Act
        val result = repository.createProduct(product, tooManyImages)

        // Assert
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Maximum 5 images allowed") == true)
    }

    @Test
    fun `createProduct enforces 1_5MB size limit after compression`() = runBlocking {
        // Arrange
        val product = ProductEntity(productId = "test-product", sellerId = "user-123")
        val largeImage = ByteArray(2_000_000) { 1 } // 2MB, exceeds 1.5MB limit
        val imageBytes = listOf(largeImage)

        // Mock compression that still exceeds the limit
        mockkStatic(CompressionUtils::class)
        every { CompressionUtils.compressImage(any()) } returns largeImage

        // Act
        val result = repository.createProduct(product, imageBytes)

        // Assert
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Image exceeds 1.5MB limit") == true)

        // Clean up static mock
        unmockkStatic(CompressionUtils::class)
    }

    @Test
    fun `createProduct handles upload failures gracefully`() = runBlocking {
        // Arrange
        val product = ProductEntity(productId = "test-product", sellerId = "user-123")
        val imageBytes = listOf(ByteArray(100) { 1 })

        coEvery {
            firebaseStorageUploader.uploadFile(any<String>(), any<String>(), any<Boolean>(), any<Long>())
        } throws Exception("Upload failed")

        // Mock file operations
        val tempFile = mockk<File>(relaxed = true)
        every { tempFile.delete() } returns true
        every { mockCacheDir.resolve(any<String>()) } returns tempFile

        // Act
        val result = repository.createProduct(product, imageBytes)

        // Assert
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Failed to upload images") == true)
    }

    @Test
    fun `createProduct with empty imageBytes works correctly`() = runBlocking {
        // Arrange
        val product = ProductEntity(productId = "test-product", sellerId = "user-123", imageUrls = listOf("existing-url"))

        // Act
        val result = repository.createProduct(product, emptyList())

        // Assert
        assertTrue(result is Resource.Success)
        coVerify { productDao.upsert(any<ProductEntity>()) }
    }
}
