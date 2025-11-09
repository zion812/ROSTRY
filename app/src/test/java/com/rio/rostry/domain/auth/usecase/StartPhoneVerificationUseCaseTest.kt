package com.rio.rostry.domain.auth.usecase

import android.app.Activity
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for StartPhoneVerificationUseCase.
 * 
 * Tests:
 * - Success case
 * - Error case
 * - Repository interaction
 */
class StartPhoneVerificationUseCaseTest {
    
    private lateinit var mockRepository: AuthRepository
    private lateinit var useCase: StartPhoneVerificationUseCase
    private lateinit var mockActivity: Activity
    
    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = StartPhoneVerificationUseCase(mockRepository)
        mockActivity = mockk(relaxed = true)
    }
    
    @Test
    fun `when repository returns success then use case returns success`() = runTest {
        // Arrange
        val phoneNumber = PhoneNumber("+919876543210")
        val verificationId = VerificationId("test_verification_id")
        coEvery { 
            mockRepository.startPhoneVerification(mockActivity, phoneNumber) 
        } returns AuthResult.Success(verificationId)
        
        // Act
        val result = useCase(mockActivity, phoneNumber)
        
        // Assert
        assertTrue(result is AuthResult.Success)
        assertEquals(verificationId, (result as AuthResult.Success).data)
        coVerify(exactly = 1) { 
            mockRepository.startPhoneVerification(mockActivity, phoneNumber) 
        }
    }
    
    @Test
    fun `when repository returns error then use case returns error`() = runTest {
        // Arrange
        val phoneNumber = PhoneNumber("+919876543210")
        val error = com.rio.rostry.domain.auth.model.UserFriendlyError.NetworkError
        coEvery { 
            mockRepository.startPhoneVerification(mockActivity, phoneNumber) 
        } returns AuthResult.Error(error)
        
        // Act
        val result = useCase(mockActivity, phoneNumber)
        
        // Assert
        assertTrue(result is AuthResult.Error)
        assertEquals(error, (result as AuthResult.Error).error)
    }
    
    @Test
    fun `when repository returns loading then use case returns loading`() = runTest {
        // Arrange
        val phoneNumber = PhoneNumber("+919876543210")
        coEvery { 
            mockRepository.startPhoneVerification(mockActivity, phoneNumber) 
        } returns AuthResult.Loading
        
        // Act
        val result = useCase(mockActivity, phoneNumber)
        
        // Assert
        assertTrue(result is AuthResult.Loading)
    }
}
