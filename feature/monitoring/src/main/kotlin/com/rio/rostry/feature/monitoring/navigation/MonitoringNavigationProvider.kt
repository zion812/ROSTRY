package com.rio.rostry.feature.monitoring.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.rio.rostry.core.navigation.NavigationProvider

/**
 * Navigation provider for monitoring feature.
 * Handles farm monitoring, health tracking, breeding, hatching, daily logs, and farm activities.
 * 
 * NOTE: This is a placeholder. The actual MonitoringNavigationProvider is currently in the
 * app module (com.rio.rostry.ui.monitoring.navigation.MonitoringNavigationProvider) because
 * the monitoring screens haven't been migrated to this feature module yet.
 * 
 * Once the screens are migrated from app/ui/monitoring to feature/monitoring/ui, this
 * provider should be implemented here and the app module version should be removed.
 * 
 * See Task 7.1.5 for screen migration.
 */
class MonitoringNavigationProvider : NavigationProvider {
    override val featureId: String = "monitoring-placeholder"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        // Placeholder - actual implementation is in app module
        // This will be implemented when screens are migrated to this feature module
    }

    override fun getDeepLinks(): List<String> = emptyList()
}

