package com.rio.rostry.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.rio.rostry.session.CurrentUserProvider

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val users: List<UserEntity> = emptyList(),
        val filteredUsers: List<UserEntity> = emptyList(),
        val searchQuery: String = "",
        val roleFilter: UserType? = null,
        val verificationFilter: VerificationStatus? = null, // null = All
        val showSuspendedOnly: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isRefreshing: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // In a real app with many users, we should use pagination.
            // For now, we fetch a limit of 100 system users for the admin dashboard.
            when (val result = userRepository.getSystemUsers(limit = 100)) {
                is Resource.Success -> {
                    val users = result.data ?: emptyList()
                    _uiState.update { state ->
                        state.copy(
                            users = users,
                            isLoading = false,
                            filteredUsers = filterUsers(users, state)
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                    _toastEvent.emit("Failed to load users: ${result.message}")
                }
                else -> Unit
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val newState = state.copy(searchQuery = query)
            newState.copy(filteredUsers = filterUsers(state.users, newState))
        }
    }

    fun onRoleFilterChanged(role: UserType?) {
        _uiState.update { state ->
            val newState = state.copy(roleFilter = role)
            newState.copy(filteredUsers = filterUsers(state.users, newState))
        }
    }

    fun onVerificationFilterChanged(status: VerificationStatus?) {
        _uiState.update { state ->
            val newState = state.copy(verificationFilter = status)
            newState.copy(filteredUsers = filterUsers(state.users, newState))
        }
    }

    fun onSuspensionFilterChanged(showSuspendedOnly: Boolean) {
        _uiState.update { state ->
            val newState = state.copy(showSuspendedOnly = showSuspendedOnly)
            newState.copy(filteredUsers = filterUsers(state.users, newState))
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = userRepository.deleteUser(userId)) {
                is Resource.Success -> {
                    _toastEvent.emit("User deleted successfully")
                    // Remove from local list to avoid full reload
                    _uiState.update { state ->
                        val updatedList = state.users.filterNot { it.userId == userId }
                        state.copy(
                            users = updatedList,
                            isLoading = false,
                            filteredUsers = filterUsers(updatedList, state)
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _toastEvent.emit("Failed to delete user: ${result.message}")
                }
                else -> Unit
            }
        }
    }

    fun updateUserRole(userId: String, newRole: UserType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = userRepository.updateUserType(userId, newRole)) {
                is Resource.Success -> {
                    _toastEvent.emit("User role updated to ${newRole.displayName}")
                    // Optimistic update
                    _uiState.update { state ->
                        val updatedList = state.users.map { 
                            if (it.userId == userId) it.copy(userType = newRole.name) else it 
                        }
                        state.copy(
                            users = updatedList,
                            isLoading = false,
                            filteredUsers = filterUsers(updatedList, state)
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _toastEvent.emit("Failed to update role: ${result.message}")
                }
                else -> Unit
            }
        }
    }
    
    fun suspendUser(userId: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = userRepository.suspendUser(userId, reason)) {
                is Resource.Success -> {
                    _toastEvent.emit("User suspended successfully")
                    // Note: In real app, we might want to refresh the list or update local state
                    // Basic local update:
                     _uiState.update { state ->
                        val updatedList = state.users.map { 
                            if (it.userId == userId) it.copy(isSuspended = true, suspensionReason = reason) else it 
                        }
                        state.copy(
                            users = updatedList,
                            isLoading = false,
                            filteredUsers = filterUsers(updatedList, state)
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _toastEvent.emit("Failed to suspend user: ${result.message}")
                }
                else -> Unit
            }
        }
    }

    fun unsuspendUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = userRepository.unsuspendUser(userId)) {
                is Resource.Success -> {
                    _toastEvent.emit("User unsuspended")
                     _uiState.update { state ->
                        val updatedList = state.users.map { 
                            if (it.userId == userId) it.copy(isSuspended = false, suspensionReason = null) else it 
                        }
                        state.copy(
                            users = updatedList,
                            isLoading = false,
                            filteredUsers = filterUsers(updatedList, state)
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _toastEvent.emit("Failed to unsuspend user: ${result.message}")
                }
                else -> Unit
            }
        }
    }

    fun impersonateUser(userId: String) {
        currentUserProvider.impersonate(userId)
        // Trigger app restart or navigation to make it effective? 
        // For now, simpler toast
        viewModelScope.launch {
            _toastEvent.emit("Impersonation active. Please restart app or navigate home to see changes.")
        }
    }

    private fun filterUsers(users: List<UserEntity>, state: UiState): List<UserEntity> {
        val query = state.searchQuery
        return users.filter { user ->
            val matchesQuery = if (query.isBlank()) true else {
                (user.fullName?.contains(query, ignoreCase = true) == true) ||
                (user.email?.contains(query, ignoreCase = true) == true) ||
                (user.phoneNumber?.contains(query, ignoreCase = true) == true)
            }
            // Role Filter
            val matchesRole = if (state.roleFilter == null) true else user.role == state.roleFilter
            
            // Verification Filter
            val matchesVerification = if (state.verificationFilter == null) true else {
                user.verificationStatus == state.verificationFilter
            }
            
            // Suspension Filter
            val matchesSuspension = if (!state.showSuspendedOnly) true else user.isSuspended
            
            matchesQuery && matchesRole && matchesVerification && matchesSuspension
        }
    }
}
