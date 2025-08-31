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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rio.rostry.data.models.UserType
import com.rio.rostry.navigation.MainScreen
import com.rio.rostry.ui.main.screens.EnthusiastHomeScreen
import com.rio.rostry.ui.main.screens.FarmerHomeScreen
import com.rio.rostry.ui.fowl.FowlListScreen
import com.rio.rostry.ui.main.screens.GeneralHomeScreen
import com.rio.rostry.ui.main.screens.PlaceholderScreen
import com.rio.rostry.ui.main.screens.profile.ProfileScreen
import com.rio.rostry.ui.main.screens.SettingsScreen
import com.rio.rostry.ui.market.MarketplaceScreen
import com.rio.rostry.ui.market.sell.CreateListingScreen
import com.rio.rostry.ui.market.sell.SelectFowlScreen
import com.rio.rostry.ui.market.sell.SellViewModel
import com.rio.rostry.ui.market.cart.CartScreen
import com.rio.rostry.ui.market.payment.PaymentScreen
import com.rio.rostry.ui.messaging.ChatScreen
import com.rio.rostry.ui.messaging.ConversationScreen
import com.rio.rostry.ui.market.order.OrderListScreen
import com.rio.rostry.ui.market.order.OrderDetailScreen
import com.rio.rostry.ui.market.wishlist.WishlistScreen
import com.rio.rostry.ui.transfer.TransfersScreen

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToFowlRegistration: () -> Unit,
    onNavigateToFowlDetail: (String) -> Unit,
    onNavigateToTransferVerification: (String) -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val navController = rememberNavController()

    val items = when (user?.userType) {
        is UserType.General -> listOf(
            MainScreen.GeneralHome,
            MainScreen.GeneralFeed,
            MainScreen.Conversations,
            MainScreen.Orders,
            MainScreen.Transfers,
            MainScreen.GeneralCommunity,
            MainScreen.GeneralProfile
        )
        is UserType.Farmer -> listOf(
            MainScreen.FarmerHome,
            MainScreen.Fowls,
            MainScreen.FarmerMarketplace,
            MainScreen.Conversations,
            MainScreen.Orders,
            MainScreen.Transfers,
            MainScreen.FarmerProfile
        )
        is UserType.HighLevelEnthusiast -> listOf(
            MainScreen.EnthusiastHome,
            MainScreen.Fowls,
            MainScreen.Conversations,
            MainScreen.Orders,
            MainScreen.Transfers,
            MainScreen.EnthusiastAnalytics,
            MainScreen.EnthusiastProfile
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
                            icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                            label = { screen.title?.let { Text(it) } },
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
                composable(MainScreen.GeneralProfile.route) { ProfileScreen(onNavigateToAuth = onNavigateToAuth) }
                composable(MainScreen.GeneralSettings.route) { SettingsScreen() }

                // Farmer
                                composable(MainScreen.FarmerMarketplace.route) { MarketplaceScreen(
                    onSellFowl = { navController.navigate(MainScreen.SelectFowl.route) },
                    onNavigateToCart = { navController.navigate(MainScreen.Cart.route) },
                    onNavigateToWishlist = { navController.navigate(MainScreen.Wishlist.route) },
                    onNavigateToChat = { conversationId ->
                        navController.navigate(MainScreen.Chat.createRoute(conversationId))
                    }
                ) }
                composable(MainScreen.Cart.route) { 
                    CartScreen(onNavigateToCheckout = { navController.navigate(MainScreen.Payment.route) }) 
                }
                composable(MainScreen.Wishlist.route) { WishlistScreen() }
                composable(MainScreen.SelectFowl.route) {
                    SelectFowlScreen(
                        onFowlSelected = { fowlId ->
                            navController.navigate("create_listing/$fowlId")
                        },
                        onNavigateUp = { navController.navigateUp() }
                    )
                }
                composable(
                    route = MainScreen.CreateListing.route,
                    arguments = listOf(navArgument("fowlId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val fowlId = backStackEntry.arguments?.getString("fowlId")
                    val sellViewModel: SellViewModel = hiltViewModel()
                    if (fowlId != null) {
                        LaunchedEffect(fowlId) {
                            sellViewModel.onFowlSelected(fowlId)
                        }
                        CreateListingScreen(
                            viewModel = sellViewModel,
                            onListingCreated = {
                                navController.popBackStack(MainScreen.FarmerMarketplace.route, inclusive = false)
                            },
                            onNavigateUp = { navController.navigateUp() }
                        )
                    } else {
                        navController.navigateUp()
                    }
                }
                composable(MainScreen.FarmerWeather.route) { PlaceholderScreen(name = "Weather") }
                composable(MainScreen.FarmerProfile.route) { ProfileScreen(onNavigateToAuth = onNavigateToAuth) }
                composable(MainScreen.FarmerSettings.route) { SettingsScreen() }

                composable(MainScreen.Conversations.route) {
                    ConversationScreen(onNavigateToChat = {
                        navController.navigate(MainScreen.Chat.createRoute(it))
                    })
                }
                composable(
                    route = MainScreen.Chat.route,
                    arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val conversationId = backStackEntry.arguments?.getString("conversationId")
                    if (conversationId != null) {
                        ChatScreen(conversationId = conversationId)
                    } else {
                        navController.navigateUp()
                    }
                }

                composable(MainScreen.Orders.route) {
                    OrderListScreen(onOrderClick = { orderId ->
                        navController.navigate(MainScreen.OrderDetail.createRoute(orderId))
                    })
                }

                composable(
                    route = MainScreen.OrderDetail.route,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) {
                    OrderDetailScreen()
                }

                composable(MainScreen.Payment.route) { com.rio.rostry.ui.market.payment.PaymentScreen() }

                composable(MainScreen.Transfers.route) {
                    TransfersScreen(onNavigateToVerification = onNavigateToTransferVerification)
                }

                // Enthusiast
                composable(MainScreen.EnthusiastAnalytics.route) { PlaceholderScreen(name = "Analytics") }
                composable(MainScreen.EnthusiastResearch.route) { PlaceholderScreen(name = "Research") }
                composable(MainScreen.EnthusiastProfile.route) { ProfileScreen(onNavigateToAuth = onNavigateToAuth) }
                composable(MainScreen.EnthusiastSettings.route) { SettingsScreen() }
                composable(MainScreen.Cart.route) { 
                    CartScreen(onNavigateToCheckout = { navController.navigate(MainScreen.Payment.route) }) 
                }
                composable(MainScreen.Wishlist.route) { WishlistScreen() }
            }
        }
    }
}
