package com.rio.rostry.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

/**
 * Sealed class hierarchy that defines all the possible destinations in the app.
 * Each object or class in this hierarchy represents a screen or a group of screens
 * with specific arguments.
 */
sealed class AppDestination(val route: String) {
    object Login : AppDestination("login")
    object DebugLogin : AppDestination("debug_login")
    object SignUp : AppDestination("signup")
    object ProfileSetup : AppDestination("profile_setup")
    object Home : AppDestination("home")
    object Marketplace : AppDestination("marketplace")
    object Community : AppDestination("community")
    object Cart : AppDestination("cart")
    object Profile : AppDestination("profile")
    object Dashboard : AppDestination("dashboard")
    object Transfer : AppDestination("transfer")
    
    object AddFowl : AppDestination("add_fowl")
    data class EditFowl(val fowlId: String) : AppDestination("edit_fowl/{$fowlIdArg}") {
        companion object {
            const val fowlIdArg = "fowlId"
        }
        
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(fowlIdArg) { type = NavType.StringType }
            )
            
        override val deepLinks: List<androidx.navigation.NavDeepLink>
            get() = listOf(
                navDeepLink { uriPattern = "rostry://edit_fowl/{fowlId}" }
            )
    }
    
    data class FowlDetail(val fowlId: String) : AppDestination("fowl_detail/{$fowlIdArg}") {
        companion object {
            const val fowlIdArg = "fowlId"
        }
        
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(fowlIdArg) { type = NavType.StringType }
            )
            
        override val deepLinks: List<androidx.navigation.NavDeepLink>
            get() = listOf(
                navDeepLink { uriPattern = "rostry://fowl/{fowlId}" }
            )
    }
    
    data class AddHealthRecord(val fowlId: String) : AppDestination("add_health_record/{$fowlIdArg}") {
        companion object {
            const val fowlIdArg = "fowlId"
        }
        
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(fowlIdArg) { type = NavType.StringType }
            )
            
        override val deepLinks: List<androidx.navigation.NavDeepLink>
            get() = listOf(
                navDeepLink { uriPattern = "rostry://add_health_record/{fowlId}" }
            )
    }
    
    /**
     * Override this property to provide navigation arguments for destinations
     * that require them.
     */
    open val arguments: List<NamedNavArgument> = emptyList()
    
    /**
     * Override this property to provide deep links for destinations
     * that support them.
     */
    open val deepLinks: List<androidx.navigation.NavDeepLink> = emptyList()
}

// Route building utilities
object AppRoutes {
    fun editFowl(fowlId: String) = "edit_fowl/$fowlId"
    fun fowlDetail(fowlId: String) = "fowl_detail/$fowlId"
    fun addHealthRecord(fowlId: String) = "add_health_record/$fowlId"
}