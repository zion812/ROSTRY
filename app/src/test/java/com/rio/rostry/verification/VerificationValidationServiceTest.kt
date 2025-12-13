package com.rio.rostry.verification

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.validation.VerificationValidationService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class VerificationValidationServiceTest {

    private val context: Context = mockk(relaxed = true)
    private val contentResolver: ContentResolver = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)

    private fun mockMime(uri: String, mime: String?, sizeBytes: Long = 1024): Unit {
        every { context.contentResolver } returns contentResolver
        every { contentResolver.getType(Uri.parse(uri)) } returns mime
        every { contentResolver.openInputStream(Uri.parse(uri)) } answers {
            ByteArrayInputStream(ByteArray(sizeBytes.toInt()))
        }
    }

    // --- EXIF evaluation tests ---
    private class FakeExifReader(
        private val hasLatLong: Boolean,
        private val lat: Double? = null,
        private val lng: Double? = null,
        private val ts: Long? = null,
    ) : VerificationValidationService.ExifReader {
        override fun read(input: java.io.InputStream): VerificationValidationService.ExifReader.Parsed {
            return VerificationValidationService.ExifReader.Parsed(
                hasLatLong = hasLatLong,
                latitude = lat,
                longitude = lng,
                timestampMillis = ts,
            )
        }
    }

    @Test
    fun exif_noGps_recent_ok() = runBlocking {
        val recentTs = System.currentTimeMillis() - 1_000L
        val service = VerificationValidationService(context, userRepository, FakeExifReader(false, null, null, recentTs))
        val uri = "content://test/photo.jpg"
        every { context.contentResolver } returns contentResolver
        every { contentResolver.openInputStream(Uri.parse(uri)) } returns ByteArrayInputStream(ByteArray(16))
        val res = service.validateFarmPhotoLocation(uri, 12.0, 77.0, context)
        assertTrue(res.isValid)
        assertEquals(false, res.hasGPS)
        assertEquals(null, res.errorMessage)
    }

    @Test
    fun exif_gps_mismatch_over500m_warns() = runBlocking {
        val recentTs = System.currentTimeMillis() - 1_000L
        // Photo at (12.0, 77.0), claimed (13.0, 77.0) -> > 500m
        val service = VerificationValidationService(context, userRepository, FakeExifReader(true, 12.0, 77.0, recentTs))
        val uri = "content://test/photo2.jpg"
        every { context.contentResolver } returns contentResolver
        every { contentResolver.openInputStream(Uri.parse(uri)) } returns ByteArrayInputStream(ByteArray(16))
        val res = service.validateFarmPhotoLocation(uri, 13.0, 77.0, context)
        assertEquals(false, res.isValid)
        assertEquals(true, res.hasGPS)
        assertTrue(res.errorMessage?.contains("over 500m") == true)
    }

    @Test
    fun exif_old_photo_over30days_warns() = runBlocking {
        val oldTs = System.currentTimeMillis() - 40L * 24 * 60 * 60 * 1000
        val service = VerificationValidationService(context, userRepository, FakeExifReader(true, 12.0, 77.0, oldTs))
        val uri = "content://test/photo3.jpg"
        every { context.contentResolver } returns contentResolver
        every { contentResolver.openInputStream(Uri.parse(uri)) } returns ByteArrayInputStream(ByteArray(16))
        val res = service.validateFarmPhotoLocation(uri, 12.0, 77.0, context)
        assertEquals(false, res.isValid)
        assertTrue(res.errorMessage?.contains("older than 30 days") == true)
    }
    // 1) File validation paths
    @Test
    fun validateImage_invalidMime_fails() = runBlocking {
        val service = VerificationValidationService(context, userRepository)
        val uri = "content://test/invalid.txt"
        mockMime(uri, "text/plain")
        val res = service.validateImageFile(uri)
        assertTrue(res is VerificationValidationService.ValidationResult.Error)
    }

    @Test
    fun validateDocument_tooLarge_fails() = runBlocking {
        val service = VerificationValidationService(context, userRepository)
        val uri = "content://test/large.pdf"
        // 6MB
        mockMime(uri, "application/pdf", sizeBytes = 6_000_000)
        val res = service.validateDocumentFile(uri)
        assertTrue(res is VerificationValidationService.ValidationResult.Error)
    }

    @Test
    fun validateImage_valid_ok() = runBlocking {
        val service = VerificationValidationService(context, userRepository)
        val uri = "content://test/photo.jpg"
        mockMime(uri, "image/jpeg", sizeBytes = 400_000)
        val res = service.validateImageFile(uri)
        assertTrue(res is VerificationValidationService.ValidationResult.Success)
    }

    // 3) Submission validation
    @Test
    fun validateFarmer_missingLocation_fails() {
        val service = VerificationValidationService(context, userRepository)
        val res = service.validateFarmerSubmission(null, null, emptyList(), emptyList())
        assertTrue(res is VerificationValidationService.ValidationResult.Error)
    }

    @Test
    fun validateEnthusiast_missingId_fails() {
        val service = VerificationValidationService(context, userRepository)
        val images = listOf("https://firebasestorage.googleapis.com/x.jpg")
        val docs = listOf("https://firebasestorage.googleapis.com/y.pdf")
        val types = mapOf(docs[0] to "ADDRESS_PROOF")
        val res = service.validateEnthusiastSubmission(images, docs, types)
        assertTrue(res is VerificationValidationService.ValidationResult.Error)
        res as VerificationValidationService.ValidationResult.Error
        assertEquals("id", res.field)
    }

    // 4) Duplicate detection
    @Test
    fun duplicate_whenPending_returnsTrue() = runBlocking {
        val service = VerificationValidationService(context, userRepository)
        val user = UserEntity(userId = "u1", verificationStatus = VerificationStatus.PENDING)
        coEvery { userRepository.getUserById("u1") } returns flowOf(Resource.Success(user))
        val dup = service.checkDuplicateSubmission("u1")
        assertEquals(true, dup)
    }

    @Test
    fun duplicate_whenRecentSubmission_returnsTrue() = runBlocking {
        val service = VerificationValidationService(context, userRepository)
        val user = UserEntity(userId = "u2")
        coEvery { userRepository.getUserById("u2") } returns flowOf(Resource.Success(user))
        val dup = service.checkDuplicateSubmission("u2")
        assertEquals(true, dup)
    }

    @Test
    fun duplicate_whenOldOrNone_returnsFalse() = runBlocking {
        val service = VerificationValidationService(context, userRepository)
        val user = UserEntity(userId = "u3")
        coEvery { userRepository.getUserById("u3") } returns flowOf(Resource.Success(user))
        val dup = service.checkDuplicateSubmission("u3")
        assertEquals(false, dup)
    }
}
