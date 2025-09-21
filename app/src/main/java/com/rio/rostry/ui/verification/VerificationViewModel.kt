package com.rio.rostry.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val user: UserEntity? = null,
        val message: String? = null,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            userRepository.getCurrentUser().collectLatest { res ->
                when (res) {
                    is Resource.Success -> _ui.value = UiState(user = res.data, isLoading = false)
                    is Resource.Error -> _ui.value = UiState(error = res.message, isLoading = false)
                    is Resource.Loading -> _ui.value = _ui.value.copy(isLoading = true)
                }
            }
        }
    }

    fun submitFarmerLocation(lat: Double?, lng: Double?) {
        val current = _ui.value.user ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val updated = current.copy(
                farmLocationLat = lat,
                farmLocationLng = lng,
                locationVerified = false,
                verificationStatus = VerificationStatus.PENDING,
                updatedAt = System.currentTimeMillis()
            )
            val res = userRepository.updateUserProfile(updated)
            _ui.value = if (res is Resource.Error) {
                _ui.value.copy(isLoading = false, error = res.message)
            } else {
                _ui.value.copy(isLoading = false, message = "Location submitted for verification")
            }
        }
    }

    fun submitEnthusiastKyc(level: Int?) {
        val current = _ui.value.user ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val updated = current.copy(
                kycLevel = level,
                verificationStatus = VerificationStatus.PENDING,
                updatedAt = System.currentTimeMillis()
            )
            val res = userRepository.updateUserProfile(updated)
            _ui.value = if (res is Resource.Error) {
                _ui.value.copy(isLoading = false, error = res.message)
            } else {
                _ui.value.copy(isLoading = false, message = "KYC submitted for verification")
            }
        }
    }
}
