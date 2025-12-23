package com.rio.rostry.ui.main

import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.ui.base.BaseViewModel
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// Example User State data class for the UI
data class UserProfileUiState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val email: String? = null,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: com.rio.rostry.session.SessionManager
) : BaseViewModel() {

    private val _userProfileState = MutableStateFlow(UserProfileUiState())
    val userProfileState: StateFlow<UserProfileUiState> = _userProfileState

    init {
        fetchCurrentUserProfile()
    }

    fun fetchCurrentUserProfile() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _userProfileState.value = UserProfileUiState(isLoading = true)
                    }
                    is Resource.Success -> {
                        val user = resource.data
                        _userProfileState.value = UserProfileUiState(
                            isLoading = false,
                            userName = user?.fullName,
                            email = user?.email
                        )
                        // NOTE: Removed automatic role sync here.
                        // The role should be updated by GeneralProfileViewModel AFTER claims are verified.
                        // Syncing here causes a race condition where navigation happens before
                        // the Firebase ID token is refreshed with the new role claims.
                    }
                    is Resource.Error -> {
                        _userProfileState.value = UserProfileUiState(
                            isLoading = false,
                            error = resource.message ?: "An unknown error occurred"
                        )
                        handleError(resource.message) // Use BaseViewModel error handling
                    }
                }
            }
        }
    }

    // Add other business logic methods here that interact with repositories
    // For example, to trigger a refresh:
    fun refreshUserProfile(userId: String) {
        viewModelScope.launch {
            _userProfileState.value = _userProfileState.value.copy(isLoading = true)
            val result = userRepository.refreshCurrentUser(userId)
            if (result is Resource.Error) {
                handleError(result.message)
                 _userProfileState.value = _userProfileState.value.copy(isLoading = false, error = result.message)
            } else {
                // Data will be re-emitted by the getCurrentUser flow if refresh was successful
                // Or you might need to re-trigger fetchCurrentUserProfile explicitly if refreshCurrentUser doesn't directly update the observed Flow
                 _userProfileState.value = _userProfileState.value.copy(isLoading = false)
            }
        }
    }

    // Placeholder for FirebaseAuth if needed directly in VM, though better to keep it in repository
    // For the refreshUserProfile example, you'd typically get the current user ID from a session manager or auth state flow.
    // For simplicity, let's assume it's available or passed in.
    // @Inject lateinit var firebaseAuth: com.google.firebase.auth.FirebaseAuth
}
