package com.rio.rostry.domain.validation

import androidx.exifinterface.media.ExifInterface
import com.rio.rostry.domain.config.ConfigurationManager
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validator for EXIF data in image verification flows.
 * 
 * Validates that images contain required EXIF metadata and checks
 * against blocklist for tampered or suspicious images.
 * 
 * Requirements: 3.3, 9.4
 */
@Singleton
class ExifDataValidator @Inject constructor(
    private val configurationManager: ConfigurationManager
) : InputValidator<File> {

    override fun validate(value: File): InputValidationResult {
        val errors = mutableListOf<InputValidationError>()
        
        // Check file exists and is readable
        if (!value.exists() || !value.canRead()) {
            errors.add(
                InputValidationError(
                    field = "file",
                    message = "File does not exist or is not readable",
                    code = "FILE_NOT_ACCESSIBLE"
                )
            )
            return InputValidationResult.Invalid(errors)
        }
        
        try {
            val exif = ExifInterface(value.absolutePath)
            
            // Validate that image has EXIF data
            val hasExifData = exif.getAttribute(ExifInterface.TAG_DATETIME) != null ||
                             exif.getAttribute(ExifInterface.TAG_MAKE) != null ||
                             exif.getAttribute(ExifInterface.TAG_MODEL) != null
            
            if (!hasExifData) {
                errors.add(
                    InputValidationError(
                        field = "exif",
                        message = "Image does not contain EXIF metadata. This may indicate tampering.",
                        code = "EXIF_MISSING"
                    )
                )
            }
            
            // Check for GPS coordinates (optional but recommended for verification)
            val latLong = exif.latLong
            val latitude = latLong?.get(0)
            val longitude = latLong?.get(1)
            
            if (latitude != null && longitude != null) {
                // Validate coordinates are within valid ranges
                if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude > 180.0) {
                    errors.add(
                        InputValidationError(
                            field = "gps",
                            message = "GPS coordinates in EXIF data are invalid",
                            code = "INVALID_GPS_COORDINATES"
                        )
                    )
                }
            }
            
            // Check for software/editor tags that might indicate manipulation
            val software = exif.getAttribute(ExifInterface.TAG_SOFTWARE)
            if (software != null) {
                val blocklist = configurationManager.get().security.moderationBlocklist
                val suspiciousKeywords = listOf("photoshop", "gimp", "editor", "manipulate")
                
                val isSuspicious = suspiciousKeywords.any { keyword ->
                    software.lowercase().contains(keyword)
                } || blocklist.any { blocked ->
                    software.lowercase().contains(blocked.lowercase())
                }
                
                if (isSuspicious) {
                    errors.add(
                        InputValidationError(
                            field = "software",
                            message = "Image appears to have been edited with: $software",
                            code = "IMAGE_EDITED",
                        )
                    )
                }
            }
            
            // Validate image orientation
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            if (orientation == ExifInterface.ORIENTATION_UNDEFINED) {
                // This is just a warning, not a hard error
                // Some cameras don't set orientation
            }
            
        } catch (e: Exception) {
            errors.add(
                InputValidationError(
                    field = "exif",
                    message = "Failed to read EXIF data: ${e.message}",
                    code = "EXIF_READ_ERROR"
                )
            )
        }
        
        return if (errors.isEmpty()) {
            InputValidationResult.Valid
        } else {
            InputValidationResult.Invalid(errors)
        }
    }
    
    /**
     * Extracts EXIF data from an image file.
     */
    fun extractExifData(file: File): ExifData? {
        return try {
            val exif = ExifInterface(file.absolutePath)
            ExifData(
                dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME),
                make = exif.getAttribute(ExifInterface.TAG_MAKE),
                model = exif.getAttribute(ExifInterface.TAG_MODEL),
                software = exif.getAttribute(ExifInterface.TAG_SOFTWARE),
                latitude = exif.latLong?.get(0),
                longitude = exif.latLong?.get(1),
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            )
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Extracted EXIF data from an image.
 */
data class ExifData(
    val dateTime: String?,
    val make: String?,
    val model: String?,
    val software: String?,
    val latitude: Double?,
    val longitude: Double?,
    val orientation: Int
)
