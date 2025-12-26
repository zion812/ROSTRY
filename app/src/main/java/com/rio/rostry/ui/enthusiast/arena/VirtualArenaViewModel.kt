package com.rio.rostry.ui.enthusiast.arena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.CompetitionEntryEntity
import com.rio.rostry.data.mock.VirtualArenaMockData
import com.rio.rostry.data.repository.VirtualArenaRepository
import com.rio.rostry.domain.model.CompetitionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VirtualArenaViewModel @Inject constructor(
    private val repository: VirtualArenaRepository
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow(CompetitionStatus.LIVE)
    val selectedStatus = _selectedStatus.asStateFlow()

    // In a real app, we would observe the DB. 
    // For this prototype/MVP without a running backend sync, we will load mocks if DB is empty.
    private val _competitions = MutableStateFlow<List<CompetitionEntryEntity>>(emptyList())
    val competitions = combine(_selectedStatus, _competitions) { status, comps ->
        comps.filter { it.status == status }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Initialize with default/mock data for demonstration
        loadCompetitions()
    }

    private fun loadCompetitions() {
        viewModelScope.launch {
            // Check if DB has data (not implemented fully for demo, just pushing mocks)
            // In real logic: repository.getCompetitions(status).collect { ... }
            _competitions.value = VirtualArenaMockData.getMockCompetitions()
        }
    }

    fun setStatusFilter(status: CompetitionStatus) {
        _selectedStatus.value = status
    }
    
    fun refresh() {
        // Trigger sync worker in real implementation
        loadCompetitions()
    }
}
