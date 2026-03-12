package com.rio.rostry.ui.insights
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

enum class InsightPriority {
    URGENT,
    WARNING,
    OPPORTUNITY,
    INFO
}

enum class InsightCategory {
    HEALTH,
    FEEDING,
    FINANCIAL,
    PRODUCTION,
    COMPLIANCE
}

data class Insight(
    val id: String,
    val title: String,
    val description: String,
    val priority: InsightPriority,
    val category: InsightCategory,
    val actionLabel: String? = null,
    val actionRoute: String? = null,
    val icon: String = "*"
)

data class InsightsState(
    val isLoading: Boolean = true,
    val insights: List<Insight> = emptyList(),
    val selectedCategory: InsightCategory? = null,
    val error: String? = null
)
