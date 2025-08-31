package com.rio.rostry.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rio.rostry.data.models.UserType
import com.rio.rostry.navigation.MainScreen
import com.rio.rostry.ui.main.screens.EnthusiastHomeScreen
import com.rio.rostry.ui.main.screens.FarmerHomeScreen
import com.rio.rostry.ui.fowl.FowlListScreen
import com.rio.rostry.ui.main.screens.GeneralHomeScreen
import com.rio.rostry.ui.main.screens.PlaceholderScreen
import com.rio.rostry.ui.main.screens.ProfileScreen
import com.rio.rostry.ui.main.screens.SettingsScreen
import com.rio.rostry.ui.market.MarketplaceScreen

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToFowlRegistration: () -> Unit,
    onNavigateToFowlDetail: (String) -> Unit
) {
    val user by viewModel.user.collectAsState()
    val navController = rememberNavController()

    val items = when (user?.userType) {
        UserType.General.role -> listOf(
            MainScreen.GeneralHome,
            MainScreen.GeneralFeed,
            MainScreen.GeneralCommunity,
            MainScreen.GeneralProfile,
            MainScreen.GeneralSettings
        )
        UserType.Farmer.role -> listOf(
            MainScreen.FarmerHome,
            MainScreen.Fowls,
            MainScreen.FarmerMarketplace,
            MainScreen.FarmerWeather,
            MainScreen.FarmerProfile,
            MainScreen.FarmerSettings
        )
        UserType.HighLevelEnthusiast.role -> listOf(
            MainScreen.EnthusiastHome,
            MainScreen.Fowls,
            MainScreen.EnthusiastAnalytics,
            MainScreen.EnthusiastResearch,
            MainScreen.EnthusiastProfile,
            MainScreen.EnthusiastSettings
        )
        else -> emptyList()
    }

    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToFowlRegistration) {
                    Icon(Icons.Default.Add, contentDescription = "Add Fowl")
                }
            },
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = items.first().route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // General
                composable(MainScreen.GeneralHome.route) { GeneralHomeScreen() }
                // Farmer
                composable(MainScreen.FarmerHome.route) { FarmerHomeScreen() }
                // Enthusiast
                composable(MainScreen.Fowls.route) { FowlListScreen(onFowlClick = onNavigateToFowlDetail, onNavigateToRegistration = onNavigateToFowlRegistration) }
                composable(MainScreen.EnthusiastHome.route) { EnthusiastHomeScreen() }
                composable(MainScreen.GeneralFeed.route) { PlaceholderScreen(name = "Feed") }
                composable(MainScreen.GeneralCommunity.route) { PlaceholderScreen(name = "Community") }
                composable(MainScreen.GeneralProfile.route) { ProfileScreen() }
                composable(MainScreen.GeneralSettings.route) { SettingsScreen() }

                // Farmer
                                composable(MainScreen.FarmerMarketplace.route) { MarketplaceScreen() }
                composable(MainScreen.FarmerWeather.route) { PlaceholderScreen(name = "Weather") }
                composable(MainScreen.FarmerProfile.route) { ProfileScreen() }
                composable(MainScreen.FarmerSettings.route) { SettingsScreen() }

                // Enthusiast
                composable(MainScreen.EnthusiastAnalytics.route) { PlaceholderScreen(name = "Analytics") }
                composable(MainScreen.EnthusiastResearch.route) { PlaceholderScreen(name = "Research") }
                composable(MainScreen.EnthusiastProfile.route) { ProfileScreen() }
                composable(MainScreen.EnthusiastSettings.route) { SettingsScreen() }
            }
        }
    }
}
