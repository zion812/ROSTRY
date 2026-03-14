package com.rio.rostry.ui.transfer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.transfer.TransferCreateScreen
import com.rio.rostry.ui.transfer.TransferVerificationScreen

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
 * 
 * Connects all transfer-related screens with proper navigation callbacks.
 */
class TransferNavigationProvider : NavigationProvider {
    override val featureId: String = "transfers"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(TransferRoute.List.route) {
                // Transfer list screen - placeholder until implemented
                // For now, navigate to farmer transfers
                navController.navigate(TransferRoute.FarmerTransfers.route) {
                    popUpTo(TransferRoute.List.route) { inclusive = true }
                }
            }

            composable(TransferRoute.Details.route) { backStackEntry ->
                val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
                // Transfer details - using verification screen for now
                // TODO: Implement dedicated TransferDetailsScreen
                TransferVerificationScreen(
                    transferId = transferId,
                    onScanProduct = { /* TODO: Navigate to QR scanner */ }
                )
            }

            composable(TransferRoute.Verify.route) { backStackEntry ->
                val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
                TransferVerificationScreen(
                    transferId = transferId,
                    onScanProduct = { /* TODO: Navigate to QR scanner */ }
                )
            }

            composable(TransferRoute.Create.route) {
                TransferCreateScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToQuarantine = { 
                        // TODO: Navigate to quarantine screen
                    },
                    onNavigateToFarmData = {
                        // TODO: Navigate to farm data screen
                    }
                )
            }

            composable(TransferRoute.FarmerTransfers.route) {
                // Farmer transfers list - placeholder
                // TODO: Implement FarmerTransfersScreen with list of farmer's transfers
                androidx.compose.material3.Text("Farmer Transfers - Coming Soon")
            }

            composable(TransferRoute.ComplianceDetails.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // Compliance details - placeholder
                // TODO: Implement ComplianceDetailsScreen showing product compliance status
                androidx.compose.material3.Text("Compliance Details for Product: $productId")
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://transfers",
        "rostry://transfer/{transferId}",
        "rostry://transfer/{transferId}/verify",
        "https://rostry.app/transfers"
    )
}
