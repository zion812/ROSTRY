package com.rio.rostry.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.onboardingChecklistDataStore by preferencesDataStore(name = "onboarding_checklist")

/**
 * Repository for managing onboarding checklist data and operations.
 * Handles all DataStore operations and legacy migration logic.
 */
@Singleton
class OnboardingChecklistRepository @Inject constructor(
    private val context: Context
) {

    /**
     * Load the checklist state for a user.
     */
    suspend fun loadChecklistState(userId: String): ChecklistState {
        // Perform migration if needed
        migrateLegacyIfNeeded(userId)
        
        val dismissedPrefKey = booleanPreferencesKey("onboarding_checklist_dismissed_$userId")
        val completedPrefKey = stringSetPreferencesKey("onboarding_checklist_$userId")
        
        val prefs = runCatching {
            context.onboardingChecklistDataStore.data.first()
        }.getOrElse {
            return ChecklistState(
                completedIds = emptySet(),
                isDismissed = false,
                items = emptyList(),
                completionPercentage = 0,
                currentRole = null,
                showCelebration = false,
                smartSuggestions = emptyList(),
                isChecklistRelevant = false,
                error = null
            )
        }

        val isDismissed = prefs[dismissedPrefKey] ?: false
        if (isDismissed) {
            return ChecklistState(
                completedIds = emptySet(),
                isDismissed = true,
                items = emptyList(),
                completionPercentage = 100,
                currentRole = null,
                showCelebration = false,
                smartSuggestions = emptyList(),
                isChecklistRelevant = false,
                error = null
            )
        }
        
        val completedIds = prefs[completedPrefKey] ?: emptySet()
        return ChecklistState(
            completedIds = completedIds,
            isDismissed = isDismissed
        )
    }
    
    /**
     * Get checklist items for a specific user role.
     */
    fun getChecklistItemsForRole(role: UserType): List<ChecklistItem> {
        return when (role) {
            UserType.GENERAL -> listOf(
                ChecklistItem("complete_profile", "Complete Profile", "Fill in your name, email, and phone number", false, "profile"),
                ChecklistItem("browse_marketplace", "Browse Marketplace", "Explore products from farmers", false, "marketplace"),
                ChecklistItem("join_community", "Join Community", "Connect with other users", false, "groups")
            )
            UserType.FARMER -> listOf(
                ChecklistItem("complete_profile", "Complete Profile", "Fill in your name, email, and phone number", false, "profile"),
                ChecklistItem("verify_location", "Verify Location", "Verify your farm location for selling", false, "verify-farmer-location"),
                ChecklistItem("add_first_product", "Add First Product", "List your first product for sale", false, "create-product"),
                ChecklistItem("create_first_listing", "Create First Listing", "Create your first marketplace listing", false, "create-listing")
            )
            UserType.ENTHUSIAST -> listOf(
                ChecklistItem("complete_profile", "Complete Profile", "Fill in your name, email, and phone number", false, "profile"),
                ChecklistItem("complete_kyc", "Complete KYC", "Verify your identity for advanced features", false, "verify-enthusiast-kyc"),
                ChecklistItem("add_first_bird", "Add First Bird", "Add your first bird to the system", false, "onboarding-farm-bird-enthusiast"),
                ChecklistItem("start_breeding_record", "Start Breeding Record", "Begin tracking breeding activities", false, "monitoring-breeding")
            )
            UserType.ADMIN -> emptyList()
        }
    }
    
    /**
     * Check if an item is completed based on user data.
     */
    fun isItemCompleted(item: ChecklistItem, user: UserEntity?): Boolean {
        if (user == null) return false
        return when (item.id) {
            "complete_profile" -> !user.fullName.isNullOrBlank() && !user.email.isNullOrBlank() && !user.phoneNumber.isNullOrBlank()
            "verify_location" -> user.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED
            "complete_kyc" -> user.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED
            "browse_marketplace" -> user.role != UserType.GENERAL // Assume completed if upgraded from GENERAL
            "join_community" -> user.role != UserType.GENERAL // Assume completed if upgraded
            "add_first_product", "create_first_listing", "add_first_bird", "start_breeding_record" -> false // Manual or require external marking
            else -> false
        }
    }

    /**
     * Mark a checklist item as completed for a user.
     */
    suspend fun markItemCompleted(userId: String, itemId: String): Result<Unit> {
        return runCatching {
            val completedPrefKey = stringSetPreferencesKey("onboarding_checklist_$userId")
            context.onboardingChecklistDataStore.edit { prefs ->
                val current = prefs[completedPrefKey] ?: emptySet()
                prefs[completedPrefKey] = current + itemId
            }
        }
    }

    /**
     * Dismiss the checklist for a user.
     */
    suspend fun dismissChecklist(userId: String): Result<Unit> {
        return runCatching {
            val dismissedPrefKey = booleanPreferencesKey("onboarding_checklist_dismissed_$userId")
            context.onboardingChecklistDataStore.edit { prefs ->
                prefs[dismissedPrefKey] = true
            }
        }
    }

    /**
     * Check if the checklist is relevant for a user.
     */
    fun isChecklistRelevant(user: UserEntity?, items: List<ChecklistItem>): Boolean {
        if (user == null) return false

        // Check if the user registered within the last 7 days
        val registrationDate = user.createdAt?.time ?: 0L
        val now = System.currentTimeMillis()
        val sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000 // 7 days in milliseconds

        val isNewUser = (now - registrationDate) < sevenDaysInMillis
        val isIncomplete = items.any { !it.isCompleted }

        // Show checklist for new users (<7 days) who haven't completed all items
        return isNewUser && isIncomplete
    }
    
    /**
     * Track that onboarding was completed for a user.
     */
    suspend fun markOnboardingCompletedTracked(userId: String): Result<Unit> {
        return runCatching {
            val key = booleanPreferencesKey("onboarding_completed_tracked_$userId")
            context.onboardingChecklistDataStore.edit { prefs ->
                prefs[key] = true
            }
        }
    }
    
    /**
     * Check if onboarding completion was already tracked for a user.
     */
    suspend fun isOnboardingCompletedTracked(userId: String): Boolean {
        val key = booleanPreferencesKey("onboarding_completed_tracked_$userId")
        val prefs = runCatching { 
            context.onboardingChecklistDataStore.data.first() 
        }.getOrElse { 
            return false 
        }
        return prefs[key] ?: false
    }

    /**
     * Migrate legacy SharedPreferences data if needed.
     */
    private suspend fun migrateLegacyIfNeeded(userId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val migratedKey = booleanPreferencesKey("onboarding_migrated_$userId")
        val dismissedPrefKey = booleanPreferencesKey("onboarding_checklist_dismissed_$userId")
        val completedPrefKey = stringSetPreferencesKey("onboarding_checklist_$userId")
        
        val prefs = runCatching { 
            context.onboardingChecklistDataStore.data.first() 
        }.getOrElse { 
            return@withContext
        }
        
        val alreadyMigrated = prefs[migratedKey] ?: false
        if (alreadyMigrated) return@withContext

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

        // Write to DataStore with error handling
        runCatching {
            context.onboardingChecklistDataStore.edit { ds ->
                if (legacyDismissed) ds[dismissedPrefKey] = true
                if (legacyCompleted.isNotEmpty()) ds[completedPrefKey] = legacyCompleted
                ds[migratedKey] = true
            }
        }

        // Optionally clear legacy values
        legacy.edit().remove("onboarding_checklist_dismissed_$userId").remove("onboarding_checklist_$userId").apply()
    }
    
    data class ChecklistItem(
        val id: String,
        val title: String,
        val description: String,
        val isCompleted: Boolean,
        val route: String?
    )
    
    data class ChecklistState(
        val completedIds: Set<String> = emptySet(),
        val isDismissed: Boolean = false,
        val items: List<ChecklistItem> = emptyList(),
        val completionPercentage: Int = 0,
        val currentRole: UserType? = null,
        val showCelebration: Boolean = false,
        val smartSuggestions: List<String> = emptyList(),
        val isChecklistRelevant: Boolean = false,
        val error: String? = null
    )
}