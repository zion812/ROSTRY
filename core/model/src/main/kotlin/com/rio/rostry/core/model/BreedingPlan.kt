package com.rio.rostry.core.model

/**
 * Domain model for breeding plans.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class BreedingPlan(
    val id: String,
    val farmerId: String,
    val sireId: String?,
    val sireName: String?,
    val damId: String?,
    val damName: String?,
    val createdAt: Long,
    val note: String? = null,
    val simulatedOffspringJson: String,
    val status: String = "PLANNED", // PLANNED, COMPLETED, ARCHIVED
    val priority: Int = 1 // 1=Normal, 2=High
)
