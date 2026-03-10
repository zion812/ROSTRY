package com.rio.rostry.core.model

data class OnboardingActivity(
    val productId: String,
    val productName: String,
    val addedAt: Long,
    val tasksCreated: Int,
    val vaccinationsScheduled: Int,
    val isBatch: Boolean
)

data class OnboardingStats(
    val birdsAddedThisWeek: Int,
    val batchesAddedThisWeek: Int,
    val tasksGenerated: Int
)
