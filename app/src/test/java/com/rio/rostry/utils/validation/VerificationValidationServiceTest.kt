package com.rio.rostry.utils.validation

import android.content.Context
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

class VerificationValidationServiceTest {

    private val context: Context = Mockito.mock(Context::class.java)
    private val userRepository: UserRepository = Mockito.mock(UserRepository::class.java)

    @Test
    fun validateEnthusiastSubmission_requiresIdSelfieAndAddress() {
        val service = VerificationValidationService(context, userRepository)

        val images = listOf("https://firebasestorage.googleapis.com/selfie1.jpg")
        val docs = listOf(
            "https://firebasestorage.googleapis.com/doc1.pdf",
            "https://firebasestorage.googleapis.com/doc2.pdf",
        )
        val types = mapOf(
            docs[0] to "AADHAAR",
            docs[1] to "ADDRESS_PROOF",
        )

        val result = service.validateEnthusiastSubmission(images, docs, types)
        assertTrue(result is VerificationValidationService.ValidationResult.Success)
    }

    @Test
    fun validateEnthusiastSubmission_missingIdFails() {
        val service = VerificationValidationService(context, userRepository)

        val images = listOf("https://firebasestorage.googleapis.com/selfie1.jpg")
        val docs = listOf("https://firebasestorage.googleapis.com/doc1.pdf")
        val types = mapOf(docs[0] to "ADDRESS_PROOF")

        val result = service.validateEnthusiastSubmission(images, docs, types)
        assertTrue(result is VerificationValidationService.ValidationResult.Error)
        result as VerificationValidationService.ValidationResult.Error
        assertEquals("id", result.field)
    }

    @Test
    fun checkDuplicateSubmission_emptyUser_returnsFalse() = runBlocking {
        val service = VerificationValidationService(context, userRepository)
        val dup = service.checkDuplicateSubmission(null)
        assertEquals(false, dup)
    }
}
