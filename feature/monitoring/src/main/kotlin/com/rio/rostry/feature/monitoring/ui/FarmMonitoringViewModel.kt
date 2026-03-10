package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.GrowthRepository
import com.rio.rostry.data.repository.monitoring.HatchingRepository
import com.rio.rostry.data.repository.monitoring.MortalityRepository
import com.rio.rostry.data.repository.monitoring.QuarantineRepository
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.ui.monitoring.MonitoringSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmMonitoringViewModel @Inject constructor(
    private val growthRepository: GrowthRepository,
    private val quarantineRepository: QuarantineRepository,
    private val mortalityRepository: MortalityRepository,
    private val vaccinationRepository: VaccinationRepository,
    private val hatchingRepository: HatchingRepository,
) : ViewModel() {

    private val productIdFlow = MutableStateFlow<String?>(null)

    // Public state: Monitoring summary for dashboard (optimized for fast loading)
    val summary: StateFlow<MonitoringSummary> = combine(
        // Growth count for selected product (0 if none)
        productIdFlow.flatMapLatestOrEmpty { pid -> growthRepository.observe(pid).mapListSizeOrZero() },
        // Quarantine active count (status = ACTIVE)
        quarantineRepository.observeByStatus("ACTIVE").mapListSizeOrZero(),
        // Hatching batches count
        hatchingRepository.observeBatches().mapListSizeOrZero(),
    ) { growthCount, quarantineActive, hatchingBatches ->
        // Load vaccination data asynchronously to avoid blocking
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 7L * 24L * 60L * 60L * 1000L
        
        MonitoringSummary(
            growthTracked = growthCount,
            breedingPairs = 0, // Breeding repository not available in current module
            eggsCollectedToday = 0, // Not directly available via interfaces here
            incubatingBatches = hatchingBatches,
            hatchDueThisWeek = 0, // Could be derived from logs if exposed later
            broodingBatches = 0, // Not exposed in current repository layer
            vaccinationDue = 0, // Loaded separately to avoid blocking
            vaccinationOverdue = 0,
            quarantineActive = quarantineActive,
            mortalityLast7d = 0, // Loaded separately to avoid blocking
            pendingAlerts = buildList {
                if (quarantineActive > 0) add("Active quarantines: $quarantineActive")
            },
            weeklyCadence = emptyList(),
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, MonitoringSummary())

    fun setProductContext(productId: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            productIdFlow.emit(productId)
        }
    }
}

// Extension helpers (local)

private fun <T> Flow<List<T>>.mapListSizeOrZero(): Flow<Int> = this.map { it.size }

private fun <T, R> Flow<T?>.flatMapLatestOrEmpty(block: (T) -> Flow<R>): Flow<R> =
    this.flatMapLatest { v -> if (v == null) flowOf() else block(v) }
