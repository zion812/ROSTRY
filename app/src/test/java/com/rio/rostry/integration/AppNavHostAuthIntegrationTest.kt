package com.rio.rostry.integration

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.SessionManager
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.ui.navigation.AppNavHost
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.FeatureToggles
import com.rio.rostry.utils.analytics.AuthAnalyticsTracker
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import com.google.firebase.auth.FirebaseUser
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.ui.auth.needsPhoneVerificationBanner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AppNavHostAuthIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

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

    @Test
    fun `AuthViewModel UiState needsPhoneVerificationBanner returns true when pendingPhoneVerificationReason is not null`() = runTest {
        // Set pendingPhoneVerificationReason to a non-null value
        authViewModel.deferPhoneVerification("guest_upgrade")
        
        val state = authViewModel.uiState.value
        assertTrue("needsPhoneVerificationBanner should be true when pendingPhoneVerificationReason is not null", 
            state.needsPhoneVerificationBanner)
        assertEquals("pendingPhoneVerificationReason should be set correctly", 
            "guest_upgrade", state.pendingPhoneVerificationReason)
    }

    @Test
    fun `AuthViewModel UiState needsPhoneVerificationBanner returns false when pendingPhoneVerificationReason is null`() = runTest {
        val state = authViewModel.uiState.value
        assertFalse("needsPhoneVerificationBanner should be false when pendingPhoneVerificationReason is null", 
            state.needsPhoneVerificationBanner)
        assertNull("pendingPhoneVerificationReason should be null by default", 
            state.pendingPhoneVerificationReason)
    }

    @Test
    fun `AuthViewModel updates state correctly when deferring phone verification`() = runTest {
        // Initially, no pending verification
        var state = authViewModel.uiState.value
        assertFalse(state.needsPhoneVerificationBanner)
        assertNull(state.pendingPhoneVerificationReason)

        // Defer phone verification
        authViewModel.deferPhoneVerification("new_user_google")

        // Check that state has been updated
        state = authViewModel.uiState.value
        assertTrue(state.needsPhoneVerificationBanner)
        assertEquals("new_user_google", state.pendingPhoneVerificationReason)
    }
}
