package com.rio.rostry.integration

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.SessionManager
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.ui.auth.AuthViewModel.NavAction
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.normalizeToE164India
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthFlowIntegrationTest {

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        authViewModel = AuthViewModel(authRepository, userRepository, sessionManager, savedStateHandle)
    }

    // Unit tests for normalizeToE164India

    @Test
    fun `normalizeToE164India with 10 digits returns +91 prefixed`() {
        val result = normalizeToE164India("9876543210")
        assertEquals("+919876543210", result)
    }

    @Test
    fun `normalizeToE164India with +91 prefixed valid returns same`() {
        val result = normalizeToE164India("+919876543210")
        assertEquals("+919876543210", result)
    }

    @Test
    fun `normalizeToE164India with 91 prefixed 12 digits returns + prefixed`() {
        val result = normalizeToE164India("919876543210")
        assertEquals("+919876543210", result)
    }

    @Test
    fun `normalizeToE164India invalid length returns null`() {
        val result = normalizeToE164India("123")
        assertNull(result)
    }

    @Test
    fun `normalizeToE164India with letters returns null`() {
        val result = normalizeToE164India("abc1234567")
        assertNull(result)
    }

    @Test
    fun `normalizeToE164India with spaces and dashes normalizes correctly`() {
        val result = normalizeToE164India("987-654 3210")
        assertEquals("+919876543210", result)
    }

    @Test
    fun `normalizeToE164India with invalid prefix returns null`() {
        val result = normalizeToE164India("+929876543210")
        assertNull(result)
    }

    // Integration tests for AuthViewModel

    @Test
    fun `startVerification enforces client-side rate limiting`() = runTest {
        `when`(authRepository.startPhoneVerification(any(Activity::class.java), anyString())).thenReturn(Resource.Success(Unit))

        // First call
        authViewModel.startVerification(mock(Activity::class.java))

        // Rapid second call should be blocked
        authViewModel.startVerification(mock(Activity::class.java))

        val state = authViewModel.uiState.value
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("wait"))
    }

    @Test
    fun `verifyOtpAndSignIn on success navigates to home for farmer`() = runTest {
        val user = UserEntity(userId = "test-user-1", userType = UserType.FARMER)
        `when`(authRepository.verifyOtp(anyString(), anyString())).thenReturn(Resource.Success(Unit))
        `when`(userRepository.getCurrentUser()).thenReturn(flowOf(Resource.Success(user)))
        `when`(sessionManager.markAuthenticated(anyLong(), any(UserType::class.java))).then { }

        val navActions = mutableListOf<NavAction>()
        val job = launch {
            authViewModel.navigation.collect { navActions.add(it) }
        }

        authViewModel.verifyOtpAndSignIn()

        assertEquals(1, navActions.size)
        assertTrue(navActions[0] is NavAction.ToHome)
        assertEquals(UserType.FARMER, (navActions[0] as NavAction.ToHome).userType)

        job.cancel()
    }

    @Test
    fun `verifyOtpAndSignIn on success navigates to user setup for general user`() = runTest {
        val user = UserEntity(userId = "test-user-2", userType = UserType.GENERAL)
        `when`(authRepository.verifyOtp(anyString(), anyString())).thenReturn(Resource.Success(Unit))
        `when`(userRepository.getCurrentUser()).thenReturn(flowOf(Resource.Success(user)))
        `when`(sessionManager.markAuthenticated(anyLong(), any(UserType::class.java))).then { }

        val navActions = mutableListOf<NavAction>()
        val job = launch {
            authViewModel.navigation.collect { navActions.add(it) }
        }

        authViewModel.verifyOtpAndSignIn()

        assertEquals(1, navActions.size)
        assertTrue(navActions[0] is NavAction.ToUserSetup)

        job.cancel()
    }

    @Test
    fun `resendOtp clears otp and starts cooldown`() = runTest {
        `when`(authRepository.resendVerificationCode(any(Activity::class.java))).thenReturn(Resource.Success(Unit))

        authViewModel.onOtpChanged("123456")
        authViewModel.resendOtp(mock(Activity::class.java))

        val state = authViewModel.uiState.value
        assertEquals("", state.otp)
        assertTrue(state.resendCooldownSec > 0)
    }

    // Removed direct instantiation test for AuthRepositoryImpl; its constructor requires Android dependencies.
}
