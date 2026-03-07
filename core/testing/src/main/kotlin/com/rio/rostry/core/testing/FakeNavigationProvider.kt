package com.rio.rostry.core.testing

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.rio.rostry.core.navigation.NavigationProvider

/**
 * Fake implementation of NavigationProvider for testing.
 */
class FakeNavigationProvider(
    override val featureId: String = "test-feature",
    private val deepLinks: List<String> = emptyList(),
    private val onBuildGraph: (NavGraphBuilder, NavHostController) -> Unit = { _, _ -> }
) : NavigationProvider {
    
    var buildGraphCallCount = 0
        private set
    
    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        buildGraphCallCount++
        onBuildGraph(navGraphBuilder, navController)
    }
    
    override fun getDeepLinks(): List<String> = deepLinks
}
