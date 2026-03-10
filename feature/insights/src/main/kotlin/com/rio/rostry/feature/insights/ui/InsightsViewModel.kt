package com.rio.rostry.ui.insights

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InsightsViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        InsightsState(
            isLoading = false,
            insights = defaultInsights(),
            selectedCategory = null,
            error = null
        )
    )
    val state: StateFlow<InsightsState> = _state.asStateFlow()

    fun loadInsights() {
        _state.value = _state.value.copy(
            isLoading = false,
            insights = defaultInsights(),
            error = null
        )
    }

    fun filterByCategory(category: InsightCategory?) {
        _state.value = _state.value.copy(selectedCategory = category)
    }

    fun refresh() {
        loadInsights()
    }

    private fun defaultInsights(): List<Insight> {
        return listOf(
            Insight(
                id = "insight_vax",
                title = "Vaccination Check",
                description = "Review upcoming vaccinations this week.",
                priority = InsightPriority.WARNING,
                category = InsightCategory.HEALTH,
                actionLabel = "Open Monitoring",
                actionRoute = "monitoring/vaccination",
                icon = "V"
            ),
            Insight(
                id = "insight_perf",
                title = "Performance Snapshot",
                description = "Check growth and mortality trends for your active flock.",
                priority = InsightPriority.INFO,
                category = InsightCategory.PRODUCTION,
                actionLabel = "Open Performance",
                actionRoute = "monitoring/performance",
                icon = "L"
            )
        )
    }
}
