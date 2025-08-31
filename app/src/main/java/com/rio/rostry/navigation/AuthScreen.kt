package com.rio.rostry.navigation

sealed class AuthScreen(val route: String) {
    data object Login : AuthScreen("login")
    data object Register : AuthScreen("register")
    data object ProfileSetup : AuthScreen("profile_setup")
}
