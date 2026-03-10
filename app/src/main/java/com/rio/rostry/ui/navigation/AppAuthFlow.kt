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
import com.rio.rostry.ui.session.SessionViewModel
import com.rio.rostry.core.navigation.NavigationRegistry
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun AuthFlow(
    state: SessionViewModel.SessionUiState,
    onAuthenticated: () -> Unit,
    navigationRegistry: NavigationRegistry
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "login/welcome",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Register modularized routes from all features
            navigationRegistry.getProviders().forEach { provider ->
                provider.buildGraph(this, navController)
            }

            // Public lookup route (to be modularized later)
            composable(Routes.PUBLIC_BIRD_LOOKUP) {
                com.rio.rostry.ui.publicaccess.PublicBirdLookupScreen()
            }
        }
    }
}
