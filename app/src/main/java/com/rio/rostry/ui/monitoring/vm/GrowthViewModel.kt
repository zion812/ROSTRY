package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.GrowthRepository
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.rio.rostry.data.repository.monitoring.GrowthPoint

@HiltViewModel
class GrowthViewModel @Inject constructor(
    private val repo: GrowthRepository
): ViewModel() {

    data class UiState(
        val productId: String = "",
        val records: List<GrowthRecordEntity> = emptyList(),
        val points: List<GrowthPoint> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun observe(productId: String) {
        _ui.update { it.copy(productId = productId) }
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                _ui.update { it.copy(records = list) }
                refreshAnalytics()
            }
        }
    }

    fun recordToday(productId: String, weightGrams: Double?, heightCm: Double?) {
        viewModelScope.launch {
            // week calculation can be moved to repo if birth date available; default to 0 for now
            val record = GrowthRecordEntity(
                recordId = UUID.randomUUID().toString(),
                productId = productId,
                week = 0,
                weightGrams = weightGrams,
                heightCm = heightCm
            )
            repo.upsert(record)
            refreshAnalytics()
        }
    }

    fun refreshAnalytics() {
        val pid = _ui.value.productId
        if (pid.isBlank()) return
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            try {
                val pts = repo.analytics(pid, breed = null)
                _ui.update { it.copy(points = pts, isLoading = false) }
            } catch (e: Exception) {
                _ui.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
