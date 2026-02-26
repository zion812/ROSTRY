package com.rio.rostry.domain.validation

import com.rio.rostry.domain.config.AppConfiguration
import com.rio.rostry.domain.config.ConfigurationManager
import com.rio.rostry.domain.config.FeatureConfig
import com.rio.rostry.domain.config.SecurityConfig
import com.rio.rostry.domain.config.ThresholdConfig
import com.rio.rostry.domain.config.TimeoutConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

class FileUploadValidatorTest {

    private lateinit var configurationManager: ConfigurationManager
    private lateinit var validator: FileUploadValidator

    @Before
    fun setup() {
        configurationManager = mockk()
        
        val config = AppConfiguration(
            security = SecurityConfig(
                adminIdentifiers = emptyList(),
                moderationBlocklist = emptyList(),
                allowedFileTypes = listOf("image/jpeg", "image/png", "image/webp", "video/mp4", "video/3gpp")
            ),
            thresholds = ThresholdConfig(
                storageQuotaMB = 500,
                maxBatchSize = 100,
                circuitBreakerFailureRate = 0.5,
                hubCapacity = 1000,
                deliveryRadiusKm = 50.0
            ),
            timeouts = TimeoutConfig(
                networkRequestSeconds = 30,
                circuitBreakerOpenSeconds = 30,
                retryDelaysSeconds = listOf(1, 2, 4)
            ),
            features = FeatureConfig(
                enableRecommendations = true,
                enableDisputes = true,
                enableBreedingCompatibility = true
            )
        )
        
        every { configurationManager.get() } returns config
        
        validator = FileUploadValidator(configurationManager)
    }

    @Test
    fun validate_rejectsNonExistentFile() {
        val file = File("/nonexistent/file.jpg")
        val result = validator.validate(file)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "FILE_NOT_FOUND" })
    }

    @Test
    fun validate_detectsJpegMagicNumber() {
        // Create a temporary file with JPEG magic number
        val tempFile = File.createTempFile("test", ".jpg")
        tempFile.deleteOnExit()
        
        // Write JPEG magic number
        tempFile.writeBytes(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte()))
        
        val result = validator.validate(tempFile)
        
        // Should pass magic number detection
        // May fail on other checks (dimensions, etc.) but not on file type
        if (result is InputValidationResult.Invalid) {
            val errors = result.errors
            assertFalse("Should not have file type error", errors.any { it.code == "INVALID_FILE_TYPE" })
        }
    }

    @Test
    fun validate_detectsPngMagicNumber() {
        val tempFile = File.createTempFile("test", ".png")
        tempFile.deleteOnExit()
        
        // Write PNG magic number
        tempFile.writeBytes(byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte()))
        
        val result = validator.validate(tempFile)
        
        if (result is InputValidationResult.Invalid) {
            val errors = result.errors
            assertFalse("Should not have file type error", errors.any { it.code == "INVALID_FILE_TYPE" })
        }
    }

    @Test
    fun validate_rejectsUnknownFileType() {
        val tempFile = File.createTempFile("test", ".txt")
        tempFile.deleteOnExit()
        
        // Write some random bytes that don't match any magic number
        tempFile.writeBytes(byteArrayOf(0x00, 0x01, 0x02, 0x03))
        
        val result = validator.validate(tempFile)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "INVALID_FILE_TYPE" })
    }

    @Test
    fun validate_rejectsDisallowedFileType() {
        // Update config to only allow JPEG
        val restrictiveConfig = AppConfiguration(
            security = SecurityConfig(
                adminIdentifiers = emptyList(),
                moderationBlocklist = emptyList(),
                allowedFileTypes = listOf("image/jpeg")
            ),
            thresholds = ThresholdConfig(
                storageQuotaMB = 500,
                maxBatchSize = 100,
                circuitBreakerFailureRate = 0.5,
                hubCapacity = 1000,
                deliveryRadiusKm = 50.0
            ),
            timeouts = TimeoutConfig(
                networkRequestSeconds = 30,
                circuitBreakerOpenSeconds = 30,
                retryDelaysSeconds = listOf(1, 2, 4)
            ),
            features = FeatureConfig(
                enableRecommendations = true,
                enableDisputes = true,
                enableBreedingCompatibility = true
            )
        )
        
        every { configurationManager.get() } returns restrictiveConfig
        
        val tempFile = File.createTempFile("test", ".png")
        tempFile.deleteOnExit()
        
        // Write PNG magic number
        tempFile.writeBytes(byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte()))
        
        val result = validator.validate(tempFile)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "FILE_TYPE_NOT_ALLOWED" })
    }

    @Test
    fun validate_checksFileSize() {
        val tempFile = File.createTempFile("test", ".jpg")
        tempFile.deleteOnExit()
        
        // Write JPEG magic number
        val jpegHeader = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte())
        
        // Create a file larger than 10MB
        val largeData = ByteArray(11 * 1024 * 1024) { 0 }
        System.arraycopy(jpegHeader, 0, largeData, 0, jpegHeader.size)
        tempFile.writeBytes(largeData)
        
        val result = validator.validate(tempFile)
        
        assertTrue(result is InputValidationResult.Invalid)
        val errors = (result as InputValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == "FILE_TOO_LARGE" })
    }
}
