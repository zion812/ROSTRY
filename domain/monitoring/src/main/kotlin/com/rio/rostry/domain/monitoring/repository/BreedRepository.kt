package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.Breed
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for breed management.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface BreedRepository {
    /**
     * Get all available breeds.
     */
    fun getAllBreeds(): Flow<List<Breed>>
    
    /**
     * Get breeds filtered by culinary profile.
     */
    fun getBreedsByCulinaryProfile(profile: String): Flow<List<Breed>>
    
    /**
     * Get breeds filtered by difficulty level.
     */
    fun getBreedsByDifficulty(difficulty: String): Flow<List<Breed>>
    
    /**
     * Get a specific breed by ID.
     */
    suspend fun getBreedById(breedId: String): Breed?
    
    /**
     * Seed initial breed data (for first-time setup).
     */
    suspend fun seedBreeds()
}
