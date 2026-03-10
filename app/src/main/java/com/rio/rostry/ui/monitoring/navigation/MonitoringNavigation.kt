package com.rio.rostry.ui.monitoring.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for monitoring feature.
 */
sealed class MonitoringRoute(override val route: String) : NavigationRoute {
    object Dashboard : MonitoringRoute("monitoring/dashboard")
    object Vaccination : MonitoringRoute("monitoring/vaccination")
    object Mortality : MonitoringRoute("monitoring/mortality")
    object Quarantine : MonitoringRoute("monitoring/quarantine")
    object Breeding : MonitoringRoute("monitoring/breeding")
    object BreedingUnit : MonitoringRoute("monitoring/breeding/unit")
    object BreedingPerformance : MonitoringRoute("monitoring/breeding/performance")
    object Growth : MonitoringRoute("monitoring/growth")
    object Hatching : MonitoringRoute("monitoring/hatching")
    object HatchingBatch : MonitoringRoute("hatching/batch/{batchId}") {
        fun createRoute(batchId: String) = "hatching/batch/$batchId"
    }
    object Performance : MonitoringRoute("monitoring/performance")
    object DailyLog : MonitoringRoute("monitoring/daily_log")
    object DailyLogProduct : MonitoringRoute("monitoring/daily_log/{productId}") {
        fun createRoute(productId: String) = "monitoring/daily_log/$productId"
    }
    object Tasks : MonitoringRoute("monitoring/tasks")
    object FarmLog : MonitoringRoute("monitoring/farm_log")
    object VaccinationDetail : MonitoringRoute("monitoring/vaccination/{vaccinationId}") {
        fun createRoute(vaccinationId: String) = "monitoring/vaccination/$vaccinationId"
    }
    object GrowthDetail : MonitoringRoute("monitoring/growth/{recordId}") {
        fun createRoute(recordId: String) = "monitoring/growth/$recordId"
    }
    object MortalityDetail : MonitoringRoute("monitoring/mortality/{deathId}") {
        fun createRoute(deathId: String) = "monitoring/mortality/$deathId"
    }
    object FarmActivityDetail : MonitoringRoute("monitoring/activity/{activityId}") {
        fun createRoute(activityId: String) = "monitoring/activity/$activityId"
    }
    object BirdHistory : MonitoringRoute("monitoring/history/{assetId}") {
        fun createRoute(assetId: String) = "monitoring/history/$assetId"
    }
    object FcrCalculator : MonitoringRoute("monitoring/fcr/{assetId}") {
        fun createRoute(assetId: String) = "monitoring/fcr/$assetId"
    }
    object ExpenseLedger : MonitoringRoute("monitoring/expenses")
    object Profitability : MonitoringRoute("monitoring/profitability")
    object AssetDocument : MonitoringRoute("monitoring/document/{assetId}") {
        fun createRoute(assetId: String) = "monitoring/document/$assetId"
    }
    object FarmDocument : MonitoringRoute("monitoring/farm_document")
}

/**
 * Navigation provider for monitoring feature.
 */
class MonitoringNavigationStubProvider : NavigationProvider {
    override val featureId: String = "monitoring"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(MonitoringRoute.Dashboard.route) {
                // TODO: Connect to FarmMonitoringScreen
            }

            composable(MonitoringRoute.Vaccination.route) {
                // TODO: Connect to VaccinationScheduleScreen
            }

            composable(MonitoringRoute.Mortality.route) {
                // TODO: Connect to MortalityTrackingScreen
            }

            composable(MonitoringRoute.Quarantine.route) {
                // TODO: Connect to QuarantineManagementScreen
            }

            composable(MonitoringRoute.Breeding.route) {
                // TODO: Connect to BreedingScreen
            }

            composable(MonitoringRoute.BreedingUnit.route) {
                // TODO: Connect to BreedingUnitScreen
            }

            composable(MonitoringRoute.BreedingPerformance.route) {
                // TODO: Connect to BreedingPerformanceScreen
            }

            composable(MonitoringRoute.Growth.route) {
                // TODO: Connect to GrowthTrackingScreen
            }

            composable(MonitoringRoute.Hatching.route) {
                // TODO: Connect to HatchingProcessScreen
            }

            composable(MonitoringRoute.HatchingBatch.route) { backStackEntry ->
                val batchId = backStackEntry.arguments?.getString("batchId") ?: ""
                // TODO: Connect to HatchingBatchScreen
            }

            composable(MonitoringRoute.Performance.route) {
                // TODO: Connect to FarmPerformanceScreen
            }

            composable(MonitoringRoute.DailyLog.route) {
                // TODO: Connect to DailyLogScreen
            }

            composable(MonitoringRoute.DailyLogProduct.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                // TODO: Connect to DailyLogProductScreen
            }

            composable(MonitoringRoute.Tasks.route) {
                // TODO: Connect to TasksScreen
            }

            composable(MonitoringRoute.FarmLog.route) {
                // TODO: Connect to FarmLogScreen
            }

            composable(MonitoringRoute.VaccinationDetail.route) { backStackEntry ->
                val vaccinationId = backStackEntry.arguments?.getString("vaccinationId") ?: ""
                // TODO: Connect to VaccinationDetailScreen
            }

            composable(MonitoringRoute.GrowthDetail.route) { backStackEntry ->
                val recordId = backStackEntry.arguments?.getString("recordId") ?: ""
                // TODO: Connect to GrowthDetailScreen
            }

            composable(MonitoringRoute.MortalityDetail.route) { backStackEntry ->
                val deathId = backStackEntry.arguments?.getString("deathId") ?: ""
                // TODO: Connect to MortalityDetailScreen
            }

            composable(MonitoringRoute.FarmActivityDetail.route) { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
                // TODO: Connect to FarmActivityDetailScreen
            }

            composable(MonitoringRoute.BirdHistory.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to BirdHistoryScreen
            }

            composable(MonitoringRoute.FcrCalculator.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to FcrCalculatorScreen
            }

            composable(MonitoringRoute.ExpenseLedger.route) {
                // TODO: Connect to ExpenseLedgerScreen
            }

            composable(MonitoringRoute.Profitability.route) {
                // TODO: Connect to ProfitabilityScreen
            }

            composable(MonitoringRoute.AssetDocument.route) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
                // TODO: Connect to AssetDocumentScreen
            }

            composable(MonitoringRoute.FarmDocument.route) {
                // TODO: Connect to FarmDocumentScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://monitoring",
        "https://rostry.app/monitoring"
    )
}

