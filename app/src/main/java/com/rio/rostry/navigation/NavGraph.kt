package com.rio.rostry.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.ui.auth.LoginScreen
import com.rio.rostry.ui.auth.ProfileSetupScreen
import com.rio.rostry.ui.auth.RegisterScreen
import com.rio.rostry.ui.fowl.FowlDetailScreen
import com.rio.rostry.ui.fowl.FowlListScreen
import com.rio.rostry.ui.fowl.FowlRecordCreationScreen
import com.rio.rostry.ui.fowl.FowlHistoryScreen
import com.rio.rostry.ui.transfer.TransferInitiationScreen
import com.rio.rostry.ui.transfer.TransferVerificationScreen
import com.rio.rostry.ui.transfer.TransferManagementScreen
import com.rio.rostry.ui.transfer.TransferHistoryScreen
import com.rio.rostry.ui.fowl.FowlRegistrationScreen
import com.rio.rostry.ui.main.MainScreen
import com.rio.rostry.ui.main.MainViewModel
import android.util.Log // Added for logging

@Composable
fun NavGraph(startDestination: String = "auth_graph") {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        navigation(startDestination = AuthScreen.Login.route, route = "auth_graph") {
            composable(AuthScreen.Login.route) {
                LoginScreen(
                    onLoginClick = { email, pass, onSuccess, onError ->
                        authViewModel.loginUser(email, pass, onSuccess = {
                            onSuccess()
                            navController.navigate("main_graph") {
                                popUpTo("auth_graph") { inclusive = true }
                            }
                        }, onError = onError)
                    },
                    onNavigateToRegister = {
                        navController.navigate(AuthScreen.Register.route)
                    }
                )
            }
            composable(AuthScreen.Register.route) {
                RegisterScreen(
                    onRegisterClick = { email, pass, phone, onSuccess, onError ->
                        authViewModel.registerUser(email, pass, phone, onSuccess = { uid ->
                            onSuccess()
                            navController.navigate("${AuthScreen.ProfileSetup.route}/$uid/$email/${phone ?: "null"}")
                        }, onError = onError)
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable(
                route = "${AuthScreen.ProfileSetup.route}/{uid}/{email}/{phone}",
                arguments = listOf(
                    navArgument("uid") { type = NavType.StringType },
                    navArgument("email") { type = NavType.StringType },
                    navArgument("phone") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid")!!
                val email = backStackEntry.arguments?.getString("email")!!
                val phone = backStackEntry.arguments?.getString("phone").let { if (it == "null") null else it }
                ProfileSetupScreen(
                    onProfileSetupComplete = { name, location, userType, language, onSuccess, onError ->
                        authViewModel.completeUserProfile(uid, email, phone, name, location, userType, language, onSuccess = {
                            onSuccess()
                            navController.navigate("main_graph") {
                                popUpTo("auth_graph") { inclusive = true }
                            }
                        }, onError = onError)
                    }
                )
            }
        }
        navigation(startDestination = "main_screen", route = "main_graph") {
            composable("main_screen") {
                val mainViewModel: MainViewModel = hiltViewModel()
                MainScreen(
                    viewModel = mainViewModel,
                    onNavigateToFowlRegistration = { navController.navigate(Screen.FowlRegistration.route) },
                    onNavigateToFowlDetail = { fowlId ->
                        navController.navigate(Screen.FowlDetail.createRoute(fowlId))
                    },
                    onNavigateToTransferVerification = { transferId ->
                        navController.navigate(Screen.TransferVerification.createRoute(transferId))
                    },
                    onNavigateToAuth = {
                        navController.navigate("auth_graph") {
                            popUpTo("main_graph") { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.FowlRegistration.route) {
                FowlRegistrationScreen(
                    onFowlRegistered = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.FowlDetail.route,
                arguments = listOf(navArgument("fowlId") { type = NavType.StringType })
            ) { backStackEntry ->
                val fowlId = backStackEntry.arguments?.getString("fowlId")!!
                FowlDetailScreen(
                    fowlId = fowlId,
                    onNavigateToRecordCreation = { fId -> navController.navigate(Screen.FowlRecordCreation.createRoute(fId)) },
                    onNavigateToFowlDetail = { fId -> navController.navigate(Screen.FowlDetail.createRoute(fId)) },
                    onNavigateToTransfer = { fId -> navController.navigate(Screen.InitiateTransfer.createRoute(fId)) },
                    onNavigateToHistory = { fId -> navController.navigate(Screen.FowlHistory.createRoute(fId)) }
                )
            }
            composable(
                route = Screen.FowlRecordCreation.route,
                arguments = listOf(navArgument("fowlId") { type = NavType.StringType })
            ) { backStackEntry ->
                val fowlId = backStackEntry.arguments?.getString("fowlId")!!
                FowlRecordCreationScreen(
                    fowlId = fowlId,
                    onRecordCreated = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.InitiateTransfer.route,
                arguments = listOf(navArgument("fowlId") { type = NavType.StringType })
            ) { backStackEntry ->
                val fowlId = backStackEntry.arguments?.getString("fowlId")!!
                TransferInitiationScreen(
                    fowlId = fowlId,
                    onTransferInitiated = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.TransferVerification.route,
                arguments = listOf(navArgument("transferId") { type = NavType.StringType })
            ) { backStackEntry ->
                TransferVerificationScreen(
                    onActionCompleted = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.FowlHistory.route,
                arguments = listOf(navArgument("fowlId") { type = NavType.StringType })
            ) {
                FowlHistoryScreen(onNavigateUp = { navController.popBackStack() })
            }
            composable("transfer_management") {
                TransferManagementScreen()
            }
            composable(
                route = "transfer_history/{fowlId}",
                arguments = listOf(navArgument("fowlId") { type = NavType.StringType })
            ) { backStackEntry ->
                val fowlId = backStackEntry.arguments?.getString("fowlId")!!
                TransferHistoryScreen(fowlId = fowlId)
            }
            composable(
                route = "transfer_initiation/{fowlId}",
                arguments = listOf(navArgument("fowlId") { type = NavType.StringType })
            ) { backStackEntry ->
                val fowlId = backStackEntry.arguments?.getString("fowlId")!!
                TransferInitiationScreen(
                    fowlId = fowlId,
                    onTransferInitiated = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
