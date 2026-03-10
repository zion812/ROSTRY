package com.rio.rostry.core.model

/**
 * Domain model for bird breeds.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class Breed(
    val id: String,
    val name: String,
    val description: String,
    val culinaryProfile: String, // "Soft", "Textured", "Medicinal"
    val farmingDifficulty: String, // "Beginner", "Intermediate", "Expert"
    val imageUrl: String? = null,
    val tags: List<String> = emptyList()
)
