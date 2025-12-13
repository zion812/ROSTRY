package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.BreedEntity
import kotlinx.coroutines.flow.Flow

interface BreedRepository {
    fun getAllBreeds(): Flow<List<BreedEntity>>
    fun getBreedsByCulinaryProfile(profile: String): Flow<List<BreedEntity>>
    fun getBreedsByDifficulty(difficulty: String): Flow<List<BreedEntity>>
    suspend fun getBreedById(breedId: String): BreedEntity?
    suspend fun seedBreeds() // For initial data population
}
