package com.rio.rostry.feature.onboarding.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.feature.onboarding.ui.OnboardingScreen
import com.rio.rostry.feature.onboarding.ui.UserSetupScreen
import com.rio.rostry.feature.onboarding.ui.OnboardFarmBirdScreen
import com.rio.rostry.feature.onboarding.ui.OnboardFarmBatchScreen
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for onboarding feature.
 */
sealed class OnboardingRoute(override val route: String) : NavigationRoute {
    object General : OnboardingRoute("onboard/general")
    object Farmer : OnboardingRoute("onboard/farmer")
    object Enthusiast : OnboardingRoute("onboard/enthusiast")
    object UserSetup : OnboardingRoute("onboard/user_setup")
    object FarmBird : OnboardingRoute("onboard/farm/bird")
    object FarmBatch : OnboardingRoute("onboard/farm/batch")
}

/**
 * Navigation provider for onboarding feature.
 */
class OnboardingNavigationProvider : NavigationProvider {
    override val featureId: String = "onboarding"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(OnboardingRoute.General.route) {
                // Keep onboarding navigation compiling even if UI contracts evolve.
                // TODO(modularization): wire real callbacks once the feature UI is stabilized.
                OnboardingScreen(role = null)
            }

            composable(OnboardingRoute.Farmer.route) {
                OnboardFarmBirdScreen()
            }

            composable(OnboardingRoute.Enthusiast.route) {
                OnboardingScreen(role = null)
            }

            composable(OnboardingRoute.UserSetup.route) {
                UserSetupScreen()
            }

            composable(OnboardingRoute.FarmBird.route) {
                OnboardFarmBirdScreen()
            }

            composable(OnboardingRoute.FarmBatch.route) {
                OnboardFarmBatchScreen()
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://onboarding",
        "https://rostry.app/onboarding"
    )
}
