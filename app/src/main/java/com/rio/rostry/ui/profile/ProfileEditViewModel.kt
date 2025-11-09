package com.rio.rostry.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import com.rio.rostry.utils.media.MediaUploadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mediaUploadManager: MediaUploadManager,
    private val currentUserProvider: CurrentUserProvider,
    private val flowAnalyticsTracker: FlowAnalyticsTracker
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val user: UserEntity? = null,
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val location: String = "",
        val bio: String = "",
        val photoUri: String? = null,
        val uploadProgress: Int = 0,
        val message: String? = null,
        val error: String? = null,
        val validationErrors: Map<String, String> = emptyMap(),
        val isSaving: Boolean = false,
        val isDirty: Boolean = false,
        val farmLocation: String = ""
    )

    sealed class NavigationEvent {
        object ProfileUpdated : NavigationEvent()
    }

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents

    init {
        loadUserData()
        observeUploadEvents()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            userRepository.getCurrentUser().collectLatest { res ->
                when (res) {
                    is Resource.Success -> {
                        val user = res.data
                        val farmLoc = if (user?.farmLocationLat != null && user.farmLocationLng != null) {
                            "${user.farmLocationLat},${user.farmLocationLng}"
                        } else ""
                        _ui.value = UiState(
                            isLoading = false,
                            user = user,
                            name = user?.fullName ?: "",
                            email = user?.email ?: "",
                            phone = user?.phoneNumber ?: "",
                            location = user?.address ?: "",
                            bio = "", // Bio not stored in UserEntity, initialize empty
                            photoUri = user?.profilePictureUrl,
                            farmLocation = farmLoc
                        )
                    }
                    is Resource.Error -> _ui.value = _ui.value.copy(isLoading = false, error = res.message)
                    is Resource.Loading -> _ui.value = _ui.value.copy(isLoading = true)
                }
            }
        }
    }

    fun updateName(name: String) {
        _ui.value = _ui.value.copy(name = name, isDirty = true)
        validateField("name", name)
    }

    fun updateEmail(email: String) {
        _ui.value = _ui.value.copy(email = email, isDirty = true)
        validateField("email", email)
    }

    fun updatePhone(phone: String) {
        _ui.value = _ui.value.copy(phone = phone, isDirty = true)
        validateField("phone", phone)
    }

    fun updateLocation(location: String) {
        _ui.value = _ui.value.copy(location = location, isDirty = true)
        validateField("location", location)
    }

    fun updateBio(bio: String) {
        _ui.value = _ui.value.copy(bio = bio, isDirty = true)
        // No validation for bio as it's not saved
    }

    fun updateFarmLocation(value: String) {
        _ui.value = _ui.value.copy(farmLocation = value, isDirty = true)
        validateField("farmLocation", value)
    }

    fun uploadProfilePhoto(localUri: String) {
        val userId = _ui.value.user?.userId ?: return
        viewModelScope.launch {
            val remotePath = "profiles/$userId/photo_${UUID.randomUUID()}.jpg"
            mediaUploadManager.enqueueToOutbox(localPath = localUri, remotePath = remotePath)
        }
    }

    private fun observeUploadEvents() {
        viewModelScope.launch {
            mediaUploadManager.events.collect { event ->
                when (event) {
                    is MediaUploadManager.UploadEvent.Progress -> {
                        _ui.value = _ui.value.copy(uploadProgress = event.percent)
                    }
                    is MediaUploadManager.UploadEvent.Success -> {
                        _ui.value = _ui.value.copy(
                            uploadProgress = 0,
                            photoUri = event.downloadUrl,
                            isDirty = true
                        )
                    }
                    is MediaUploadManager.UploadEvent.Failed -> {
                        _ui.value = _ui.value.copy(
                            uploadProgress = 0,
                            error = event.error
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun validateField(field: String, value: String) {
        val errors = _ui.value.validationErrors.toMutableMap()
        when (field) {
            "name" -> {
                if (value.isBlank()) {
                    errors[field] = "Name is required"
                } else if (value.length < 2) {
                    errors[field] = "Name must be at least 2 characters"
                } else {
                    errors.remove(field)
                }
            }
            "email" -> {
                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
                if (value.isBlank()) {
                    errors[field] = "Email is required"
                } else if (!emailRegex.matches(value)) {
                    errors[field] = "Invalid email format"
                } else {
                    errors.remove(field)
                }
            }
            "phone" -> {
                val phoneRegex = "^[+]?[0-9]{10,15}$".toRegex()
                if (value.isBlank()) {
                    errors[field] = "Phone is required"
                } else if (!phoneRegex.matches(value)) {
                    errors[field] = "Invalid phone format"
                } else {
                    errors.remove(field)
                }
            }
            "location" -> {
                if (value.isBlank()) {
                    errors[field] = "Location is required"
                } else {
                    errors.remove(field)
                }
            }
            "farmLocation" -> {
                if (value.isBlank()) {
                    errors[field] = "Farm Location is required"
                } else {
                    errors.remove(field)
                }
            }
        }
        _ui.value = _ui.value.copy(validationErrors = errors)
    }

    fun updateProfile() {
        if (_ui.value.validationErrors.isNotEmpty()) {
            _ui.value = _ui.value.copy(error = "Please fix validation errors")
            return
        }

        val currentUser = _ui.value.user ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSaving = true, error = null, message = null)

            // Parse farm location if provided as "lat,lng"
            val (lat, lng) = run {
                val v = _ui.value.farmLocation
                if (v.isNotBlank() && v.contains(",")) {
                    val parts = v.split(",")
                    val la = parts.getOrNull(0)?.trim()?.toDoubleOrNull()
                    val ln = parts.getOrNull(1)?.trim()?.toDoubleOrNull()
                    la to ln
                } else null to null
            }

            val updatedUser = currentUser.copy(
                fullName = _ui.value.name,
                email = _ui.value.email,
                phoneNumber = _ui.value.phone,
                address = _ui.value.location,
                profilePictureUrl = _ui.value.photoUri,
                farmLocationLat = lat ?: currentUser.farmLocationLat,
                farmLocationLng = lng ?: currentUser.farmLocationLng,
                updatedAt = System.currentTimeMillis()
            )

            val res = userRepository.updateUserProfile(updatedUser)
            when (res) {
                is Resource.Success -> {
                    _ui.value = _ui.value.copy(isSaving = false, message = "Profile updated successfully", isDirty = false)
                    flowAnalyticsTracker.trackProfileEdited("profile_update")
                    _navigationEvents.emit(NavigationEvent.ProfileUpdated)
                }
                is Resource.Error -> {
                    _ui.value = _ui.value.copy(isSaving = false, error = res.message ?: "Failed to update profile")
                }
                is Resource.Loading -> {
                    // Handle if needed
                }
            }
        }
    }

    fun clearError() {
        _ui.value = _ui.value.copy(error = null)
    }

    fun clearMessage() {
        _ui.value = _ui.value.copy(message = null)
    }
}