package com.rio.rostry.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDestination

/**
 * Utility class for handling navigation analytics
 */
object NavigationAnalytics {
    private const val TAG = "NavigationAnalytics"
    
    /**
     * Sets up navigation tracking for the NavController
     */
    fun setupNavigationTracking(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            trackDestination(destination, arguments)
        }
    }
    
    /**
     * Tracks a destination change
     */
    private fun trackDestination(destination: NavDestination, arguments: android.os.Bundle?) {
        val route = destination.route ?: "unknown"
        val args = arguments?.keySet()?.joinToString(", ") { key -> 
            "$key=${arguments.get(key)}" 
        } ?: "none"
        
        Log.d(TAG, "Navigated to: $route with arguments: $args")
        
        // In a real app, you would send this data to your analytics service
        // For example:
        // AnalyticsService.logEvent("screen_view", mapOf("screen_name" to route))
    }
}