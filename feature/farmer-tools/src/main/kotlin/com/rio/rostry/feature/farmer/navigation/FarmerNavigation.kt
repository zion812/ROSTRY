package com.rio.rostry.ui.farmer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for farmer feature.
 */
sealed class FarmerRoute(override val route: String) : NavigationRoute {
    object Home : FarmerRoute("home/farmer")
    object Market : FarmerRoute("farmer/market")
    object Create : FarmerRoute("farmer/create")
    object CreateWithPrefill : FarmerRoute("farmer/create?prefillProductId={prefillProductId}") {
        fun createRoute(prefillProductId: String) = "farmer/create?prefillProductId=$prefillProductId"
    }
    object Community : FarmerRoute("farmer/community")
    object Profile : FarmerRoute("farmer/profile")
    object DigitalFarm : FarmerRoute("farmer/digital_farm")
    object FarmAssets : FarmerRoute("farmer/farm_assets")
    object AssetDetails : FarmerRoute("farmer/asset/{assetId}") {
        fun createRoute(assetId: String) = "farmer/asset/$assetId"
    }
    object CreateAsset : FarmerRoute("farmer/create_asset")
    object CreateListing : FarmerRoute("farmer/create_listing/{assetId}") {
        fun createRoute(assetId: String) = "farmer/create_listing/$assetId"
    }
    object CreateAuction : FarmerRoute("farmer/create_auction/{assetId}") {
        fun createRoute(assetId: String) = "farmer/create_auction/$assetId"
    }
    object Calendar : FarmerRoute("farmer/calendar")
    object FeedHistory : FarmerRoute("farmer/feed_history")
    object Gallery : FarmerRoute("farmer/gallery")
    object AssetMedia : FarmerRoute("farmer/asset_media/{assetId}") {
        fun createRoute(assetId: String) = "farmer/asset_media/$assetId"
    }
    object MediaViewer : FarmerRoute("farmer/media_viewer/{mediaId}") {
        fun createRoute(mediaId: String) = "farmer/media_viewer/$mediaId"
    }
    object PublicProfilePreview : FarmerRoute("farmer/profile/preview")
    object EditProfile : FarmerRoute("farmer/profile/edit")
    object PrivacySettings : FarmerRoute("farmer/profile/privacy")
    object FarmLog : FarmerRoute("monitoring/farm_log")
    object BreedingUnit : FarmerRoute("monitoring/breeding/unit")
}

/**
 * Navigation provider for farmer feature.
 */
class FarmerNavigationProvider : NavigationProvider {
    override val featureId: String = "farmer"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(FarmerRoute.Home.route) {
                com.rio.rostry.feature.farmer.ui.FarmerHomeScreen(
                    onOpenMarket = { navController.navigate(FarmerRoute.Market.route) },
                    onOpenCreate = { navController.navigate(FarmerRoute.Create.route) },
                    onOpenCommunity = { navController.navigate(FarmerRoute.Community.route) },
                    onOpenProfile = { navController.navigate(FarmerRoute.Profile.route) }
                )
            }

            composable(FarmerRoute.Market.route) {
                com.rio.rostry.feature.farmer.ui.FarmerMarketScreen(
                    onBack = { navController.popBackStack() },
                    onOpenListing = { /* navigate to listing */ }
                )
            }

            composable(FarmerRoute.Create.route) {
                com.rio.rostry.feature.farmer.ui.FarmerCreateScreen(
                    onBack = { navController.popBackStack() },
                    onComplete = { navController.popBackStack() }
                )
            }

            composable(FarmerRoute.CreateWithPrefill.route) { backStackEntry ->
                val prefillProductId = backStackEntry.arguments?.getString("prefillProductId")
                com.rio.rostry.feature.farmer.ui.FarmerCreateScreen(
                    prefillProductId = prefillProductId,
                    onBack = { navController.popBackStack() },
                    onComplete = { navController.popBackStack() }
                )
            }

            composable(FarmerRoute.Community.route) {
                com.rio.rostry.feature.farmer.ui.FarmerCommunityScreen(
                    onBack = { navController.popBackStack() },
                    onOpenThread = { /* navigate to thread */ }
                )
            }

            composable(FarmerRoute.Profile.route) {
                com.rio.rostry.feature.farmer.ui.FarmerProfileScreen(
                    onBack = { navController.popBackStack() },
                    onEditProfile = { navController.navigate(FarmerRoute.EditProfile.route) }
                )
            }

            composable(FarmerRoute.DigitalFarm.route) {
                // TODO: Connect to DigitalFarmScreen
            }

            composable(FarmerRoute.FarmAssets.route) {
                // TODO: Connect to FarmAssetListScreen
            }

            composable(FarmerRoute.AssetDetails.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to FarmAssetDetailScreen
            }

            composable(FarmerRoute.CreateAsset.route) {
                // TODO: Connect to CreateAssetScreen
            }

            composable(FarmerRoute.CreateListing.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to CreateListingFromAssetScreen
            }

            composable(FarmerRoute.CreateAuction.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to CreateAuctionScreen
            }

            composable(FarmerRoute.Calendar.route) {
                // TODO: Connect to CalendarScreen
            }

            composable(FarmerRoute.FeedHistory.route) {
                // TODO: Connect to FeedHistoryScreen
            }

            composable(FarmerRoute.Gallery.route) {
                // TODO: Connect to GalleryScreen
            }

            composable(FarmerRoute.AssetMedia.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to AssetMediaScreen
            }

            composable(FarmerRoute.MediaViewer.route) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
                // TODO: Connect to MediaViewerScreen
            }

            composable(FarmerRoute.PublicProfilePreview.route) {
                // TODO: Connect to PublicProfilePreviewScreen
            }

            composable(FarmerRoute.EditProfile.route) {
                // TODO: Connect to EditProfileScreen
            }

            composable(FarmerRoute.PrivacySettings.route) {
                // TODO: Connect to PrivacySettingsScreen
            }

            composable(FarmerRoute.FarmLog.route) {
                // TODO: Connect to FarmLogScreen
            }

            composable(FarmerRoute.BreedingUnit.route) {
                // TODO: Connect to BreedingUnitScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://farmer",
        "https://rostry.app/farmer"
    )
}
