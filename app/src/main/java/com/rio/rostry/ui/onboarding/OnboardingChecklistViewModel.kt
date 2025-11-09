package com.rio.rostry.ui.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingChecklistViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
    @ApplicationContext private val context: Context
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
        val smartSuggestions: List<String> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("onboarding_checklist") }
    )
    private var latestUser: com.rio.rostry.data.database.entity.UserEntity? = null

    init {
        loadChecklistState()
        observeUserChanges()
        observeChecklistPrefs()
        flowAnalyticsTracker.trackEvent("onboarding_checklist_viewed")
    }

    private fun loadChecklistState() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            // One-time migration from legacy SharedPreferences if present
            migrateLegacyIfNeeded(userId)
            val dismissedPrefKey = booleanPreferencesKey("onboarding_checklist_dismissed_$userId")
            val completedPrefKey = stringSetPreferencesKey("onboarding_checklist_$userId")
            val prefs = dataStore.data.first()
            val isDismissed = prefs[dismissedPrefKey] ?: false
            if (isDismissed) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                return@launch
            }
            val completedIds = prefs[completedPrefKey] ?: emptySet()

            userRepository.getCurrentUser().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        latestUser = user
                        val role = user?.userType ?: UserType.GENERAL
                        val items = getChecklistItemsForRole(role).map { item ->
                            item.copy(isCompleted = completedIds.contains(item.id) || isItemCompleted(item, user))
                        }
                        val completionPercentage = getCompletionPercentage(items)
                        val showCelebration = completionPercentage == 100 && !_uiState.value.showCelebration
                        val smartSuggestions = generateSmartSuggestions(items, role)
                        _uiState.value = UiState(
                            items = items,
                            completionPercentage = completionPercentage,
                            currentRole = role,
                            isLoading = false,
                            showCelebration = showCelebration,
                            smartSuggestions = smartSuggestions
                        )
                        if (showCelebration) {
                            viewModelScope.launch { ensureTrackOnboardingCompletedOnce() }
                        }
                    }
                    is Resource.Error -> {
                        // Handle error, perhaps show default state
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private suspend fun migrateLegacyIfNeeded(userId: String) {
        val migratedKey = booleanPreferencesKey("onboarding_migrated_$userId")
        val dismissedPrefKey = booleanPreferencesKey("onboarding_checklist_dismissed_$userId")
        val completedPrefKey = stringSetPreferencesKey("onboarding_checklist_$userId")
        val prefs = dataStore.data.first()
        val alreadyMigrated = prefs[migratedKey] ?: false
        if (alreadyMigrated) return

        // Read legacy SharedPreferences
        val legacy = context.getSharedPreferences("onboarding_checklist", Context.MODE_PRIVATE)
        val legacyDismissed = legacy.getBoolean("onboarding_checklist_dismissed_$userId", false)
        val legacyCompletedJson = legacy.getString("onboarding_checklist_$userId", null)
        val legacyCompleted: Set<String> = when {
            legacyCompletedJson.isNullOrBlank() -> emptySet()
            else -> try {
                // Stored as a JSON set; be tolerant: it might be a JSON array of strings.
                val org = org.json.JSONArray(legacyCompletedJson)
                buildSet {
                    for (i in 0 until org.length()) add(org.optString(i))
                }
            } catch (e: Exception) {
                emptySet()
            }
        }

        // Write to DataStore
        dataStore.edit { ds ->
            if (legacyDismissed) ds[dismissedPrefKey] = true
            if (legacyCompleted.isNotEmpty()) ds[completedPrefKey] = legacyCompleted
            ds[migratedKey] = true
        }
        // Optionally clear legacy values
        legacy.edit().remove("onboarding_checklist_dismissed_$userId").remove("onboarding_checklist_$userId").apply()
    }

    private fun observeUserChanges() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { resource ->
                if (resource is Resource.Success) {
                    latestUser = resource.data
                    checkCompletion()
                }
            }
        }
    }

    private fun observeChecklistPrefs() {
        viewModelScope.launch {
            dataStore.data.collectLatest { prefs ->
                val userId = currentUserProvider.userIdOrNull() ?: return@collectLatest
                val dismissedPrefKey = booleanPreferencesKey("onboarding_checklist_dismissed_$userId")
                val completedPrefKey = stringSetPreferencesKey("onboarding_checklist_$userId")

                val isDismissed = prefs[dismissedPrefKey] ?: false
                if (isDismissed) {
                    _uiState.value = _uiState.value.copy(items = emptyList(), completionPercentage = 100, isLoading = false)
                    return@collectLatest
                }

                val completedIds = prefs[completedPrefKey] ?: emptySet()

                val role = latestUser?.userType ?: _uiState.value.currentRole ?: UserType.GENERAL
                val items = getChecklistItemsForRole(role).map { item ->
                    item.copy(isCompleted = completedIds.contains(item.id) || isItemCompleted(item, latestUser))
                }
                val completionPercentage = getCompletionPercentage(items)
                val showCelebration = completionPercentage == 100 && !_uiState.value.showCelebration
                val smartSuggestions = generateSmartSuggestions(items, role)
                _uiState.value = _uiState.value.copy(
                    items = items,
                    completionPercentage = completionPercentage,
                    currentRole = role,
                    isLoading = false,
                    showCelebration = showCelebration,
                    smartSuggestions = smartSuggestions
                )
                if (showCelebration) {
                    viewModelScope.launch { ensureTrackOnboardingCompletedOnce() }
                }
            }
        }
    }

    private fun getChecklistItemsForRole(role: UserType): List<ChecklistItem> {
        return when (role) {
            UserType.GENERAL -> listOf(
                ChecklistItem("complete_profile", "Complete Profile", "Fill in your name, email, and phone number", false, Routes.PROFILE),
                ChecklistItem("browse_marketplace", "Browse Marketplace", "Explore products from farmers", false, Routes.PRODUCT_MARKET),
                ChecklistItem("join_community", "Join Community", "Connect with other users", false, Routes.GROUPS)
            )
            UserType.FARMER -> listOf(
                ChecklistItem("complete_profile", "Complete Profile", "Fill in your name, email, and phone number", false, Routes.PROFILE),
                ChecklistItem("verify_location", "Verify Location", "Verify your farm location for selling", false, Routes.VERIFY_FARMER_LOCATION),
                ChecklistItem("add_first_product", "Add First Product", "List your first product for sale", false, Routes.FarmerNav.CREATE),
                ChecklistItem("create_first_listing", "Create First Listing", "Create your first marketplace listing", false, Routes.FarmerNav.CREATE)
            )
            UserType.ENTHUSIAST -> listOf(
                ChecklistItem("complete_profile", "Complete Profile", "Fill in your name, email, and phone number", false, Routes.PROFILE),
                ChecklistItem("complete_kyc", "Complete KYC", "Verify your identity for advanced features", false, Routes.VERIFY_ENTHUSIAST_KYC),
                ChecklistItem("add_first_bird", "Add First Bird", "Add your first bird to the system", false, Routes.Builders.onboardingFarmBird("enthusiast")),
                ChecklistItem("start_breeding_record", "Start Breeding Record", "Begin tracking breeding activities", false, Routes.MONITORING_BREEDING)
            )
        }
    }

    private fun isItemCompleted(item: ChecklistItem, user: com.rio.rostry.data.database.entity.UserEntity?): Boolean {
        if (user == null) return false
        return when (item.id) {
            "complete_profile" -> !user.fullName.isNullOrBlank() && !user.email.isNullOrBlank() && !user.phoneNumber.isNullOrBlank()
            "verify_location" -> user.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED
            "complete_kyc" -> user.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED
            // Enhanced auto-detection: assume "browse_marketplace" and "join_community" are completed if user has been active (e.g., based on role upgrade or other indicators)
            // For Farmer/Enthusiast, "add_first_product"/"add_first_bird" could be checked via product count if available, but for now, keep manual
            "browse_marketplace" -> user.userType != UserType.GENERAL // Assume completed if upgraded from GENERAL
            "join_community" -> user.userType != UserType.GENERAL // Assume completed if upgraded
            "add_first_product", "create_first_listing", "add_first_bird", "start_breeding_record" -> false // Manual or require external marking
            else -> false
        }
    }

    fun checkCompletion() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            val completedPrefKey = stringSetPreferencesKey("onboarding_checklist_$userId")
            val prefs = dataStore.data.first()
            val completedIds = prefs[completedPrefKey] ?: emptySet()

            val updatedItems = currentState.items.map { item ->
                item.copy(isCompleted = completedIds.contains(item.id) || isItemCompleted(item, latestUser))
            }
            val completionPercentage = getCompletionPercentage(updatedItems)
            val showCelebration = completionPercentage == 100 && !currentState.showCelebration
            val smartSuggestions = generateSmartSuggestions(updatedItems, currentState.currentRole ?: UserType.GENERAL)
            _uiState.value = currentState.copy(
                items = updatedItems,
                completionPercentage = completionPercentage,
                showCelebration = showCelebration,
                smartSuggestions = smartSuggestions
            )
            if (showCelebration) {
                ensureTrackOnboardingCompletedOnce()
            }
        }
    }

    fun markItemCompleted(itemId: String) {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            val completedPrefKey = stringSetPreferencesKey("onboarding_checklist_$userId")
            // Update DataStore
            dataStore.edit { prefs ->
                val current = prefs[completedPrefKey] ?: emptySet()
                prefs[completedPrefKey] = current + itemId
            }
            // Update UI state optimistically
            val currentState = _uiState.value
            val updatedItems = currentState.items.map { item ->
                if (item.id == itemId) item.copy(isCompleted = true) else item
            }
            val completionPercentage = getCompletionPercentage(updatedItems)
            val showCelebration = completionPercentage == 100 && !currentState.showCelebration
            val smartSuggestions = generateSmartSuggestions(updatedItems, currentState.currentRole ?: UserType.GENERAL)
            _uiState.value = currentState.copy(
                items = updatedItems,
                completionPercentage = completionPercentage,
                showCelebration = showCelebration,
                smartSuggestions = smartSuggestions
            )
            // Track analytics for item completion
            flowAnalyticsTracker.trackOnboardingChecklistProgress(itemId, true)
            if (showCelebration) {
                ensureTrackOnboardingCompletedOnce()
            }
        }
    }

    private suspend fun ensureTrackOnboardingCompletedOnce() {
        val userId = currentUserProvider.userIdOrNull() ?: return
        val key = booleanPreferencesKey("onboarding_completed_tracked_${'$'}userId")
        val prefs = dataStore.data.first()
        val alreadyTracked = prefs[key] ?: false
        if (!alreadyTracked) {
            flowAnalyticsTracker.trackOnboardingCompleted()
            dataStore.edit { it[key] = true }
        }
    }

    fun getCompletionPercentage(items: List<ChecklistItem>): Int {
        if (items.isEmpty()) return 0
        val completedCount = items.count { it.isCompleted }
        return (completedCount * 100) / items.size
    }

    fun dismissChecklist() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            val dismissedPrefKey = booleanPreferencesKey("onboarding_checklist_dismissed_$userId")
            dataStore.edit { prefs ->
                prefs[dismissedPrefKey] = true
            }
            _uiState.value = _uiState.value.copy(items = emptyList(), completionPercentage = 100, showCelebration = false, smartSuggestions = emptyList())
        }
    }

    private fun generateSmartSuggestions(items: List<ChecklistItem>, role: UserType): List<String> {
        val incompleteItems = items.filter { !it.isCompleted }
        return incompleteItems.mapNotNull { item ->
            when (item.id) {
                "complete_profile" -> "Complete your profile to unlock marketplace access and advanced features."
                "verify_location" -> "Verify your farm location to start selling products."
                "complete_kyc" -> "Complete KYC verification to access breeding and transfer features."
                "browse_marketplace" -> "Browse the marketplace to discover products from local farmers."
                "join_community" -> "Join the community to connect with other users and get support."
                "add_first_product", "create_first_listing" -> "Create your first listing to start selling on the marketplace."
                "add_first_bird" -> "Add your first bird to begin tracking and monitoring."
                "start_breeding_record" -> "Start a breeding record to track your breeding activities."
                else -> null
            }
        }
    }

    fun markChecklistViewed() {
        flowAnalyticsTracker.trackEvent("onboarding_checklist_viewed")
    }
}
