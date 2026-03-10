package com.rio.rostry.ui.monitoring.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.ui.common.ErrorScreen
import com.rio.rostry.ui.farmer.FarmLogScreen
import com.rio.rostry.ui.farmer.FarmActivityDetailScreen
import com.rio.rostry.ui.farmer.asset.BirdHistoryScreen
import com.rio.rostry.ui.farmer.documentation.AssetDocumentScreen
import com.rio.rostry.ui.farmer.documentation.FarmDocumentScreen
import com.rio.rostry.ui.farmer.expense.ExpenseLedgerScreen
import com.rio.rostry.feature.analytics.financial.ProfitabilityScreen
import com.rio.rostry.ui.monitoring.*

/**
 * Navigation provider for monitoring feature.
 * Handles farm monitoring, health tracking, breeding, hatching, daily logs, and farm activities.
 * 
 * Migrated from AppNavHost - Task 6.1.3
 * Total routes: 20
 * 
 * NOTE: This provider is in the app module because the screens haven't been migrated
 * to the feature:monitoring module yet. Once screens are migrated, this provider
 * should be moved to feature:monitoring module.
 */
class MonitoringNavigationProvider : NavigationProvider {
    override val featureId: String = "monitoring"
    
    /**
     * Local route definitions for monitoring feature.
     * These routes are owned by this feature module.
     */
    private object Routes {
        const val FARM_LOG = "monitoring/farm_log"
        const val FCR_CALCULATOR = "monitoring/fcr/{assetId}"
        const val EXPENSE_LEDGER = "monitoring/expenses"
        const val PROFITABILITY = "monitoring/profitability"
        const val ASSET_DOCUMENT = "monitoring/document/{assetId}"
        const val FARM_DOCUMENT = "monitoring/farm_document"
        const val VACCINATION_DETAIL = "monitoring/vaccination/{vaccinationId}"
        const val GROWTH_DETAIL = "monitoring/growth/{recordId}"
        const val MORTALITY_DETAIL = "monitoring/mortality/{deathId}"
        const val FARM_ACTIVITY_DETAIL = "monitoring/activity/{activityId}"
        const val BIRD_HISTORY = "monitoring/history/{assetId}"
        const val DAILY_LOG = "monitoring/daily_log"
        const val DAILY_LOG_PRODUCT = "monitoring/daily_log/{productId}"
        const val TASKS = "monitoring/tasks"
        const val PERFORMANCE = "monitoring/performance"
        const val VACCINATION = "monitoring/vaccination"
        const val MORTALITY = "monitoring/mortality"
        const val GROWTH = "monitoring/growth"
        const val BREEDING = "monitoring/breeding"
        const val QUARANTINE = "monitoring/quarantine"
        const val HATCHING = "monitoring/hatching"
        
        // External routes
        const val PRODUCT_DETAILS = "product/{productId}"
        const val ONBOARD_BIRD = "onboard/farm/bird"
        const val ONBOARD_BATCH = "onboard/farm/batch"
    }

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // Farm Log Screen - Comprehensive activity log
            composable(Routes.FARM_LOG) {
                FarmLogScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateRoute = { route -> navController.navigate(route) },
                    onBirdClick = { productId -> navController.navigate("product/$productId") }
                )
            }
            
            // FCR Calculator Screen
            composable(
                route = Routes.FCR_CALCULATOR,
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                if (assetId.isBlank()) {
                    ErrorScreen(
                        message = "Invalid asset ID",
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    FCRCalculatorScreen(
                        assetId = assetId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            // Expense Ledger Screen
            composable(Routes.EXPENSE_LEDGER) {
                ExpenseLedgerScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Profitability Screen
            composable(Routes.PROFITABILITY) {
                ProfitabilityScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Asset Lifecycle Documentation Screen
            composable(
                route = Routes.ASSET_DOCUMENT,
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                if (assetId.isBlank()) {
                    ErrorScreen(
                        message = "Invalid asset ID",
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    AssetDocumentScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            
            // Farm-Wide Documentation Screen
            composable(Routes.FARM_DOCUMENT) {
                FarmDocumentScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Vaccination Detail Screen
            composable(
                route = Routes.VACCINATION_DETAIL,
                arguments = listOf(navArgument("vaccinationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vaccinationId = backStackEntry.arguments?.getString("vaccinationId") ?: ""
                if (vaccinationId.isBlank()) {
                    ErrorScreen(
                        message = "Invalid vaccination ID",
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    VaccinationDetailScreen(
                        vaccinationId = vaccinationId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToProduct = { productId -> navController.navigate("product/$productId") }
                    )
                }
            }
            
            // Growth Record Detail Screen
            composable(
                route = Routes.GROWTH_DETAIL,
                arguments = listOf(navArgument("recordId") { type = NavType.StringType })
            ) { backStackEntry ->
                val recordId = backStackEntry.arguments?.getString("recordId")
                if (recordId.isNullOrBlank()) {
                    ErrorScreen(
                        message = "Invalid record ID",
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    GrowthRecordDetailScreen(
                        recordId = recordId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToProduct = { productId -> navController.navigate("product/$productId") }
                    )
                }
            }
            
            // Mortality Detail Screen
            composable(
                route = Routes.MORTALITY_DETAIL,
                arguments = listOf(navArgument("deathId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deathId = backStackEntry.arguments?.getString("deathId")
                if (deathId.isNullOrBlank()) {
                    ErrorScreen(
                        message = "Invalid death ID",
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    MortalityDetailScreen(
                        deathId = deathId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToProduct = { productId -> navController.navigate("product/$productId") }
                    )
                }
            }
            
            // Farm Activity Detail Screen
            composable(
                route = Routes.FARM_ACTIVITY_DETAIL,
                arguments = listOf(navArgument("activityId") { type = NavType.StringType })
            ) { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId")
                if (activityId.isNullOrBlank()) {
                    ErrorScreen(
                        message = "Invalid activity ID",
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    FarmActivityDetailScreen(
                        activityId = activityId,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToProduct = { productId -> navController.navigate("product/$productId") }
                    )
                }
            }
            
            // Bird History Screen - Unified timeline for a bird/batch
            composable(
                route = Routes.BIRD_HISTORY,
                arguments = listOf(navArgument("assetId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId")
                if (assetId.isNullOrBlank()) {
                    ErrorScreen(
                        message = "Invalid asset ID",
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    BirdHistoryScreen(
                        assetId = assetId,
                        onNavigateBack = { navController.popBackStack() },
                        onEventClick = { eventId, eventType ->
                            // Navigate to appropriate event detail screen based on type
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
            
            // Daily Log Screen (general entry point)
            composable(Routes.DAILY_LOG) {
                DailyLogScreen(
                    onNavigateBack = { navController.popBackStack() },
                    productId = null,
                    onNavigateToAddBird = { navController.navigate(Routes.ONBOARD_BIRD) },
                    onNavigateToAddBatch = { navController.navigate(Routes.ONBOARD_BATCH) }
                )
            }
            
            // Daily Log Screen for specific product
            composable(
                route = Routes.DAILY_LOG_PRODUCT,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                DailyLogScreen(
                    onNavigateBack = { navController.popBackStack() },
                    productId = productId.ifBlank { null },
                    onNavigateToAddBird = { navController.navigate(Routes.ONBOARD_BIRD) },
                    onNavigateToAddBatch = { navController.navigate(Routes.ONBOARD_BATCH) }
                )
            }
            
            // Tasks Screen
            composable(Routes.TASKS) {
                TasksScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId -> navController.navigate("product/$productId") }
                )
            }
            
            // Farm Performance Screen
            composable(Routes.PERFORMANCE) {
                FarmPerformanceScreen()
            }
            
            // Vaccination Schedule Screen
            composable(Routes.VACCINATION) {
                VaccinationScheduleScreen(
                    onListProduct = { productId -> navController.navigate("product/$productId") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Mortality Tracking Screen
            composable(Routes.MORTALITY) {
                MortalityTrackingScreen(
                    productId = null
                )
            }
            
            // Growth Tracking Screen
            composable(Routes.GROWTH) {
                GrowthTrackingScreen(
                    productId = "",
                    onListProduct = { productId -> navController.navigate("product/$productId") }
                )
            }
            
            // Breeding Management Screen
            composable(Routes.BREEDING) {
                BreedingManagementScreen(
                    onBack = { navController.popBackStack() },
                    onListProduct = { productId, _ -> navController.navigate("product/$productId") }
                )
            }
            
            // Quarantine Management Screen
            composable(Routes.QUARANTINE) {
                QuarantineManagementScreen(
                    productId = ""
                )
            }
            
            // Hatching Process Screen
            composable(Routes.HATCHING) {
                HatchingProcessScreen()
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://monitoring/*",
        "rostry://hatching",
        "rostry://hatching/*",
        "https://rostry.app/monitoring/*"
    )
}
