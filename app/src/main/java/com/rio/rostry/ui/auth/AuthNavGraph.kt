package com.rio.rostry.ui.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rio.rostry.domain.model.User
import com.rio.rostry.ui.auth.screens.*

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    startDestination: String = "phone_verification"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("phone_verification") {
            PhoneVerificationScreen(
                onVerificationCodeSent = { phoneNumber ->
                    navController.currentBackStackEntry?.arguments?.putString("phone_number", phoneNumber)
                    navController.navigate("verification_code")
                }
            )
        }

        composable("verification_code") {
            val phoneNumber = navController.previousBackStackEntry?.arguments?.getString("phone_number") ?: ""
            VerificationCodeScreen(
                phoneNumber = phoneNumber,
                onVerificationSuccess = {
                    // Navigate to main app or profile setup
                    navController.navigate("user_profile") {
                        popUpTo("phone_verification") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("user_profile") {
            // In a real implementation, we would get the current user from ViewModel
            val mockUser = User(
                id = "123",
                phone = "+919876543210",
                userType = com.rio.rostry.domain.model.UserType.GENERAL
            )
            UserProfileScreen(
                user = mockUser,
                onProfileUpdated = {
                    // Navigate to main app
                    navController.navigate("main") {
                        popUpTo("phone_verification") { inclusive = true }
                    }
                }
            )
        }

        composable("role_upgrade") {
            // In a real implementation, we would get the current user from ViewModel
            val mockUser = User(
                id = "123",
                phone = "+919876543210",
                userType = com.rio.rostry.domain.model.UserType.GENERAL
            )
            RoleUpgradeScreen(
                user = mockUser,
                onRoleUpgraded = {
                    navController.popBackStack()
                }
            )
        }

        composable("farmer_verification") {
            // In a real implementation, we would get the current user from ViewModel
            val mockUser = User(
                id = "123",
                phone = "+919876543210",
                userType = com.rio.rostry.domain.model.UserType.FARMER
            )
            FarmerVerificationScreen(
                user = mockUser,
                onVerificationSubmitted = {
                    navController.popBackStack()
                }
            )
        }

        composable("enthusiast_verification") {
            // In a real implementation, we would get the current user from ViewModel
            val mockUser = User(
                id = "123",
                phone = "+919876543210",
                userType = com.rio.rostry.domain.model.UserType.ENTHUSIAST
            )
            EnthusiastVerificationScreen(
                user = mockUser,
                onVerificationSubmitted = {
                    navController.popBackStack()
                }
            )
        }
    }
}