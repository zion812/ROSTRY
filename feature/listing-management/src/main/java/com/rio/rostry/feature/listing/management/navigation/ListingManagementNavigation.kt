package com.rio.rostry.feature.listing.management.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

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
 */
class ListingManagementNavigationProvider : NavigationProvider {
    override val featureId: String = "listing-management"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(ListingManagementRoute.Create.route) {
                // TODO: Connect to CreateListingScreen
            }

            composable(ListingManagementRoute.Edit.route) { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
                // TODO: Connect to EditListingScreen
            }

            composable(ListingManagementRoute.MyListings.route) {
                // TODO: Connect to MyListingsScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://marketplace/listings",
        "https://rostry.app/marketplace/listings"
    )
}
