package com.rio.rostry.feature.asset.management.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for asset management feature.
 */
sealed class AssetManagementRoute(override val route: String) : NavigationRoute {
    object List : AssetManagementRoute("farm/assets")
    object Detail : AssetManagementRoute("farm/asset/{assetId}") {
        fun createRoute(assetId: String) = "farm/asset/$assetId"
    }
    object Create : AssetManagementRoute("farm/asset/create")
}

/**
 * Navigation provider for asset management feature.
 */
class AssetManagementNavigationProvider : NavigationProvider {
    override val featureId: String = "asset-management"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(AssetManagementRoute.List.route) {
                // TODO: Connect to FarmAssetListScreen
            }

            composable(AssetManagementRoute.Detail.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to FarmAssetDetailScreen
            }

            composable(AssetManagementRoute.Create.route) {
                // TODO: Connect to CreateAssetScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://farm/assets",
        "https://rostry.app/farm/assets"
    )
}
