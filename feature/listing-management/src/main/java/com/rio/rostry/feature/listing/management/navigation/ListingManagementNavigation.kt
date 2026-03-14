package com.rio.rostry.feature.listing.management.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.feature.listing.management.ListingManagementScreen
import com.rio.rostry.feature.listing.management.ListingManagementViewModel
import com.rio.rostry.feature.farmer.ui.listing.CreateListingScreen

/**
 * Navigation routes for listing management feature.
 */
sealed class ListingManagementRoute(override val route: String) : NavigationRoute {
    object Create : ListingManagementRoute("marketplace/listing/create")
    object Edit : ListingManagementRoute("marketplace/listing/{listingId}/edit") {
        fun createRoute(listingId: String) = "marketplace/listing/$listingId/edit"
    }
    object MyListings : ListingManagementRoute("marketplace/listings/my")
}

/**
 * Navigation provider for listing management feature.
 * Connects CreateListingScreen, EditListingScreen, and MyListingsScreen.
 */
class ListingManagementNavigationProvider : NavigationProvider {
    override val featureId: String = "listing-management"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // My Listings Screen - main listing management screen
            composable(ListingManagementRoute.MyListings.route) {
                ListingManagementScreen(
                    onNavigateToCreate = {
                        navController.navigate(ListingManagementRoute.Create.route)
                    }
                )
            }

            // Create Listing Screen
            composable(ListingManagementRoute.Create.route) {
                CreateListingScreen(
                    assetId = "", // Empty assetId for standalone listing creation
                    onNavigateBack = { navController.popBackStack() },
                    onListingCreated = {
                        navController.popBackStack()
                    }
                )
            }

            // Edit Listing Screen
            composable(
                route = ListingManagementRoute.Edit.route,
                arguments = listOf(
                    navArgument("listingId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
                if (listingId.isNotBlank()) {
                    CreateListingScreen(
                        assetId = listingId,
                        onNavigateBack = { navController.popBackStack() },
                        onListingCreated = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://marketplace/listings",
        "https://rostry.app/marketplace/listings"
    )
}