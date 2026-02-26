package com.rio.rostry.domain.validation

import com.rio.rostry.domain.config.ConfigurationManager
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validator for file uploads with magic number validation and Configuration Manager integration.
 * Requirements: 3.7, 5.6
 */
@Singleton
class FileUploadValidator @Inject constructor(
    private val configurationManager: ConfigurationManager
) : InputValidator<File> {
    
    companion object {
        // Magic numbers for file type detection
        private val MAGIC_NUMBERS = mapOf(
            "image/jpeg" to listOf(
                byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
            ),
            "image/png" to listOf(
                byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())
            ),
            "image/webp" to listOf(
                byteArrayOf(0x52.toByte(), 0x49.toByte(), 0x46.toByte(), 0x46.toByte()) // RIFF
            ),
            "video/mp4" to listOf(
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x18.toByte(), 0x66.toByte(), 0x74.toByte(), 0x79.toByte(), 0x70.toByte()),
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x1C.toByte(), 0x66.toByte(), 0x74.toByte(), 0x79.toByte(), 0x70.toByte())
            ),
            "video/3gpp" to listOf(
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x14.toByte(), 0x66.toByte(), 0x74.toByte(), 0x79.toByte(), 0x70.toByte())
            )
        )
        
        private const val MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024L // 10MB
        private const val MAX_VIDEO_SIZE_BYTES = 100 * 1024 * 1024L // 100MB
        private const val MIN_IMAGE_WIDTH = 100
        private const val MIN_IMAGE_HEIGHT = 100
        private const val MAX_IMAGE_WIDTH = 8192
        private const val MAX_IMAGE_HEIGHT = 8192
    }
    
    override fun validate(value: File): InputValidationResult {
        val errors = mutableListOf<InputValidationError>()
        
        // Check file exists
        if (!value.exists()) {
            errors.add(
                InputValidationError(
                    field = "file",
                    message = "File does not exist",
                    code = "FILE_NOT_FOUND"
                )
            )
            return InputValidationResult.Invalid(errors)
        }
        
        // Check file is readable
        if (!value.canRead()) {
            errors.add(
                InputValidationError(
                    field = "file",
                    message = "File is not readable",
                    code = "FILE_NOT_READABLE"
                )
            )
            return InputValidationResult.Invalid(errors)
        }
        
        // Detect file type using magic numbers
        val detectedMimeType = detectMimeType(value)
        if (detectedMimeType == null) {
            errors.add(
                InputValidationError(
                    field = "file",
                    message = "Unable to detect file type or unsupported file format",
                    code = "INVALID_FILE_TYPE"
                )
            )
            return InputValidationResult.Invalid(errors)
        }
        
        // Check against allowed file types from Configuration Manager
        val allowedTypes = configurationManager.get().security.allowedFileTypes
        if (detectedMimeType !in allowedTypes) {
            errors.add(
                InputValidationError(
                    field = "file",
                    message = "File type $detectedMimeType is not allowed. Allowed types: ${allowedTypes.joinToString(", ")}",
                    code = "FILE_TYPE_NOT_ALLOWED"
                )
            )
        }
        
        // Check file size
        val fileSize = value.length()
        val isImage = detectedMimeType.startsWith("image/")
        val isVideo = detectedMimeType.startsWith("video/")
        val maxSize = if (isImage) MAX_IMAGE_SIZE_BYTES else MAX_VIDEO_SIZE_BYTES
        
        if (fileSize > maxSize) {
            errors.add(
                InputValidationError(
                    field = "file",
                    message = "File size ${fileSize / 1024 / 1024}MB exceeds limit ${maxSize / 1024 / 1024}MB",
                    code = "FILE_TOO_LARGE"
                )
            )
        }
        
        // For images, validate dimensions
        if (isImage && errors.isEmpty()) {
            try {
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                android.graphics.BitmapFactory.decodeFile(value.path, options)
                
                val width = options.outWidth
                val height = options.outHeight
                
                if (width < MIN_IMAGE_WIDTH || height < MIN_IMAGE_HEIGHT) {
                    errors.add(
                        InputValidationError(
                            field = "file",
                            message = "Image dimensions ${width}x${height} are too small. Minimum: ${MIN_IMAGE_WIDTH}x${MIN_IMAGE_HEIGHT}",
                            code = "IMAGE_TOO_SMALL"
                        )
                    )
                }
                
                if (width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT) {
                    errors.add(
                        InputValidationError(
                            field = "file",
                            message = "Image dimensions ${width}x${height} are too large. Maximum: ${MAX_IMAGE_WIDTH}x${MAX_IMAGE_HEIGHT}",
                            code = "IMAGE_TOO_LARGE"
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add(
                    InputValidationError(
                        field = "file",
                        message = "Failed to read image dimensions: ${e.message}",
                        code = "IMAGE_READ_ERROR"
                    )
                )
            }
        }
        
        return if (errors.isEmpty()) {
            InputValidationResult.Valid
        } else {
            InputValidationResult.Invalid(errors)
        }
    }
    
    /**
     * Detects MIME type using magic numbers (file signatures).
     * This is more secure than relying on file extensions.
     */
    private fun detectMimeType(file: File): String? {
        return try {
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(12) // Read first 12 bytes for detection
                val bytesRead = fis.read(buffer)
                if (bytesRead < 4) return null
                
                // Check each known magic number
                for ((mimeType, signatures) in MAGIC_NUMBERS) {
                    for (signature in signatures) {
                        if (buffer.startsWith(signature)) {
                            return mimeType
                        }
                    }
                }
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Checks if byte array starts with the given signature.
     */
    private fun ByteArray.startsWith(signature: ByteArray): Boolean {
        if (this.size < signature.size) return false
        for (i in signature.indices) {
            if (this[i] != signature[i]) return false
        }
        return true
    }
}
