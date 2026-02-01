package com.rio.rostry.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Types of insights categorized by priority/action type.
 */
enum class InsightPriority {
    URGENT,      // Immediate action needed
    WARNING,     // Attention recommended
    OPPORTUNITY, // Potential improvement
    INFO         // General information
}

/**
 * Categories of insights.
 */
enum class InsightCategory {
    HEALTH,
    FEEDING,
    FINANCIAL,
    PRODUCTION,
    COMPLIANCE
}

/**
 * Data class representing a single insight/recommendation.
 */
data class Insight(
    val id: String,
    val title: String,
    val description: String,
    val priority: InsightPriority,
    val category: InsightCategory,
    val actionLabel: String? = null,
    val actionRoute: String? = null,
    val icon: String = "💡"
)

/**
 * State for the InsightsScreen.
 */
data class InsightsState(
    val isLoading: Boolean = true,
    val insights: List<Insight> = emptyList(),
    val selectedCategory: InsightCategory? = null,
    val error: String? = null
)

/**
 * ViewModel for Smart Insights screen.
 * Analyzes farm data and generates personalized recommendations.
 */
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val farmAssetDao: FarmAssetDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val mortalityRecordDao: MortalityRecordDao,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {
    
    private val _state = MutableStateFlow(InsightsState())
    val state: StateFlow<InsightsState> = _state.asStateFlow()
    
    init {
        loadInsights()
    }
    
    /**
     * Load and generate all insights.
     */
    fun loadInsights() {
        val userId = currentUserProvider.userIdOrNull() ?: return
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val insights = mutableListOf<Insight>()
                
                // 1. Vaccination Insights
                insights.addAll(generateVaccinationInsights(userId))
                
                // 2. Mortality Insights
                insights.addAll(generateMortalityInsights(userId))
                
                // 3. Production Insights
                insights.addAll(generateProductionInsights(userId))
                
                // Sort by priority
                val sortedInsights = insights.sortedBy { 
                    when (it.priority) {
                        InsightPriority.URGENT -> 0
                        InsightPriority.WARNING -> 1
                        InsightPriority.OPPORTUNITY -> 2
                        InsightPriority.INFO -> 3
                    }
                }
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    insights = sortedInsights
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to generate insights"
                )
            }
        }
    }
    
    private suspend fun generateVaccinationInsights(userId: String): List<Insight> {
        val insights = mutableListOf<Insight>()
        val now = System.currentTimeMillis()
        
        // Check for overdue vaccinations using existing DAO method
        val overdueRecords = vaccinationRecordDao.getOverdueForFarmer(userId, now)
        if (overdueRecords.isNotEmpty()) {
            insights.add(
                Insight(
                    id = "vax_overdue",
                    title = "Overdue Vaccinations",
                    description = "${overdueRecords.size} vaccination(s) are past due. Immediate attention recommended.",
                    priority = InsightPriority.URGENT,
                    category = InsightCategory.HEALTH,
                    actionLabel = "View Schedule",
                    actionRoute = "monitoring/vaccination",
                    icon = "💉"
                )
            )
        }
        
        // Check all vaccinations for upcoming ones
        val allVaccinations = vaccinationRecordDao.getAllByFarmer(userId)
        val sevenDaysAhead = now + (7 * 24 * 60 * 60 * 1000L)
        val upcomingCount = allVaccinations.count { 
            it.scheduledAt > now && it.scheduledAt <= sevenDaysAhead && it.administeredAt == null
        }
        
        if (upcomingCount > 0) {
            insights.add(
                Insight(
                    id = "vax_upcoming",
                    title = "Upcoming Vaccinations",
                    description = "$upcomingCount vaccination(s) due in the next 7 days.",
                    priority = InsightPriority.WARNING,
                    category = InsightCategory.HEALTH,
                    actionLabel = "View Schedule",
                    actionRoute = "monitoring/vaccination",
                    icon = "📅"
                )
            )
        }
        
        return insights
    }
    
    private suspend fun generateMortalityInsights(userId: String): List<Insight> {
        val insights = mutableListOf<Insight>()
        
        // Get mortality records for farmer in last 30 days
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30 * 24 * 60 * 60 * 1000L)
        
        val recentMortalities = mortalityRecordDao.getRecordsForFarmerBetween(userId, thirtyDaysAgo, now)
        val totalDeaths = recentMortalities.sumOf { it.quantity }
        
        if (totalDeaths > 10) {
            insights.add(
                Insight(
                    id = "mortality_high",
                    title = "Elevated Mortality Rate",
                    description = "$totalDeaths deaths recorded in the last 30 days. Review health protocols.",
                    priority = InsightPriority.URGENT,
                    category = InsightCategory.HEALTH,
                    actionLabel = "View Details",
                    actionRoute = "monitoring/mortality",
                    icon = "⚠️"
                )
            )
        } else if (totalDeaths > 5) {
            insights.add(
                Insight(
                    id = "mortality_moderate",
                    title = "Monitor Mortality Trend",
                    description = "$totalDeaths deaths in the last 30 days. Stay vigilant.",
                    priority = InsightPriority.WARNING,
                    category = InsightCategory.HEALTH,
                    icon = "📊"
                )
            )
        }
        
        return insights
    }
    
    private suspend fun generateProductionInsights(userId: String): List<Insight> {
        val insights = mutableListOf<Insight>()
        
        // Get assets using existing Flow method
        val assetsFlow = farmAssetDao.getAssetsByFarmer(userId)
        val assets = assetsFlow.first()
        val activeAssets = assets.filter { it.status?.uppercase() == "ACTIVE" }
        val totalBirds = activeAssets.fold(0) { acc, asset -> acc + asset.quantity.toInt() }
        
        if (totalBirds == 0) {
            insights.add(
                Insight(
                    id = "no_assets",
                    title = "No Active Flock",
                    description = "Add your first batch or bird to start tracking performance.",
                    priority = InsightPriority.INFO,
                    category = InsightCategory.PRODUCTION,
                    actionLabel = "Add Asset",
                    actionRoute = "farmer/create_asset",
                    icon = "🐓"
                )
            )
        } else {
            insights.add(
                Insight(
                    id = "flock_summary",
                    title = "Flock Overview",
                    description = "You have $totalBirds birds across ${activeAssets.size} batches.",
                    priority = InsightPriority.INFO,
                    category = InsightCategory.PRODUCTION,
                    icon = "📋"
                )
            )
            
        // Note: Financial insights would require calculating costs from activity logs
        // For now, just showing flock size info
        }
        
        return insights
    }
    
    /**
     * Filter insights by category.
     */
    fun filterByCategory(category: InsightCategory?) {
        _state.value = _state.value.copy(selectedCategory = category)
    }
    
    /**
     * Refresh insights.
     */
    fun refresh() {
        loadInsights()
    }
}
