package com.rio.rostry.feature.farm.dashboard.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.feature.farm.dashboard.FarmDashboardScreen

/**
 * Navigation routes for farm dashboard feature.
 */
sealed class FarmDashboardRoute(override val route: String) : NavigationRoute {
    object Home : FarmDashboardRoute("home/farmer")
    object Dashboard : FarmDashboardRoute("farm/dashboard")
}

/**
 * Navigation provider for farm dashboard feature.
 */
class FarmDashboardNavigationProvider : NavigationProvider {
    override val featureId: String = "farm-dashboard"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // Main Farmer Home / Dashboard
            composable("home/farmer") {
                FarmDashboardScreen(
                    onOpenAlerts = { navController.navigate("notifications") },
                    onNavigateToAddBird = { navController.navigate("onboard/farm/bird") },
                    onNavigateToAddBatch = { navController.navigate("onboard/farm/batch") },
                    onNavigateRoute = { route -> navController.navigate(route) }
                )
            }

            // Compatibility route
            composable("farm/dashboard") {
                FarmDashboardScreen(
                    onOpenAlerts = { navController.navigate("notifications") },
                    onNavigateToAddBird = { navController.navigate("onboard/farm/bird") },
                    onNavigateToAddBatch = { navController.navigate("onboard/farm/batch") },
                    onNavigateRoute = { route -> navController.navigate(route) }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://farm",
        "https://rostry.app/farm"
    )
}
