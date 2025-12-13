package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.OnboardingChecklistRepository
import javax.inject.Inject

/**
 * Use case to determine if the onboarding checklist is relevant for a user.
 * A checklist is relevant if the user is new (registered within 7 days) and has incomplete items.
 */
class DetermineChecklistRelevanceUseCase @Inject constructor(
    private val repository: OnboardingChecklistRepository
) {
    operator fun invoke(user: UserEntity?, items: List<OnboardingChecklistRepository.ChecklistItem>): Boolean {
        return repository.isChecklistRelevant(user, items)
    }
}