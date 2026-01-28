package com.rio.rostry.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.upgrade.RoleUpgradeManager
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val roleUpgradeManager: RoleUpgradeManager
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

    /**
     * Requests a role upgrade for the current user.
     * 
     * DEPRECATED for interactive flows: This method is primarily for administrative or test scenarios.
     * For interactive user upgrades, navigate to the upgrade wizard instead.
     * 
     * This method now routes through RoleUpgradeManager to enforce prerequisite validation.
     */
    fun requestUpgrade(toType: UserType) {
        val userId = _ui.value.user?.userId ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            
            // Route through RoleUpgradeManager for validation and consistency
            val result = roleUpgradeManager.forceUpgradeRole(userId, toType)
            
            _ui.value = when (result) {
                is Resource.Error<*> -> {
                    _ui.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to upgrade role. Please complete all prerequisites."
                    )
                }
                
                is Resource.Success<*> -> {
                    _ui.value.copy(
                        isLoading = false,
                        message = "Role upgraded to ${toType.name}"
                    )
                }
                else -> _ui.value.copy(isLoading = false)
            }
            refresh()
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val res = userRepository.updateUserProfile(user)
            _ui.value = if (res is Resource.Error<*>) {
                _ui.value.copy(isLoading = false, error = res.message)
            } else {
                _ui.value.copy(isLoading = false, message = "Profile updated")
            }
            refresh()
        }
    }

    fun updateVerification(status: VerificationStatus) {
        val userId = _ui.value.user?.userId ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val result = userRepository.updateVerificationStatus(userId, status)
            _ui.value = if (result is Resource.Error<*>) {
                _ui.value.copy(isLoading = false, error = result.message)
            } else {
                _ui.value.copy(isLoading = false, message = "Verification ${status.name}")
            }
            refresh()
        }
    }
}
