package com.rio.rostry.ui.profile.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.ui.profile.EditProfileScreen
import com.rio.rostry.ui.profile.ProfileScreen
import com.rio.rostry.ui.profile.StorageQuotaScreen
import com.rio.rostry.ui.session.SessionViewModel
import com.rio.rostry.ui.verification.FarmerLocationVerificationScreen

/**
 * Navigation provider for profile feature.
 * Handles user profiles, settings, verification, and storage management.
 * 
 * Migrated from AppNavHost - Task 6.1.6
 * Updated in Task 6.2 - Local route definitions added, Routes.kt dependency removed
 * Total routes: 8
 * 
 * Routes included:
 * - PROFILE (main profile screen)
 * - PROFILE_EDIT (edit profile)
 * - STORAGE_QUOTA (storage quota management)
 * - VERIFY_FARMER_LOCATION (farmer location verification)
 * - VERIFY_ENTHUSIAST_KYC (enthusiast KYC verification)
 * - SETTINGS (settings screen)
 * - BACKUP_RESTORE (backup and restore)
 * - ADDRESS_SELECTION (address selection web view)
 * 
 * NOTE: This provider is in the app module because the screens haven't been migrated
 * to the feature:profile module yet. Once screens are migrated, this provider
 * should be moved to feature:profile module.
 */
class ProfileNavigationProvider : NavigationProvider {
    override val featureId: String = "profile"
    
    // Local route definitions (Task 6.2)
    companion object Routes {
        // Profile routes
        const val PROFILE = "profile"
        const val PROFILE_EDIT = "user/edit"
        const val STORAGE_QUOTA = "storage/quota"
        
        // Verification routes
        const val VERIFY_FARMER_LOCATION = "verify/farmer/location"
        const val VERIFY_ENTHUSIAST_KYC = "verify/enthusiast/kyc"
        
        // Settings routes
        const val SETTINGS = "settings"
        const val ADDRESS_SELECTION = "settings/address_selection"
        const val BACKUP_RESTORE = "settings/backup_restore"
        
        // Analytics routes (for navigation)
        const val ANALYTICS_FARMER = "analytics/farmer"
        
        // Admin routes (for navigation)
        const val ADMIN_DASHBOARD = "admin/dashboard"
        
        // Builder functions
        fun upgradeWizard(targetRole: UserType): String = "upgrade/wizard/${targetRole.name}"
    }

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // ============ Profile Routes ============
            
            // Main Profile Screen
            composable(PROFILE) {
                val sessionVm: SessionViewModel = hiltViewModel()
                val state by sessionVm.uiState.collectAsState()
                ProfileScreen(
                    onVerifyFarmerLocation = { navController.navigate(VERIFY_FARMER_LOCATION) },
                    onVerifyEnthusiastKyc = { navController.navigate(VERIFY_ENTHUSIAST_KYC) },
                    onNavigateToAnalytics = { navController.navigate(ANALYTICS_FARMER) },
                    onNavigateToStorageQuota = { navController.navigate(STORAGE_QUOTA) },
                    onNavigateToAdminDashboard = { navController.navigate(ADMIN_DASHBOARD) },
                    onUpgradeClick = { type -> navController.navigate(Routes.upgradeWizard(type)) },
                    isAdmin = state.isAdmin
                )
            }
            
            // Edit Profile Screen
            composable(PROFILE_EDIT) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Storage Quota Screen
            composable(STORAGE_QUOTA) {
                StorageQuotaScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // ============ Verification Routes ============
            
            // Farmer Location Verification Screen
            composable(VERIFY_FARMER_LOCATION) {
                FarmerLocationVerificationScreen(onDone = { navController.popBackStack() })
            }
            
            // Enthusiast KYC Verification Screen
            composable(VERIFY_ENTHUSIAST_KYC) {
                com.rio.rostry.feature.enthusiast.ui.verification.EnthusiastVerificationScreen(
                    onNavigateUp = { navController.popBackStack() }
                )
            }
            
            // ============ Settings Routes ============
            
            // Settings Screen
            composable(SETTINGS) {
                val sessionVm: SessionViewModel = hiltViewModel()
                val state by sessionVm.uiState.collectAsState()
                val handle = navController.currentBackStackEntry?.savedStateHandle
                val selectedFlow = remember(handle) { handle?.getStateFlow("address_selection_result", null as String?) }
                val lastSelected = selectedFlow?.collectAsState()?.value
                com.rio.rostry.ui.settings.SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenAddressSelection = { navController.navigate(ADDRESS_SELECTION) },
                    onNavigateToAdminVerification = { navController.navigate(ADMIN_DASHBOARD) },
                    onNavigateToBackupRestore = { navController.navigate(BACKUP_RESTORE) },
                    lastSelectedAddressJson = lastSelected,
                    isAdmin = state.isAdmin,
                    pendingCount = state.pendingVerificationCount
                )
            }
            
            // Backup & Restore Screen
            composable(BACKUP_RESTORE) {
                com.rio.rostry.ui.settings.BackupRestoreScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Address Selection (Web-based via WebView)
            composable(ADDRESS_SELECTION) {
                com.rio.rostry.ui.settings.AddressSelectionWebViewScreen(
                    onBack = { navController.popBackStack() },
                    onSubmit = { json ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "address_selection_result",
                            json
                        )
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://profile",
        "rostry://settings/*",
        "https://rostry.app/profile",
        "https://rostry.app/settings/*"
    )
}
