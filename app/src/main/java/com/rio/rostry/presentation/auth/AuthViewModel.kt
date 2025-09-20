package com.rio.rostry.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.presentation.viewmodel.BaseViewModel
import com.rio.rostry.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _currentUser.value = user
            }
        }
    }

    private val _authState = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val authState: StateFlow<Resource<Unit>> = _authState

    private val _userState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val userState: StateFlow<Resource<User>> = _userState

    fun sendVerificationCode(phoneNumber: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading
            try {
                val result = authRepository.sendPhoneVerificationCode(phoneNumber)
                _authState.value = result
            } catch (e: Exception) {
                _authState.value = Resource.Error("Failed to send verification code: ${e.message}", e)
            }
        }
    }

    fun verifyPhoneCode(phoneNumber: String, code: String) {
        viewModelScope.launch {
            _userState.value = Resource.Loading
            try {
                val result = authRepository.verifyPhoneCode(phoneNumber, code)
                _userState.value = result
            } catch (e: Exception) {
                _userState.value = Resource.Error("Failed to verify phone code: ${e.message}", e)
            }
        }
    }

    fun login(phoneNumber: String) {
        viewModelScope.launch {
            _userState.value = Resource.Loading
            try {
                val result = authRepository.login(phoneNumber)
                _userState.value = result
            } catch (e: Exception) {
                _userState.value = Resource.Error("Failed to login: ${e.message}", e)
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            _userState.value = Resource.Loading
            try {
                val result = authRepository.registerUser(user)
                _userState.value = result
            } catch (e: Exception) {
                _userState.value = Resource.Error("Failed to register: ${e.message}", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = Resource.Loading
            try {
                val result = authRepository.logout()
                _authState.value = result
            } catch (e: Exception) {
                _authState.value = Resource.Error("Failed to logout: ${e.message}", e)
            }
        }
    }

    fun upgradeUserRole(userId: String, newUserType: UserType) {
        viewModelScope.launch {
            _userState.value = Resource.Loading
            try {
                val result = authRepository.upgradeUserRole(userId, newUserType)
                _userState.value = result
            } catch (e: Exception) {
                _userState.value = Resource.Error("Failed to upgrade user role: ${e.message}", e)
            }
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            _userState.value = Resource.Loading
            try {
                val result = authRepository.updateProfile(user)
                _userState.value = result
            } catch (e: Exception) {
                _userState.value = Resource.Error("Failed to update profile: ${e.message}", e)
            }
        }
    }

    fun verifyFarmer(userId: String, location: String) {
        viewModelScope.launch {
            _userState.value = Resource.Loading
            try {
                val result = authRepository.verifyFarmer(userId, location)
                _userState.value = result
            } catch (e: Exception) {
                _userState.value = Resource.Error("Failed to verify farmer: ${e.message}", e)
            }
        }
    }

    fun verifyEnthusiast(userId: String, kycData: Map<String, String>) {
        viewModelScope.launch {
            _userState.value = Resource.Loading
            try {
                val result = authRepository.verifyEnthusiast(userId, kycData)
                _userState.value = result
            } catch (e: Exception) {
                _userState.value = Resource.Error("Failed to verify enthusiast: ${e.message}", e)
            }
        }
    }
}