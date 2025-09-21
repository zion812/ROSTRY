package com.rio.rostry.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
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

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            userRepository.getCurrentUser().collectLatest { res ->
                when (res) {
                    is Resource.Success -> _ui.value = UiState(isLoading = false, user = res.data)
                    is Resource.Error -> _ui.value = UiState(isLoading = false, error = res.message)
                    is Resource.Loading -> _ui.value = _ui.value.copy(isLoading = true)
                }
            }
        }
    }

    fun requestUpgrade(toType: UserType) {
        val userId = _ui.value.user?.userId ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val result = userRepository.updateUserType(userId, toType)
            _ui.value = if (result is Resource.Error) {
                _ui.value.copy(isLoading = false, error = result.message)
            } else {
                _ui.value.copy(isLoading = false, message = "Role updated to ${toType.name}")
            }
            refresh()
        }
    }

    fun updateVerification(status: VerificationStatus) {
        val userId = _ui.value.user?.userId ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val result = userRepository.updateVerificationStatus(userId, status)
            _ui.value = if (result is Resource.Error) {
                _ui.value.copy(isLoading = false, error = result.message)
            } else {
                _ui.value.copy(isLoading = false, message = "Verification ${status.name}")
            }
            refresh()
        }
    }
}
