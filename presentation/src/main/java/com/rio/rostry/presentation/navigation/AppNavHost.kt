package com.rio.rostry.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rio.rostry.presentation.screens.home.HomeScreen
import com.rio.rostry.presentation.screens.home.EnthusiastHomeScreen
import com.rio.rostry.presentation.screens.home.FarmerHomeScreen
import com.rio.rostry.presentation.screens.onboarding.OnboardingScreen
import com.rio.rostry.presentation.viewmodel.SessionViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.presentation.navigation.RoleStartDestinationProviderImpl
import com.rio.rostry.presentation.screens.onboarding.EmailSignInScreen
import com.rio.rostry.presentation.screens.onboarding.EmailSignUpScreen
import com.rio.rostry.presentation.screens.onboarding.PhoneVerificationScreen
import com.rio.rostry.presentation.screens.farmer.FarmerLocationVerificationScreen
import com.rio.rostry.presentation.screens.enthusiast.EnthusiastKycScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val userType by sessionViewModel.userType
        .collectAsState(initial = null)
    val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState(initial = false)
    val expiry by sessionViewModel.sessionExpiryMillis.collectAsState(initial = null)

    // Clear session if expired
    LaunchedEffect(expiry, isLoggedIn) {
        val exp = expiry
        if (isLoggedIn && exp != null && System.currentTimeMillis() >= exp) {
            sessionViewModel.clearSession()
            navController.navigate(Routes.ONBOARDING) {
                popUpTo(0)
            }
        }
    }

    // Stateless provider; DI alternative exists via PresentationModule
    val startProvider = RoleStartDestinationProviderImpl()
    val startDest = if (isLoggedIn) startProvider.startDestinationFor(userType) else Routes.ONBOARDING

    NavHost(navController = navController, startDestination = startDest) {
        // Onboarding flow
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }
        composable(Routes.EMAIL_SIGNIN) { EmailSignInScreen(navController) }
        composable(Routes.EMAIL_SIGNUP) { EmailSignUpScreen(navController) }
        composable(Routes.PHONE_VERIFY) { PhoneVerificationScreen(navController) }

        // Role-specific homes
        composable(Routes.HOME_GENERAL) { HomeScreen() }
        composable(Routes.HOME_FARMER) { FarmerHomeScreen(navController) }
        composable(Routes.HOME_ENTHUSIAST) { EnthusiastHomeScreen() }

        // Farmer flows
        composable(Routes.FARMER_LOCATION_VERIFY) { FarmerLocationVerificationScreen(navController) }

        // Enthusiast flows
        composable(Routes.ENTHUSIAST_KYC) { EnthusiastKycScreen(navController) }
    }
}

