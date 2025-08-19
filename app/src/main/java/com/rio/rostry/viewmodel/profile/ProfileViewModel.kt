package com.rio.rostry.viewmodel.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.UserProfile
import com.rio.rostry.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun fetchProfile() {
        // In a real implementation, you would get the current user ID
        // For now, we'll use a placeholder
        val userId = "placeholder_user_id"
        
        viewModelScope.launch {
            val result = repository.getProfile(userId)
            if (result.isSuccess) {
                _profile.value = result.getOrNull()
                _errorMessage.value = null
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to fetch profile"
            }
        }
    }

    fun saveProfile(profile: UserProfile) {
        _isSaving.value = true
        viewModelScope.launch {
            val result = repository.saveProfile(profile)
            if (result.isSuccess) {
                _profile.value = profile
                _errorMessage.value = null
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to save profile"
            }
            _isSaving.value = false
        }
    }
}

class ProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(UserProfileRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}