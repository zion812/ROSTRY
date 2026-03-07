package com.rio.rostry.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * Fake implementation of NavigationProvider for testing.
 */
class FakeNavigationProvider(
    override val featureId: String,
    private val deepLinks: List<String> = emptyList(),
    private val buildGraphAction: (NavGraphBuilder, NavHostController) -> Unit = { _, _ -> }
) : NavigationProvider {

    var buildGraphCallCount = 0
        private set

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        buildGraphCallCount++
        buildGraphAction(navGraphBuilder, navController)
    }

    override fun getDeepLinks(): List<String> = deepLinks
}
