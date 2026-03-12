package com.rio.rostry.feature.farmer.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import android.content.Intent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.feature.farmer.ui.*
import com.rio.rostry.feature.farmer.ui.asset.*
import com.rio.rostry.feature.farmer.ui.calendar.FarmCalendarScreen
import com.rio.rostry.feature.farmer.ui.listing.CreateListingFromAssetScreen
import com.rio.rostry.feature.farmer.viewmodel.*

/**
 * Navigation provider for farmer tools feature.
 * 
 * Implements farmer-related navigation routes for screens that have been migrated
 * to the feature module including:
 * - Home, Market, Create, Community, Profile
 * - Digital Farm, Calendar
 * - Farm Assets Management
 * - Listing Creation
 * 
 * Note: Some routes (Gallery, Media Viewer, Auction, Feed History) remain in AppNavHost
 * as their screens are still in the app module and will be migrated in later tasks.
 */
class FarmerToolsNavigationProvider : NavigationProvider {
    override val featureId: String = "farmer-tools"
    
    /**
     * Local route definitions for farmer tools feature.
     * These routes are owned by this feature module.
     */
    private object Routes {
        const val HOME = "home/farmer"
        const val DIGITAL_FARM = "farmer/digital_farm"
        const val MARKET = "farmer/market"
        const val CREATE = "farmer/create"
        const val CREATE_WITH_PARAMS = "farmer/create?prefillProductId={prefillProductId}&pairId={pairId}"
        const val COMMUNITY = "farmer/community"
        const val PROFILE = "farmer/profile"
        const val CALENDAR = "farmer/calendar"
        const val FARM_ASSETS = "farmer/farm_assets"
        const val ASSET_DETAILS = "farmer/asset/{assetId}"
        const val CREATE_ASSET = "farmer/create_asset"
        const val CREATE_LISTING_FROM_ASSET = "farmer/create_listing/{assetId}"
        const val BIRD_HISTORY = "monitoring/bird_history/{assetId}"
        
        // External routes this feature navigates to
        const val NOTIFICATIONS = "notifications"
        const val ONBOARD_BIRD = "onboard/farm/bird"
        const val ONBOARD_BATCH = "onboard/farm/batch"
        const val PRODUCT_DETAILS = "product/{productId}"
        const val PRODUCT_FAMILY_TREE = "product/{productId}/family_tree"
        const val MONITORING_FARM_LOG = "monitoring/farm_log"
        const val MESSAGES_THREAD = "messages/thread/{threadId}"
        const val ORDER_DETAILS = "order/{orderId}"
        const val ANALYTICS_REPORTS = "analytics/reports"
        const val GENERAL_CART = "home/general/cart"
        const val SCAN_QR = "scan/qr?source=market"
        const val EXPERT_BOOKING = "expert/booking"
        const val LEADERBOARD = "leaderboard"
        const val VERIFY_FARMER_LOCATION = "verify/farmer/location"
        const val MONITORING_VACCINATION = "monitoring/vaccination/{eventId}"
        const val MONITORING_GROWTH = "monitoring/growth/{eventId}"
        const val MONITORING_MORTALITY = "monitoring/mortality/{eventId}"
        const val MONITORING_ACTIVITY = "monitoring/activity/{eventId}"
    }

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // Farmer Home Screen
            composable(Routes.HOME) {
                FarmerHomeScreen(
                    onOpenAlerts = { navController.navigate(Routes.NOTIFICATIONS) },
                    onNavigateToAddBird = { navController.navigate(Routes.ONBOARD_BIRD) },
                    onNavigateToAddBatch = { navController.navigate(Routes.ONBOARD_BATCH) },
                    onNavigateRoute = { route -> navController.navigate(route) }
                )
            }

            // Digital Farm Screen
            composable(Routes.DIGITAL_FARM) {
                val context = LocalContext.current
                DigitalFarmScreen(
                    onBack = { navController.popBackStack() },
                    onManageBird = { productId -> navController.navigate("product/$productId") },
                    onViewLineage = { productId -> navController.navigate("product/$productId/family_tree") },
                    onListForSale = { productId -> navController.navigate("${Routes.CREATE}?prefillProductId=$productId") },
                    onAddBird = { navController.navigate(Routes.ONBOARD_BIRD) },
                    onTimelapse = { navController.navigate(Routes.MONITORING_FARM_LOG) },
                    onShare = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "My ROSTRY Digital Farm")
                            putExtra(Intent.EXTRA_TEXT, "Check out my digital farm on ROSTRY! Tracking my flock usage and performance.")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Farm"))
                    }
                )
            }

            // Farmer Market Screen
            composable(Routes.MARKET) {
                val viewModel: FarmerMarketViewModel = hiltViewModel()
                val state by viewModel.ui.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.navigateToChat.collect { threadId ->
                        navController.navigate("messages/thread/$threadId")
                    }
                }

                FarmerMarketScreen(
                    browse = state.filteredBrowse.ifEmpty { state.browse },
                    mine = state.mine,
                    isLoadingBrowse = state.isLoadingBrowse,
                    isLoadingMine = state.isLoadingMine,
                    metricsRevenue = state.metricsRevenue,
                    metricsOrders = state.metricsOrders,
                    metricsViews = state.metricsViews,
                    selectedTabIndex = state.selectedTabIndex,
                    onSelectTab = { viewModel.setTab(it) },
                    onRefresh = { viewModel.refresh() },
                    verificationStatus = state.verificationStatus,
                    onCreateListing = { navController.navigate(Routes.CREATE) },
                    onEditListing = { listingId -> 
                        navController.navigate("${Routes.CREATE}?prefillProductId=$listingId") 
                    },
                    onBoostListing = { /* TODO */ },
                    onPauseListing = { /* TODO */ },
                    onOpenOrder = { orderId -> navController.navigate("order/$orderId") },
                    onOpenProduct = { productId -> navController.navigate("product/$productId") },
                    onSelectCategoryMeat = { viewModel.selectCategory(FarmerMarketViewModel.CategoryFilter.Meat) },
                    onSelectCategoryAdoption = { viewModel.selectCategory(FarmerMarketViewModel.CategoryFilter.Adoption) },
                    onSelectTraceable = { viewModel.selectTrace(FarmerMarketViewModel.TraceFilter.Traceable) },
                    onSelectNonTraceable = { viewModel.selectTrace(FarmerMarketViewModel.TraceFilter.NonTraceable) },
                    categoryMeatSelected = state.categoryFilter == FarmerMarketViewModel.CategoryFilter.Meat,
                    categoryAdoptionSelected = state.categoryFilter == FarmerMarketViewModel.CategoryFilter.Adoption,
                    traceableSelected = state.traceFilter == FarmerMarketViewModel.TraceFilter.Traceable,
                    nonTraceableSelected = state.traceFilter == FarmerMarketViewModel.TraceFilter.NonTraceable,
                    onApplyPriceBreed = { min, max, breed -> viewModel.applyPriceBreed(min, max, breed) },
                    onApplyDateFilter = { start, end -> viewModel.applyDateFilter(start, end) },
                    onClearDateFilter = { viewModel.clearDateFilter() },
                    startDate = state.startDate,
                    endDate = state.endDate,
                    onScanQr = { navController.navigate(Routes.SCAN_QR) },
                    onOpenPromoteListings = { /* TODO */ },
                    onOpenReports = { navController.navigate(Routes.ANALYTICS_REPORTS) },
                    onOpenCart = { navController.navigate(Routes.GENERAL_CART) },
                    onChat = { sellerId -> viewModel.startChatWithSeller(sellerId) }
                )
            }

            // Farmer Create route with optional prefill arguments
            composable(
                route = Routes.CREATE_WITH_PARAMS,
                arguments = listOf(
                    navArgument("prefillProductId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("pairId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val createVm: FarmerCreateViewModel = hiltViewModel()
                val createState by createVm.ui.collectAsState()
                
                val prefillProductId = backStackEntry.arguments?.getString("prefillProductId")
                val pairId = backStackEntry.arguments?.getString("pairId")

                LaunchedEffect(createState.successProductId) {
                    if (!createState.successProductId.isNullOrBlank()) {
                        navController.navigate(Routes.MARKET) {
                            launchSingleTop = true
                        }
                    }
                }

                FarmerCreateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    prefillProductId = prefillProductId,
                    pairId = pairId
                )
            }

            // Explicit base route without query to avoid matching issues
            composable(Routes.CREATE) {
                FarmerCreateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    prefillProductId = null,
                    pairId = null
                )
            }

            // Farmer Community Screen
            composable(Routes.COMMUNITY) {
                FarmerCommunityScreen(
                    onOpenThread = { threadId -> navController.navigate("messages/thread/$threadId") },
                    onOpenGroupDirectory = { navController.navigate(Routes.LEADERBOARD) },
                    onOpenExpertBooking = { navController.navigate(Routes.EXPERT_BOOKING) },
                    onOpenRegionalNews = { navController.navigate(Routes.LEADERBOARD) }
                )
            }

            // Farmer Profile Screen
            composable(Routes.PROFILE) {
                FarmerProfileScreen(
                    onEditProfile = { navController.navigate("profile") },
                    onManageCertifications = { navController.navigate(Routes.VERIFY_FARMER_LOCATION) },
                    onContactSupport = { /* open support */ }
                )
            }

            // Farm Calendar Screen
            composable(Routes.CALENDAR) {
                FarmCalendarScreen(
                    onNavigateUp = { navController.popBackStack() }
                )
            }

            // Farm Assets List
            composable(Routes.FARM_ASSETS) {
                FarmAssetListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAssetClick = { assetId -> navController.navigate("farmer/asset/$assetId") },
                    onAddAsset = { navController.navigate(Routes.CREATE_ASSET) },
                    onAddBatch = { navController.navigate("onboard/farm/batch/farmer") },
                    onTagBatch = { assetId, qty -> /* Handled by ViewModel */ },
                    onOpenGallery = { navController.navigate("farmer/gallery") }
                )
            }

            // Farm Asset Detail
            composable(
                route = Routes.ASSET_DETAILS,
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId")
                if (!assetId.isNullOrBlank()) {
                    FarmAssetDetailScreen(
                        assetId = assetId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateRoute = { route -> navController.navigate(route) },
                        onCreateListing = { 
                            navController.navigate("farmer/create_listing/$assetId")
                        },
                        onCreateAuction = {
                            navController.navigate("farmer/create_auction/$assetId")
                        },
                        onViewHistory = {
                            navController.navigate("monitoring/bird_history/$assetId")
                        }
                    )
                }
            }

            // Create Asset
            composable(Routes.CREATE_ASSET) {
                FarmerCreateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    prefillProductId = null,
                    pairId = null
                )
            }

            // Create Listing from Asset
            composable(
                route = Routes.CREATE_LISTING_FROM_ASSET,
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId")
                if (!assetId.isNullOrBlank()) {
                    CreateListingFromAssetScreen(
                        assetId = assetId,
                        onNavigateBack = { navController.popBackStack() },
                        onListingCreated = { 
                            navController.popBackStack(Routes.FARM_ASSETS, inclusive = false)
                        }
                    )
                }
            }

            // Bird History Screen
            composable(
                route = Routes.BIRD_HISTORY,
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: return@composable
                BirdHistoryScreen(
                    assetId = assetId,
                    onNavigateBack = { navController.popBackStack() },
                    onEventClick = { eventId, eventType ->
                        when (eventType) {
                            "VACCINATION" -> navController.navigate("monitoring/vaccination/$eventId")
                            "GROWTH" -> navController.navigate("monitoring/growth/$eventId")
                            "MORTALITY" -> navController.navigate("monitoring/mortality/$eventId")
                            else -> navController.navigate("monitoring/activity/$eventId")
                        }
                    }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://farmer/*",
        "https://rostry.app/farmer/*"
    )
}

