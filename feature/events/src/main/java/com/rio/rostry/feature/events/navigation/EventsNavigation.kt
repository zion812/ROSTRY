package com.rio.rostry.feature.events.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute
import com.rio.rostry.ui.events.EventsScreen

/**
 * Navigation routes for events feature.
 */
sealed class EventsRoute(override val route: String) : NavigationRoute {
    object Events : EventsRoute("social/events")
    object EventDetails : EventsRoute("social/events/{eventId}") {
        fun createRoute(eventId: String) = "social/events/$eventId"
    }
}

/**
 * Navigation provider for events feature.
 */
class EventsNavigationProvider : NavigationProvider {
    override val featureId: String = "events"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(EventsRoute.Events.route) {
                EventsScreen(
                    events = emptyList(),
                    onRsvpGoing = {},
                    onRsvpInterested = {},
                    onCreateEvent = { _, _, _ -> },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(EventsRoute.EventDetails.route) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventsScreen(
                    events = emptyList(),
                    onRsvpGoing = {},
                    onRsvpInterested = {},
                    onCreateEvent = { _, _, _ -> },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://social/events",
        "https://rostry.app/social/events"
    )
}
