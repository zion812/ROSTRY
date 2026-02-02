package com.rio.rostry.ui.admin.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DiseaseZoneEntity
import com.rio.rostry.data.database.entity.ZoneSeverity
import com.rio.rostry.data.repository.BiosecurityRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiosecurityMonitoringViewModel @Inject constructor(
    private val repository: BiosecurityRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val zones: List<DiseaseZoneEntity> = emptyList(),
        val activeZones: Int = 0,
        val warningZones: Int = 0,
        val restrictedZones: Int = 0,
        val lockdownZones: Int = 0,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadZones()
    }

    private fun loadZones() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            repository.getActiveZones().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val zones = result.data ?: emptyList()
                        val activeZones = zones.filter { it.isActive }
                        _state.update { it.copy(
                            isLoading = false,
                            zones = zones,
                            activeZones = activeZones.size,
                            warningZones = activeZones.count { z -> z.severity == ZoneSeverity.WARNING },
                            restrictedZones = activeZones.count { z -> z.severity == ZoneSeverity.RESTRICTED },
                            lockdownZones = activeZones.count { z -> z.severity == ZoneSeverity.LOCKDOWN }
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

    fun refresh() {
        loadZones()
    }
}
