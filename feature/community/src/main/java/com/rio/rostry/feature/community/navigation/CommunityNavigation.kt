package com.rio.rostry.feature.community.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.feature.community.CommunityScreen

sealed class CommunityRoute(override val route: String) : NavigationRoute {
    object Community : CommunityRoute("community")
}

class CommunityNavigationProvider : NavigationProvider {
    override val featureId: String = "community"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(CommunityRoute.Community.route) {
                CommunityScreen()
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://community",
        "https://rostry.app/community"
    )
}
