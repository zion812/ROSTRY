package com.rio.rostry.core.navigation

import androidx.navigation.NavHostController

/**
 * Base interface for type-safe navigation routes.
 */
interface NavigationRoute {
    val route: String
}

/**
 * Extension function to navigate with type safety.
 */
fun NavHostController.navigateTo(route: NavigationRoute) {
    navigate(route.route)
}
