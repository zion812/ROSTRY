package com.rio.rostry.feature.admin.ui.navigation

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
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminNavViewModel @Inject constructor(
    val currentUserProvider: CurrentUserProvider
) : ViewModel()

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
                // TODO: Restore SessionViewModel integration once extracted from app module
                val adminNavViewModel: AdminNavViewModel = hiltViewModel()
                AdminShell(
                    onExitAdmin = { navController.popBackStack() },
                    onSignOut = { /* TODO: Wire up sign out */ },
                    onSearchClick = { /* Search functionality */ },
                    onNotificationsClick = { navController.navigate(NOTIFICATIONS) },
                    currentUserProvider = adminNavViewModel.currentUserProvider,
                    pendingVerificationsCount = 0,
                    syncStatus = SyncStatus.SYNCED,
                    alertCount = 0
                )
            }
            
            // ============ Moderation Routes ============
            
            // Content Moderation Screen
            composable(MODERATION) { 
                com.rio.rostry.feature.admin.ui.moderation.ModerationQueueScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Moderation Verifications Screen
            composable(MODERATION_VERIFICATIONS) {
                com.rio.rostry.feature.admin.ui.moderation.ModerationQueueScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
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
