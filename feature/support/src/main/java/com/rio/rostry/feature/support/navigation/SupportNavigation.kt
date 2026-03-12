package com.rio.rostry.feature.support.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.support.HelpScreen

/**
 * Navigation routes for support feature.
 */
sealed class SupportRoute(override val route: String) : NavigationRoute {
    object Help : SupportRoute("love/help")
    object Support : SupportRoute("love/support")
}

/**
 * Navigation provider for support feature.
 */
class SupportNavigationProvider : NavigationProvider {
    override val featureId: String = "support"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(SupportRoute.Help.route) {
                HelpScreen()
            }
            composable(SupportRoute.Support.route) {
                HelpScreen()
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://love/help",
        "rostry://love/support",
        "https://rostry.app/love/help"
    )
}
