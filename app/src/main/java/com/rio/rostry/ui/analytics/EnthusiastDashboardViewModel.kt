package com.rio.rostry.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.EnthusiastDashboard
import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.MatingLogDao
import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
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

/**
 * ENHANCED: Enthusiast Dashboard ViewModel with REAL sparklines from AnalyticsDao.
 * No more mock data!
 */
@HiltViewModel
class EnthusiastDashboardViewModel @Inject constructor(
    private val repo: AnalyticsRepository,
    private val analyticsDao: AnalyticsDao,
    private val breedingPairDao: BreedingPairDao,
    private val eggCollectionDao: EggCollectionDao,
    private val matingLogDao: MatingLogDao,
    private val hatchingBatchDao: com.rio.rostry.data.database.dao.HatchingBatchDao,
    private val hatchingLogDao: com.rio.rostry.data.database.dao.HatchingLogDao,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {
    private val empty = EnthusiastDashboard(breedingSuccessRate = 0.0, transfers = 0, engagementScore = 0.0)
    private val uid = currentUserProvider.userIdOrNull()
    
    val dashboard: StateFlow<EnthusiastDashboard> =
        (uid?.let { repo.enthusiastDashboard(it) } ?: MutableStateFlow(empty))
            .stateIn(viewModelScope, SharingStarted.Eagerly, empty)

    // ========== Premium Component Data (REAL DATA NOW!) ==========
    
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
     * NOW uses real historical data!
     */
    private val _flockHealthTrend = MutableStateFlow(0f)
    val flockHealthTrend: StateFlow<Float> = _flockHealthTrend
    
    /**
     * REAL Sparkline data for inline charts.
     * Keys: "birds", "eggs", "transfers", "breeding"
     */
    private val _sparklineData = MutableStateFlow<Map<String, List<Float>>>(emptyMap())
    val sparklineData: StateFlow<Map<String, List<Float>>> = _sparklineData
    
    // ========== Hatchability Tracker Data ==========
    
    data class BreedingPairStat(
        val pairId: String,
        val pairName: String,
        val eggsCollected: Int,
        val hatchRate: Float, // 0.0 to 1.0
        val trend: Float, // percentage change
        val isActive: Boolean
    )

    /**
     * Real-time stats for active breeding pairs.
     * Aggregates data from BreedingPairDao, EggCollectionDao, and HatchingBatchDao.
     */
    val breedingPairStats: StateFlow<List<BreedingPairStat>> = flow {
        val userId = uid
        if (userId != null) {
            breedingPairDao.observeActive(userId).collect { pairs ->
                // For each pair, calculate stats (this could be optimized with a complex query, 
                // but for MVP doing it imperatively here is acceptable as pair count is low <20)
                val statsList = pairs.map { pair ->
                    // 1. Get total eggs collected for this pair
                    val totalEggs = eggCollectionDao.getTotalEggsByPair(pair.pairId)
                    
                    // 2. Calculate Hatch Rate
                    // Get all egg collections -> find linked hatching batches
                    val collections = eggCollectionDao.getCollectionsByPair(pair.pairId)
                    val collectionIds = collections.map { it.collectionId }
                    
                    var hatchedCount = 0
                    var incubatedCount = 0
                    
                    if (collectionIds.isNotEmpty()) {
                        val batches = hatchingBatchDao.getBySourceCollectionIds(collectionIds)
                        batches.forEach { batch ->
                            val eggsSet = batch.eggsCount ?: 0
                            if (eggsSet > 0) {
                                incubatedCount += eggsSet
                                // Count 'HATCHED' logs for this batch
                                val hatchedInBatch = hatchingLogDao.countByBatchAndType(batch.batchId, "HATCHED")
                                hatchedCount += hatchedInBatch
                            }
                        }
                    }
                    
                    val realHatchRate = if (incubatedCount > 0) {
                        (hatchedCount.toFloat() / incubatedCount.toFloat())
                    } else {
                        0f
                    }
                    
                    BreedingPairStat(
                        pairId = pair.pairId,
                        pairName = "Pair ${pair.pairId.takeLast(4).uppercase()}", // Friendly name fallback
                        eggsCollected = totalEggs,
                        hatchRate = realHatchRate,
                        trend = 0.0f, // Trend requires historical snapshots comparison
                        isActive = true
                    )
                }
                emit(statsList)
            }
        } else {
            emit(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
    
    init {
        loadRealSparklineData()
        calculateHealthTrend()
    }
    
    /**
     * Load REAL sparkline data from AnalyticsDao (last 7 days).
     */
    private fun loadRealSparklineData() {
        val userId = uid ?: return
        
        viewModelScope.launch {
            // Get last 7 days date range
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val endDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val startDate = dateFormat.format(calendar.time)
            
            analyticsDao.streamRange(userId, startDate, endDate).collect { entities ->
                if (entities.isNotEmpty()) {
                    // Build REAL sparkline data from analytics entities
                    val birdsData = mutableListOf<Float>()
                    val eggsData = mutableListOf<Float>()
                    val transfersData = mutableListOf<Float>()
                    val breedingData = mutableListOf<Float>()
                    
                    entities.forEach { entity ->
                        // For birds, use product views as a proxy (would need product count tracking)
                        birdsData.add(entity.productViews.toFloat())
                        // Breeding rate as percentage
                        breedingData.add((entity.breedingSuccessRate * 100).toFloat())
                        // Transfers
                        transfersData.add(entity.transfersCount.toFloat())
                        // Engagement as proxy for eggs activity
                        eggsData.add(entity.engagementScore.toFloat())
                    }
                    
                    _sparklineData.value = mapOf(
                        "birds" to birdsData.takeLast(7),
                        "eggs" to eggsData.takeLast(7),
                        "transfers" to transfersData.takeLast(7),
                        "breeding" to breedingData.takeLast(7)
                    )
                } else {
                    // Fallback: Generate minimal placeholder if no data exists yet
                    _sparklineData.value = mapOf(
                        "birds" to listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
                        "eggs" to listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
                        "transfers" to listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
                        "breeding" to listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)
                    )
                }
            }
        }
    }
    
    /**
     * Calculate health trend from real historical data.
     */
    private fun calculateHealthTrend() {
        val userId = uid ?: return
        
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val endDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -14)
            val startDate = dateFormat.format(calendar.time)
            
            val entities = analyticsDao.listRange(userId, startDate, endDate)
            
            if (entities.size >= 2) {
                // Compare first half vs second half average engagement
                val midpoint = entities.size / 2
                val firstHalf = entities.take(midpoint)
                val secondHalf = entities.drop(midpoint)
                
                val firstAvg = firstHalf.map { it.engagementScore }.average()
                val secondAvg = secondHalf.map { it.engagementScore }.average()
                
                val trend = ((secondAvg - firstAvg) / maxOf(firstAvg, 1.0) * 100).toFloat()
                _flockHealthTrend.value = trend.coerceIn(-20f, 20f)
            } else {
                _flockHealthTrend.value = 0f
            }
        }
    }
    
    /**
     * Refresh all data.
     */
    fun refresh() {
        loadRealSparklineData()
        calculateHealthTrend()
    }
}
