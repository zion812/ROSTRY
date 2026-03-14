package com.rio.rostry.feature.admin.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for admin dashboard feature.
 */
sealed class AdminDashboardRoute(override val route: String) : NavigationRoute {
    object Dashboard : AdminDashboardRoute("admin/dashboard")
    object Users : AdminDashboardRoute("admin/users")
    object Metrics : AdminDashboardRoute("admin/metrics")
}

/**
 * Navigation provider for admin dashboard feature.
 */
class AdminDashboardNavigationProvider : NavigationProvider {
    override val featureId: String = "admin-dashboard"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(AdminDashboardRoute.Dashboard.route) {
                // TODO: Connect to AdminDashboardScreen
            }

            composable(AdminDashboardRoute.Users.route) {
                // TODO: Connect to UserManagementScreen
            }

            composable(AdminDashboardRoute.Metrics.route) {
                // TODO: Connect to AdminMetricsScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://admin",
        "https://rostry.app/admin"
    )
}
