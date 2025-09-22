package com.rio.rostry.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.traceability.TraceabilityViewModel
import com.rio.rostry.ui.traceability.FamilyTreeView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.rio.rostry.ui.product.ProductDetailsScreen
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton

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

        // Product Details screen with button to open Traceability
        composable(
            route = Routes.PRODUCT_DETAILS,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailsScreen(
                productId = productId,
                onOpenTraceability = { navController.navigate("traceability/$productId") }
            )
        }

        // Traceability Screen with deep link support
        composable(
            route = Routes.TRACEABILITY,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://traceability/{productId}" })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val vm: TraceabilityViewModel = hiltViewModel()
            TraceabilityScreen(vm = vm, productId = productId, onBack = { navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceabilityScreen(vm: TraceabilityViewModel, productId: String, onBack: () -> Unit) {
    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) vm.load(productId)
    }
    val state by vm.state.collectAsState()
    Column(Modifier.padding(16.dp)) {
        TopAppBar(
            title = { Text(text = "Traceability") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Text(text = "For: ${state.rootId}", modifier = Modifier.padding(top = 8.dp))
        var resetKey by remember { mutableIntStateOf(0) }
        OutlinedButton(onClick = { resetKey++ }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Reset Zoom & Center")
        }
        FamilyTreeView(
            rootId = state.rootId,
            layersUp = state.layersUp,
            layersDown = state.layersDown,
            edges = state.edges,
            resetKey = resetKey,
            modifier = Modifier.padding(top = 12.dp)
        )
        if (state.transferChain.isNotEmpty()) {
            Text(text = "Transfer Chain", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            LazyColumn {
                items(state.transferChain) { item ->
                    when (item) {
                        is com.rio.rostry.data.database.entity.TransferEntity -> {
                            Text("Transfer • ${item.type} • ${item.status} • ${item.initiatedAt}")
                        }
                        is com.rio.rostry.data.database.entity.ProductTrackingEntity -> {
                            Text("Tracking • ${item.status} • ${item.timestamp}")
                        }
                        else -> Text(item.toString())
                    }
                }
            }
        }
    }
}
