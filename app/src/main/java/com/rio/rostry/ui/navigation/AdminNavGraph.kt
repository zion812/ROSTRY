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
        com.rio.rostry.ui.admin.shell.AdminShell(
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
        com.rio.rostry.ui.admin.AdminDashboardScreen(
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
        com.rio.rostry.ui.admin.AdminUpgradeRequestsScreen(
            onNavigateBack = { navController.popBackStack() },
            currentUserProvider = sessionVm.currentUserProvider
        )
    }

    composable(Routes.Admin.VERIFICATION) {
        com.rio.rostry.ui.admin.AdminVerificationScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Admin.USER_MANAGEMENT) {
        com.rio.rostry.ui.admin.UserManagementScreen(
            onNavigateBack = { navController.popBackStack() },
            onUserClick = { userId -> navController.navigate("admin/users/$userId") }
        )
    }

    composable(Routes.Admin.BIOSECURITY) {
        com.rio.rostry.ui.admin.biosecurity.BiosecurityManagementScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Admin.MORTALITY_DASHBOARD) {
        com.rio.rostry.ui.admin.mortality.MortalityDashboardScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Admin.DISPUTES) {
        com.rio.rostry.ui.admin.dispute.AdminDisputeScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Admin.PRODUCT_MODERATION) {
        com.rio.rostry.ui.admin.commerce.AdminProductScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(Routes.Admin.ORDER_INTERVENTION) {
        com.rio.rostry.ui.admin.commerce.AdminOrderScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(Routes.Admin.AUDIT_LOGS) {
        com.rio.rostry.ui.admin.audit.AdminAuditScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(Routes.Admin.INVOICES) {
        com.rio.rostry.ui.admin.commerce.AdminInvoiceScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
