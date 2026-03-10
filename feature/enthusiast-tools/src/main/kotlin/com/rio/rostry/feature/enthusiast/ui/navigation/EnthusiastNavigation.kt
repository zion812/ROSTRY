package com.rio.rostry.feature.enthusiast.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for enthusiast feature.
 */
sealed class EnthusiastRoute(override val route: String) : NavigationRoute {
    object Home : EnthusiastRoute("home/enthusiast")
    object Explore : EnthusiastRoute("enthusiast/explore")
    object Create : EnthusiastRoute("enthusiast/create")
    object Dashboard : EnthusiastRoute("enthusiast/dashboard")
    object Transfers : EnthusiastRoute("enthusiast/transfers")
    object Profile : EnthusiastRoute("enthusiast/profile")
    object EggCollection : EnthusiastRoute("enthusiast/egg_collection")
    object LineageFeed : EnthusiastRoute("enthusiast/lineage_feed")
    object DigitalFarm : EnthusiastRoute("enthusiast/digital_farm")
    object RoosterCard : EnthusiastRoute("enthusiast/rooster_card/{productId}") {
        fun createRoute(productId: String) = "enthusiast/rooster_card/$productId"
    }
    object BreedingCalculator : EnthusiastRoute("enthusiast/breeding_calculator")
    object PerformanceJournal : EnthusiastRoute("enthusiast/performance_journal")
    object AnalyticsGenetics : EnthusiastRoute("enthusiast/analytics/genetics/{birdId}") {
        fun createRoute(birdId: String) = "enthusiast/analytics/genetics/$birdId"
    }
    object PedigreeExport : EnthusiastRoute("enthusiast/pedigree/{birdId}/export") {
        fun createRoute(birdId: String) = "enthusiast/pedigree/$birdId/export"
    }
    object VirtualArena : EnthusiastRoute("enthusiast/virtual_arena")
    object Pedigree : EnthusiastRoute("enthusiast/pedigree/{productId}") {
        fun createRoute(productId: String) = "enthusiast/pedigree/$productId"
    }
    object HallOfFame : EnthusiastRoute("enthusiast/hall_of_fame")
    object TransferCode : EnthusiastRoute("enthusiast/transfer_code/{productId}") {
        fun createRoute(productId: String) = "enthusiast/transfer_code/$productId"
    }
    object ClaimTransfer : EnthusiastRoute("enthusiast/claim_transfer")
    object ShowcaseCard : EnthusiastRoute("enthusiast/showcase/{productId}") {
        fun createRoute(productId: String) = "enthusiast/showcase/$productId"
    }
    object ShowLog : EnthusiastRoute("enthusiast/show_log/{productId}") {
        fun createRoute(productId: String) = "enthusiast/show_log/$productId"
    }
    object ShowRecords : EnthusiastRoute("enthusiast/show_records/{productId}") {
        fun createRoute(productId: String) = "enthusiast/show_records/$productId"
    }
    object ShowEntry : EnthusiastRoute("enthusiast/show_entry/{productId}") {
        fun createRoute(productId: String) = "enthusiast/show_entry/$productId"
    }
    object CompetitionDetail : EnthusiastRoute("enthusiast/virtual_arena/{competitionId}") {
        fun createRoute(competitionId: String) = "enthusiast/virtual_arena/$competitionId"
    }
    object BreedingCalendar : EnthusiastRoute("enthusiast/breeding_calendar")
    object BirdComparison : EnthusiastRoute("enthusiast/bird_comparison")
    object BirdStudio : EnthusiastRoute("enthusiast/bird_studio/{productId}") {
        fun createRoute(productId: String) = "enthusiast/bird_studio/$productId"
    }
    object JudgingMode : EnthusiastRoute("enthusiast/virtual_arena/{competitionId}/judge") {
        fun createRoute(competitionId: String) = "enthusiast/virtual_arena/$competitionId/judge"
    }
    object TraitRecording : EnthusiastRoute("enthusiast/trait_recording/{productId}") {
        fun createRoute(productId: String) = "enthusiast/trait_recording/$productId"
    }
    object BirdProfile : EnthusiastRoute("enthusiast/bird_profile/{productId}") {
        fun createRoute(productId: String) = "enthusiast/bird_profile/$productId"
    }
    object HealthLog : EnthusiastRoute("enthusiast/health_log/{productId}") {
        fun createRoute(productId: String) = "enthusiast/health_log/$productId"
    }
    object LineageExplorer : EnthusiastRoute("enthusiast/lineage_explorer/{productId}") {
        fun createRoute(productId: String) = "enthusiast/lineage_explorer/$productId"
    }
    object MateFinder : EnthusiastRoute("enthusiast/mate_finder/{productId}") {
        fun createRoute(productId: String) = "enthusiast/mate_finder/$productId"
    }
    object BreedingSimulator : EnthusiastRoute("enthusiast/breeding_simulator")
    object FlockAnalytics : EnthusiastRoute("enthusiast/flock_analytics")
    object DigitalTwinDashboard : EnthusiastRoute("enthusiast/digital_twin/{birdId}") {
        fun createRoute(birdId: String) = "enthusiast/digital_twin/$birdId"
    }
    object GrowthTracker : EnthusiastRoute("enthusiast/growth_tracker/{birdId}") {
        fun createRoute(birdId: String) = "enthusiast/growth_tracker/$birdId"
    }
    object MorphologyGrading : EnthusiastRoute("enthusiast/grading/{birdId}") {
        fun createRoute(birdId: String) = "enthusiast/grading/$birdId"
    }
    object Gallery : EnthusiastRoute("enthusiast/gallery")
    object AssetMedia : EnthusiastRoute("enthusiast/asset_media/{assetId}") {
        fun createRoute(assetId: String) = "enthusiast/asset_media/$assetId"
    }
    object MediaViewer : EnthusiastRoute("enthusiast/media_viewer/{mediaId}") {
        fun createRoute(mediaId: String) = "enthusiast/media_viewer/$mediaId"
    }
}

/**
 * Navigation provider for enthusiast feature.
 */
class EnthusiastNavigationProvider : NavigationProvider {
    override val featureId: String = "enthusiast"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // Core enthusiast screens
            composable(EnthusiastRoute.Home.route) {
                // TODO: Connect to EnthusiastHomeScreen
            }

            composable(EnthusiastRoute.Explore.route) {
                // TODO: Connect to EnthusiastExploreScreen
            }

            composable(EnthusiastRoute.Create.route) {
                // TODO: Connect to EnthusiastCreateScreen
            }

            composable(EnthusiastRoute.Dashboard.route) {
                // TODO: Connect to EnthusiastDashboardScreen
            }

            composable(EnthusiastRoute.Transfers.route) {
                // TODO: Connect to EnthusiastTransfersScreen
            }

            composable(EnthusiastRoute.Profile.route) {
                // TODO: Connect to EnthusiastProfileScreen
            }

            // Breeding and genetics
            composable(EnthusiastRoute.BreedingCalculator.route) {
                // TODO: Connect to BreedingCalculatorScreen
            }

            composable(EnthusiastRoute.Pedigree.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to PedigreeScreen
            }

            composable(EnthusiastRoute.BreedingCalendar.route) {
                // TODO: Connect to BreedingCalendarScreen
            }

            // Digital farm and visualization
            composable(EnthusiastRoute.DigitalFarm.route) {
                // TODO: Connect to DigitalFarmScreen
            }

            composable(EnthusiastRoute.RoosterCard.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to RoosterCardScreen
            }

            composable(EnthusiastRoute.ShowcaseCard.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to ShowcaseCardScreen
            }

            // Virtual arena and competitions
            composable(EnthusiastRoute.VirtualArena.route) {
                // TODO: Connect to VirtualArenaScreen
            }

            composable(EnthusiastRoute.CompetitionDetail.route) { backStackEntry ->
                val competitionId = backStackEntry.arguments?.getString("competitionId") ?: ""
                // TODO: Connect to CompetitionDetailScreen
            }

            composable(EnthusiastRoute.JudgingMode.route) { backStackEntry ->
                val competitionId = backStackEntry.arguments?.getString("competitionId") ?: ""
                // TODO: Connect to JudgingModeScreen
            }

            composable(EnthusiastRoute.HallOfFame.route) {
                // TODO: Connect to HallOfFameScreen
            }

            // Analytics and performance
            composable(EnthusiastRoute.AnalyticsGenetics.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                // TODO: Connect to AnalyticsGeneticsScreen
            }

            composable(EnthusiastRoute.PerformanceJournal.route) {
                // TODO: Connect to PerformanceJournalScreen
            }

            composable(EnthusiastRoute.FlockAnalytics.route) {
                // TODO: Connect to FlockAnalyticsScreen
            }

            // Egg and hatching
            composable(EnthusiastRoute.EggCollection.route) {
                // TODO: Connect to EggCollectionScreen
            }

            composable(EnthusiastRoute.LineageFeed.route) {
                // TODO: Connect to LineageFeedScreen
            }

            // Transfer and showcase
            composable(EnthusiastRoute.TransferCode.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to TransferCodeScreen
            }

            composable(EnthusiastRoute.ClaimTransfer.route) {
                // TODO: Connect to ClaimTransferScreen
            }

            // Show records
            composable(EnthusiastRoute.ShowLog.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to ShowLogScreen
            }

            composable(EnthusiastRoute.ShowRecords.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to ShowRecordsScreen
            }

            composable(EnthusiastRoute.ShowEntry.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to ShowEntryScreen
            }

            // Bird Studio and customization
            composable(EnthusiastRoute.BirdStudio.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to BirdStudioScreen
            }

            composable(EnthusiastRoute.BirdComparison.route) {
                // TODO: Connect to BirdComparisonScreen
            }

            // Premium tools
            composable(EnthusiastRoute.TraitRecording.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to TraitRecordingScreen
            }

            composable(EnthusiastRoute.BirdProfile.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to BirdProfileScreen
            }

            composable(EnthusiastRoute.HealthLog.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to HealthLogScreen
            }

            composable(EnthusiastRoute.LineageExplorer.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to LineageExplorerScreen
            }

            composable(EnthusiastRoute.MateFinder.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to MateFinderScreen
            }

            composable(EnthusiastRoute.BreedingSimulator.route) {
                // TODO: Connect to BreedingSimulatorScreen
            }

            // Digital twin
            composable(EnthusiastRoute.DigitalTwinDashboard.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                // TODO: Connect to DigitalTwinDashboardScreen
            }

            composable(EnthusiastRoute.GrowthTracker.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                // TODO: Connect to GrowthTrackerScreen
            }

            composable(EnthusiastRoute.MorphologyGrading.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                // TODO: Connect to MorphologyGradingScreen
            }

            // Gallery
            composable(EnthusiastRoute.Gallery.route) {
                // TODO: Connect to GalleryScreen
            }

            composable(EnthusiastRoute.AssetMedia.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to AssetMediaScreen
            }

            composable(EnthusiastRoute.MediaViewer.route) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
                // TODO: Connect to MediaViewerScreen
            }

            // Export
            composable(EnthusiastRoute.PedigreeExport.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                // TODO: Connect to PedigreeExportScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://enthusiast",
        "https://rostry.app/enthusiast"
    )
}
