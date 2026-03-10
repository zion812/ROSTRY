package com.rio.rostry.core.model

/**
 * Represents an item in the onboarding checklist.
 */
data class OnboardingChecklistItem(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val route: String?
)
