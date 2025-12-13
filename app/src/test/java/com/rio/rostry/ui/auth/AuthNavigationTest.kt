package com.rio.rostry.ui.auth

import androidx.lifecycle.SavedStateHandle
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.SessionManager
import com.rio.rostry.ui.auth.AuthWelcomeViewModel.NavigationEvent
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class AuthNavigationTest {

    private lateinit var viewModel: AuthWelcomeViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    // Mocks
    private val sessionManager: SessionManager = mock()
    private val flowAnalyticsTracker: FlowAnalyticsTracker = mock()
    private val savedStateHandle = SavedStateHandle() // Use real instance for simplicity

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthWelcomeViewModel(
            sessionManager = sessionManager,
            flowAnalyticsTracker = flowAnalyticsTracker,
            savedStateHandle = savedStateHandle
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startAuthentication with Farmer role emits ToAuth event`() = runTest {
        viewModel.startAuthentication(UserType.FARMER)
        
        val event = viewModel.navigationEvent.first()
        assertTrue(event is NavigationEvent.ToAuth)
        assertEquals(UserType.FARMER, (event as NavigationEvent.ToAuth).role)
    }

    @Test
    fun `startGuestMode with Enthusiast role emits ToGuestHome event`() = runTest {
        viewModel.startGuestMode(UserType.ENTHUSIAST)
        
        val event = viewModel.navigationEvent.first()
        assertTrue(event is NavigationEvent.ToGuestHome)
        assertEquals(UserType.ENTHUSIAST, (event as NavigationEvent.ToGuestHome).role)
    }
}
