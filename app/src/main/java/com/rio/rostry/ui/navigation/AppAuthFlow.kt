package com.rio.rostry.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.ui.auth.AuthWelcomeScreen
import com.rio.rostry.ui.auth.AuthWelcomeViewModel
import com.rio.rostry.ui.session.SessionViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun AuthFlow(
    state: SessionViewModel.SessionUiState,
    onAuthenticated: () -> Unit,
    fromGuest: Boolean = false
) {
    val navController = rememberNavController()
    val authVm: AuthViewModel = hiltViewModel()
    val welcomeVm: AuthWelcomeViewModel = hiltViewModel()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }
    }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = com.firebase.ui.auth.FirebaseAuthUIActivityResultContract()
    ) { res ->
        authVm.setFromGuest(fromGuest)
        authVm.handleFirebaseUIResult(res.idpResponse, res.resultCode)
    }

    LaunchedEffect(Unit) {
        authVm.navigation.collectLatest { event ->
            when (event) {
                is AuthViewModel.NavAction.ToHome -> onAuthenticated()
                is AuthViewModel.NavAction.ToUserSetup -> navController.navigate("onboard/user_setup")
                else -> Unit
            }
        }
    }

    LaunchedEffect(Unit) {
        welcomeVm.navigationEvent.collectLatest { event ->
            when (event) {
                is AuthWelcomeViewModel.NavigationEvent.ToGuestHome -> onAuthenticated()
                is AuthWelcomeViewModel.NavigationEvent.ToAuth -> {
                    val providers = listOf(
                        com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder().build(),
                        com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder().build()
                    )
                    val intent = com.firebase.ui.auth.AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .build()
                    launcher.launch(intent)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "auth/welcome",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable(
                route = "auth/welcome",
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                val isLoading by welcomeVm.isLoading.collectAsState()
                AuthWelcomeScreen(
                    onPreviewAsRole = { role -> welcomeVm.startGuestMode(role) },
                    onSignInAsRole = { role -> welcomeVm.startAuthentication(role) },
                    isLoading = isLoading
                )
            }

            composable("onboard/user_setup") {
                com.rio.rostry.ui.onboarding.UserSetupScreen(
                    onRoleSelected = onAuthenticated,
                    onSkip = onAuthenticated
                )
            }

            composable(
                route = "onboard/tour/{role}",
                arguments = listOf(
                    navArgument("role") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val roleStr = backStackEntry.arguments?.getString("role") ?: "GENERAL"
                val role = try {
                    com.rio.rostry.domain.model.UserType.valueOf(roleStr.uppercase())
                } catch (_: Exception) {
                    com.rio.rostry.domain.model.UserType.GENERAL
                }

                com.rio.rostry.ui.onboarding.UserOnboardingTourScreen(
                    userRole = role,
                    onComplete = { onAuthenticated() },
                    onSkip = { onAuthenticated() }
                )
            }

            composable(Routes.PUBLIC_BIRD_LOOKUP) {
                com.rio.rostry.ui.publicaccess.PublicBirdLookupScreen()
            }
        }
    }
}
