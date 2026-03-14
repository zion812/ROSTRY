package com.rio.rostry.ui.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for profile feature.
 */
sealed class ProfileRoute(override val route: String) : NavigationRoute {
    object UserProfile : ProfileRoute("user/{userId}") {
        fun createRoute(userId: String) = "user/$userId"
    }
    object EditProfile : ProfileRoute("user/edit")
    object VerifyFarmerLocation : ProfileRoute("verify/farmer/location")
    object VerifyEnthusiastKyc : ProfileRoute("verify/enthusiast/kyc")
    object VerificationWithType : ProfileRoute("verification/{upgradeType}") {
        fun createRoute(upgradeType: String) = "verification/$upgradeType"
    }
    object StorageQuota : ProfileRoute("storage/quota")
    object PublicFarmProfile : ProfileRoute("social/farm/{farmerId}") {
        fun createRoute(farmerId: String) = "social/farm/$farmerId"
    }
}

/**
 * Navigation provider for profile feature.
 * 
 * Connects all profile and verification screens with proper navigation callbacks.
 */
class ProfileNavigationProvider : NavigationProvider {
    override val featureId: String = "profile"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(ProfileRoute.UserProfile.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                // User profile screen - placeholder
                // TODO: Implement ProfileScreen showing user details, stats, and showcase
                androidx.compose.material3.Text("User Profile: $userId - Coming Soon")
            }

            composable(ProfileRoute.EditProfile.route) {
                // Edit profile screen - placeholder
                // TODO: Implement EditProfileScreen for updating user information
                androidx.compose.material3.Text("Edit Profile - Coming Soon")
            }

            composable(ProfileRoute.VerifyFarmerLocation.route) {
                // Farmer location verification - placeholder
                // TODO: Implement VerifyFarmerLocationScreen with GPS verification
                androidx.compose.material3.Text("Verify Farmer Location - Coming Soon")
            }

            composable(ProfileRoute.VerifyEnthusiastKyc.route) {
                // Enthusiast KYC verification - placeholder
                // TODO: Implement VerifyEnthusiastKycScreen with document upload
                androidx.compose.material3.Text("Verify Enthusiast KYC - Coming Soon")
            }

            composable(ProfileRoute.VerificationWithType.route) { backStackEntry ->
                val upgradeType = backStackEntry.arguments?.getString("upgradeType") ?: ""
                // Verification with type - placeholder
                // TODO: Implement VerificationScreen for role upgrades
                androidx.compose.material3.Text("Verification: $upgradeType - Coming Soon")
            }

            composable(ProfileRoute.StorageQuota.route) {
                // Storage quota screen - shows user's storage usage
                // TODO: Implement StorageQuotaScreen with usage breakdown and cleanup options
                androidx.compose.material3.Text("Storage Quota - Coming Soon")
            }

            composable(ProfileRoute.PublicFarmProfile.route) { backStackEntry ->
                val farmerId = backStackEntry.arguments?.getString("farmerId") ?: ""
                // Public farm profile - placeholder
                // TODO: Implement PublicFarmProfileScreen showing farm transparency data
                androidx.compose.material3.Text("Public Farm Profile: $farmerId - Coming Soon")
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://profile",
        "rostry://user/{userId}",
        "rostry://user/edit",
        "rostry://storage/quota",
        "https://rostry.app/profile"
    )
}
