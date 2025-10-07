package com.rio.rostry.utils.validation

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.math.*

class VerificationValidationService(
    private val appContext: Context,
    private val userRepository: UserRepository,
    private val exifReader: ExifReader = DefaultExifReader(),
) {
    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String, val field: String? = null) : ValidationResult()
        data class Warning(val message: String) : ValidationResult()
    }

    data class ExifValidationResult(
        val hasGPS: Boolean,
        val latitude: Double?,
        val longitude: Double?,
        val timestampMillis: Long?,
        val isValid: Boolean,
        val errorMessage: String? = null,
    )

    private fun getMimeType(cr: ContentResolver, uri: Uri): String? {
        return cr.getType(uri) ?: run {
            val ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            if (ext != null) MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.lowercase()) else null
        }
    }

    private fun getFileSize(cr: ContentResolver, uri: Uri): Long {
        val cursor = cr.query(uri, arrayOf(android.provider.OpenableColumns.SIZE), null, null, null)
        cursor?.use {
            val idx = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (idx >= 0 && it.moveToFirst()) return it.getLong(idx)
        }
        return -1
    }

    suspend fun validateImageFile(uriString: String, context: Context = appContext): ValidationResult = withContext(Dispatchers.IO) {
        val uri = Uri.parse(uriString)
        val cr = context.contentResolver
        val mime = getMimeType(cr, uri) ?: return@withContext ValidationResult.Error("Unable to determine file type", "file")
        if (mime !in setOf("image/jpeg", "image/png")) return@withContext ValidationResult.Error("Unsupported image format. Use JPEG or PNG.", "file")
        val size = getFileSize(cr, uri)
        if (size < 0) return@withContext ValidationResult.Error("File not found or inaccessible", "file")
        if (size > 5_000_000) return@withContext ValidationResult.Error("Image too large (max 5MB)", "file")
        ValidationResult.Success
    }

    suspend fun validateDocumentFile(uriString: String, context: Context = appContext): ValidationResult = withContext(Dispatchers.IO) {
        val uri = Uri.parse(uriString)
        val cr = context.contentResolver
        val mime = getMimeType(cr, uri) ?: return@withContext ValidationResult.Error("Unable to determine document type", "document")
        val allowed = setOf("application/pdf", "image/jpeg", "image/png")
        if (mime !in allowed) return@withContext ValidationResult.Error("Unsupported document format. Use PDF or image.", "document")
        val size = getFileSize(cr, uri)
        if (size < 0) return@withContext ValidationResult.Error("File not found or inaccessible", "document")
        if (size > 5_000_000) return@withContext ValidationResult.Error("Document too large (max 5MB)", "document")
        ValidationResult.Success
    }

    suspend fun validateFarmPhotoLocation(
        imageUri: String,
        claimedLat: Double?,
        claimedLng: Double?,
        context: Context = appContext,
    ): ExifValidationResult = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(imageUri)
            val input: InputStream? = context.contentResolver.openInputStream(uri)
            if (input == null) return@withContext ExifValidationResult(false, null, null, null, isValid = true, errorMessage = null)
            input.use { stream ->
                val parsed = exifReader.read(stream)
                val now = System.currentTimeMillis()
                val tooOld = parsed.timestampMillis != null && (now - parsed.timestampMillis) > 30L * 24 * 60 * 60 * 1000
                if (!parsed.hasLatLong) {
                    return@withContext ExifValidationResult(
                        hasGPS = false,
                        latitude = null,
                        longitude = null,
                        timestampMillis = parsed.timestampMillis,
                        isValid = !tooOld,
                        errorMessage = if (tooOld) "Photo appears older than 30 days" else null
                    )
                }
                val lat = parsed.latitude!!
                val lng = parsed.longitude!!
                val within = if (claimedLat != null && claimedLng != null) withinRadius(lat, lng, claimedLat, claimedLng, 500.0) else true
                val valid = within && !tooOld
                val msg = when {
                    tooOld -> "Photo appears older than 30 days"
                    !within -> "Photo location doesn't match selected farm location (over 500m)"
                    else -> null
                }
                ExifValidationResult(hasGPS = true, latitude = lat, longitude = lng, timestampMillis = parsed.timestampMillis, isValid = valid, errorMessage = msg)
            }
        } catch (t: Throwable) {
            ExifValidationResult(false, null, null, null, isValid = true, errorMessage = null)
        }
    }

    interface ExifReader {
        data class Parsed(
            val hasLatLong: Boolean,
            val latitude: Double?,
            val longitude: Double?,
            val timestampMillis: Long?,
        )
        fun read(input: InputStream): Parsed
    }

    class DefaultExifReader : ExifReader {
        override fun read(input: InputStream): ExifReader.Parsed {
            val exif = ExifInterface(input)
            val latLong = FloatArray(2)
            val hasLatLong = exif.getLatLong(latLong)
            val ts = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            val timestampMillis = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)?.let {
                exif.dateTimeOriginal
            } ?: exif.dateTime
            val lat = if (hasLatLong) latLong[0].toDouble() else null
            val lng = if (hasLatLong) latLong[1].toDouble() else null
            return ExifReader.Parsed(hasLatLong = hasLatLong, latitude = lat, longitude = lng, timestampMillis = timestampMillis)
        }
    }

    fun validateFarmerSubmission(
        lat: Double?,
        lng: Double?,
        uploadedImages: List<String>,
        uploadedDocuments: List<String>,
    ): ValidationResult {
        if (lat == null || lng == null) return ValidationResult.Error("Please select a valid location", "location")
        if (lat !in -90.0..90.0 || lng !in -180.0..180.0) return ValidationResult.Error("Invalid coordinates", "location")
        if (uploadedImages.isEmpty()) return ValidationResult.Error("Upload at least one farm photo", "images")
        return ValidationResult.Success
    }

    fun validateEnthusiastSubmission(
        uploadedImages: List<String>,
        uploadedDocuments: List<String>,
        uploadedDocTypes: Map<String, String>,
    ): ValidationResult {
        val hasSelfie = uploadedImages.isNotEmpty()
        val types = uploadedDocuments.mapNotNull { uploadedDocTypes[it]?.uppercase() }
        val hasId = types.any { it in setOf("AADHAAR","PAN","DL","PASSPORT") }
        val hasAddress = types.any { it == "ADDRESS_PROOF" }
        if (!hasId) return ValidationResult.Error("Please upload at least one government ID (Aadhaar/PAN/DL/Passport)", "id")
        if (!hasSelfie) return ValidationResult.Error("Please upload a selfie image", "selfie")
        if (!hasAddress) return ValidationResult.Error("Please upload an address proof", "address")
        return ValidationResult.Success
    }

    suspend fun checkDuplicateSubmission(userId: String?): Boolean {
        if (userId.isNullOrEmpty()) return false
        // Fetch once
        val res = userRepository.getUserById(userId).first()
        val user = (res as? Resource.Success<UserEntity?>)?.data
        if (user == null) return false
        if (user.verificationStatus?.name in setOf("PENDING", "VERIFIED")) return true
        val last = user.kycUploadedAt ?: 0L
        val now = System.currentTimeMillis()
        return (now - last) < 24L * 60 * 60 * 1000
    }

    // Haversine distance and radius check
    fun withinRadius(lat1: Double, lon1: Double, lat2: Double, lon2: Double, radiusMeters: Double): Boolean {
        val R = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c
        return distance <= radiusMeters
    }
}
