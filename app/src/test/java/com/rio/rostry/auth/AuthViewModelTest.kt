package com.rio.rostry.auth

import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.presentation.auth.AuthViewModel
import com.rio.rostry.util.Resource
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var authRepository: AuthRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        authViewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `sendVerificationCode should update authState with result`() = runTest {
        val phoneNumber = "+919876543210"
        coEvery { authRepository.sendPhoneVerificationCode(phoneNumber) } returns Resource.Success(Unit)

        authViewModel.sendVerificationCode(phoneNumber)
        testDispatcher.scheduler.advanceUntilIdle()

        val authState = authViewModel.authState.value
        assertTrue(authState is Resource.Success)
    }

    @Test
    fun `verifyPhoneCode should update userState with result`() = runTest {
        val phoneNumber = "+919876543210"
        val code = "123456"
        val user = User(
            id = "123",
            phone = phoneNumber,
            userType = UserType.GENERAL,
            createdAt = java.util.Date(),
            updatedAt = java.util.Date()
        )
        coEvery { authRepository.verifyPhoneCode(phoneNumber, code) } returns Resource.Success(user)

        authViewModel.verifyPhoneCode(phoneNumber, code)
        testDispatcher.scheduler.advanceUntilIdle()

        val userState = authViewModel.userState.value
        assertTrue(userState is Resource.Success)
        assertEquals(user, (userState as Resource.Success).data)
    }

    @Test
    fun `login should update userState with result`() = runTest {
        val phoneNumber = "+919876543210"
        val user = User(
            id = "123",
            phone = phoneNumber,
            userType = UserType.GENERAL,
            createdAt = java.util.Date(),
            updatedAt = java.util.Date()
        )
        coEvery { authRepository.login(phoneNumber) } returns Resource.Success(user)

        authViewModel.login(phoneNumber)
        testDispatcher.scheduler.advanceUntilIdle()

        val userState = authViewModel.userState.value
        assertTrue(userState is Resource.Success)
        assertEquals(user, (userState as Resource.Success).data)
    }

    @Test
    fun `logout should update authState with result`() = runTest {
        coEvery { authRepository.logout() } returns Resource.Success(Unit)

        authViewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        val authState = authViewModel.authState.value
        assertTrue(authState is Resource.Success)
    }
}