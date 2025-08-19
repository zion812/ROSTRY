package com.rio.rostry.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rio.rostry.navigation.AppDestination
import com.rio.rostry.navigation.BottomNavBar

@Composable
fun MainScreen(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination
    
    // Define which screens should show the bottom navigation bar
    val showBottomBar = currentDestination?.route in listOf(
        AppDestination.Home.route,
        AppDestination.Marketplace.route,
        AppDestination.Community.route,
        AppDestination.Cart.route,
        AppDestination.Profile.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}