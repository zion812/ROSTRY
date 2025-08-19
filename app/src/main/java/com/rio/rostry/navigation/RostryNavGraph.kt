package com.rio.rostry.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import com.rio.rostry.ui.screens.auth.DebugLoginScreen
import com.rio.rostry.ui.screens.auth.LoginScreen
import com.rio.rostry.ui.screens.auth.SignUpScreen
import com.rio.rostry.ui.screens.auth.ProfileSetupScreen
import com.rio.rostry.ui.screens.home.HomeScreen
import com.rio.rostry.ui.screens.marketplace.MarketplaceScreen
import com.rio.rostry.ui.screens.community.CommunityScreen
import com.rio.rostry.ui.screens.cart.CartScreen
import com.rio.rostry.ui.screens.profile.ProfileScreen
import com.rio.rostry.ui.screens.dashboard.DashboardScreen
import com.rio.rostry.ui.screens.transfer.TransferScreen
import com.rio.rostry.ui.screens.fowl.FowlListScreen
import com.rio.rostry.ui.screens.fowl.AddEditFowlScreen
import com.rio.rostry.ui.screens.fowl.FowlDetailScreen
import com.rio.rostry.ui.screens.fowl.AddHealthRecordScreen
import com.rio.rostry.ui.screens.main.MainScreen
import com.rio.rostry.viewmodel.AuthViewModel
import com.rio.rostry.viewmodel.FowlViewModel
import com.rio.rostry.viewmodel.FowlDetailViewModel
import com.rio.rostry.viewmodel.MarketplaceViewModel
import com.rio.rostry.viewmodel.TransferViewModel

@Composable
fun RostryNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val fowlViewModel: FowlViewModel = viewModel()
    val marketplaceViewModel: MarketplaceViewModel = viewModel()
    val transferViewModel: TransferViewModel = viewModel()
    
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.user.collectAsState()

    // Navigate to the correct screen when auth state changes
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            // Check if we're currently on the login screen
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute == AppDestination.Login.route || 
                currentRoute == AppDestination.DebugLogin.route ||
                currentRoute == AppDestination.SignUp.route) {
                navController.navigate(AppDestination.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        } else {
            // Check if we're currently on a protected screen
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val protectedRoutes = listOf(
                AppDestination.Home.route,
                AppDestination.Marketplace.route,
                AppDestination.Community.route,
                AppDestination.Cart.route,
                AppDestination.Profile.route,
                AppDestination.Dashboard.route,
                AppDestination.Transfer.route,
                AppDestination.AddFowl.route,
                AppDestination.EditFowl("").route,
                AppDestination.FowlDetail("").route,
                AppDestination.AddHealthRecord("").route
            )
            
            if (currentRoute in protectedRoutes) {
                navController.navigate(AppDestination.Login.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppDestination.Login.route
    ) {
        composable(AppDestination.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to home after successful login
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(AppDestination.SignUp.route)
                }
            )
        }
        composable(AppDestination.DebugLogin.route) {
            DebugLoginScreen(
                onLoginSuccess = {
                    // Navigate to home after successful login
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.DebugLogin.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(AppDestination.SignUp.route)
                }
            )
        }
        composable(AppDestination.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    // Navigate to profile setup after successful sign up
                    navController.navigate(AppDestination.ProfileSetup.route)
                },
                onLoginClick = {
                    navController.navigate(AppDestination.Login.route)
                }
            )
        }
        composable(AppDestination.ProfileSetup.route) {
            ProfileSetupScreen(
                onProfileSetupComplete = {
                    // Navigate to home after profile setup
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestination.Home.route) {
            MainScreen(navController = navController) {
                // Get the current user ID from the auth system
                val currentUserId = currentUser?.userId ?: ""
                
                // Fetch fowls when the screen is first composed
                LaunchedEffect(Unit) {
                    if (currentUserId.isNotEmpty()) {
                        fowlViewModel.fetchFowls(currentUserId)
                    }
                }
                
                FowlListScreen(
                    fowls = fowlViewModel.fowls.value,
                    loading = fowlViewModel.loading.value,
                    error = fowlViewModel.error.value,
                    onAddFowlClick = {
                        navController.navigate(AppDestination.AddFowl.route)
                    },
                    onEditFowl = { fowl ->
                        navController.navigate(AppRoutes.editFowl(fowl.fowlId))
                    },
                    onDeleteFowl = { fowlId ->
                        if (currentUserId.isNotEmpty()) {
                            fowlViewModel.deleteFowl(fowlId, currentUserId)
                        }
                    },
                    onViewDetails = { fowl ->
                        navController.navigate(AppRoutes.fowlDetail(fowl.fowlId))
                    },
                    onRefresh = {
                        if (currentUserId.isNotEmpty()) {
                            fowlViewModel.fetchFowls(currentUserId)
                        }
                    }
                )
            }
        }
        composable(AppDestination.AddFowl.route) {
            // Get the current user ID from the auth system
            val currentUserId = currentUser?.userId ?: ""
            
            AddEditFowlScreen(
                fowl = null,
                onAddEditFowl = { fowl ->
                    // Add the owner user ID to the fowl
                    if (currentUserId.isNotEmpty()) {
                        val fowlWithOwner = fowl.copy(ownerUserId = currentUserId)
                        fowlViewModel.addFowl(fowlWithOwner)
                    }
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            AppDestination.EditFowl("").route,
            arguments = AppDestination.EditFowl("").arguments,
            deepLinks = AppDestination.EditFowl("").deepLinks
        ) { backStackEntry ->
            val fowlId = backStackEntry.arguments?.getString(AppDestination.EditFowl.fowlIdArg) ?: ""
            // In a real app, you would fetch the fowl by ID
            val fowl = fowlViewModel.fowls.value.find { it.fowlId == fowlId }
            
            AddEditFowlScreen(
                fowl = fowl,
                onAddEditFowl = { updatedFowl ->
                    fowlViewModel.updateFowl(updatedFowl)
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            AppDestination.FowlDetail("").route,
            arguments = AppDestination.FowlDetail("").arguments,
            deepLinks = AppDestination.FowlDetail("").deepLinks
        ) { backStackEntry ->
            val fowlId = backStackEntry.arguments?.getString(AppDestination.FowlDetail.fowlIdArg) ?: ""
            // Create a SavedStateHandle with the fowlId
            val savedStateHandle = androidx.lifecycle.SavedStateHandle().apply {
                set(NavArgs.FOWL_ID, fowlId)
            }
            FowlDetailScreen(
                navController = navController,
                viewModel = FowlDetailViewModel(savedStateHandle)
            )
        }
        composable(
            AppDestination.AddHealthRecord("").route,
            arguments = AppDestination.AddHealthRecord("").arguments,
            deepLinks = AppDestination.AddHealthRecord("").deepLinks
        ) { backStackEntry ->
            val fowlId = backStackEntry.arguments?.getString(AppDestination.AddHealthRecord.fowlIdArg) ?: ""
            // In a real app, you would fetch the fowl by ID to get its name
            val fowl = fowlViewModel.fowls.value.find { it.fowlId == fowlId }
            val fowlName = fowl?.name ?: "Unknown Fowl"
            
            AddHealthRecordScreen(
                fowlName = fowlName,
                onAddHealthRecord = { healthRecord ->
                    // Get the current user ID from the auth system
                    val currentUserId = currentUser?.userId ?: ""
                    if (currentUserId.isNotEmpty()) {
                        fowlViewModel.addHealthRecord(fowlId, healthRecord, currentUserId)
                    }
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(AppDestination.Marketplace.route) {
            MainScreen(navController = navController) {
                // Fetch active listings when the screen is first composed
                LaunchedEffect(Unit) {
                    marketplaceViewModel.fetchActiveListings()
                }
                
                MarketplaceScreen(
                    listings = marketplaceViewModel.listings.value,
                    loading = marketplaceViewModel.loading.value,
                    error = marketplaceViewModel.error.value,
                    onRefresh = {
                        marketplaceViewModel.fetchActiveListings()
                    }
                )
            }
        }
        composable(AppDestination.Community.route) { 
            MainScreen(navController = navController) {
                CommunityScreen() 
            }
        }
        composable(AppDestination.Cart.route) { 
            MainScreen(navController = navController) {
                CartScreen() 
            }
        }
        composable(AppDestination.Profile.route) {
            MainScreen(navController = navController) {
                ProfileScreen(
                    onLogout = {
                        // Navigate to login after logout
                        navController.navigate(AppDestination.Login.route) {
                            popUpTo(AppDestination.Login.route) { inclusive = true }
                        }
                    }
                )
            }
        }
        composable(AppDestination.Dashboard.route) { DashboardScreen() }
        composable(AppDestination.Transfer.route) {
            TransferScreen(
                transfers = transferViewModel.transfers.value
            )
        }
    }
}