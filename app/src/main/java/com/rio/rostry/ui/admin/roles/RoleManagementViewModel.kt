package com.rio.rostry.ui.admin.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val users: List<UserEntity> = emptyList(),
        val filteredUsers: List<UserEntity> = emptyList(),
        val selectedRole: UserType? = null,
        val searchQuery: String = "",
        val isProcessing: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            when (val result = userRepository.getSystemUsers(limit = 200)) {
                is Resource.Success -> {
                    val users = result.data ?: emptyList()
                    _state.update { it.copy(
                        isLoading = false,
                        users = users,
                        filteredUsers = filterUsers(users, it.selectedRole, it.searchQuery)
                    ) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { state ->
            state.copy(
                searchQuery = query,
                filteredUsers = filterUsers(state.users, state.selectedRole, query)
            )
        }
    }

    fun onRoleFilterChanged(role: UserType?) {
        _state.update { state ->
            state.copy(
                selectedRole = role,
                filteredUsers = filterUsers(state.users, role, state.searchQuery)
            )
        }
    }

    private fun filterUsers(users: List<UserEntity>, role: UserType?, query: String): List<UserEntity> {
        return users.filter { user ->
            val matchesRole = role == null || user.role == role
            val matchesQuery = query.isBlank() || 
                user.fullName?.contains(query, ignoreCase = true) == true ||
                user.email?.contains(query, ignoreCase = true) == true ||
                user.phoneNumber?.contains(query, ignoreCase = true) == true
            matchesRole && matchesQuery
        }
    }

    fun updateUserRole(userId: String, newRole: UserType) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            
            when (val result = userRepository.updateUserType(userId, newRole)) {
                is Resource.Success -> {
                    _toastEvent.emit("Role updated to ${newRole.displayName}")
                    // Update local list
                    _state.update { state ->
                        val updatedUsers = state.users.map { user ->
                            if (user.userId == userId) user.copy(userType = newRole.name)
                            else user
                        }
                        state.copy(
                            isProcessing = false,
                            users = updatedUsers,
                            filteredUsers = filterUsers(updatedUsers, state.selectedRole, state.searchQuery)
                        )
                    }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed: ${result.message}")
                    _state.update { it.copy(isProcessing = false) }
                }
                else -> {}
            }
        }
    }

    fun suspendUser(userId: String, reason: String) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            
            when (val result = userRepository.suspendUser(userId, reason, null)) {
                is Resource.Success -> {
                    _toastEvent.emit("User suspended")
                    _state.update { state ->
                        val updatedUsers = state.users.map { user ->
                            if (user.userId == userId) user.copy(isSuspended = true, suspensionReason = reason)
                            else user
                        }
                        state.copy(
                            isProcessing = false,
                            users = updatedUsers,
                            filteredUsers = filterUsers(updatedUsers, state.selectedRole, state.searchQuery)
                        )
                    }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed: ${result.message}")
                    _state.update { it.copy(isProcessing = false) }
                }
                else -> {}
            }
        }
    }

    fun unsuspendUser(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            
            when (val result = userRepository.unsuspendUser(userId)) {
                is Resource.Success -> {
                    _toastEvent.emit("User unsuspended")
                    _state.update { state ->
                        val updatedUsers = state.users.map { user ->
                            if (user.userId == userId) user.copy(isSuspended = false, suspensionReason = null)
                            else user
                        }
                        state.copy(
                            isProcessing = false,
                            users = updatedUsers,
                            filteredUsers = filterUsers(updatedUsers, state.selectedRole, state.searchQuery)
                        )
                    }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Failed: ${result.message}")
                    _state.update { it.copy(isProcessing = false) }
                }
                else -> {}
            }
        }
    }

    fun refresh() {
        loadUsers()
    }
}
