package com.rio.rostry.integration

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.utils.analytics.AuthAnalyticsTracker
import com.rio.rostry.utils.network.FeatureToggles
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneLinkingFlowTest {

    private lateinit var authRepo: AuthRepository
    private lateinit var userRepo: UserRepository
    private lateinit var featureToggles: FeatureToggles
    private lateinit var analytics: AuthAnalyticsTracker
    private lateinit var firebaseAuth: FirebaseAuth

    private val testDispatcher = StandardTestDispatcher()
    private val scope = TestScope(testDispatcher)

    @Before
    fun setup() {
        authRepo = mockk(relaxed = true)
        userRepo = mockk(relaxed = true)
        featureToggles = mockk(relaxed = true)
        analytics = mockk(relaxed = true)
        firebaseAuth = mockk(relaxed = true)
    }

    @Test
    fun handleFirebaseUIResult_newUserWithoutPhone_setsNeedsPhoneLink() = scope.runTest {
        // given
        every { featureToggles.isPhoneVerificationRequired() } returns true
        val user = mockk<FirebaseUser>()
        val meta = mockk<com.google.firebase.auth.FirebaseUserMetadata>()
        every { meta.creationTimestamp } returns 1000
        every { meta.lastSignInTimestamp } returns 1000
        every { user.metadata } returns meta
        every { user.phoneNumber } returns null
        every { firebaseAuth.currentUser } returns user

        val vm = AuthViewModel(
            authRepository = authRepo,
            userRepository = userRepo,
            sessionManager = mockk(relaxed = true),
            savedStateHandle = androidx.lifecycle.SavedStateHandle(),
            featureToggles = featureToggles,
            authAnalytics = analytics,
            firebaseAuth = firebaseAuth
        )

        // when
        vm.handleFirebaseUIResult(response = null, resultCode = android.app.Activity.RESULT_OK)

        // then
        val state = vm.uiState.value
        assertEquals(true, state.needsPhoneLink)
    }

    @Test
    fun cancelPhoneLinking_signsOut_andClearsFlag() = scope.runTest {
        // given
        every { featureToggles.isPhoneVerificationRequired() } returns true
        val user = mockk<FirebaseUser>(relaxed = true)
        val meta = mockk<com.google.firebase.auth.FirebaseUserMetadata>()
        every { meta.creationTimestamp } returns 1000
        every { meta.lastSignInTimestamp } returns 1000
        every { user.metadata } returns meta
        every { user.phoneNumber } returns null
        every { firebaseAuth.currentUser } returns user

        val vm = AuthViewModel(
            authRepository = authRepo,
            userRepository = userRepo,
            sessionManager = mockk(relaxed = true),
            savedStateHandle = androidx.lifecycle.SavedStateHandle(),
            featureToggles = featureToggles,
            authAnalytics = analytics,
            firebaseAuth = firebaseAuth
        )
        vm.handleFirebaseUIResult(response = null, resultCode = android.app.Activity.RESULT_OK)

        // when
        vm.cancelPhoneLinking()

        // then
        coVerify { authRepo.signOut() }
        assertEquals(false, vm.uiState.value.needsPhoneLink)
    }
}
