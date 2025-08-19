package com.rio.rostry.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.User
import com.rio.rostry.data.models.UserType
import com.rio.rostry.repository.AuthRepository
import com.rio.rostry.repository.AuthErrorType
import com.rio.rostry.repository.AuthenticationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    
    private val _user = MutableStateFlow<User?>(authRepository.getCurrentUser())
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(_user.value != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authError = MutableStateFlow<AuthError?>(null)
    val authError: StateFlow<AuthError?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Observe auth state changes
        viewModelScope.launch {
            authRepository.observeAuthState().collect { authState ->
                when (authState) {
                    is com.rio.rostry.repository.AuthState.Authenticated -> {
                        _user.value = authRepository.getCurrentUser()
                        _isLoggedIn.value = _user.value != null
                    }
                    is com.rio.rostry.repository.AuthState.Unauthenticated -> {
                        _user.value = null
                        _isLoggedIn.value = false
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        // Validate inputs
        if (email.isBlank() || password.isBlank()) {
            _authError.value = AuthError(
                type = AuthErrorType.INVALID_CREDENTIALS,
                message = "Email and password are required"
            )
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _user.value = result.getOrNull()
                _isLoggedIn.value = _user.value != null
            } else {
                handleAuthException(result.exceptionOrNull())
            }
            
            _isLoading.value = false
        }
    }

    fun signUp(name: String, email: String, password: String) {
        // Validate inputs
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authError.value = AuthError(
                type = AuthErrorType.INVALID_CREDENTIALS,
                message = "Name, email, and password are required"
            )
            return
        }
        
        if (password.length < 6) {
            _authError.value = AuthError(
                type = AuthErrorType.INVALID_CREDENTIALS,
                message = "Password must be at least 6 characters long"
            )
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            
            val result = authRepository.signUp(email, password, name)
            if (result.isSuccess) {
                _user.value = result.getOrNull()
                _isLoggedIn.value = _user.value != null
            } else {
                handleAuthException(result.exceptionOrNull())
            }
            
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = authRepository.logout()
            if (result.isSuccess) {
                _user.value = null
                _isLoggedIn.value = false
                _authError.value = null
            } else {
                handleAuthException(result.exceptionOrNull())
            }
            
            _isLoading.value = false
        }
    }

    fun updateProfile(userType: UserType) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(type = userType, updatedAt = System.currentTimeMillis())
                _user.value = updatedUser
                // In a real app, you would also update this in the database
                _authError.value = null
            } else {
                _authError.value = AuthError(
                    type = AuthErrorType.UNKNOWN_ERROR,
                    message = "User not authenticated"
                )
            }
        }
    }
    
    fun refreshToken() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = authRepository.refreshToken()
            if (result.isFailure) {
                handleAuthException(result.exceptionOrNull())
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearAuthError() {
        _authError.value = null
    }
    
    private fun handleAuthException(throwable: Throwable?) {
        when (throwable) {
            is AuthenticationException -> {
                _authError.value = AuthError(
                    type = throwable.errorType,
                    message = throwable.message ?: "Authentication failed"
                )
            }
            else -> {
                _authError.value = AuthError(
                    type = AuthErrorType.UNKNOWN_ERROR,
                    message = throwable?.message ?: "An unknown error occurred"
                )
            }
        }
    }
}

// Auth error data class
data class AuthError(
    val type: AuthErrorType,
    val message: String
)