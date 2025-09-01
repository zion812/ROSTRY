package com.rio.rostry.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.User
import com.rio.rostry.data.models.UserType
import android.net.Uri
import com.rio.rostry.data.repo.StorageRepository
import com.rio.rostry.data.repo.UserRepository
import com.rio.rostry.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    fun loginUser(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = userRepository.loginUser(email, pass)) {
                is Result.Success -> withContext(Dispatchers.Main) { onSuccess() }
                is Result.Error -> withContext(Dispatchers.Main) { onError(result.exception.message ?: "An unknown error occurred.") }
                is Result.Loading -> {} // Handle loading state if needed
            }
        }
    }

    fun registerUser(email: String, pass: String, phone: String?, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = userRepository.registerUser(email, pass, phone)) {
                is Result.Success -> {
                    val uid = result.data.user?.uid
                    if (uid != null) {
                        withContext(Dispatchers.Main) { onSuccess(uid) }
                    } else {
                        withContext(Dispatchers.Main) { onError("Could not get user ID.") }
                    }
                }
                is Result.Error -> withContext(Dispatchers.Main) { onError(result.exception.message ?: "An unknown error occurred.") }
                is Result.Loading -> {} // Handle loading state if needed
            }
        }
    }

    fun completeUserProfile(
        uid: String, 
        email: String, 
        phone: String?, 
        name: String, 
        location: String, 
        userType: UserType, 
        language: String, 
        bio: String?, 
        profileImageUri: Uri?, 
        onSuccess: () -> Unit, 
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val imageUrl = profileImageUri?.let {
                    storageRepository.uploadProfileImage(it, uid)
                }

                val user = User(
                    uid = uid,
                    name = name,
                    email = email,
                    phone = phone,
                    location = location,
                    userType = userType,
                    language = language,
                    bio = bio,
                    profileImageUrl = imageUrl
                )

                when (val result = userRepository.updateUserProfile(user)) {
                    is Result.Success -> withContext(Dispatchers.Main) { onSuccess() }
                    is Result.Error -> withContext(Dispatchers.Main) { onError(result.exception.message ?: "An unknown error occurred.") }
                    is Result.Loading -> { /* Handle loading state if needed */ }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "An unknown error occurred.") }
            }
        }
    }
}
