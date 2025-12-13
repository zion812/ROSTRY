package com.rio.rostry.ui.general

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.general.cart.GeneralCartRoute
import com.rio.rostry.ui.general.create.GeneralCreateRoute
import com.rio.rostry.ui.general.explore.GeneralExploreRoute
import com.rio.rostry.ui.general.market.GeneralMarketRoute
import com.rio.rostry.ui.general.profile.GeneralProfileRoute
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsViewModel
import com.rio.rostry.ui.navigation.Routes

private data class GeneralNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun GeneralUserScreen(
    modifier: Modifier = Modifier,
    onOpenProductDetails: (String) -> Unit,
    onOpenTraceability: (String) -> Unit,
    onOpenSocialFeed: () -> Unit,

    onOpenMessages: (String) -> Unit,
    onScanQr: () -> Unit = {},
    initialTabRoute: String? = null
) {
    val navController = rememberNavController()
    val analyticsViewModel: GeneralAnalyticsViewModel = hiltViewModel()
    val tabs = listOf(
        GeneralNavItem(Routes.GeneralNav.MARKET, "Market", Icons.Filled.Home),
        GeneralNavItem(Routes.GeneralNav.EXPLORE, "Explore", Icons.Filled.Search),
        GeneralNavItem(Routes.GeneralNav.CREATE, "Create", Icons.Filled.Add),
        GeneralNavItem(Routes.GeneralNav.CART, "Cart", Icons.Filled.ShoppingCart),
        GeneralNavItem(Routes.GeneralNav.PROFILE, "Profile", Icons.Filled.Person)
    )

    // If an initial tab route is provided (e.g., Routes.GeneralNav.MARKET), navigate to it once
    androidx.compose.runtime.LaunchedEffect(initialTabRoute) {
        val route = initialTabRoute
        if (!route.isNullOrBlank()) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            GeneralBottomBar(
                navController = navController,
                items = tabs,
                onTabSelected = { analyticsViewModel.tracker.navTabClick(it) }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.GeneralNav.MARKET,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.GeneralNav.MARKET) {
                GeneralMarketRoute(
                    onOpenProductDetails = onOpenProductDetails,
                    onOpenTraceability = onOpenTraceability
                )
            }
            composable(Routes.GeneralNav.EXPLORE) {
                GeneralExploreRoute(
                    onOpenSocialFeed = onOpenSocialFeed,
                    onOpenMessages = onOpenMessages,
                    onScanQr = onScanQr
                )
            }
            composable(Routes.GeneralNav.CREATE) {
                GeneralCreateRoute(
                    onPostCreated = { navController.navigate(Routes.GeneralNav.EXPLORE) }
                )
            }
            composable(Routes.GeneralNav.CART) {
                GeneralCartRoute(
                    onCheckoutComplete = { navController.navigate(Routes.GeneralNav.MARKET) }
                )
            }
            composable(Routes.GeneralNav.PROFILE) {
                GeneralProfileRoute()
            }
        }
    }
}

@Composable
private fun GeneralBottomBar(
    navController: NavHostController,
    items: List<GeneralNavItem>,
    onTabSelected: (String) -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination.isOnRoute(item.route)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    onTabSelected(item.route)
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

private fun NavDestination?.isOnRoute(route: String): Boolean {
    if (this == null) return false
    val currentRoute = this.route ?: return false
    return currentRoute.substringBefore("/{") == route.substringBefore("/{")
}

