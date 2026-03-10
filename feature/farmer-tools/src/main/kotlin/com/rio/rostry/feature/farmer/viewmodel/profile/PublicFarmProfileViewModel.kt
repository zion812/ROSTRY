package com.rio.rostry.ui.farmer.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.FarmProfileEntity
import com.rio.rostry.data.database.entity.FarmTimelineEventEntity
import com.rio.rostry.data.repository.FarmProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublicFarmProfileUiState(
    val isLoading: Boolean = true,
    val profile: FarmProfileEntity? = null,
    val timeline: List<FarmTimelineEventEntity> = emptyList(),
    val error: String? = null,
    val isOwnProfile: Boolean = false
)

@HiltViewModel
class PublicFarmProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val farmProfileRepository: FarmProfileRepository
) : ViewModel() {
    
    private val farmerId: String = savedStateHandle.get<String>("farmerId") ?: ""
    
    private val _uiState = MutableStateFlow(PublicFarmProfileUiState())
    val uiState: StateFlow<PublicFarmProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfile()
    }
    
    private fun loadProfile() {
        if (farmerId.isBlank()) {
            _uiState.value = PublicFarmProfileUiState(isLoading = false, error = "Invalid farmer ID")
            return
        }
        
        viewModelScope.launch {
            // Observe profile
            farmProfileRepository.observeProfile(farmerId).collect { profile ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile
                )
            }
        }
        
        viewModelScope.launch {
            // Observe timeline
            farmProfileRepository.observePublicTimeline(farmerId).collect { events ->
                _uiState.value = _uiState.value.copy(timeline = events)
            }
        }
    }
    
    fun getBadges(): List<String> {
        val badgesJson = _uiState.value.profile?.badgesJson ?: "[]"
        return try {
            com.google.gson.Gson().fromJson(badgesJson, Array<String>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
