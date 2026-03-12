package com.rio.rostry.feature.farm.profile.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for farm profile feature.
 */
sealed class FarmProfileRoute(override val route: String) : NavigationRoute {
    object Profile : FarmProfileRoute("farm/profile")
    object Edit : FarmProfileRoute("farm/profile/edit")
    object Verification : FarmProfileRoute("farm/verification")
}

/**
 * Navigation provider for farm profile feature.
 */
class FarmProfileNavigationProvider : NavigationProvider {
    override val featureId: String = "farm-profile"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(FarmProfileRoute.Profile.route) {
                // TODO: Connect to FarmProfileScreen
            }

            composable(FarmProfileRoute.Edit.route) {
                // TODO: Connect to EditFarmProfileScreen
            }

            composable(FarmProfileRoute.Verification.route) {
                // TODO: Connect to FarmVerificationScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://farm/profile",
        "https://rostry.app/farm/profile"
    )
}
