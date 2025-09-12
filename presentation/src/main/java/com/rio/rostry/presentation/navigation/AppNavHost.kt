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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rio.rostry.presentation.bidding.AuctionListViewModel
import com.rio.rostry.presentation.bidding.AuctionDetailViewModel
import com.rio.rostry.presentation.bidding.AuctionListScreen
import com.rio.rostry.presentation.bidding.AuctionDetailScreen
import com.rio.rostry.domain.bidding.AuctionStatus
import com.rio.rostry.presentation.marketplace.MarketplaceViewModel
import com.rio.rostry.presentation.marketplace.MarketplaceScreen
import com.rio.rostry.presentation.listing.ProductListingViewModel
import com.rio.rostry.presentation.listing.ProductListingScreen
import com.rio.rostry.presentation.payments.CheckoutScreen
import com.rio.rostry.presentation.payments.CheckoutViewModel
import com.rio.rostry.presentation.tracking.OrderTrackingScreen
import com.rio.rostry.presentation.tracking.OrderTrackingViewModel
import com.rio.rostry.presentation.order.PlaceOrderScreen
import com.rio.rostry.presentation.analytics.AnalyticsScreen
import com.rio.rostry.presentation.recommendations.RecommendationsScreen
import com.rio.rostry.presentation.analytics.AnalyticsSettingsScreen
import com.rio.rostry.presentation.performance.ProductPerformanceScreen

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

        // Marketplace
        composable(Routes.MARKETPLACE) {
            val vm: MarketplaceViewModel = hiltViewModel()
            MarketplaceScreen(vm)
        }

        // Listing
        composable(Routes.PRODUCT_LISTING) {
            val vm: ProductListingViewModel = hiltViewModel()
            ProductListingScreen(viewModel = vm)
        }

        // Bidding: Auction list (optionally filter by seller)
        composable(Routes.AUCTION_LIST) {
            val vm: AuctionListViewModel = hiltViewModel()
            val sellerId = sessionViewModel.currentUserId.collectAsState(initial = null).value
            AuctionListScreen(
                viewModel = vm,
                status = AuctionStatus.ACTIVE,
                sellerId = sellerId,
                onAuctionClick = { auctionId ->
                    navController.navigate("${Routes.AUCTION_DETAIL}/$auctionId")
                }
            )
        }

        // Bidding: Auction detail with auctionId argument
        composable(
            route = "${Routes.AUCTION_DETAIL}/{auctionId}",
            arguments = listOf(navArgument("auctionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vm: AuctionDetailViewModel = hiltViewModel()
            val auctionId = backStackEntry.arguments?.getString("auctionId") ?: return@composable
            val bidderId = sessionViewModel.currentUserId.collectAsState(initial = null).value ?: return@composable
            AuctionDetailScreen(
                viewModel = vm,
                auctionId = auctionId,
                bidderId = bidderId,
                onPlaced = { navController.popBackStack() },
                onError = { /* show snackbar in your scaffold */ }
            )
        }

        // Checkout (demo)
        composable(
            route = "${Routes.CHECKOUT}/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vm: CheckoutViewModel = hiltViewModel()
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            CheckoutScreen(
                viewModel = vm,
                orderId = orderId,
                onTrack = { navController.navigate("${Routes.ORDER_TRACK}/$orderId") },
                onBack = { navController.popBackStack() }
            )
        }

        // Order Tracking (demo)
        composable(
            route = "${Routes.ORDER_TRACK}/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vm: OrderTrackingViewModel = hiltViewModel()
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            OrderTrackingScreen(
                viewModel = vm,
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }

        // Place Order (demo)
        composable(Routes.PLACE_ORDER) {
            PlaceOrderScreen(onBack = { navController.popBackStack() })
        }

        // Analytics & Reports (debug/ops)
        composable(Routes.ANALYTICS) {
            AnalyticsScreen(
                onOpenSettings = { navController.navigate(Routes.ANALYTICS_SETTINGS) }
            )
        }

        // Recommendations
        composable(Routes.RECOMMENDATIONS) {
            RecommendationsScreen()
        }

        // Analytics Settings (monthly goal)
        composable(Routes.ANALYTICS_SETTINGS) {
            AnalyticsSettingsScreen()
        }

        // Product Performance
        composable(Routes.PRODUCT_PERFORMANCE) {
            ProductPerformanceScreen()
        }
    }
}

