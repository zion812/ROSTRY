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
import com.rio.rostry.utils.normalizeToE164
import com.rio.rostry.utils.formatToE164
import com.rio.rostry.utils.network.FeatureToggles
import com.rio.rostry.utils.analytics.AuthAnalyticsTracker
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
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
import com.firebase.ui.auth.IdpResponse
import com.rio.rostry.ui.auth.needsPhoneVerificationBanner

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

    @Mock
    private lateinit var featureToggles: FeatureToggles

    @Mock
    private lateinit var authAnalytics: AuthAnalyticsTracker

    @Mock
    private lateinit var flowAnalyticsTracker: FlowAnalyticsTracker

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        authViewModel = AuthViewModel(
            authRepository = authRepository,
            userRepository = userRepository,
            sessionManager = sessionManager,
            savedStateHandle = savedStateHandle,
            featureToggles = featureToggles,
            authAnalytics = authAnalytics,
            flowAnalyticsTracker = flowAnalyticsTracker,
            firebaseAuth = firebaseAuth
        )
    }

    // Unit tests for E.164 normalization/formatting

    @Test
    fun `formatToE164 with +91 and 10 digits formats correctly`() {
        val result = formatToE164("+91", "9876543210")
        assertEquals("+919876543210", result)
    }

    @Test
    fun `normalizeToE164 with + prefixed valid returns same`() {
        val result = normalizeToE164("+919876543210")
        assertEquals("+919876543210", result)
    }

    @Test
    fun `formatToE164 with country code 91 and national digits builds E164`() {
        val result = formatToE164("91", "9876543210")
        assertEquals("+919876543210", result)
    }

    @Test
    fun `normalizeToE164 invalid length returns null`() {
        val result = normalizeToE164("123")
        assertNull(result)
    }

    @Test
    fun `normalizeToE164 with letters returns null`() {
        val result = normalizeToE164("abc1234567")
        assertNull(result)
    }

    @Test
    fun `formatToE164 removes spaces and dashes in national number`() {
        val result = formatToE164("+91", "987-654 3210")
        assertEquals("+919876543210", result)
    }

    @Test
    fun `normalizeToE164 with invalid country code returns null`() {
        val result = normalizeToE164("+0123456789")
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
        val user = UserEntity(userId = "test-user-1", userType = UserType.FARMER.name)
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
        val user = UserEntity(userId = "test-user-2", userType = UserType.GENERAL.name)
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

    @Test
    fun `guest upgrade defers phone verification when feature enabled`() = runTest {
        val user = UserEntity(userId = "test-user-3", userType = UserType.FARMER.name, phoneNumber = null)
        `when`(featureToggles.isPhoneVerificationRequired()).thenReturn(true)
        `when`(sessionManager.isGuestSession()).thenReturn(flowOf(true))
        `when`(userRepository.getCurrentUser()).thenReturn(flowOf(Resource.Success(user)))
        
        authViewModel.setFromGuest(true)
        // Simulate FirebaseUI result for guest upgrade
        val response = mock(IdpResponse::class.java)
        `when`(response.providerType).thenReturn("google.com")
        authViewModel.handleFirebaseUIResult(response, Activity.RESULT_OK)
        
        val state = authViewModel.uiState.value
        assertEquals("guest_upgrade", state.pendingPhoneVerificationReason)
        assertTrue(state.needsPhoneVerificationBanner)
    }

    @Test
    fun `new user without phone sets pending verification reason`() = runTest {
        val user = UserEntity(userId = "test-user-4", userType = UserType.FARMER.name, phoneNumber = null)
        `when`(featureToggles.isPhoneVerificationRequired()).thenReturn(true)
        `when`(sessionManager.isGuestSession()).thenReturn(flowOf(false))
        `when`(userRepository.getCurrentUser()).thenReturn(flowOf(Resource.Success(user)))
        
        val response = mock(IdpResponse::class.java)
        `when`(response.providerType).thenReturn("google.com")
        authViewModel.handleFirebaseUIResult(response, Activity.RESULT_OK)
        
        val state = authViewModel.uiState.value
        assertNotNull(state.pendingPhoneVerificationReason)
        assertTrue(state.pendingPhoneVerificationReason!!.startsWith("new_user_"))
    }

    @Test
    fun `existing user with phone has no pending verification`() = runTest {
        val user = UserEntity(userId = "test-user-5", userType = UserType.FARMER.name, phoneNumber = "+919876543210")
        `when`(featureToggles.isPhoneVerificationRequired()).thenReturn(true)
        `when`(sessionManager.isGuestSession()).thenReturn(flowOf(false))
        `when`(userRepository.getCurrentUser()).thenReturn(flowOf(Resource.Success(user)))
        
        val response = mock(IdpResponse::class.java)
        `when`(response.providerType).thenReturn("google.com")
        authViewModel.handleFirebaseUIResult(response, Activity.RESULT_OK)
        
        val state = authViewModel.uiState.value
        assertNull(state.pendingPhoneVerificationReason)
        assertFalse(state.needsPhoneVerificationBanner)
    }
}
