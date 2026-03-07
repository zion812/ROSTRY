package com.rio.rostry.feature.expert.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.expert.ExpertBookingScreen

/**
 * Navigation routes for expert booking feature.
 */
sealed class ExpertRoute(override val route: String) : NavigationRoute {
    object ExpertBooking : ExpertRoute("social/expert")
    object ExpertProfile : ExpertRoute("social/expert/{expertId}") {
        fun createRoute(expertId: String) = "social/expert/$expertId"
    }
}

/**
 * Navigation provider for expert booking feature.
 */
class ExpertNavigationProvider : NavigationProvider {
    override val featureId: String = "expert"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(ExpertRoute.ExpertBooking.route) {
                ExpertBookingScreen(
                    bookings = emptyList(),
                    onConfirm = {},
                    onComplete = {},
                    onRequest = { _, _ -> },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(ExpertRoute.ExpertProfile.route) { backStackEntry ->
                val expertId = backStackEntry.arguments?.getString("expertId") ?: ""
                ExpertBookingScreen(
                    bookings = emptyList(),
                    onConfirm = {},
                    onComplete = {},
                    onRequest = { _, _ -> },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://social/expert",
        "https://rostry.app/social/expert"
    )
}
