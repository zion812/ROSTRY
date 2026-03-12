package com.rio.rostry.feature.insights.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.insights.InsightsScreen
import com.rio.rostry.ui.insights.InsightsState

/**
 * Navigation routes for insights feature.
 */
sealed class InsightsRoute(override val route: String) : NavigationRoute {
    object Insights : InsightsRoute("love/insights")
}

/**
 * Navigation provider for insights feature.
 */
class InsightsNavigationProvider : NavigationProvider {
    override val featureId: String = "insights"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(InsightsRoute.Insights.route) {
                InsightsScreen(
                    state = InsightsState(),
                    onRefresh = {},
                    onCategorySelected = {},
                    onNavigateToRoute = { route ->
                        // navigate to appropriate route (handled elsewhere or here if applicable)
                    }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://love/insights",
        "https://rostry.app/love/insights"
    )
}
