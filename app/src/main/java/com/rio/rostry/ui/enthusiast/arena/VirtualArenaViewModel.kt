package com.rio.rostry.ui.enthusiast.arena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VirtualArenaViewModel @Inject constructor(
    private val repository: VirtualArenaRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow(CompetitionStatus.UPCOMING)
    val selectedStatus = _selectedStatus.asStateFlow()

    // Competitions data
    private val _competitions = MutableStateFlow<List<CompetitionEntryEntity>>(emptyList())
    val competitions = combine(_selectedStatus, _competitions) { status, comps ->
        comps.filter { it.status == status }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Loading/Error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadCompetitions()
    }

    private fun loadCompetitions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Try to load from repository first, fallback to mocks
                val localCompetitions = repository.getCompetitionsByStatus(CompetitionStatus.UPCOMING)
                if (localCompetitions.isEmpty()) {
                    _competitions.value = VirtualArenaMockData.getMockCompetitions()
                } else {
                    // Merge local with mocks for demo
                    val mocks = VirtualArenaMockData.getMockCompetitions()
                    _competitions.value = (localCompetitions + mocks).distinctBy { it.competitionId }
                }
            } catch (e: Exception) {
                // On error, use mocks
                _competitions.value = VirtualArenaMockData.getMockCompetitions()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setStatusFilter(status: CompetitionStatus) {
        _selectedStatus.value = status
    }
    
    /**
     * Create a new competition.
     */
    fun createCompetition(
        title: String,
        description: String,
        category: String,
        prizePool: String?,
        startDate: Long,
        endDate: Long
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = firebaseAuth.currentUser?.uid ?: return@launch
                val now = System.currentTimeMillis()
                
                val competition = CompetitionEntryEntity(
                    competitionId = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    region = category, // Using region field for category
                    prizePool = prizePool,
                    status = if (startDate <= now) CompetitionStatus.LIVE else CompetitionStatus.UPCOMING,
                    startTime = startDate,
                    endTime = endDate,
                    participantCount = 0,
                    createdAt = now
                )
                
                // Save to repository
                repository.insertCompetition(competition)
                
                // Update local list
                _competitions.value = _competitions.value + competition
                
                // Switch to the appropriate tab
                _selectedStatus.value = competition.status
                
            } catch (e: Exception) {
                _error.value = "Failed to create competition: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refresh() {
        loadCompetitions()
    }
    
    fun clearError() {
        _error.value = null
    }
}
