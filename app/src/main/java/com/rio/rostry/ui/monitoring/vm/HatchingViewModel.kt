package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.HatchingRepository
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.database.entity.HatchingLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HatchingViewModel @Inject constructor(
    private val hatchingRepository: HatchingRepository
) : ViewModel() {

    data class UiState(
        val batches: List<HatchingBatchEntity> = emptyList(),
        val selectedBatch: HatchingBatchEntity? = null,
        val logs: List<HatchingLogEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    private val _selectedBatchId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            hatchingRepository.observeBatches().collect { batches ->
                _ui.update { it.copy(batches = batches) }
            }
        }
        
        // Use flatMapLatest to automatically cancel previous log collection when batch changes
        viewModelScope.launch {
            _selectedBatchId.flatMapLatest { batchId ->
                if (batchId != null) {
                    hatchingRepository.observeLogs(batchId)
                } else {
                    flowOf(emptyList())
                }
            }.collect { logs ->
                _ui.update { it.copy(logs = logs) }
            }
        }
    }

    fun startBatch(
        batchName: String,
        temperatureC: Double? = null,
        humidityPct: Double? = null,
        expectedHatchDays: Int? = null
    ) {
        if (batchName.isBlank()) {
            _ui.update { it.copy(error = "Batch name cannot be empty") }
            return
        }

        viewModelScope.launch {
            try {
                val batchId = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val expectedHatchAt = expectedHatchDays?.let { days ->
                    now + (days * 24L * 60 * 60 * 1000)
                }

                val batch = HatchingBatchEntity(
                    batchId = batchId,
                    name = batchName,
                    startedAt = now,
                    expectedHatchAt = expectedHatchAt,
                    temperatureC = temperatureC,
                    humidityPct = humidityPct
                )

                hatchingRepository.upsert(batch)
                _ui.update { it.copy(successMessage = "Batch '$batchName' created successfully") }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Failed to create batch") }
            }
        }
    }

    fun selectBatch(batchId: String) {
        val batch = _ui.value.batches.firstOrNull { it.batchId == batchId }
        _ui.update { it.copy(selectedBatch = batch) }
        // Update the selected batch ID, which will trigger flatMapLatest to switch log collection
        _selectedBatchId.value = batchId
    }

    fun clearMessages() {
        _ui.update { it.copy(error = null, successMessage = null) }
    }
}
