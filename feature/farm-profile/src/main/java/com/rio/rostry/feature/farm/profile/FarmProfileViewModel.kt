package com.rio.rostry.feature.farm.profile
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.farm.repository.FarmAssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmProfileViewModel @Inject constructor(
    private val farmAssetRepository: FarmAssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmProfileUiState())
    val uiState: StateFlow<FarmProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateProfile(farmName: String, bio: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Profile updated")
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class FarmProfileUiState(
    val isLoading: Boolean = false,
    val farmName: String = "",
    val bio: String = "",
    val location: String = "",
    val isVerified: Boolean = false,
    val trustScore: Int = 0,
    val error: String? = null,
    val successMessage: String? = null
)
