package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.BreedingRepository
import com.rio.rostry.ui.monitoring.BreedingPairDetail
import com.rio.rostry.ui.monitoring.WeeklyTrendData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class BreedingPerformanceStats(
    val totalPairs: Int = 0,
    val activePairs: Int = 0,
    val totalEggsCollected: Int = 0,
    val averageHatchRate: Double = 0.0,
    val topPerformingPairName: String? = null,
    val topPerformingPairRate: Double = 0.0
)

@HiltViewModel
class BreedingPerformanceViewModel @Inject constructor(
    private val breedingRepository: BreedingRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val farmerId = flow { firebaseAuth.currentUser?.uid?.let { emit(it) } }

    val stats: StateFlow<BreedingPerformanceStats> = farmerId.flatMapLatest { id: String? ->
        if (id == null) flowOf(BreedingPerformanceStats())
        else breedingRepository.observeActive(id).map { pairs ->
            if (pairs.isEmpty()) {
                BreedingPerformanceStats()
            } else {
                val activeCount = pairs.count { it.status == "ACTIVE" }
                val totalEggs = pairs.sumOf { it.eggsCollected }
                val avgHatch = if (pairs.isNotEmpty()) pairs.map { it.hatchSuccessRate }.average() else 0.0
                
                val topPair = pairs.maxByOrNull { it.hatchSuccessRate }
                
                BreedingPerformanceStats(
                    totalPairs = pairs.size,
                    activePairs = activeCount,
                    totalEggsCollected = totalEggs,
                    averageHatchRate = avgHatch,
                    topPerformingPairName = topPair?.let { "Pair ${it.pairId.take(6).uppercase()}" },
                    topPerformingPairRate = topPair?.hatchSuccessRate ?: 0.0
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BreedingPerformanceStats())
    
    /**
     * Detailed pair data for comparison view.
     */
    val pairDetails: StateFlow<List<BreedingPairDetail>> = farmerId.flatMapLatest { id: String? ->
        if (id == null) flowOf(emptyList())
        else breedingRepository.observeActive(id).map { pairs ->
            pairs.map { pair ->
                BreedingPairDetail(
                    pairId = pair.pairId,
                    name = "Pair ${pair.pairId.take(6).uppercase()}",
                    eggsCollected = pair.eggsCollected,
                    hatchRate = pair.hatchSuccessRate,
                    status = pair.status
                )
            }.sortedByDescending { it.hatchRate }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    /**
     * Weekly trend data for charts.
     * In a real app, this would come from historical records.
     * For now, we simulate trend data based on current stats.
     */
    val weeklyTrends: StateFlow<List<WeeklyTrendData>> = farmerId.flatMapLatest { id: String? ->
        if (id == null) flowOf(emptyList())
        else breedingRepository.observeActive(id).map { pairs ->
            if (pairs.isEmpty()) emptyList()
            else {
                // Simulate weekly trends based on current data
                // In production, this would query historical breeding records
                val baseHatchRate = pairs.map { it.hatchSuccessRate }.average()
                val baseEggs = pairs.sumOf { it.eggsCollected } / 4
                
                listOf(
                    WeeklyTrendData(
                        weekNumber = 1,
                        hatchRate = (baseHatchRate * 0.85).coerceIn(0.0, 1.0),
                        eggsCollected = (baseEggs * 0.7).toInt()
                    ),
                    WeeklyTrendData(
                        weekNumber = 2,
                        hatchRate = (baseHatchRate * 0.90).coerceIn(0.0, 1.0),
                        eggsCollected = (baseEggs * 0.85).toInt()
                    ),
                    WeeklyTrendData(
                        weekNumber = 3,
                        hatchRate = (baseHatchRate * 0.95).coerceIn(0.0, 1.0),
                        eggsCollected = (baseEggs * 0.95).toInt()
                    ),
                    WeeklyTrendData(
                        weekNumber = 4,
                        hatchRate = baseHatchRate.coerceIn(0.0, 1.0),
                        eggsCollected = baseEggs
                    )
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
