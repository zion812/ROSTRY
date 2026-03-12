package com.rio.rostry.feature.leaderboard.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.social.LeaderboardScreen

/**
 * Navigation routes for leaderboard feature.
 */
sealed class LeaderboardRoute(override val route: String) : NavigationRoute {
    object Leaderboard : LeaderboardRoute("social/leaderboard")
}

/**
 * Navigation provider for leaderboard feature.
 */
class LeaderboardNavigationProvider : NavigationProvider {
    override val featureId: String = "leaderboard"
    
    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(LeaderboardRoute.Leaderboard.route) {
                // TODO: Connect to ViewModel when implementing full navigation
                LeaderboardScreen(
                    top = emptyList(),
                    onPeriodSelected = { }
                )
            }
        }
    }
    
    override fun getDeepLinks(): List<String> = listOf(
        "rostry://social/leaderboard",
        "https://rostry.app/social/leaderboard"
    )
}
