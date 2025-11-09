package com.rio.rostry.presentation.auth.phoneauth

import android.app.Activity
import com.rio.rostry.domain.auth.model.AuthResult
import com.rio.rostry.domain.auth.model.PhoneNumber
import com.rio.rostry.domain.auth.model.UserFriendlyError
import com.rio.rostry.domain.auth.model.VerificationId
import com.rio.rostry.domain.auth.usecase.StartPhoneVerificationUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for PhoneAuthViewModel.
 * 
 * Tests:
 * - Phone input validation
 * - Send OTP success
 * - Send OTP error
 * - State management
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PhoneAuthViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var mockUseCase: StartPhoneVerificationUseCase
    private lateinit var viewModel: PhoneAuthViewModel
    private lateinit var mockActivity: Activity
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUseCase = mockk()
        mockActivity = mockk(relaxed = true)
        viewModel = PhoneAuthViewModel(mockUseCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `when phone changed then state updates`() {
        // Act
        viewModel.onPhoneChanged("+919876543210")
        
        // Assert
        assertEquals("+919876543210", viewModel.uiState.value.phoneInput)
        assertEquals("+919876543210", viewModel.uiState.value.phoneE164)
    }
    
    @Test
    fun `when send OTP success then state updates with verification ID`() = runTest {
        // Arrange
        val phoneNumber = PhoneNumber("+919876543210")
        val verificationId = VerificationId("test_vid")
        coEvery { 
            mockUseCase(any(), phoneNumber) 
        } returns AuthResult.Success(verificationId)
        
        viewModel.onPhoneChanged("+919876543210")
        
        // Act
        viewModel.sendOtp(mockActivity)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(verificationId, viewModel.uiState.value.verificationId)
        assertNull(viewModel.uiState.value.error)
        coVerify { mockUseCase(any(), phoneNumber) }
    }
    
    @Test
    fun `when send OTP fails then state updates with error`() = runTest {
        // Arrange
        val phoneNumber = PhoneNumber("+919876543210")
        val error = UserFriendlyError.NetworkError
        coEvery { 
            mockUseCase(any(), phoneNumber) 
        } returns AuthResult.Error(error)
        
        viewModel.onPhoneChanged("+919876543210")
        
        // Act
        viewModel.sendOtp(mockActivity)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.verificationId)
        assertEquals(error.getMessage(), viewModel.uiState.value.error)
    }
    
    @Test
    fun `when phone invalid then shows error`() {
        // Arrange
        viewModel.onPhoneChanged("invalid")
        
        // Act
        viewModel.sendOtp(mockActivity)
        
        // Assert
        assertNotNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `when clear error then error is null`() {
        // Arrange
        viewModel.onPhoneChanged("invalid")
        viewModel.sendOtp(mockActivity)
        
        // Act
        viewModel.clearError()
        
        // Assert
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `when send OTP starts then loading is true`() = runTest {
        // Arrange
        val phoneNumber = PhoneNumber("+919876543210")
        coEvery { 
            mockUseCase(any(), phoneNumber) 
        } coAnswers {
            // Simulate delay
            kotlinx.coroutines.delay(100)
            AuthResult.Success(VerificationId("test"))
        }
        
        viewModel.onPhoneChanged("+919876543210")
        
        // Act
        viewModel.sendOtp(mockActivity)
        
        // Assert - should be loading before completion
        assertTrue(viewModel.uiState.value.isLoading)
        
        // Wait for completion
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
