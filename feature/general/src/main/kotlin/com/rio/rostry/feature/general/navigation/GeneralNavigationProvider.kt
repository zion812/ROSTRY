package com.rio.rostry.feature.general.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider

/**
 * Navigation provider for general user feature.
 * Handles general user home, market, explore, discover, and cart screens.
 */
class GeneralNavigationProvider : NavigationProvider {
    override val featureId: String = "general"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // General routes will be migrated here from AppNavHost
            // Routes to migrate:
            // - GeneralNav.HOME
            // - GeneralNav.MARKET
            // - GeneralNav.EXPLORE
            // - GeneralNav.DISCOVER
            // - GeneralNav.CREATE
            // - GeneralNav.CART
            // - GeneralNav.PROFILE
            // - HOME_GENERAL
            // - HomeGeneralScreen
            // - GeneralUserScreen
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://general/*",
        "https://rostry.app/general/*"
    )
}
