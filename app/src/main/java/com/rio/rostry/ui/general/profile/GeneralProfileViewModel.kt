package com.rio.rostry.ui.general.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@HiltViewModel
class GeneralProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val sessionManager: SessionManager
) : ViewModel() {

    sealed class UpgradeEvent {
        data class Success(val newRole: com.rio.rostry.domain.model.UserType) : UpgradeEvent()
        data class Error(val message: String) : UpgradeEvent()
    }

    data class PreferenceToggle(
        val title: String,
        val enabled: Boolean,
        val key: String
    )

    data class SupportOption(
        val title: String,
        val description: String,
        val action: () -> Unit = {}
    )

    data class ProfileUiState(
        val isAuthenticated: Boolean = true,
        val isLoading: Boolean = true,
        val profile: UserEntity? = null,
        val preferences: List<PreferenceToggle> = emptyList(),
        val orderHistory: List<OrderEntity> = emptyList(),
        val supportOptions: List<SupportOption> = emptyList(),
        val error: String? = null,
        val success: String? = null
    )

    private val preferenceToggles = MutableStateFlow(
        listOf(
            PreferenceToggle(title = "Push notifications", enabled = true, key = "notifications"),
            PreferenceToggle(title = "Weekly marketplace digest", enabled = true, key = "weekly_digest"),
            PreferenceToggle(title = "Location-based offers", enabled = false, key = "location_offers")
        )
    )

    private val error = MutableStateFlow<String?>(null)
    private val success = MutableStateFlow<String?>(null)

    private val _upgradeEvent = MutableSharedFlow<UpgradeEvent>()
    val upgradeEvent: SharedFlow<UpgradeEvent> = _upgradeEvent

    private val _isUpgrading = MutableStateFlow(false)
    val isUpgrading: StateFlow<Boolean> = _isUpgrading

    private val userId = currentUserProvider.userIdOrNull()

    private val userFlow: StateFlow<Resource<UserEntity?>> = userRepository
        .getCurrentUser()
        .debounce(300)
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading()
        )

    private val orderFlow: StateFlow<List<OrderEntity>> = userId?.let { id ->
        orderRepository.getOrdersByBuyer(id).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    } ?: MutableStateFlow(emptyList())

    val uiState: StateFlow<ProfileUiState> = combine(
        userFlow,
        orderFlow,
        preferenceToggles,
        error,
        success
    ) { userResource, orders, toggles, errorMessage, successMessage ->
        if (userId == null) {
            return@combine ProfileUiState(
                isAuthenticated = false,
                isLoading = false,
                error = "Sign in to manage your profile"
            )
        }
        val profile = userResource.data
        ProfileUiState(
            isAuthenticated = true,
            isLoading = userResource is Resource.Loading && profile == null,
            profile = profile,
            preferences = toggles,
            orderHistory = orders.sortedByDescending { it.orderDate }.take(10),
            supportOptions = buildSupportOptions(),
            error = errorMessage ?: (userResource as? Resource.Error)?.message,
            success = successMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState(isAuthenticated = userId != null)
    )

    fun updatePreference(key: String, enabled: Boolean) {
        preferenceToggles.update { toggles ->
            toggles.map { toggle ->
                if (toggle.key == key) toggle.copy(enabled = enabled) else toggle
            }
        }
        success.value = "Preference updated"
    }

    fun updateProfile(name: String, email: String, address: String) {
        val current = uiState.value.profile ?: return
        viewModelScope.launch {
            val updated = current.copy(
                fullName = name,
                email = email,
                address = address
            )
            when (val result = userRepository.updateUserProfile(updated)) {
                is Resource.Success -> success.value = "Profile updated"
                is Resource.Error -> error.value = result.message ?: "Failed to update profile"
                else -> Unit
            }
        }
    }

    fun upgradeToFarmer(
        address: String,
        chickenCount: Int,
        farmerType: String,
        raisingSince: Long,
        favoriteBreed: String,
        lat: Double?,
        lng: Double?
    ) {
        android.util.Log.e("GeneralProfileViewModel", "upgradeToFarmer called with: address='$address', count=$chickenCount, loc=[$lat, $lng]")
        val current = uiState.value.profile
        if (current == null) {
            android.util.Log.e("GeneralProfileViewModel", "Current profile is null! Aborting upgrade.")
            return
        }

        if (current.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.PENDING || 
            current.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED) {
            _upgradeEvent.tryEmit(UpgradeEvent.Error("Verification is already pending or completed."))
            return
        }
        android.util.Log.e("GeneralProfileViewModel", "Current profile: ${current.userId}, starting upgrade...")

        // Explicitly use IO dispatcher to ensure it runs
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                android.util.Log.e("GeneralProfileViewModel", "Coroutine started on thread: ${Thread.currentThread().name}")
                _isUpgrading.value = true
                
                // CRITICAL: Set upgrade flag BEFORE any database updates
                // This prevents SessionViewModel from reacting to role changes
                sessionManager.setUpgradeInProgress(true)
                android.util.Log.e("GeneralProfileViewModel", "Set upgradeInProgress=true")

                // Auto-verify location if GPS coordinates are provided
                val isLocationVerified = lat != null && lng != null

                // Update profile with ALL farmer data INCLUDING userType
                // ZERO-COST ARCHITECTURE: We no longer need Cloud Functions for custom claims
                // Firestore security rules now read userType directly from the user document
                val updatedProfile = current.copy(
                    address = address,
                    chickenCount = chickenCount,
                    farmerType = farmerType,
                    raisingSince = raisingSince,
                    favoriteBreed = favoriteBreed,
                    // Persist Farm Location Data
                    farmLocationLat = lat,
                    farmLocationLng = lng,
                    locationVerified = isLocationVerified,
                    // Update both verification status AND userType
                    verificationStatus = com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED,
                    userType = com.rio.rostry.domain.model.UserType.FARMER.name
                )

                android.util.Log.e("GeneralProfileViewModel", "Saving profile with userType=FARMER...")
                val saveResult = userRepository.updateUserProfile(updatedProfile)
                
                if (saveResult is Resource.Error) {
                    android.util.Log.e("GeneralProfileViewModel", "Failed to save profile: ${saveResult.message}")
                    _upgradeEvent.emit(UpgradeEvent.Error(saveResult.message ?: "Failed to save profile data"))
                    return@launch
                }

                android.util.Log.e("GeneralProfileViewModel", "Profile saved successfully!")

                // ZERO-COST ARCHITECTURE: No more waiting for Cloud Functions!
                // The Firestore security rules now read userType directly from the document.
                // We can update the session role immediately without waiting for custom claims.
                kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) {
                    try {
                        android.util.Log.e("GeneralProfileViewModel", "Updating SessionManager with FARMER role...")
                        sessionManager.markAuthenticated(
                            nowMillis = System.currentTimeMillis(),
                            userType = com.rio.rostry.domain.model.UserType.FARMER
                        )

                        android.util.Log.e("GeneralProfileViewModel", "Upgrade complete! Emitting success event.")
                        _upgradeEvent.emit(UpgradeEvent.Success(com.rio.rostry.domain.model.UserType.FARMER))
                    } catch (e: Exception) {
                        android.util.Log.e("GeneralProfileViewModel", "Failed to finalize Farmer upgrade: ${e.message}", e)
                        _upgradeEvent.emit(UpgradeEvent.Error("Failed to update session: ${e.message}"))
                    }
                }
            } catch (t: Throwable) {
                // Catch EVERYTHING including RuntimeExceptions and Errors
                android.util.Log.e("GeneralProfileViewModel", "CRITICAL EXCEPTION in upgradeToFarmer: ${t.message}", t)
                _upgradeEvent.emit(UpgradeEvent.Error("Critical Error: ${t.message}"))
            } finally {
                android.util.Log.e("GeneralProfileViewModel", "Setting isUpgrading=false and upgradeInProgress=false")
                _isUpgrading.value = false
                // CRITICAL: Clear the upgrade flag so SessionViewModel can sync again
                sessionManager.setUpgradeInProgress(false)
            }
        }
    }

    // REMOVED: waitForCustomClaims function
    // ZERO-COST ARCHITECTURE: We no longer wait for Cloud Functions to set custom claims.
    // Firestore security rules now read userType directly from user documents.

    fun clearMessages() {
        error.value = null
        success.value = null
    }

    private fun buildSupportOptions(): List<SupportOption> = listOf(
        SupportOption(title = "FAQs", description = "Browse common questions"),
        SupportOption(title = "Chat with support", description = "Reach out for assistance"),
        SupportOption(title = "Submit feedback", description = "Share your ideas to improve ROSTRY")
    )
}
