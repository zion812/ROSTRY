package com.rio.rostry.feature.admin.ui.navigation
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.common.session.CurrentUserProvider
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.feature.admin.ui.shell.AdminShell
import com.rio.rostry.feature.admin.ui.shell.SyncStatus
import com.rio.rostry.ui.session.SessionViewModel

/**
 * Navigation provider for admin feature.
 * Handles admin dashboard, moderation, user management, and content moderation.
 * 
 * Migrated from AppNavHost - Task 6.1.7
 * Updated in Task 6.2 - Local route definitions added, Routes.kt dependency removed
 * Total routes: 3
 * 
 * Routes included:
 * - ADMIN_DASHBOARD (admin portal entry point)
 * - MODERATION (content moderation)
 * - MODERATION_VERIFICATIONS (moderation verifications)
 * 
 * NOTE: This provider is in the app module because the screens haven't been migrated
 * to the feature:admin-dashboard module yet. The admin portal has its own internal
 * navigation structure (AdminNavHost) that handles all admin sub-routes.
 */
class AdminNavigationProvider : NavigationProvider {
    override val featureId: String = "admin"
    
    // Local route definitions (Task 6.2)
    companion object Routes {
        // Admin routes
        const val ADMIN_DASHBOARD = "admin/dashboard"
        const val ADMIN_PORTAL = "admin/portal"
        const val MODERATION = "social/moderation"
        const val MODERATION_VERIFICATIONS = "moderation/verifications"
        
        // Notifications route (for navigation)
        const val NOTIFICATIONS = "notifications"
    }

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // ============ Admin Portal Routes ============
            
            // Admin Dashboard/Portal Entry Point
            composable(ADMIN_DASHBOARD) {
                val sessionVm: SessionViewModel = hiltViewModel()
                val state by sessionVm.uiState.collectAsState()
                
                // Admin Shell contains its own internal navigation (AdminNavHost)
                AdminShell(
                    onExitAdmin = { 
                        // Navigate back to user's home screen based on their role
                        val roleConfig = state.navConfig
                        if (roleConfig != null) {
                            navController.navigate(roleConfig.startDestination) {
                                popUpTo(ADMIN_DASHBOARD) { inclusive = true }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onSignOut = { sessionVm.signOut() },
                    onSearchClick = { /* Search functionality */ },
                    onNotificationsClick = { navController.navigate(NOTIFICATIONS) },
                    currentUserProvider = object : CurrentUserProvider {
                        override fun getCurrentUserId(): String? = state.userId
                        override fun getCurrentUserType(): com.rio.rostry.domain.model.UserType? = state.role
                    },
                    pendingVerificationsCount = state.pendingVerificationCount,
                    syncStatus = SyncStatus.SYNCED, // TODO: Wire up actual sync status
                    alertCount = 0 // TODO: Wire up actual alert count
                )
            }
            
            // ============ Moderation Routes ============
            
            // Content Moderation Screen
            composable(MODERATION) { 
                com.rio.rostry.ui.moderation.ModerationScreen(
                    onOpenVerifications = { navController.navigate(MODERATION_VERIFICATIONS) }
                ) 
            }
            
            // Moderation Verifications Screen
            composable(MODERATION_VERIFICATIONS) {
                // Re-use ModerationScreen with verifications tab
                com.rio.rostry.ui.moderation.ModerationScreen(initialTab = 1) // Assuming 1 is verifications
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://admin/*",
        "rostry://moderation",
        "https://rostry.app/admin/*",
        "https://rostry.app/moderation"
    )
}
