package com.rio.rostry.domain.usecase

import com.rio.rostry.data.repository.OnboardingChecklistRepository
import com.rio.rostry.domain.model.UserType
import javax.inject.Inject

/**
 * Use case to generate smart suggestions for incomplete checklist items.
 */
class GenerateChecklistSuggestionsUseCase @Inject constructor(
    private val repository: OnboardingChecklistRepository
) {
    operator fun invoke(
        items: List<OnboardingChecklistRepository.ChecklistItem>,
        role: UserType
    ): List<String> {
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
}