package com.rio.rostry.feature.enthusiast.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.core.navigation.NavigationProvider

/**
 * Navigation provider for enthusiast tools feature.
 * 
 * Implements enthusiast-related navigation routes for screens including:
 * - Home, Explore, Create, Dashboard, Transfers, Profile
 * - Digital Farm, Breeding Tools (Calculator, Calendar, Simulator)
 * - Performance Journal, Hall of Fame
 * - Virtual Arena, Bird Studio
 * - Premium Toolset (Bird Profile, Lineage Explorer, Mate Finder, Flock Analytics)
 * - Egg Collection, Claim Transfer
 * - Gallery and Media Viewer
 * 
 * All screens have been migrated to feature/enthusiast-tools module (Task 7.1.2 complete)
 */
class EnthusiastToolsNavigationProvider : NavigationProvider {
    override val featureId: String = "enthusiast-tools"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // ============ Core Enthusiast Routes ============
            
            // Enthusiast Home Screen
            composable("home/enthusiast") {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastHomeScreen(
                    onOpenProfile = { navController.navigate("enthusiast/profile") },
                    onOpenAnalytics = { navController.navigate("analytics/enthusiast") },
                    onOpenPerformanceAnalytics = { navController.navigate("analytics/dashboard") },
                    onOpenFinancialAnalytics = { navController.navigate("analytics/farmer") },
                    onOpenTransfers = { navController.navigate("enthusiast/transfers") },
                    onOpenTraceability = { id -> navController.navigate("traceability/$id") },
                    onOpenNotifications = { navController.navigate("notifications") },
                    onVerifyKyc = { navController.navigate("verify/enthusiast/kyc") },
                    onOpenReports = { navController.navigate("reports") },
                    onOpenMonitoringDashboard = { navController.navigate("monitoring/dashboard") },
                    onOpenVaccination = { navController.navigate("monitoring/vaccination") },
                    onOpenMortality = { navController.navigate("monitoring/mortality") },
                    onOpenQuarantine = { navController.navigate("monitoring/quarantine") },
                    onOpenBreeding = { navController.navigate("monitoring/breeding") },
                    onNavigateToAddBird = { navController.navigate("onboard/farm/bird/enthusiast") },
                    onNavigateToAddBatch = { navController.navigate("onboard/farm/batch/enthusiast") },
                    onOpenRoosterCard = { pid -> navController.navigate("enthusiast/rooster_card/$pid") },
                    onOpenBreedingCalculator = { navController.navigate("enthusiast/breeding_calculator") },
                    onOpenPerformanceJournal = { navController.navigate("enthusiast/performance_journal") },
                    onOpenVirtualArena = { navController.navigate("enthusiast/virtual_arena") },
                    onOpenFarmAssets = { navController.navigate("farmer/farm_assets") },
                    onOpenFarmLog = { navController.navigate("monitoring/farm_log") },
                    onNavigateRoute = { route -> navController.navigate(route) },
                    onOpenHallOfFame = { navController.navigate("enthusiast/hall_of_fame") },
                    onOpenDigitalFarm = { navController.navigate("enthusiast/digital_farm") },
                    onOpenGallery = { navController.navigate("enthusiast/gallery") }
                )
            }

            // Enthusiast Explore Screen
            composable("enthusiast/explore") {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastExploreScreen(
                    onOpenProduct = { productId -> navController.navigate("product/$productId") },
                    onOpenEvent = { eventId -> navController.navigate("event/$eventId") },
                    onShare = { _ -> /* share sheet */ Unit },
                    onNavigateBack = { navController.popBackStack() },
                    onOpenGallery = { navController.navigate("enthusiast/gallery") }
                )
            }

            // Enthusiast Create Screen
            composable("enthusiast/create") {
                com.rio.rostry.feature.enthusiast.ui.creation.EnthusiastCreateScreen(
                    onScheduleContent = { _ -> navController.navigate("social/feed") },
                    onStartLive = { navController.navigate("social/feed") },
                    onCreateShowcase = { _ -> navController.navigate("social/feed") },
                    onOpenRoosterCard = { pid -> navController.navigate("enthusiast/rooster_card/$pid") }
                )
            }

            // Enthusiast Dashboard
            composable("enthusiast/dashboard") {
                com.rio.rostry.feature.enthusiast.ui.dashboard.EnthusiastDashboardTabs(
                    onOpenReports = { navController.navigate("reports") },
                    onOpenFeed = { navController.navigate("social/feed") },
                    onOpenTraceability = { id -> navController.navigate("traceability/$id") },
                    navController = navController
                )
            }

            // Enthusiast Transfers Screen
            composable("enthusiast/transfers") {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastTransfersScreen(
                    onOpenTransfer = { id -> navController.navigate("transfer/$id") },
                    onVerifyTransfer = { id -> navController.navigate("transfer/$id") },
                    onCreateTransfer = { navController.navigate("enthusiast/transfer_code") },
                    onOpenTraceability = { id -> navController.navigate("traceability/$id") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Enthusiast Profile Screen
            composable("enthusiast/profile") {
                com.rio.rostry.feature.enthusiast.ui.profile.EnthusiastProfileScreen(
                    onVerifyKyc = { navController.navigate("verify/enthusiast/kyc") },
                    onContactSupport = { /* open support */ },
                    onNavigateToStorageQuota = { navController.navigate("common/storage_quota") },
                    onNavigateToBird = { productId -> navController.navigate("enthusiast/bird_profile/$productId") }
                )
            }

            // ============ Digital Farm & Studio ============

            // Digital Farm - Evolutionary Visuals
            composable("enthusiast/digital_farm") {
                com.rio.rostry.feature.enthusiast.ui.digitalfarm.DigitalFarmScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId -> navController.navigate("product/$productId") },
                    onNavigateToListProduct = { productId ->
                        navController.navigate("farmer/create?prefillProductId=$productId")
                    },
                    onNavigateToLogEggs = { unitId ->
                        navController.navigate("enthusiast/egg_collection")
                    },
                    onNavigateToAddBird = { navController.navigate("onboard/farm/bird") },
                    onNavigateToBirdStudio = { productId ->
                        navController.navigate("enthusiast/bird_studio/$productId")
                    }
                )
            }

            // Bird Studio - Visual Customization
            composable(
                route = "enthusiast/bird_studio/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.digitalfarm.studio.BirdStudioScreen(
                    birdId = productId,
                    onBack = { navController.popBackStack() },
                    onNavigateUp = { navController.popBackStack() }
                )
            }

            // ============ Breeding Tools ============

            // Breeding Calculator (Compatibility Analysis)
            composable("enthusiast/breeding_calculator") {
                com.rio.rostry.feature.enthusiast.ui.breeding.BreedingCompatibilityScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Breeding Calendar
            composable("enthusiast/breeding_calendar") {
                com.rio.rostry.feature.enthusiast.ui.breeding.BreedingCalendarScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Breeding Simulator
            composable("enthusiast/breeding_simulator") {
                com.rio.rostry.feature.enthusiast.ui.breeding.simulator.BreedingSimulatorScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Egg Collection Screen
            composable("enthusiast/egg_collection") {
                com.rio.rostry.feature.enthusiast.ui.breeding.EggCollectionScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ============ Performance & Analytics ============

            // Performance Journal
            composable("enthusiast/performance_journal") {
                com.rio.rostry.feature.enthusiast.ui.journal.PerformanceJournalScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Flock Analytics Dashboard
            composable("enthusiast/flock_analytics") {
                com.rio.rostry.feature.enthusiast.ui.analytics.FlockAnalyticsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBirdProfile = { navController.navigate("enthusiast/bird_profile/$it") }
                )
            }

            // ============ Virtual Arena ============

            // Virtual Arena Dashboard
            composable("enthusiast/virtual_arena") {
                com.rio.rostry.feature.enthusiast.ui.arena.VirtualArenaScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToStatus = { id -> navController.navigate("enthusiast/virtual_arena/$id") }
                )
            }

            // Competition Detail
            composable(
                route = "enthusiast/virtual_arena/{competitionId}",
                arguments = listOf(navArgument("competitionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val competitionId = backStackEntry.arguments?.getString("competitionId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.arena.CompetitionDetailScreen(
                    competitionId = competitionId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEntry = { /* TODO: Implement entry flow */ },
                    onNavigateToJudging = { 
                        navController.navigate("enthusiast/virtual_arena/$competitionId/judge")
                    }
                )
            }

            // Judging Mode
            composable(
                route = "enthusiast/virtual_arena/{competitionId}/judge",
                arguments = listOf(navArgument("competitionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val competitionId = backStackEntry.arguments?.getString("competitionId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.arena.JudgingScreen(
                    competitionId = competitionId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ============ Hall of Fame & Transfers ============

            // Hall of Fame
            composable("enthusiast/hall_of_fame") {
                com.rio.rostry.feature.enthusiast.ui.halloffame.HallOfFameScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onBirdClick = { productId -> navController.navigate("product/$productId") }
                )
            }

            // Claim Transfer
            composable("enthusiast/claim_transfer") {
                com.rio.rostry.feature.enthusiast.ui.transfer.ClaimTransferScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onClaimSuccess = { 
                        navController.popBackStack("home/enthusiast", inclusive = false)
                    }
                )
            }

            // Transfer Code Screen
            composable(
                route = "enthusiast/transfer_code/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.transfer.TransferCodeScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Base transfer code route without argument
            composable("enthusiast/transfer_code") {
                com.rio.rostry.feature.enthusiast.ui.transfer.TransferCodeScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ============ Cards & Showcase ============

            // Rooster Card Generator
            composable(
                route = "enthusiast/rooster_card/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val pid = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.creation.ShowcaseCardGeneratorScreen(
                    birdId = pid,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Showcase Card Preview
            composable(
                route = "enthusiast/showcase/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.showcase.ShowcaseCardPreviewScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ============ Premium Toolset - Phase A ============

            // Bird Profile (Single-Bird Dashboard)
            composable(
                route = "enthusiast/bird_profile/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.lineage.BirdProfileScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTraitRecording = { navController.navigate("enthusiast/trait_recording/$it") },
                    onNavigateToHealthLog = { navController.navigate("enthusiast/health_log/$it") },
                    onNavigateToPedigree = { navController.navigate("enthusiast/pedigree/$it") },
                    onNavigateToLineageExplorer = { navController.navigate("enthusiast/lineage_explorer/$it") },
                    onNavigateToMateFinder = { navController.navigate("enthusiast/mate_finder/$it") }
                )
            }

            // Trait Recording
            composable(
                route = "enthusiast/trait_recording/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.lineage.TraitRecordingScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Health Log
            composable(
                route = "enthusiast/health_log/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.lineage.HealthLogScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ============ Premium Toolset - Phase B ============

            // Lineage Explorer (Interactive Tree)
            composable(
                route = "enthusiast/lineage_explorer/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.lineage.LineageExplorerScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBirdProfile = { navController.navigate("enthusiast/bird_profile/$it") }
                )
            }

            // Lineage Feed
            composable("enthusiast/lineage_feed") {
                com.rio.rostry.feature.enthusiast.ui.EnthusiastLineageFeedScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ============ Premium Toolset - Phase C ============

            // Mate Finder (Mate Recommendation Engine)
            composable(
                route = "enthusiast/mate_finder/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.breeding.MateFinderScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBirdProfile = { navController.navigate("enthusiast/bird_profile/$it") }
                )
            }

            // ============ Show Records & Comparison ============

            // Show Records
            composable(
                route = "enthusiast/show_records/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.showrecords.ShowRecordsScreen(
                    birdId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Show Log (alias for Show Records)
            composable(
                route = "enthusiast/show_log/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.showrecords.ShowRecordsScreen(
                    birdId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Show Entry
            composable(
                route = "enthusiast/show_entry/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.showrecords.ShowEntryScreen(
                    birdId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Bird Comparison Tool
            composable("enthusiast/bird_comparison") {
                com.rio.rostry.feature.enthusiast.ui.comparison.BirdComparisonScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ============ Gallery & Media ============

            // Multimedia Gallery
            composable("enthusiast/gallery") {
                com.rio.rostry.ui.shared.gallery.GalleryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMediaViewer = { mediaId ->
                        navController.navigate("enthusiast/media_viewer/$mediaId")
                    }
                )
            }

            // Asset Media
            composable(
                route = "enthusiast/asset_media/{assetId}",
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                com.rio.rostry.ui.shared.gallery.AssetMediaScreen(
                    assetId = assetId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMediaViewer = { mediaId ->
                        navController.navigate("enthusiast/media_viewer/$mediaId")
                    },
                    onNavigateToUpload = { aId -> /* TODO: Launch System Camera/Gallery Picker */ }
                )
            }

            // Media Viewer
            composable(
                route = "enthusiast/media_viewer/{mediaId}",
                arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
                com.rio.rostry.ui.shared.gallery.MediaViewerScreen(
                    mediaId = mediaId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ============ Digital Twin & Grading ============

            // Digital Twin Dashboard
            composable(
                route = "enthusiast/digital_twin/{birdId}",
                arguments = listOf(navArgument("birdId") { type = NavType.StringType })
            ) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.digitaltwin.DigitalTwinDashboardScreen(
                    birdId = birdId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Growth Tracker
            composable(
                route = "enthusiast/growth_tracker/{birdId}",
                arguments = listOf(navArgument("birdId") { type = NavType.StringType })
            ) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.digitaltwin.GrowthTrackerScreen(
                    birdId = birdId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Morphology Grading
            composable(
                route = "enthusiast/grading/{birdId}",
                arguments = listOf(navArgument("birdId") { type = NavType.StringType })
            ) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.digitalfarm.grading.MorphologyGradingScreen(
                    birdId = birdId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ============ Pedigree & Analytics ============

            // Pedigree Screen
            composable(
                route = "enthusiast/pedigree/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.pedigree.PedigreeScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToExport = { navController.navigate("enthusiast/pedigree/$productId/export") }
                )
            }

            // Pedigree Export
            composable(
                route = "enthusiast/pedigree/{birdId}/export",
                arguments = listOf(navArgument("birdId") { type = NavType.StringType })
            ) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.pedigree.export.PedigreeExportScreen(
                    birdId = birdId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Genetics Analytics
            composable(
                route = "enthusiast/analytics/genetics/{birdId}",
                arguments = listOf(navArgument("birdId") { type = NavType.StringType })
            ) { backStackEntry ->
                val birdId = backStackEntry.arguments?.getString("birdId") ?: ""
                com.rio.rostry.feature.enthusiast.ui.analytics.BloodlineAnalyticsScreen(
                    birdId = birdId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://enthusiast/*",
        "https://rostry.app/enthusiast/*"
    )
}
