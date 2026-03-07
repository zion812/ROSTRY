package com.rio.rostry.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * Registry for feature modules to register their navigation graphs.
 * Each feature module implements NavigationProvider and registers itself.
 */
interface NavigationRegistry {
    /**
     * Register a navigation provider for a feature.
     * @param provider The navigation provider to register
     */
    fun register(provider: NavigationProvider)
    
    /**
     * Get all registered navigation providers.
     * @return List of all registered providers
     */
    fun getProviders(): List<NavigationProvider>
    
    /**
     * Build all registered navigation graphs into the NavGraphBuilder.
     * @param navGraphBuilder The builder to add graphs to
     * @param navController The navigation controller for navigation actions
     */
    fun buildGraphs(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}

/**
 * Provider interface that each feature module implements.
 */
interface NavigationProvider {
    /**
     * Unique identifier for this feature's navigation.
     */
    val featureId: String
    
    /**
     * Build this feature's navigation graph.
     * @param navGraphBuilder The builder to add navigation destinations to
     * @param navController The navigation controller for navigation actions
     */
    fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
    
    /**
     * Get deep link patterns supported by this feature.
     * @return List of deep link URI patterns
     */
    fun getDeepLinks(): List<String> = emptyList()
}
