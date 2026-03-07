package com.rio.rostry.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    fun loadInsights() {
        val userId = currentUserProvider.userIdOrNull() ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val insights = mutableListOf<Insight>()
                insights.addAll(generateVaccinationInsights(userId))
                insights.addAll(generateMortalityInsights(userId))
                insights.addAll(generateProductionInsights(userId))

                _state.value = _state.value.copy(
                    isLoading = false,
                    insights = insights.sortedBy {
                        when (it.priority) {
                            InsightPriority.URGENT -> 0
                            InsightPriority.WARNING -> 1
                            InsightPriority.OPPORTUNITY -> 2
                            InsightPriority.INFO -> 3
                        }
                    }
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

        val overdueRecords = vaccinationRecordDao.getOverdueForFarmer(userId, now)
        if (overdueRecords.isNotEmpty()) {
            insights += Insight(
                id = "vax_overdue",
                title = "Overdue Vaccinations",
                description = "${overdueRecords.size} vaccination(s) are past due. Immediate attention recommended.",
                priority = InsightPriority.URGENT,
                category = InsightCategory.HEALTH,
                actionLabel = "View Schedule",
                actionRoute = "monitoring/vaccination",
                icon = "V"
            )
        }

        val allVaccinations = vaccinationRecordDao.getAllByFarmer(userId)
        val sevenDaysAhead = now + (7 * 24 * 60 * 60 * 1000L)
        val upcomingCount = allVaccinations.count {
            it.scheduledAt > now && it.scheduledAt <= sevenDaysAhead && it.administeredAt == null
        }

        if (upcomingCount > 0) {
            insights += Insight(
                id = "vax_upcoming",
                title = "Upcoming Vaccinations",
                description = "$upcomingCount vaccination(s) due in the next 7 days.",
                priority = InsightPriority.WARNING,
                category = InsightCategory.HEALTH,
                actionLabel = "View Schedule",
                actionRoute = "monitoring/vaccination",
                icon = "C"
            )
        }

        return insights
    }

    private suspend fun generateMortalityInsights(userId: String): List<Insight> {
        val insights = mutableListOf<Insight>()
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30 * 24 * 60 * 60 * 1000L)
        val recentMortalities = mortalityRecordDao.getRecordsForFarmerBetween(userId, thirtyDaysAgo, now)
        val totalDeaths = recentMortalities.sumOf { it.quantity }

        if (totalDeaths > 10) {
            insights += Insight(
                id = "mortality_high",
                title = "Elevated Mortality Rate",
                description = "$totalDeaths deaths recorded in the last 30 days. Review health protocols.",
                priority = InsightPriority.URGENT,
                category = InsightCategory.HEALTH,
                actionLabel = "View Details",
                actionRoute = "monitoring/mortality",
                icon = "!"
            )
        } else if (totalDeaths > 5) {
            insights += Insight(
                id = "mortality_moderate",
                title = "Monitor Mortality Trend",
                description = "$totalDeaths deaths in the last 30 days. Stay vigilant.",
                priority = InsightPriority.WARNING,
                category = InsightCategory.HEALTH,
                icon = "T"
            )
        }

        return insights
    }

    private suspend fun generateProductionInsights(userId: String): List<Insight> {
        val insights = mutableListOf<Insight>()
        val assets = farmAssetDao.getAssetsByFarmer(userId).first()
        val activeAssets = assets.filter { it.status?.uppercase() == "ACTIVE" }
        val totalBirds = activeAssets.fold(0) { acc, asset -> acc + asset.quantity.toInt() }

        if (totalBirds == 0) {
            insights += Insight(
                id = "no_assets",
                title = "No Active Flock",
                description = "Add your first batch or bird to start tracking performance.",
                priority = InsightPriority.INFO,
                category = InsightCategory.PRODUCTION,
                actionLabel = "Add Asset",
                actionRoute = "farmer/create_asset",
                icon = "B"
            )
        } else {
            insights += Insight(
                id = "flock_summary",
                title = "Flock Overview",
                description = "You have $totalBirds birds across ${activeAssets.size} batches.",
                priority = InsightPriority.INFO,
                category = InsightCategory.PRODUCTION,
                icon = "L"
            )
        }

        return insights
    }

    fun filterByCategory(category: InsightCategory?) {
        _state.value = _state.value.copy(selectedCategory = category)
    }

    fun refresh() {
        loadInsights()
    }
}
