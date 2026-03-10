package com.rio.rostry.ui.transfer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for transfers feature.
 */
sealed class TransferRoute(override val route: String) : NavigationRoute {
    object Details : TransferRoute("transfer/{transferId}") {
        fun createRoute(transferId: String) = "transfer/$transferId"
    }
    object List : TransferRoute("transfer/list")
    object Verify : TransferRoute("transfer/{transferId}/verify") {
        fun createRoute(transferId: String) = "transfer/$transferId/verify"
    }
    object Create : TransferRoute("transfer/create")
    object FarmerTransfers : TransferRoute("farmer/transfers")
    object ComplianceDetails : TransferRoute("compliance/{productId}") {
        fun createRoute(productId: String) = "compliance/$productId"
    }
}

/**
 * Navigation provider for transfers feature.
 */
class TransferNavigationProvider : NavigationProvider {
    override val featureId: String = "transfers"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(TransferRoute.List.route) {
                // TODO: Connect to TransferListScreen
            }

            composable(TransferRoute.Details.route) { backStackEntry ->
                val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
                // TODO: Connect to TransferDetailScreen
            }

            composable(TransferRoute.Verify.route) { backStackEntry ->
                val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
                // TODO: Connect to TransferVerifyScreen
            }

            composable(TransferRoute.Create.route) {
                // TODO: Connect to TransferCreateScreen
            }

            composable(TransferRoute.FarmerTransfers.route) {
                // TODO: Connect to FarmerTransfersScreen
            }

            composable(TransferRoute.ComplianceDetails.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to ComplianceDetailsScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://transfers",
        "https://rostry.app/transfers"
    )
}
