package com.rio.rostry.feature.feedback.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.feedback.FeedbackScreen
import com.rio.rostry.ui.feedback.FeedbackUiState

/**
 * Navigation routes for feedback feature.
 */
sealed class FeedbackRoute(override val route: String) : NavigationRoute {
    object Feedback : FeedbackRoute("love/feedback")
}

/**
 * Navigation provider for feedback feature.
 */
class FeedbackNavigationProvider : NavigationProvider {
    override val featureId: String = "feedback"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(FeedbackRoute.Feedback.route) {
                FeedbackScreen(
                    uiState = FeedbackUiState(),
                    onContentChange = {},
                    onTypeChange = {},
                    onSubmit = {},
                    onErrorConsumed = {},
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://love/feedback",
        "https://rostry.app/love/feedback"
    )
}
