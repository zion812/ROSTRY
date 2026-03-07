package com.rio.rostry.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * Implementation of NavigationRegistry.
 * Allows feature modules to register their navigation graphs.
 */
class NavigationRegistryImpl : NavigationRegistry {
    private val providers = mutableListOf<NavigationProvider>()

    override fun register(provider: NavigationProvider) {
        providers.add(provider)
    }

    override fun getProviders(): List<NavigationProvider> {
        return providers.toList()
    }

    override fun buildGraphs(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        providers.forEach { provider ->
            provider.buildGraph(navGraphBuilder, navController)
        }
    }
}
