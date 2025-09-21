package com.rio.rostry.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.ui.auth.OtpScreen
import com.rio.rostry.ui.auth.PhoneInputScreen
import com.rio.rostry.ui.screens.HomeEnthusiastScreen
import com.rio.rostry.ui.screens.HomeFarmerScreen
import com.rio.rostry.ui.screens.HomeGeneralScreen
import com.rio.rostry.ui.screens.PlaceholderScreen
import com.rio.rostry.ui.profile.ProfileScreen
import com.rio.rostry.ui.start.StartViewModel
import com.rio.rostry.ui.verification.FarmerLocationVerificationScreen
import com.rio.rostry.ui.verification.EnthusiastKycScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authVm: AuthViewModel = hiltViewModel()
    val startVm: StartViewModel = hiltViewModel()

    // React to AuthViewModel navigation events
    LaunchedEffect(Unit) {
        authVm.navigation.collectLatest { event ->
            when (event) {
                is AuthViewModel.NavAction.ToOtp -> navController.navigate("auth/otp/${event.verificationId}")
                is AuthViewModel.NavAction.ToHome -> {
                    val route = when (event.userType) {
                        com.rio.rostry.domain.model.UserType.GENERAL -> Routes.HOME_GENERAL
                        com.rio.rostry.domain.model.UserType.FARMER -> Routes.HOME_FARMER
                        com.rio.rostry.domain.model.UserType.ENTHUSIAST -> Routes.HOME_ENTHUSIAST
                    }
                    navController.navigate(route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    // Initial routing based on session and role
    LaunchedEffect(Unit) {
        startVm.decide(System.currentTimeMillis())
        startVm.nav.collectLatest { nav ->
            when (nav) {
                is StartViewModel.Nav.ToAuth -> navController.navigate(Routes.AUTH_PHONE) {
                    popUpTo(0) { inclusive = true }
                }
                is StartViewModel.Nav.ToHome -> {
                    val route = when (nav.role) {
                        com.rio.rostry.domain.model.UserType.GENERAL -> Routes.HOME_GENERAL
                        com.rio.rostry.domain.model.UserType.FARMER -> Routes.HOME_FARMER
                        com.rio.rostry.domain.model.UserType.ENTHUSIAST -> Routes.HOME_ENTHUSIAST
                    }
                    navController.navigate(route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.AUTH_PHONE) {
        composable(Routes.AUTH_PHONE) {
            PhoneInputScreen(onNavigateToOtp = { verificationId ->
                navController.navigate("auth/otp/$verificationId")
            })
        }
        composable(
            route = Routes.AUTH_OTP,
            arguments = listOf(navArgument("verificationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            OtpScreen(verificationId = verificationId, onNavigateHome = {
                navController.navigate(Routes.HOME_GENERAL) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }

        // Homes (role specific) - simple placeholders for now
        composable(Routes.HOME_GENERAL) { HomeGeneralScreen(onProfile = { navController.navigate(Routes.PROFILE) }) }
        composable(Routes.HOME_FARMER) { HomeFarmerScreen(onProfile = { navController.navigate(Routes.PROFILE) }) }
        composable(Routes.HOME_ENTHUSIAST) { HomeEnthusiastScreen(onProfile = { navController.navigate(Routes.PROFILE) }) }

        // Profile and verification placeholders
        composable(Routes.PROFILE) { ProfileScreen(
            onVerifyFarmerLocation = { navController.navigate(Routes.VERIFY_FARMER_LOCATION) },
            onVerifyEnthusiastKyc = { navController.navigate(Routes.VERIFY_ENTHUSIAST_KYC) }
        ) }
        composable(Routes.VERIFY_FARMER_LOCATION) { FarmerLocationVerificationScreen(onDone = { navController.popBackStack() }) }
        composable(Routes.VERIFY_ENTHUSIAST_KYC) { EnthusiastKycScreen(onDone = { navController.popBackStack() }) }

        // Onboarding placeholders
        composable(Routes.ONBOARD_GENERAL) { PlaceholderScreen(title = "Onboarding - General") }
        composable(Routes.ONBOARD_FARMER) { PlaceholderScreen(title = "Onboarding - Farmer") }
        composable(Routes.ONBOARD_ENTHUSIAST) { PlaceholderScreen(title = "Onboarding - Enthusiast") }
    }
}
