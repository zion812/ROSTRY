package com.rio.rostry.feature.social.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.rio.rostry.core.navigation.NavigationProvider

/**
 * Navigation provider for social feed feature.
 * Handles social feed, live broadcasts, stories, discussions, and social profiles.
 */
class SocialNavigationProvider : NavigationProvider {
    override val featureId: String = "social-feed"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // Social routes will be migrated here from AppNavHost
            // Routes to migrate:
            // - Social.FEED
            // - Social.LIVE
            // - Social.STORY_VIEWER
            // - Social.STORY_CREATOR
            // - Social.DISCUSSION_DETAIL
            // - SocialProfileScreen
            // - PublicFarm.PROFILE
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://social/*",
        "rostry://live",
        "https://rostry.app/social/*"
    )
}
