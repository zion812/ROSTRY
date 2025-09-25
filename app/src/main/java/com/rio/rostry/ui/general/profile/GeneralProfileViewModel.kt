package com.rio.rostry.ui.general.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GeneralProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

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

    private val userId = currentUserProvider.userIdOrNull()

    private val userFlow: StateFlow<Resource<UserEntity?>> = userRepository
        .getCurrentUser()
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

    fun updateProfileName(name: String) {
        val current = uiState.value.profile ?: return
        viewModelScope.launch {
            val updated = current.copy(fullName = name)
            when (val result = userRepository.updateUserProfile(updated)) {
                is Resource.Success -> success.value = "Profile updated"
                is Resource.Error -> error.value = result.message ?: "Failed to update profile"
                else -> Unit
            }
        }
    }

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
