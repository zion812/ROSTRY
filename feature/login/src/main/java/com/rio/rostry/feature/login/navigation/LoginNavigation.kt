package com.rio.rostry.feature.login.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.feature.login.ui.AuthWelcomeScreen
import com.rio.rostry.feature.login.ui.AuthWelcomeViewModel
import com.rio.rostry.feature.login.ui.LoginScreen
import com.rio.rostry.feature.login.ui.LoginViewModel

/**
 * Navigation routes for login feature.
 */
sealed class LoginRoute(override val route: String) : NavigationRoute {
    object Welcome : LoginRoute("login/welcome")
    object Main : LoginRoute("login/main")
}

/**
 * Navigation provider for login feature.
 */
class LoginNavigationProvider : NavigationProvider {
    override val featureId: String = "login"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(LoginRoute.Welcome.route) {
                val viewModel: AuthWelcomeViewModel = hiltViewModel()
                val isLoading by viewModel.isLoading.collectAsState()
                
                AuthWelcomeScreen(
                    onPreviewAsRole = { role -> viewModel.startGuestMode(role) },
                    onSignInAsRole = { role -> 
                        viewModel.selectRole(role)
                        navController.navigate(LoginRoute.Main.route)
                    },
                    isLoading = isLoading
                )
            }

            composable(LoginRoute.Main.route) {
                LoginScreen(
                    onLoginSuccess = {
                        // Success state is handled by SessionViewModel session observation
                    },
                    onNavigateToOnboarding = {
                        navController.navigate("onboard/user_setup")
                    }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://login",
        "https://rostry.app/login"
    )
}
