package com.rio.rostry.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.User
import com.rio.rostry.data.models.UserType
import com.rio.rostry.repository.DebugAuthRepository
import com.rio.rostry.repository.AuthErrorType
import com.rio.rostry.repository.AuthenticationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DebugAuthViewModel : ViewModel() {
    private val authRepository = DebugAuthRepository()
    private val TAG = "DebugAuthViewModel"
    
    private val _user = MutableStateFlow<User?>(authRepository.getCurrentUser())
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(_user.value != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authError = MutableStateFlow<AuthError?>(null)
    val authError: StateFlow<AuthError?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun login(email: String, password: String) {
        Log.d(TAG, "Login called with email: $email")
        // Validate inputs
        if (email.isBlank() || password.isBlank()) {
            Log.w(TAG, "Login validation failed: Email or password is blank")
            _authError.value = AuthError(
                type = AuthErrorType.INVALID_CREDENTIALS,
                message = "Email and password are required"
            )
            return
        }
        
        viewModelScope.launch {
            Log.d(TAG, "Starting login process")
            _isLoading.value = true
            _authError.value = null
            
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                Log.d(TAG, "Login successful")
                _user.value = result.getOrNull()
                _isLoggedIn.value = _user.value != null
            } else {
                Log.e(TAG, "Login failed: ${result.exceptionOrNull()?.message}")
                handleAuthException(result.exceptionOrNull())
            }
            
            _isLoading.value = false
            Log.d(TAG, "Login process completed")
        }
    }

    fun signUp(name: String, email: String, password: String) {
        Log.d(TAG, "SignUp called with name: $name, email: $email")
        // Validate inputs
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            Log.w(TAG, "SignUp validation failed: Required fields are blank")
            _authError.value = AuthError(
                type = AuthErrorType.INVALID_CREDENTIALS,
                message = "Name, email, and password are required"
            )
            return
        }
        
        if (password.length < 6) {
            Log.w(TAG, "SignUp validation failed: Password too short")
            _authError.value = AuthError(
                type = AuthErrorType.INVALID_CREDENTIALS,
                message = "Password must be at least 6 characters long"
            )
            return
        }
        
        viewModelScope.launch {
            Log.d(TAG, "Starting signUp process")
            _isLoading.value = true
            _authError.value = null
            
            val result = authRepository.signUp(email, password, name)
            if (result.isSuccess) {
                Log.d(TAG, "SignUp successful")
                _user.value = result.getOrNull()
                _isLoggedIn.value = _user.value != null
            } else {
                Log.e(TAG, "SignUp failed: ${result.exceptionOrNull()?.message}")
                handleAuthException(result.exceptionOrNull())
            }
            
            _isLoading.value = false
            Log.d(TAG, "SignUp process completed")
        }
    }

    fun logout() {
        Log.d(TAG, "Logout called")
        viewModelScope.launch {
            Log.d(TAG, "Starting logout process")
            _isLoading.value = true
            
            val result = authRepository.logout()
            if (result.isSuccess) {
                Log.d(TAG, "Logout successful")
                _user.value = null
                _isLoggedIn.value = false
                _authError.value = null
            } else {
                Log.e(TAG, "Logout failed: ${result.exceptionOrNull()?.message}")
                handleAuthException(result.exceptionOrNull())
            }
            
            _isLoading.value = false
            Log.d(TAG, "Logout process completed")
        }
    }

    fun updateProfile(userType: UserType) {
        Log.d(TAG, "UpdateProfile called with userType: $userType")
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                Log.d(TAG, "Updating profile for user: ${currentUser.userId}")
                val updatedUser = currentUser.copy(type = userType, updatedAt = System.currentTimeMillis())
                _user.value = updatedUser
                // In a real app, you would also update this in the database
                _authError.value = null
                Log.d(TAG, "Profile updated successfully")
            } else {
                Log.w(TAG, "UpdateProfile failed: User not authenticated")
                _authError.value = AuthError(
                    type = AuthErrorType.UNKNOWN_ERROR,
                    message = "User not authenticated"
                )
            }
        }
    }
    
    fun refreshToken() {
        Log.d(TAG, "RefreshToken called")
        viewModelScope.launch {
            Log.d(TAG, "Starting token refresh process")
            _isLoading.value = true
            
            val result = authRepository.refreshToken()
            if (result.isFailure) {
                Log.e(TAG, "Token refresh failed: ${result.exceptionOrNull()?.message}")
                handleAuthException(result.exceptionOrNull())
            } else {
                Log.d(TAG, "Token refresh successful")
            }
            
            _isLoading.value = false
            Log.d(TAG, "Token refresh process completed")
        }
    }
    
    fun clearAuthError() {
        Log.d(TAG, "Clearing auth error")
        _authError.value = null
    }
    
    private fun handleAuthException(throwable: Throwable?) {
        Log.d(TAG, "Handling auth exception: ${throwable?.message}")
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