package com.rio.rostry.feature.breeding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for breeding feature.
 */
sealed class BreedingRoute(override val route: String) : NavigationRoute {
    object Plan : BreedingRoute("farm/breeding/plan")
    object Pedigree : BreedingRoute("farm/breeding/pedigree")
}

/**
 * Navigation provider for breeding feature.
 */
class BreedingNavigationProvider : NavigationProvider {
    override val featureId: String = "breeding"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(BreedingRoute.Plan.route) {
                // TODO: Connect to BreedingPlanScreen
            }

            composable(BreedingRoute.Pedigree.route) {
                // TODO: Connect to PedigreeScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://farm/breeding",
        "https://rostry.app/farm/breeding"
    )
}
