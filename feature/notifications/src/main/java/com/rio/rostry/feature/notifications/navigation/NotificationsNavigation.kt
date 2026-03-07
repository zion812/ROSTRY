package com.rio.rostry.feature.notifications.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.notifications.NotificationsScreen
import com.rio.rostry.ui.notifications.NotificationsUiState

/**
 * Navigation routes for notifications feature.
 */
sealed class NotificationsRoute(override val route: String) : NavigationRoute {
    object Notifications : NotificationsRoute("notifications")
}

/**
 * Navigation provider for notifications feature.
 */
class NotificationsNavigationProvider : NavigationProvider {
    override val featureId: String = "notifications"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(NotificationsRoute.Notifications.route) {
                NotificationsScreen(
                    state = NotificationsUiState(),
                    onOpenMessages = {
                        // TODO
                    },
                    onOpenOrders = {
                        // TODO
                    },
                    onOpenRoute = { route ->
                        // TODO: Handle notification route navigation
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://notifications",
        "https://rostry.app/notifications"
    )
}
