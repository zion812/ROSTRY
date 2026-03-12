package com.rio.rostry.feature.moderation.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

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
 */
class ModerationNavigationProvider : NavigationProvider {
    override val featureId: String = "moderation"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(ModerationRoute.Queue.route) {
                // TODO: Connect to ModerationQueueScreen
            }

            composable(ModerationRoute.Review.route) { backStackEntry ->
                val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
                // TODO: Connect to ContentReviewScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://admin/moderation",
        "https://rostry.app/admin/moderation"
    )
}
