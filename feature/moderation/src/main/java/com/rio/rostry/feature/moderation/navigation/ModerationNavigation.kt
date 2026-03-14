package com.rio.rostry.feature.moderation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.feature.admin.ui.moderation.ModerationQueueScreen

/**
 * Navigation routes for moderation feature.
 */
sealed class ModerationRoute(override val route: String) : NavigationRoute {
    object Queue : ModerationRoute("admin/moderation/queue")
    object Review : ModerationRoute("admin/moderation/review/{contentId}") {
        fun createRoute(contentId: String) = "admin/moderation/review/$contentId"
    }
}

/**
 * Navigation provider for moderation feature.
 * Connects ModerationQueueScreen and ContentReviewScreen.
 */
class ModerationNavigationProvider : NavigationProvider {
    override val featureId: String = "moderation"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // Moderation Queue Screen - main moderation dashboard
            composable(ModerationRoute.Queue.route) {
                ModerationQueueScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Content Review Screen - detail view for reviewing specific content
            composable(
                route = ModerationRoute.Review.route,
                arguments = listOf(
                    navArgument("contentId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
                // TODO: Connect to ContentReviewScreen when implemented
                // For now, navigate back to queue if no contentId
                if (contentId.isBlank()) {
                    navController.popBackStack()
                }
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://admin/moderation",
        "https://rostry.app/admin/moderation"
    )
}