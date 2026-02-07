package com.rio.rostry.ui.admin.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.ui.admin.AdminDashboardScreen
import com.rio.rostry.ui.admin.AdminVerificationScreen
import com.rio.rostry.ui.admin.UserManagementScreen
import com.rio.rostry.ui.admin.AdminUpgradeRequestsScreen
import com.rio.rostry.ui.admin.audit.AdminAuditScreen
import com.rio.rostry.ui.admin.biosecurity.BiosecurityManagementScreen
import com.rio.rostry.ui.admin.commerce.AdminOrderScreen
import com.rio.rostry.ui.admin.commerce.AdminProductScreen
import com.rio.rostry.ui.admin.dispute.AdminDisputeScreen
import com.rio.rostry.ui.admin.dispute.AdminDisputeDetailScreen
import com.rio.rostry.ui.admin.mortality.MortalityDashboardScreen
import com.rio.rostry.ui.admin.monitoring.BiosecurityMonitoringScreen
import com.rio.rostry.ui.admin.monitoring.MortalityMonitoringScreen
import com.rio.rostry.ui.admin.monitoring.AlertManagementScreen
import com.rio.rostry.ui.admin.roles.RoleManagementScreen
import com.rio.rostry.ui.admin.commerce.InvoicesScreen
import com.rio.rostry.ui.admin.analytics.AnalyticsOverviewScreen
import com.rio.rostry.ui.admin.analytics.UserAnalyticsScreen
import com.rio.rostry.ui.admin.analytics.CommerceAnalyticsScreen
import com.rio.rostry.ui.admin.reports.ReportGeneratorScreen
import com.rio.rostry.ui.admin.system.SystemConfigScreen
import com.rio.rostry.ui.admin.system.SystemHealthScreen
import com.rio.rostry.ui.admin.system.FeatureTogglesScreen
import com.rio.rostry.ui.admin.moderation.ModerationQueueScreen
import com.rio.rostry.ui.admin.communication.BroadcastScreen
import com.rio.rostry.ui.admin.bulk.BulkOperationsScreen
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.session.CurrentUserProvider

/**
 * Standalone navigation host for the Admin Portal.
 * 
 * This provides a dedicated navigation graph for admin functionality,
 * separate from the regular user navigation flow. The admin portal
 * operates independently with its own scaffold, sidebar, and routes.
 * 
 * Key Features:
 * - Dedicated admin navigation graph
 * - Admin-specific deep linking support
 * - Session validation at navigation boundaries
 * - Comprehensive admin screen routing
 */
@Composable
fun AdminNavHost(
    navController: NavHostController,
    onExitAdmin: () -> Unit,
    currentUserProvider: CurrentUserProvider,
    pendingVerificationsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AdminRoutes.DASHBOARD,
        modifier = modifier
    ) {
        // ========== Dashboard ==========
        composable(AdminRoutes.DASHBOARD) {
            AdminDashboardScreen(
                onNavigateBack = onExitAdmin,
                onNavigateToVerification = { 
                    navController.navigate(AdminRoutes.Verification.PENDING)
                },
                onNavigateToUserManagement = { 
                    navController.navigate(AdminRoutes.Users.LIST)
                },
                onNavigateToBiosecurity = { 
                    navController.navigate(AdminRoutes.Biosecurity.ZONES)
                },
                onNavigateToMortality = { 
                    navController.navigate(AdminRoutes.Mortality.DASHBOARD)
                },
                onNavigateToDisputes = { 
                    navController.navigate(AdminRoutes.Commerce.DISPUTES)
                },
                onNavigateTo = { route -> 
                    // Map legacy routes to admin portal routes
                    val adminRoute = mapLegacyRoute(route)
                    navController.navigate(adminRoute)
                },
                pendingVerificationsCount = pendingVerificationsCount
            )
        }
        
        // ========== Verification ==========
        composable(AdminRoutes.Verification.PENDING) {
            // Uses existing screen with hiltViewModel() inside
            AdminVerificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Verification.UPGRADE_REQUESTS) {
            // Uses existing screen with hiltViewModel() inside
            AdminUpgradeRequestsScreen(
                onNavigateBack = { navController.popBackStack() },
                currentUserProvider = currentUserProvider
            )
        }
        
        // ========== User Management ==========
        composable(AdminRoutes.Users.LIST) {
            // Uses existing screen with hiltViewModel() inside
            UserManagementScreen(
                onNavigateBack = { navController.popBackStack() },
                onUserClick = { userId -> 
                    navController.navigate(AdminRoutes.Users.detail(userId))
                }
            )
        }
        
        composable(
            route = AdminRoutes.Users.DETAIL,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            com.rio.rostry.ui.admin.users.AdminUserDetailScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOrder = { orderId -> 
                    // Assuming we have an order detail route in Admin or general
                    navController.navigate(AdminRoutes.Commerce.orderDetail(orderId))
                }
            )
        }
        
        composable(AdminRoutes.Users.ROLES) {
            RoleManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Commerce ==========
        composable(AdminRoutes.Commerce.PRODUCTS) {
            // Uses existing screen with hiltViewModel() inside
            AdminProductScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Commerce.PRODUCT_MODERATION) {
            AdminProductScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Commerce.ORDERS) {
            AdminOrderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Commerce.ORDER_INTERVENTION) {
            AdminOrderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Commerce.INVOICES) {
            InvoicesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Commerce.DISPUTES) {
            AdminDisputeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = AdminRoutes.Commerce.DISPUTE_DETAIL,
            arguments = listOf(navArgument("disputeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disputeId = backStackEntry.arguments?.getString("disputeId") ?: return@composable
            AdminDisputeDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        

        
        // ========== Biosecurity ==========
        composable(AdminRoutes.Biosecurity.ZONES) {
            BiosecurityManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Mortality ==========
        composable(AdminRoutes.Mortality.DASHBOARD) {
            MortalityDashboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Monitoring ==========
        composable(AdminRoutes.Monitoring.BIOSECURITY) {
            BiosecurityMonitoringScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Monitoring.MORTALITY) {
            MortalityMonitoringScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Monitoring.ALERTS) {
            AlertManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Audit ==========
        composable(AdminRoutes.Audit.LOGS) {
            AdminAuditScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Analytics ==========
        composable(AdminRoutes.Analytics.OVERVIEW) {
            AnalyticsOverviewScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUsers = { navController.navigate(AdminRoutes.Analytics.USERS) },
                onNavigateToCommerce = { navController.navigate(AdminRoutes.Analytics.COMMERCE) }
            )
        }
        
        composable(AdminRoutes.Analytics.USERS) {
            UserAnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.Analytics.COMMERCE) {
            CommerceAnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Reports ==========
        composable(AdminRoutes.Reports.GENERATOR) {
            ReportGeneratorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== System ==========
        composable(AdminRoutes.System.CONFIG) {
            SystemConfigScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.System.HEALTH) {
            SystemHealthScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AdminRoutes.System.FEATURE_TOGGLES) {
            FeatureTogglesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Moderation ==========
        composable(AdminRoutes.Moderation.QUEUE) {
            ModerationQueueScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Communication ==========
        composable(AdminRoutes.Communication.BROADCAST) {
            BroadcastScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ========== Bulk Operations ==========
        composable(AdminRoutes.Bulk.OPERATIONS) {
            BulkOperationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Temporary placeholder screen for unimplemented admin features.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "This feature is under development",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Maps legacy admin routes (from Routes.Admin) to new AdminPortal routes.
 */
private fun mapLegacyRoute(route: String): String = when (route) {
    Routes.Admin.VERIFICATION -> AdminRoutes.Verification.PENDING
    Routes.Admin.DASHBOARD -> AdminRoutes.DASHBOARD
    Routes.Admin.USER_MANAGEMENT -> AdminRoutes.Users.LIST
    Routes.Admin.BIOSECURITY -> AdminRoutes.Biosecurity.ZONES
    Routes.Admin.MORTALITY_DASHBOARD -> AdminRoutes.Mortality.DASHBOARD
    Routes.Admin.DISPUTES -> AdminRoutes.Commerce.DISPUTES
    Routes.Admin.PRODUCT_MODERATION -> AdminRoutes.Commerce.PRODUCT_MODERATION
    Routes.Admin.ORDER_INTERVENTION -> AdminRoutes.Commerce.ORDER_INTERVENTION
    Routes.Admin.AUDIT_LOGS -> AdminRoutes.Audit.LOGS
    Routes.Admin.UPGRADE_REQUESTS -> AdminRoutes.Verification.UPGRADE_REQUESTS
    Routes.Admin.INVOICES -> AdminRoutes.Commerce.INVOICES
    // New route mappings
    Routes.Admin.ANALYTICS -> AdminRoutes.Analytics.OVERVIEW
    Routes.Admin.REPORTS -> AdminRoutes.Reports.GENERATOR
    Routes.Admin.SYSTEM_CONFIG -> AdminRoutes.System.CONFIG
    Routes.Admin.FEATURE_TOGGLES -> AdminRoutes.System.FEATURE_TOGGLES
    Routes.Admin.MODERATION -> AdminRoutes.Moderation.QUEUE
    Routes.Admin.BROADCAST -> AdminRoutes.Communication.BROADCAST
    Routes.Admin.BULK_OPERATIONS -> AdminRoutes.Bulk.OPERATIONS
    else -> route
}
