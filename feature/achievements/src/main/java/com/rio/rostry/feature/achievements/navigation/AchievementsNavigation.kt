package com.rio.rostry.feature.achievements.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.gamification.AchievementsScreen

/**
 * Navigation routes for achievements feature.
 */
sealed class AchievementsRoute(override val route: String) : NavigationRoute {
    object Achievements : AchievementsRoute("love/achievements")
}

/**
 * Navigation provider for achievements feature.
 */
class AchievementsNavigationProvider : NavigationProvider {
    override val featureId: String = "achievements"
    
    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(AchievementsRoute.Achievements.route) {
                // TODO: Connect to ViewModel when implementing full navigation
                AchievementsScreen(
                    achievements = emptyList(),
                    totalPoints = 0,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
    
    override fun getDeepLinks(): List<String> = listOf(
        "rostry://love/achievements",
        "https://rostry.app/love/achievements"
    )
}
