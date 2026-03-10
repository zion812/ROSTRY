package com.rio.rostry.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.ui.session.SessionViewModel

internal fun NavGraphBuilder.adminNavGraph(
    navController: NavHostController,
    sessionVm: SessionViewModel,
    state: SessionViewModel.SessionUiState
) {
    composable(Routes.Admin.PORTAL) {
        val pendingCount = state.pendingVerificationCount
        com.rio.rostry.feature.admin.ui.shell.AdminShell(
            onExitAdmin = {
                if (!navController.popBackStack()) {
                    navController.navigate(Routes.HOME_GENERAL) {
                        popUpTo(Routes.Admin.PORTAL) { inclusive = true }
                    }
                }
            },
            onSignOut = {
                sessionVm.signOut()
            },
            onSearchClick = {
                navController.navigate(Routes.GeneralNav.EXPLORE)
            },
            onNotificationsClick = {
                navController.navigate(Routes.NOTIFICATIONS)
            },
            currentUserProvider = sessionVm.currentUserProvider,
            pendingVerificationsCount = pendingCount
        )
    }

    composable(Routes.Admin.DASHBOARD) {
        val pendingCount = state.pendingVerificationCount
        com.rio.rostry.feature.admin.ui.AdminDashboardScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToVerification = { navController.navigate(Routes.Admin.VERIFICATION) },
            onNavigateToUserManagement = { navController.navigate(Routes.Admin.USER_MANAGEMENT) },
            onNavigateToBiosecurity = { navController.navigate(Routes.Admin.BIOSECURITY) },
            onNavigateToMortality = { navController.navigate(Routes.Admin.MORTALITY_DASHBOARD) },
            onNavigateToDisputes = { navController.navigate(Routes.Admin.DISPUTES) },
            onNavigateTo = { navController.navigate(it) },
            pendingVerificationsCount = pendingCount
        )
    }

    composable(Routes.Admin.UPGRADE_REQUESTS) {
        com.rio.rostry.feature.admin.ui.AdminUpgradeRequestsScreen(
            onNavigateBack = { navController.popBackStack() },
            currentUserProvider = sessionVm.currentUserProvider
        )
    }

    composable(Routes.Admin.VERIFICATION) {
        com.rio.rostry.feature.admin.ui.AdminVerificationScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Admin.USER_MANAGEMENT) {
        com.rio.rostry.feature.admin.ui.UserManagementScreen(
            onNavigateBack = { navController.popBackStack() },
            onUserClick = { userId -> navController.navigate("admin/users/$userId") }
        )
    }

    composable(Routes.Admin.BIOSECURITY) {
        com.rio.rostry.feature.admin.ui.biosecurity.BiosecurityManagementScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Admin.MORTALITY_DASHBOARD) {
        com.rio.rostry.feature.admin.ui.mortality.MortalityDashboardScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Admin.DISPUTES) {
        com.rio.rostry.feature.admin.ui.dispute.AdminDisputeScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Admin.PRODUCT_MODERATION) {
        com.rio.rostry.feature.admin.ui.commerce.AdminProductScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(Routes.Admin.ORDER_INTERVENTION) {
        com.rio.rostry.feature.admin.ui.commerce.AdminOrderScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(Routes.Admin.AUDIT_LOGS) {
        com.rio.rostry.feature.admin.ui.audit.AdminAuditScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(Routes.Admin.INVOICES) {
        com.rio.rostry.feature.admin.ui.commerce.AdminInvoiceScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
