package com.rio.rostry.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainScreen(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    // General User
    data object GeneralHome : MainScreen("general_home", "Home", Icons.Filled.Home)
    data object GeneralFeed : MainScreen("general_feed", "Feed", Icons.Filled.Feed)
    data object GeneralCommunity : MainScreen("general_community", "Community", Icons.Filled.Groups)
    data object GeneralProfile : MainScreen("general_profile", "Profile", Icons.Filled.AccountCircle)
    data object GeneralSettings : MainScreen("general_settings", "Settings", Icons.Filled.Settings)

    // Farmer
    data object FarmerHome : MainScreen("farmer_home", "Home", Icons.Filled.Home)
    data object FarmerMarketplace : MainScreen("farmer_marketplace", "Marketplace", Icons.Filled.Storefront)
    data object FarmerWeather : MainScreen("farmer_weather", "Weather", Icons.Filled.WbSunny)
    data object FarmerProfile : MainScreen("farmer_profile", "Profile", Icons.Filled.AccountCircle)
    data object Fowls : MainScreen("fowls", "Fowls", Icons.Filled.Pets)
    data object FarmerSettings : MainScreen("farmer_settings", "Settings", Icons.Filled.Settings)

    // High-Level Enthusiast
    data object EnthusiastHome : MainScreen("enthusiast_home", "Home", Icons.Filled.Home)
    data object EnthusiastAnalytics : MainScreen("enthusiast_analytics", "Analytics", Icons.Filled.Analytics)
    data object EnthusiastResearch : MainScreen("enthusiast_research", "Research", Icons.Filled.Science)
    data object EnthusiastProfile : MainScreen("enthusiast_profile", "Profile", Icons.Filled.AccountCircle)
    data object EnthusiastSettings : MainScreen("enthusiast_settings", "Settings", Icons.Filled.Settings)

    // Marketplace
    data object Splash : MainScreen("splash_screen")
    object Login : MainScreen("login_screen")
    object Cart : MainScreen("cart_screen")
    object Wishlist : MainScreen("wishlist_screen")
    data object SelectFowl : MainScreen("select_fowl", "Select Fowl", Icons.Filled.Pets)
    data object CreateListing : MainScreen("create_listing/{fowlId}", "Create Listing", Icons.Filled.Add)
    data object Conversations : MainScreen("conversations_screen", "Messages", Icons.Filled.Message)
    data object Chat : MainScreen("chat_screen/{conversationId}", "Chat") {
        fun createRoute(conversationId: String) = "chat_screen/$conversationId"
    }
    data object Orders : MainScreen("orders_screen", "Orders", Icons.Filled.Receipt)
    data object OrderDetail : MainScreen("order_detail/{orderId}", "Order Detail") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    data object Payment : MainScreen("payment_screen", "Payment")

    data object Transfers : MainScreen("transfers_screen", "Transfers", Icons.Filled.SwapHoriz)

}
