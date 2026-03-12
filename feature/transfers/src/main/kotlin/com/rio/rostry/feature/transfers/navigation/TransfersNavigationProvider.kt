package com.rio.rostry.feature.transfers.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.core.navigation.NavigationProvider

/**
 * Navigation provider for transfers feature.
 * Handles asset transfers, verification, and compliance.
 */
class TransfersNavigationProvider : NavigationProvider {
    override val featureId: String = "transfers"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // Transfer routes will be migrated here from AppNavHost
            // Routes to migrate:
            // - Transfers.DETAILS
            // - Transfers.LIST
            // - Transfers.VERIFY
            // - Transfers.CREATE
            // - Transfers.FARMER_TRANSFERS
            // - Transfers.COMPLIANCE_DETAILS
            // - TransferResponseScreen
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://transfer/*",
        "https://rostry.app/transfer/*"
    )
}
