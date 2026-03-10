package com.rio.rostry.feature.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// TODO: Extract OnboardingChecklist use cases to core/domain interfaces
import com.rio.rostry.core.common.session.CurrentUserProvider
import com.rio.rostry.core.model.OnboardingChecklistItem
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnboardingChecklistViewModel @Inject constructor(
    private val currentUserProvider: CurrentUserProvider
    // TODO: Add back OnboardingChecklistRepository, DetermineChecklistRelevanceUseCase, GenerateChecklistSuggestionsUseCase interfaces
    // TODO: Add back UserRepository interface 
) : ViewModel() {


    data class UiState(
        val items: List<OnboardingChecklistItem> = emptyList(),
        val completionPercentage: Int = 0,
        val currentRole: UserType? = null,
        val isLoading: Boolean = true,
        val showCelebration: Boolean = false,
        val smartSuggestions: List<String> = emptyList(),
        val isChecklistRelevant: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private var latestUser: com.rio.rostry.data.database.entity.UserEntity? = null

    init {
        // Track checklist viewed event once when ViewModel initializes
        // flowAnalyticsTracker.trackEvent()
        loadChecklistState()
        observeUserChanges()
    }

    private fun loadChecklistState() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            
            // TODO: Re-integrate UserRepository logic
            kotlinx.coroutines.flow.flowOf(com.rio.rostry.utils.Resource.Loading<com.rio.rostry.data.database.entity.UserEntity>()).collectLatest { resource ->
                when (resource) {
                    is Resource.Success<*> -> {
                        val user = resource.data
                        latestUser = user
                        val role = user?.role ?: com.rio.rostry.domain.model.UserType.GENERAL
                        
                        // TODO: Re-integrate checklist implementation using decoupled interfaces
                        _uiState.value = UiState(
                            items = emptyList(),
                            completionPercentage = 0,
                            currentRole = role,
                            isLoading = false,
                            showCelebration = false,
                            smartSuggestions = emptyList(),
                            isChecklistRelevant = false
                        )
                    }
                    is Resource.Error<*> -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load user data")
                    }
                    is Resource.Loading<*> -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun observeUserChanges() {
        viewModelScope.launch {
            // TODO: Replace with UserRepository interface call
            kotlinx.coroutines.flow.flowOf(com.rio.rostry.utils.Resource.Loading<com.rio.rostry.data.database.entity.UserEntity>()).collectLatest { resource ->
                if (resource is Resource.Success) {
                    latestUser = resource.data
                    // Refresh the checklist when user changes
                    loadChecklistState()
                }
            }
        }
    }

    private fun getCompletionPercentage(items: List<OnboardingChecklistItem>): Int {
        if (items.isEmpty()) return 0
        val completedCount = items.count { it.isCompleted }
        return (completedCount * 100) / items.size
    }

    fun markItemCompleted(itemId: String) {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            Timber.d("Checklist item $itemId marked as completed (Mocked)")
            // TODO: Call interface onboardingChecklistRepository.markItemCompleted(userId, itemId)
        }
    }

    fun dismissChecklist() {
        viewModelScope.launch {
            Timber.d("Checklist dismissed (Mocked)")
            // TODO: Call interface onboardingChecklistRepository.dismissChecklist(userId)
            
            _uiState.value = _uiState.value.copy(
                items = emptyList(), 
                completionPercentage = 100, 
                showCelebration = false, 
                smartSuggestions = emptyList()
            )
        }
    }
}
