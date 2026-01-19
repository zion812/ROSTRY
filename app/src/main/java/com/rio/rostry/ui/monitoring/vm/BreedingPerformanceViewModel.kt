package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.BreedingRepository
import com.rio.rostry.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

    private val farmerId = kotlinx.coroutines.flow.flow { firebaseAuth.currentUser?.uid?.let { emit(it) } }

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
                    topPerformingPairName = topPair?.let { "Pair ${it.pairId.take(4)}" },
                    topPerformingPairRate = topPair?.hatchSuccessRate ?: 0.0
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BreedingPerformanceStats())
}
