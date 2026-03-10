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
 */
class ProfileNavigationStubProvider : NavigationProvider {
    override val featureId: String = "profile"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(ProfileRoute.UserProfile.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                // TODO: Connect to ProfileScreen
            }

            composable(ProfileRoute.EditProfile.route) {
                // TODO: Connect to EditProfileScreen
            }

            composable(ProfileRoute.VerifyFarmerLocation.route) {
                // TODO: Connect to VerifyFarmerLocationScreen
            }

            composable(ProfileRoute.VerifyEnthusiastKyc.route) {
                // TODO: Connect to VerifyEnthusiastKycScreen
            }

            composable(ProfileRoute.VerificationWithType.route) { backStackEntry ->
                val upgradeType = backStackEntry.arguments?.getString("upgradeType") ?: ""
                // TODO: Connect to VerificationScreen
            }

            composable(ProfileRoute.StorageQuota.route) {
                // TODO: Connect to StorageQuotaScreen
            }

            composable(ProfileRoute.PublicFarmProfile.route) { backStackEntry ->
                val farmerId = backStackEntry.arguments?.getString("farmerId") ?: ""
                // TODO: Connect to PublicFarmProfileScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://profile",
        "https://rostry.app/profile"
    )
}

