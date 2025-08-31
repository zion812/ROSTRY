package com.rio.rostry.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.User
import com.rio.rostry.data.models.UserType
import com.rio.rostry.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun loginUser(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            userRepository.loginUser(email, pass)
                .onSuccess { withContext(Dispatchers.Main) { onSuccess() } }
                .onFailure { withContext(Dispatchers.Main) { onError(it.message ?: "An unknown error occurred.") } }
        }
    }

    fun registerUser(email: String, pass: String, phone: String?, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            userRepository.registerUser(email, pass, phone)
                .onSuccess { authResult ->
                    val uid = authResult.user?.uid
                    if (uid != null) {
                        withContext(Dispatchers.Main) { onSuccess(uid) }
                    } else {
                        withContext(Dispatchers.Main) { onError("Could not get user ID.") }
                    }
                }
                .onFailure { withContext(Dispatchers.Main) { onError(it.message ?: "An unknown error occurred.") } }
        }
    }

    fun completeUserProfile(
        uid: String, email: String, phone: String?, name: String, location: String, userType: UserType, language: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = User(
                uid = uid,
                name = name,
                email = email,
                phone = phone,
                location = location,
                userType = userType,
                language = language
            )
            userRepository.updateUserProfile(user)
                .onSuccess { withContext(Dispatchers.Main) { onSuccess() } }
                .onFailure { withContext(Dispatchers.Main) { onError(it.message ?: "An unknown error occurred.") } }
        }
    }
}
