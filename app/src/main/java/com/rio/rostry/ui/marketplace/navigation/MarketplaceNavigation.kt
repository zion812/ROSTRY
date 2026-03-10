package com.rio.rostry.ui.marketplace.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for marketplace feature.
 */
sealed class MarketplaceRoute(override val route: String) : NavigationRoute {
    object Browse : MarketplaceRoute("product/market")
    object Explore : MarketplaceRoute("product/explore")
    object ProductDetails : MarketplaceRoute("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    object Traceability : MarketplaceRoute("traceability/{productId}") {
        fun createRoute(productId: String) = "traceability/$productId"
    }
    object FamilyTree : MarketplaceRoute("product/{productId}/family_tree") {
        fun createRoute(productId: String) = "product/$productId/family_tree"
    }
    object LineagePreview : MarketplaceRoute("lineage/{productId}") {
        fun createRoute(productId: String) = "lineage/$productId"
    }
    object Create : MarketplaceRoute("product/create")
    object Cart : MarketplaceRoute("product/cart")
    object Sandbox : MarketplaceRoute("product/sandbox")
    object Auction : MarketplaceRoute("auction/{auctionId}") {
        fun createRoute(auctionId: String) = "auction/$auctionId"
    }
    object CreateDispute : MarketplaceRoute("marketplace/dispute/create/{transferId}/{reportedUserId}") {
        fun createRoute(transferId: String, reportedUserId: String) = 
            "marketplace/dispute/create/$transferId/$reportedUserId"
    }
}

/**
 * Navigation provider for marketplace feature.
 */
class MarketplaceNavigationStubProvider : NavigationProvider {
    override val featureId: String = "marketplace"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(MarketplaceRoute.Browse.route) {
                // TODO: Connect to MarketplaceBrowseScreen
            }

            composable(MarketplaceRoute.Explore.route) {
                // TODO: Connect to ExploreScreen
            }

            composable(MarketplaceRoute.ProductDetails.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to ProductDetailsScreen
            }

            composable(MarketplaceRoute.Traceability.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to TraceabilityScreen
            }

            composable(MarketplaceRoute.FamilyTree.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to FamilyTreeView
            }

            composable(MarketplaceRoute.LineagePreview.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to LineagePreviewScreen
            }

            composable(MarketplaceRoute.Create.route) {
                // TODO: Connect to ProductCreateScreen
            }

            composable(MarketplaceRoute.Cart.route) {
                // TODO: Connect to CartScreen
            }

            composable(MarketplaceRoute.Sandbox.route) {
                // TODO: Connect to MarketplaceSandboxScreen
            }

            composable(MarketplaceRoute.Auction.route) { backStackEntry ->
                val auctionId = backStackEntry.arguments?.getString("auctionId") ?: ""
                // TODO: Connect to AuctionScreen
            }

            composable(MarketplaceRoute.CreateDispute.route) { backStackEntry ->
                val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
                val reportedUserId = backStackEntry.arguments?.getString("reportedUserId") ?: ""
                // TODO: Connect to CreateDisputeScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://marketplace",
        "https://rostry.app/marketplace"
    )
}

