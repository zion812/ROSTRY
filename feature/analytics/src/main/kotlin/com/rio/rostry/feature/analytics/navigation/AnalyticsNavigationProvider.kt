package com.rio.rostry.feature.analytics.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.feature.analytics.EnthusiastDashboardScreen
import com.rio.rostry.feature.analytics.FarmerDashboardScreen
import com.rio.rostry.feature.analytics.GeneralDashboardScreen
import com.rio.rostry.feature.analytics.MonthlyReportScreen
import com.rio.rostry.feature.analytics.ReportsScreen
import com.rio.rostry.feature.analytics.financial.ProfitabilityScreen
import com.rio.rostry.ui.screens.PlaceholderScreen

/**
 * Navigation provider for analytics feature.
 * Handles analytics dashboards, reports, financial analytics, and performance analytics.
 * 
 * Migrated from AppNavHost - Task 7.1.4
 * Total routes: 7
 * 
 * Routes included:
 * - ANALYTICS_FARMER (farmer analytics dashboard)
 * - ANALYTICS_ENTHUSIAST (enthusiast analytics dashboard)
 * - MONTHLY_REPORT (monthly report)
 * - ANALYTICS_DASHBOARD (analytics dashboard alternate route)
 * - PROFITABILITY (profitability analysis)
 * - REPORTS (reports screen)
 * - GENERAL_DASHBOARD (general analytics dashboard)
 */
class AnalyticsNavigationProvider : NavigationProvider {
    override val featureId: String = "analytics"
    
    // Local route definitions
    companion object Routes {
        // Analytics routes
        const val ANALYTICS_FARMER = "analytics/farmer"
        const val ANALYTICS_ENTHUSIAST = "analytics/enthusiast"
        const val ANALYTICS_DASHBOARD = "analytics/dashboard"
        const val ANALYTICS_GENERAL = "analytics/general"
        const val MONTHLY_REPORT = "analytics/monthly_report"
        const val REPORTS = "analytics/reports"
        const val PROFITABILITY = "monitoring/profitability"
        
        // Social routes (for navigation)
        const val SOCIAL_FEED = "social/feed"
        
        // Monitoring routes (for navigation)
        const val MONITORING_PERFORMANCE = "monitoring/performance"
        
        // Enthusiast routes (for navigation)
        const val EGG_COLLECTION = "enthusiast/egg_collection"
    }

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // ============ Analytics Dashboard Routes ============
            
            // Farmer Analytics Dashboard
            composable(ANALYTICS_FARMER) {
                FarmerDashboardScreen(
                    onOpenReports = { navController.navigate(REPORTS) },
                    onOpenFeed = { navController.navigate(SOCIAL_FEED) },
                    onOpenProfitability = { navController.navigate(PROFITABILITY) }
                )
            }
            
            // Enthusiast Analytics Dashboard
            composable(ANALYTICS_ENTHUSIAST) {
                EnthusiastDashboardScreen(
                    onOpenReports = { navController.navigate(REPORTS) },
                    onOpenFeed = { navController.navigate(SOCIAL_FEED) },
                    onOpenPerformance = { navController.navigate(MONITORING_PERFORMANCE) },
                    onOpenFinancial = { /* TODO: Financial screen */ },
                    onOpenTraceability = { productId -> navController.navigate("traceability/$productId") },
                    onOpenEggCollection = { navController.navigate(EGG_COLLECTION) },
                    onKpiTap = { kpi -> /* TODO: KPI detail */ }
                )
            }
            
            // Analytics Dashboard (alternate route)
            composable(ANALYTICS_DASHBOARD) {
                EnthusiastDashboardScreen(
                    onOpenReports = { navController.navigate(REPORTS) },
                    onOpenFeed = { navController.navigate(SOCIAL_FEED) }
                )
            }
            
            // General Dashboard
            composable(ANALYTICS_GENERAL) {
                GeneralDashboardScreen(
                    onOpenReports = { navController.navigate(REPORTS) },
                    onOpenFeed = { navController.navigate(SOCIAL_FEED) }
                )
            }
            
            // ============ Reports Routes ============
            
            // Monthly Report Screen
            composable(MONTHLY_REPORT) {
                MonthlyReportScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Reports Screen
            composable(REPORTS) {
                ReportsScreen()
            }
            
            // ============ Financial Analytics Routes ============
            
            // Profitability Analysis Screen
            composable(PROFITABILITY) {
                ProfitabilityScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://analytics/*",
        "rostry://reports",
        "https://rostry.app/analytics/*",
        "https://rostry.app/reports"
    )
}
