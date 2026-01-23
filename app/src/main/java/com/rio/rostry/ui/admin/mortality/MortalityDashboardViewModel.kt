package com.rio.rostry.ui.admin.mortality

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MortalityDashboardViewModel @Inject constructor(
    private val repository: AdminMortalityRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val regions: List<RegionalMortality> = emptyList(),
        val alerts: List<OutbreakAlert> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            // Launch parallel collectors? For simplicity, we can combine or just launch separate coroutines updating state
            
            launch {
                repository.getRegionalMortalityStats().collect { res ->
                    when (res) {
                        is Resource.Success -> _state.value = _state.value.copy(
                            regions = res.data ?: emptyList(),
                            isLoading = false
                        )
                        is Resource.Error -> _state.value = _state.value.copy(
                            error = res.message,
                            isLoading = false
                        )
                        is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }

            launch {
                repository.getPotentialOutbreaks().collect { res ->
                    if (res is Resource.Success) {
                        _state.value = _state.value.copy(alerts = res.data ?: emptyList())
                    }
                }
            }
        }
    }
}
