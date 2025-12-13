package com.rio.rostry.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.OnboardingChecklistRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.usecase.DetermineChecklistRelevanceUseCase
import com.rio.rostry.domain.usecase.GenerateChecklistSuggestionsUseCase
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.navigation.Routes
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
    private val onboardingChecklistRepository: OnboardingChecklistRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
    private val determineChecklistRelevanceUseCase: DetermineChecklistRelevanceUseCase,
    private val generateChecklistSuggestionsUseCase: GenerateChecklistSuggestionsUseCase
) : ViewModel() {

    data class ChecklistItem(
        val id: String,
        val title: String,
        val description: String,
        val isCompleted: Boolean,
        val route: String?
    )

    data class UiState(
        val items: List<ChecklistItem> = emptyList(),
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
        flowAnalyticsTracker.trackEvent("onboarding_checklist_viewed")
        loadChecklistState()
        observeUserChanges()
    }

    private fun loadChecklistState() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            
            userRepository.getCurrentUser().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        latestUser = user
                        val role = user?.role ?: UserType.GENERAL
                        val checklistItems = onboardingChecklistRepository.getChecklistItemsForRole(role)
                        val state = onboardingChecklistRepository.loadChecklistState(userId)
                        
                        if (state.isDismissed) {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            return@collectLatest
                        }
                        
                        val items = checklistItems.map { repoItem ->
                            val isCompleted = state.completedIds.contains(repoItem.id) ||
                                onboardingChecklistRepository.isItemCompleted(repoItem, user)
                            // Convert repository ChecklistItem to ViewModel ChecklistItem
                            ChecklistItem(
                                id = repoItem.id,
                                title = repoItem.title,
                                description = repoItem.description,
                                isCompleted = isCompleted,
                                route = repoItem.route
                            )
                        }

                        val completionPercentage = getCompletionPercentage(items)
                        val showCelebration = completionPercentage == 100 && !_uiState.value.showCelebration
                        val smartSuggestions = generateChecklistSuggestionsUseCase(items.map { vmItem ->
                            OnboardingChecklistRepository.ChecklistItem(
                                id = vmItem.id,
                                title = vmItem.title,
                                description = vmItem.description,
                                isCompleted = vmItem.isCompleted,
                                route = vmItem.route
                            )
                        }, role)
                        val isChecklistRelevant = determineChecklistRelevanceUseCase(user, items.map { vmItem ->
                            OnboardingChecklistRepository.ChecklistItem(
                                id = vmItem.id,
                                title = vmItem.title,
                                description = vmItem.description,
                                isCompleted = vmItem.isCompleted,
                                route = vmItem.route
                            )
                        })
                        
                        _uiState.value = UiState(
                            items = items,
                            completionPercentage = completionPercentage,
                            currentRole = role,
                            isLoading = false,
                            showCelebration = showCelebration,
                            smartSuggestions = smartSuggestions,
                            isChecklistRelevant = isChecklistRelevant
                        )
                        
                        if (showCelebration) {
                            viewModelScope.launch { 
                                if (!onboardingChecklistRepository.isOnboardingCompletedTracked(userId)) {
                                    flowAnalyticsTracker.trackOnboardingCompleted()
                                    onboardingChecklistRepository.markOnboardingCompletedTracked(userId)
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load user data")
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun observeUserChanges() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { resource ->
                if (resource is Resource.Success) {
                    latestUser = resource.data
                    // Refresh the checklist when user changes
                    loadChecklistState()
                }
            }
        }
    }

    private fun getCompletionPercentage(items: List<ChecklistItem>): Int {
        if (items.isEmpty()) return 0
        val completedCount = items.count { it.isCompleted }
        return (completedCount * 100) / items.size
    }

    fun markItemCompleted(itemId: String) {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            
            val result = onboardingChecklistRepository.markItemCompleted(userId, itemId)
            
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(error = "Failed to save progress")
                Timber.e(result.exceptionOrNull(), "Failed to mark checklist item as completed")
                return@launch
            }
            
            // Update UI state optimistically
            val currentState = _uiState.value
            val updatedItems = currentState.items.map { item ->
                if (item.id == itemId) item.copy(isCompleted = true) else item
            }
            
            val completionPercentage = getCompletionPercentage(updatedItems)
            val showCelebration = completionPercentage == 100 && !currentState.showCelebration
            val smartSuggestions = generateChecklistSuggestionsUseCase(updatedItems.map { vmItem ->
                OnboardingChecklistRepository.ChecklistItem(
                    id = vmItem.id,
                    title = vmItem.title,
                    description = vmItem.description,
                    isCompleted = vmItem.isCompleted,
                    route = vmItem.route
                )
            }, currentState.currentRole ?: UserType.GENERAL)
            val isChecklistRelevant = determineChecklistRelevanceUseCase(latestUser, updatedItems.map { vmItem ->
                OnboardingChecklistRepository.ChecklistItem(
                    id = vmItem.id,
                    title = vmItem.title,
                    description = vmItem.description,
                    isCompleted = vmItem.isCompleted,
                    route = vmItem.route
                )
            })
            
            _uiState.value = currentState.copy(
                items = updatedItems,
                completionPercentage = completionPercentage,
                showCelebration = showCelebration,
                smartSuggestions = smartSuggestions,
                isChecklistRelevant = isChecklistRelevant
            )
            
            // Track analytics for item completion
            flowAnalyticsTracker.trackOnboardingChecklistProgress(itemId, true)
            
            if (showCelebration) {
                if (!onboardingChecklistRepository.isOnboardingCompletedTracked(userId)) {
                    flowAnalyticsTracker.trackOnboardingCompleted()
                    onboardingChecklistRepository.markOnboardingCompletedTracked(userId)
                }
            }
        }
    }

    fun dismissChecklist() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            
            val result = onboardingChecklistRepository.dismissChecklist(userId)
            
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(error = "Failed to dismiss checklist")
                Timber.e(result.exceptionOrNull(), "Failed to dismiss checklist")
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(
                items = emptyList(), 
                completionPercentage = 100, 
                showCelebration = false, 
                smartSuggestions = emptyList()
            )
        }
    }
}