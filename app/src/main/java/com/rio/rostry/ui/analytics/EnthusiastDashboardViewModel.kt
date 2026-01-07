package com.rio.rostry.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.EnthusiastDashboard
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Achievement data for gamification.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@HiltViewModel
class EnthusiastDashboardViewModel @Inject constructor(
    repo: AnalyticsRepository,
    currentUserProvider: CurrentUserProvider
) : ViewModel() {
    private val empty = EnthusiastDashboard(breedingSuccessRate = 0.0, transfers = 0, engagementScore = 0.0)
    private val uid = currentUserProvider.userIdOrNull()
    
    val dashboard: StateFlow<EnthusiastDashboard> =
        (uid?.let { repo.enthusiastDashboard(it) } ?: MutableStateFlow(empty))
            .stateIn(viewModelScope, SharingStarted.Eagerly, empty)

    // ========== Premium Component Data (Comment 2) ==========
    
    /**
     * Flock health score (0-100) computed from breeding success and engagement.
     */
    val flockHealthScore: StateFlow<Float> = dashboard.map { dash ->
        val breedingComponent = (dash.breedingSuccessRate * 40).toFloat()
        val engagementComponent = (dash.engagementScore / 100 * 30).toFloat().coerceAtMost(30f)
        val transferComponent = minOf(dash.transfers.toFloat() * 3f, 30f)
        (breedingComponent + engagementComponent + transferComponent).coerceIn(0f, 100f)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 50f)
    
    /**
     * Trend direction: positive = improving, negative = declining.
     */
    val flockHealthTrend: StateFlow<Float> = dashboard.map { dash ->
        // Mock trend based on engagement - would compute from historical data
        if (dash.engagementScore > 50) 5f else if (dash.engagementScore > 20) 0f else -3f
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)
    
    /**
     * Sparkline data for inline charts.
     * Keys: "birds", "eggs", "transfers", "breeding"
     */
    val sparklineData: StateFlow<Map<String, List<Float>>> = dashboard.map { dash ->
        // Generate mock historical data points - would come from time-series DB
        mapOf(
            "birds" to generateMockSparkline(baseValue = 40f, variance = 5f),
            "eggs" to generateMockSparkline(baseValue = (dash.breedingSuccessRate * 100).toFloat(), variance = 10f),
            "transfers" to generateMockSparkline(baseValue = dash.transfers.toFloat(), variance = 2f),
            "breeding" to generateMockSparkline(baseValue = (dash.breedingSuccessRate * 100).toFloat(), variance = 8f)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())
    
    /**
     * Recently unlocked achievements.
     */
    val achievements: StateFlow<List<Achievement>> = dashboard.map { dash ->
        val list = mutableListOf<Achievement>()
        
        // Check for achievement conditions
        if (dash.breedingSuccessRate > 0.8) {
            list.add(Achievement(
                id = "breeding_master",
                title = "Breeding Master",
                description = "Achieved 80%+ breeding success rate",
                emoji = "🏆"
            ))
        }
        if (dash.transfers >= 10) {
            list.add(Achievement(
                id = "trusted_seller",
                title = "Trusted Seller",
                description = "Completed 10+ successful transfers",
                emoji = "🤝"
            ))
        }
        if (dash.engagementScore > 75) {
            list.add(Achievement(
                id = "community_leader",
                title = "Community Leader",
                description = "Top engagement in the community",
                emoji = "⭐"
            ))
        }
        
        list
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    /**
     * Generate mock sparkline data points for visualization.
     */
    private fun generateMockSparkline(
        baseValue: Float,
        variance: Float,
        points: Int = 7
    ): List<Float> {
        return (0 until points).map { i ->
            val trend = i * 0.5f // Slight upward trend
            (baseValue + trend + (-variance..variance).random()).coerceAtLeast(0f)
        }
    }
    
    private fun ClosedFloatingPointRange<Float>.random(): Float {
        return start + (kotlin.random.Random.nextFloat() * (endInclusive - start))
    }
}

