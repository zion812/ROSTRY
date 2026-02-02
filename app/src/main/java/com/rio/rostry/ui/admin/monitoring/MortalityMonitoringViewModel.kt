package com.rio.rostry.ui.admin.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.admin.AdminMortalityRepository
import com.rio.rostry.data.repository.admin.MortalityRiskLevel
import com.rio.rostry.data.repository.admin.OutbreakAlert
import com.rio.rostry.data.repository.admin.RegionalMortality
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MortalityMonitoringViewModel @Inject constructor(
    private val repository: AdminMortalityRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val regionalStats: List<RegionalMortality> = emptyList(),
        val outbreakAlerts: List<OutbreakAlert> = emptyList(),
        val criticalRegions: Int = 0,
        val highRiskRegions: Int = 0,
        val activeOutbreaks: Int = 0,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Load regional stats
            launch {
                repository.getRegionalMortalityStats().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val stats = result.data ?: emptyList()
                            _state.update { it.copy(
                                regionalStats = stats,
                                criticalRegions = stats.count { s -> s.riskLevel == MortalityRiskLevel.CRITICAL },
                                highRiskRegions = stats.count { s -> s.riskLevel == MortalityRiskLevel.HIGH }
                            ) }
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(error = result.message) }
                        }
                        is Resource.Loading -> {}
                    }
                }
            }
            
            // Load outbreak alerts
            launch {
                repository.getPotentialOutbreaks().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val alerts = result.data ?: emptyList()
                            _state.update { it.copy(
                                isLoading = false,
                                outbreakAlerts = alerts,
                                activeOutbreaks = alerts.size
                            ) }
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(isLoading = false, error = result.message) }
                        }
                        is Resource.Loading -> {}
                    }
                }
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
