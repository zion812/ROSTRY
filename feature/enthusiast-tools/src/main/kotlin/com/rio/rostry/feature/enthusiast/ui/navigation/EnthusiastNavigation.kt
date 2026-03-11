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
                com.rio.rostry.feature.enthusiast.ui.EnthusiastHomeScreen(
                    onOpenExplore = { navController.navigate(EnthusiastRoute.Explore.route) },
                    onOpenCreate = { navController.navigate(EnthusiastRoute.Create.route) },
                    onOpenDashboard = { navController.navigate(EnthusiastRoute.Dashboard.route) },
                    onOpenTransfers = { navController.navigate(EnthusiastRoute.Transfers.route) },
                    onOpenProfile = { navController.navigate(EnthusiastRoute.Profile.route) }
                )
            }

            composable(EnthusiastRoute.Explore.route) {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastExploreScreen(
                    onBack = { navController.popBackStack() },
                    onShare = { /* handle share */ },
                    onOpenDiscussion = { /* navigate to community */ }
                )
            }

            composable(EnthusiastRoute.Create.route) {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastCreateScreen(
                    onScheduleContent = { /* schedule */ },
                    onStartLive = { /* start live */ },
                    onCreateShowcase = { /* create showcase */ },
                    onOpenRoosterCard = { productId ->
                        navController.navigate(EnthusiastRoute.RoosterCard.createRoute(productId))
                    }
                )
            }

            composable(EnthusiastRoute.Dashboard.route) {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastDashboardHost(
                    onOpenReports = { /* navigate to reports */ },
                    onOpenFeed = { navController.navigate(EnthusiastRoute.LineageFeed.route) },
                    onOpenTraceability = { productId ->
                        navController.navigate(EnthusiastRoute.LineageExplorer.createRoute(productId))
                    }
                )
            }

            composable(EnthusiastRoute.Transfers.route) {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastTransfersScreen(
                    onBack = { navController.popBackStack() },
                    onInitiateTransfer = { productId ->
                        navController.navigate(EnthusiastRoute.TransferCode.createRoute(productId))
                    },
                    onClaimTransfer = { navController.navigate(EnthusiastRoute.ClaimTransfer.route) }
                )
            }

            composable(EnthusiastRoute.Profile.route) {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastProfileScreen(
                    onBack = { navController.popBackStack() },
                    onEditProfile = { /* navigate to edit profile */ },
                    onOpenSettings = { /* navigate to settings */ }
                )
            }

            // Breeding and genetics
            composable(EnthusiastRoute.BreedingCalculator.route) {
                com.rio.rostry.feature.enthusiast.ui.breeding.BreedingCalculatorScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.Pedigree.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.pedigree.PedigreeScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onNavigateToExport = { navController.navigate(EnthusiastRoute.PedigreeExport.createRoute(productId)) }
                )
            }

            composable(EnthusiastRoute.BreedingCalendar.route) {
                com.rio.rostry.feature.enthusiast.ui.breeding.BreedingCalendarScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Digital farm and visualization
            composable(EnthusiastRoute.DigitalFarm.route) {
                com.rio.rostry.feature.enthusiast.ui.digitalfarm.DigitalFarmScreen(
                    onBack = { navController.popBackStack() },
                    onOpenBirdDetail = { productId ->
                        navController.navigate(EnthusiastRoute.BirdProfile.createRoute(productId))
                    }
                )
            }

            composable(EnthusiastRoute.RoosterCard.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.cards.RoosterCardScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.ShowcaseCard.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.showcase.ShowcaseCardPreviewScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            // Virtual arena and competitions
            composable(EnthusiastRoute.VirtualArena.route) {
                com.rio.rostry.feature.enthusiast.ui.arena.VirtualArenaScreen(
                    onBack = { navController.popBackStack() },
                    onOpenCompetition = { competitionId ->
                        navController.navigate(EnthusiastRoute.CompetitionDetail.createRoute(competitionId))
                    }
                )
            }

            composable(EnthusiastRoute.CompetitionDetail.route) { backStackEntry ->
                val competitionId = backStackEntry.arguments?.getString("competitionId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.arena.CompetitionDetailScreen(
                    competitionId = competitionId,
                    onNavigateBack = { navController.popBackStack() },
                    onStartJudging = { navController.navigate(EnthusiastRoute.JudgingMode.createRoute(competitionId)) }
                )
            }

            composable(EnthusiastRoute.JudgingMode.route) { backStackEntry ->
                val competitionId = backStackEntry.arguments?.getString("competitionId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.arena.JudgingScreen(
                    competitionId = competitionId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.HallOfFame.route) {
                com.rio.rostry.feature.enthusiast.ui.halloffame.HallOfFameScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Analytics and performance
            composable(EnthusiastRoute.AnalyticsGenetics.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.analytics.BloodlineAnalyticsScreen(
                    birdId = birdId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.PerformanceJournal.route) {
                com.rio.rostry.feature.enthusiast.ui.journal.PerformanceJournalScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.FlockAnalytics.route) {
                com.rio.rostry.feature.enthusiast.ui.analytics.FlockAnalyticsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Egg and hatching
            composable(EnthusiastRoute.EggCollection.route) {
                com.rio.rostry.feature.enthusiast.ui.breeding.EggCollectionScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.LineageFeed.route) {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastLineageFeedScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Transfer and showcase
            composable(EnthusiastRoute.TransferCode.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.transfer.TransferCodeScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.ClaimTransfer.route) {
                com.rio.rostry.feature.enthusiast.ui.transfer.ClaimTransferScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Show records
            composable(EnthusiastRoute.ShowLog.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.showrecords.ShowRecordsScreen(
                    birdId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.ShowRecords.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.showrecords.ShowRecordsScreen(
                    birdId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.ShowEntry.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.showrecords.ShowEntryScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            // Bird Studio and customization
            composable(EnthusiastRoute.BirdStudio.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.digitalfarm.studio.BirdStudioScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.BirdComparison.route) {
                com.rio.rostry.feature.enthusiast.ui.comparison.BirdComparisonScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Premium tools
            composable(EnthusiastRoute.TraitRecording.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.lineage.TraitRecordingScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.BirdProfile.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.lineage.BirdProfileScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.HealthLog.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.lineage.HealthLogScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.LineageExplorer.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.lineage.LineageExplorerScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.MateFinder.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.breeding.MateFinderScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.BreedingSimulator.route) {
                com.rio.rostry.feature.enthusiast.ui.breeding.simulator.BreedingSimulatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Digital twin
            composable(EnthusiastRoute.DigitalTwinDashboard.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.digitaltwin.DigitalTwinDashboardScreen(
                    birdId = birdId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.GrowthTracker.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.digitaltwin.GrowthTrackerScreen(
                    birdId = birdId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.MorphologyGrading.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.digitalfarm.grading.MorphologyGradingScreen(
                    birdId = birdId,
                    onBack = { navController.popBackStack() }
                )
            }

            // Gallery
            composable(EnthusiastRoute.Gallery.route) {
                com.rio.rostry.ui.shared.gallery.GalleryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.AssetMedia.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                com.rio.rostry.ui.shared.gallery.AssetMediaScreen(
                    assetId = assetId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(EnthusiastRoute.MediaViewer.route) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
                com.rio.rostry.ui.shared.gallery.MediaViewerScreen(
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
            }

            // Export
            composable(EnthusiastRoute.PedigreeExport.route) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.pedigree.export.PedigreeExportScreen(
                    birdId = birdId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://enthusiast",
        "https://rostry.app/enthusiast"
    )
}
